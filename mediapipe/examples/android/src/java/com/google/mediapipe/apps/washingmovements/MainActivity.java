// Copyright 2019 The MediaPipe Authors.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.mediapipe.apps.washingmovements;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.google.mediapipe.components.CameraHelper;
import com.google.mediapipe.components.CameraXPreviewHelper;
import com.google.mediapipe.components.ExternalTextureConverter;
import com.google.mediapipe.components.FrameProcessor;
import com.google.mediapipe.components.PermissionHelper;
import com.google.mediapipe.framework.AndroidAssetUtil;
import com.google.mediapipe.framework.TextureFrame;
import com.google.mediapipe.components.TextureFrameConsumer;
import com.google.mediapipe.glutil.EglManager;

import com.google.mediapipe.framework.AndroidPacketCreator;
import com.google.mediapipe.framework.AndroidPacketGetter;
import com.google.mediapipe.framework.Packet;
import com.google.mediapipe.framework.PacketGetter;
import com.google.mediapipe.framework.ProtoUtil;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.mediapipe.formats.proto.ClassificationProto.Classification;
import com.google.mediapipe.formats.proto.ClassificationProto.ClassificationList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import android.os.Environment;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.Channels;
// import android.graphics.Bitmap;
// import android.graphics.Bitmap.Config;

//import com.google.mediapipe.apps.iswashing.TaskListener;


import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.concurrent.locks.ReentrantLock;


/** Main activity of MediaPipe basic app. */
public class MainActivity extends AppCompatActivity implements TaskListener {
  private static final String TAG = "MainActivity";

  // Flips the camera-preview frames vertically by default, before sending them into FrameProcessor
  // to be processed in a MediaPipe graph, and flips the processed frames back when they are
  // displayed. This maybe needed because OpenGL represents images assuming the image origin is at
  // the bottom-left corner, whereas MediaPipe in general assumes the image origin is at the
  // top-left corner.
  // NOTE: use "flipFramesVertically" in manifest metadata to override this behavior.
  private static final boolean FLIP_FRAMES_VERTICALLY = true;

  // Number of output frames allocated in ExternalTextureConverter.
  // NOTE: use "converterNumBuffers" in manifest metadata to override number of buffers. For
  // example, when there is a FlowLimiterCalculator in the graph, number of buffers should be at
  // least `max_in_flight + max_in_queue + 1` (where max_in_flight and max_in_queue are used in
  // FlowLimiterCalculator options). That's because we need buffers for all the frames that are in
  // flight/queue plus one for the next frame from the camera.
  private static final int NUM_BUFFERS = 2;

  private static final String OUTPUT_WASHING_STATUS = "washing_status";
  private static final String OUTPUT_IMAGE_FRAMES = "output_video_cpu";

  static {
    // Load all native libraries needed by the app.
    System.loadLibrary("mediapipe_jni");
    try {
      System.loadLibrary("opencv_java3");
    } catch (java.lang.UnsatisfiedLinkError e) {
      // Some example apps (e.g. template matching) require OpenCV 4.
      System.loadLibrary("opencv_java4");
    }
  }

  private static final String SERVER_URL_STRING = "http://10.13.137.128:5000/camera.png";

  // Sends camera-preview frames into a MediaPipe graph for processing, and displays the processed
  // frames onto a {@link Surface}.
  protected FrameProcessor processor;
  // Handles camera access via the {@link CameraX} Jetpack support library.
  protected CameraXPreviewHelper cameraHelper;

  // {@link SurfaceTexture} where the camera-preview frames can be accessed.
  private SurfaceTexture previewFrameTexture;
  // {@link SurfaceView} that displays the camera-preview frames processed by a MediaPipe graph.
  private SurfaceView previewDisplayView;

  // Creates and manages an {@link EGLContext}.
  private EglManager eglManager;
  // Converts the GL_TEXTURE_EXTERNAL_OES texture from Android camera into a regular texture to be
  // consumed by {@link FrameProcessor} and the underlying MediaPipe graph.
  private ExternalTextureConverter converter;

  // ApplicationInfo for retrieving metadata defined in the manifest.
  private ApplicationInfo applicationInfo;

  // private final int W = 720;
  // private final int H = 1280;

  // there is no method to query the number of channels exported to Java, so just assume 4
  private final int IMAGE_NUM_CHANNELS = 4;

  ByteBuffer buffer;

  // class CallbackTask implements Runnable {
  //   private final Runnable task;

  //   private final MainActivity callback;

  //   CallbackTask(Runnable task, MainActivity callback) {
  //     this.task = task;
  //     this.callback = callback;
  //   }

  //   public void run() {
  //     task.run();
  //     callback.onUploadFinished();
  //   }

  //   public void start() {
  //     task.start();
  //   }
  // }

    Thread uploadThread;
  // CallbackTask uploadThread;
  //   ReentrantLock lock = new ReentrantLock();
  // Bitmap bitmap;

  // private int framebuffer = 0;

//   class MyTextureFrameConsumer implements TextureFrameConsumer {
//       public void onNewFrame(TextureFrame frame) {
//           Log.e(TAG, " lolcat: got new frame w=" + frame.getWidth() + " h="
//                   + frame.getHeight() + " ts=" + frame.getTimestamp());

// //          bindFramebuffer(frame.getTextureName(), W, H);
          
//           frame.release();
//       }
//   }

    public class NotificationThread implements Runnable{
        ByteBuffer buffer;
	/**
	 * An abstract function that children must implement. This function is where 
	 * all work - typically placed in the run of runnable - should be placed. 
	 */
///	public abstract void doWork();

	/**
	 * Our list of listeners to be notified upon thread completion.
	 */
	private List<TaskListener> listeners = Collections.synchronizedList(new ArrayList<TaskListener>() );

        public NotificationThread(ByteBuffer buffer) {
            this.buffer = buffer;
        }

	/**
	 * Adds a listener to this object. 
	 * @param listener Adds a new listener to this object. 
	 */
	public void addListener( TaskListener listener ){
		listeners.add(listener);
	}

	/**
	 * Removes a particular listener from this object, or does nothing if the listener
	 * is not registered. 
	 * @param listener The listener to remove. 
	 */
	public void removeListener( TaskListener listener ){
		listeners.remove(listener);
	}

	/**
	 * Notifies all listeners that the thread has completed.
	 */
	private final void notifyListeners() {
		synchronized ( listeners ){
			for (TaskListener listener : listeners) {
			  listener.threadComplete(this);
			}
		}
	}

	/**
	 * Implementation of the Runnable interface. This function first calls doRun(), then
	 * notifies all listeners of completion.
	 */
	public void run() {
//            doWork();
            uploadFile(buffer);
            notifyListeners();
	}
    }

  // class MyFrameProcessor extends FrameProcessor {
  //     public MyFrameProcessor(
  //     Context context,
  //     long parentNativeContext,
  //     String graphName,
  //     String inputStream,
  //     String outputStream) {
  //         super(context, parentNativeContext, graphName, inputStream, outputStream);
  //     }

  //     @Override
  //     public void onNewFrame(TextureFrame frame) {
  //         Log.e(TAG, " lolcat: got new frame w=" + frame.getWidth() + " h="
  //                 + frame.getHeight() + " ts=" + frame.getTimestamp());

  //         super.onNewFrame(frame);
  //     }

  // }

    private String getClassificationListDebugString(ClassificationList classifications) {
        String s = "Number of classifications: " + classifications.getClassificationList().size();
        for (Classification classification : classifications.getClassificationList()) {

            s += " {"
                    + " score=" + classification.getScore()
                    + " label=" + classification.getLabel()
                    + "}";
        }

        return s;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(getContentViewLayoutResId());

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    try {
      applicationInfo =
          getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
    } catch (NameNotFoundException e) {
      Log.e(TAG, "Cannot find application info: " + e);
    }

    previewDisplayView = new SurfaceView(this);
    setupPreviewDisplayView();

    // Initialize asset manager so that MediaPipe native libraries can access the app assets, e.g.,
    // binary graphs.
    AndroidAssetUtil.initializeNativeAssetManager(this);
    eglManager = new EglManager(null);
    processor =
        new FrameProcessor(
            this,
            eglManager.getNativeContext(),
            applicationInfo.metaData.getString("binaryGraphName"),
            applicationInfo.metaData.getString("inputVideoStreamName"),
            applicationInfo.metaData.getString("outputVideoStreamName"));
    processor
        .getVideoSurfaceOutput()
        .setFlipY(
            applicationInfo.metaData.getBoolean("flipFramesVertically", FLIP_FRAMES_VERTICALLY));

    PermissionHelper.checkAndRequestCameraPermissions(this);

    ProtoUtil.registerTypeName(ClassificationList.class, "mediapipe.ClassificationList");
    ProtoUtil.registerTypeName(Classification.class, "mediapipe.Classification");

    /*
    // print classifications on debug output
    processor.addPacketCallback(
            OUTPUT_WASHING_STATUS,
          (packet) -> {
            Log.e(TAG, "lolcat: washing status packet.");
            // List<ClassificationList> classifications =
            //     PacketGetter.getProtoVector(packet, ClassificationList.parser());
            // List<Classification> classifications =
            //      PacketGetter.getProtoVector(packet, Classification.parser());

            try {
                ClassificationList classifications =
                        PacketGetter.getProto(packet, ClassificationList.class);
                Log.e(TAG, "lolcat: got the packet.");

                Log.e(
                 TAG,
                "lolcat [TS:"
                     + packet.getTimestamp()
                     + "] "
                     + getClassificationListDebugString(classifications));

            } catch (InvalidProtocolBufferException e) {
                Log.e(TAG, "lolcat: got exception " + e);
            }
          });
    */

    processor.addPacketCallback(
            OUTPUT_IMAGE_FRAMES,
            (packet) -> {
                synchronized ( this ){
                    if (uploadThread != null) {
                        /* already uploading */
                        return;
                    }
                }

                int w = PacketGetter.getImageWidth(packet);
                int h = PacketGetter.getImageHeight(packet);
                Log.e(TAG, "lolcat: got image frame w=" + w + " h=" + h);

                int bbSize = w * h * IMAGE_NUM_CHANNELS;

                // allocate sufficiently large image buffer for the frame
                if (buffer == null || buffer.capacity() != bbSize) {
                    buffer = ByteBuffer.allocateDirect(bbSize);
                    // bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                }

                if (PacketGetter.getImageData(packet, buffer)) {
                    //Log.e(TAG, "lolcat: saved the image to buffer: " + buffer.toString() + " buffer.array().length=" + buffer.array().length);

                    NotificationThread nt = new NotificationThread(buffer);
                    nt.addListener(this);
                    synchronized ( this ){
                        uploadThread = new Thread(nt);
                    }
                    uploadThread.start();

                    // byte[] arr = buffer.array();
                    // for (int i = 0; i < 5; i++) {
                    //     int pos = i * 4;
                    //     byte r = arr[pos + 0];
                    //     byte g = arr[pos + 1];
                    //     byte b = arr[pos + 2];
                    //     byte a = arr[pos + 3];
                    //     Log.e(TAG, "lolcat: i=" + i + " r,g,b,a=" + r + "," + g + "," + b + "," + a);

                    // }
                    // if (isExternalStorageWritable()) {
                    //     try {
                    //         File path = new File(this.getExternalFilesDir(
                    //                         Environment.DIRECTORY_PICTURES), "iswashing");
                    //         if (!path.exists() && !path.mkdirs()) {
                    //             Log.e(TAG, "lolcat: Directory not created");
                    //         } else {
                    //             File file = new File(path, "camera-image.rgb");
                    //             if (file.exists()) {
                    //                 Log.e(TAG, "lolcat: already exists");
                    //             } else {
                    //                 FileOutputStream stream = new FileOutputStream(file);
                    //                 try {
                    //                     stream.getChannel().write(buffer);
                    //                 } finally {
                    //                     stream.close();
                    //                 }
                    //                 Log.e(TAG, "lolcat: saved to file: " + file.toString() + " bytes=" + buffer.array().length);
                    //             }
                    //         }
                    //     } catch (IOException ex) {
                    //         Log.e(TAG, "lolcat: io exception: " + ex);
                    //     }
                    // } else {
                    //     Log.e(TAG, "lolcat: ext storage not writable");
                    // }

                    // StringBuilder sb = new StringBuilder();
                    // for (byte b : arr) {
                    //     sb.append(String.format("%02X", b));
                    // }
                    // for (int i = 0; i < w / 4; ++i) {
                    //     String s = "";
                    //     for (int j = 0; j < 4 * h * IMAGE_NUM_CHANNELS; ++j) {
                    //         int pos = i * 4 * h * IMAGE_NUM_CHANNELS + j;
                    //         if (pos % 4 != 3) {
                    //             s += " " + arr[pos];
                    //         }
                    //     }
                    //     Log.e(TAG, "lolcat: row[" + i + "]=" + s);
                    // }
                    //Log.e(TAG, "lolcat: s=" + sb.toString());

                    //bitmap.copyPixelsFromBuffer(buffer);
                    //Log.e(TAG, "lolcat: bitmap copied: " + bitmap.getPixel(0, 0));

                } else {
                    Log.e(TAG, "lolcat: failed to save the image in a buffer");
                }

//                Bitmap b = AndroidPacketGetter.getBitmapFromRgba(packet);
//                Log.e(TAG, "lolcat: got bitmap w=" + b.getWidth() + " h=" + b.getHeight());
            });
  }

    public void uploadFile(ByteBuffer buffer) {
//        String fileName = sourceFileUri;

        Log.e(TAG, "lolcat: uploading file " + buffer.toString());
  
        HttpURLConnection conn = null;
        DataOutputStream dos = null;  
        final String lineEnd = "\r\n";
        final String hyphens = "--";
        final String boundary = "********";
//        int bytesRead, bytesAvailable, bufferSize;
//        byte[] buffer;
//        int maxBufferSize = 1 * 1024 * 1024; 
//        File sourceFile = new File(sourceFileUri);

        try { 
            // open a URL connection to the Servlet
            //FileInputStream fileInputStream = new FileInputStream(sourceFile);
            //URL url = new URL(upLoadServerUri);

            URL serverUrl = new URL(SERVER_URL_STRING);
                    
            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) serverUrl.openConnection(); 
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Handwash App");
//            conn.setRequestProperty("Connection", "Keep-Alive");
//            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//            conn.setRequestProperty("uploaded_file", fileName);

            Log.e(TAG, "lolcat: connection opened");

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(hyphens + boundary + lineEnd); 
            dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"camera-image.rbga\"" + lineEnd);
            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            // bytesAvailable = fileInputStream.available(); 
            // bufferSize = Math.min(bytesAvailable, maxBufferSize);
            // buffer = new byte[bufferSize];

                   // // read file and write it into form...
                   // bytesRead = fileInputStream.read(buffer, 0, bufferSize);  

                   // while (bytesRead > 0) {

                   //   dos.write(buffer, 0, bufferSize);
                   //   bytesAvailable = fileInputStream.available();
                   //   bufferSize = Math.min(bytesAvailable, maxBufferSize);
                   //   bytesRead = fileInputStream.read(buffer, 0, bufferSize);   

                   //  }

//            dos.getChannel().write(buffer);
//            WritableByteChannel channel = Channels.newChannel(dos);
//            WritableByteChannel channel = dos.getChannel();
//            channel.write(buffer);
//            channel.flush();
//            dos.flush();
            //dos.write(buffer, 0, bufferSize);

//            byte[] arr = buffer.array();
//            dos.write(arr, 0, buffer.remaining());
            buffer.rewind();
            byte[] arr = new byte[buffer.remaining()];
            buffer.get(arr);
            dos.write(arr, 0, arr.length);

            // byte[] bytesArray = new byte[buffer.remaining()];
            // buffer.get(bytesArray, 0, bytesArray.length);
            // dos.write(bytesArray, 0, bytesArray.length);

            Log.e(TAG, "lolcat: buffer written, len=" + arr.length);

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(hyphens + boundary + hyphens + lineEnd);

            Log.e(TAG, "lolcat: wait for reply");

            // Responses from the server (code and message)
            int serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.e(TAG, "lolcat: HTTP Response is " + serverResponseMessage + ": " + serverResponseCode);

            if (serverResponseCode == 200){
                // TODO: parse the responsse
            }
                    
            //close the streams //
            //fileInputStream.close();
            dos.flush();
            dos.close();
                     
        } catch (Exception e) {
            e.printStackTrace();
                   
            Log.e(TAG, "lolcat: upload file to server failed, Exception: " + e.getMessage());
        }
    }

    public void threadComplete( Runnable runner ) {
        Log.e(TAG, "lolcat: setting upload thread to null");

//        lock.lock();
//         try {
        synchronized(this) {
            uploadThread = null;
        }
        // } finally {
        //     lock.unlock();
        // }
    }

  // Used to obtain the content view for this application. If you are extending this class, and
  // have a custom layout, override this method and return the custom layout.
  protected int getContentViewLayoutResId() {
    return R.layout.activity_main;
  }

  @Override
  protected void onResume() {
    super.onResume();
    converter =
        new ExternalTextureConverter(
            eglManager.getContext(),
            applicationInfo.metaData.getInt("converterNumBuffers", NUM_BUFFERS));
    converter.setFlipY(
        applicationInfo.metaData.getBoolean("flipFramesVertically", FLIP_FRAMES_VERTICALLY));
    converter.setConsumer(processor);
    if (PermissionHelper.cameraPermissionsGranted(this)) {
      startCamera();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    converter.close();

    // Hide preview display until we re-open the camera again.
    previewDisplayView.setVisibility(View.GONE);
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, String[] permissions, int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  protected void onCameraStarted(SurfaceTexture surfaceTexture) {
    previewFrameTexture = surfaceTexture;
    // Make the display view visible to start showing the preview. This triggers the
    // SurfaceHolder.Callback added to (the holder of) previewDisplayView.
    previewDisplayView.setVisibility(View.VISIBLE);

    Log.e(TAG, "lolcat: camera started");
  }

  protected Size cameraTargetResolution() {
      // smaller sizes do not work: 640x360 appears to be the minimum always
      // used if smaller resolution is specified
      return new Size(640, 360);
  }

  public void startCamera() {
    cameraHelper = new CameraXPreviewHelper();
    cameraHelper.setOnCameraStartedListener(
        surfaceTexture -> {
          onCameraStarted(surfaceTexture);
        });
    CameraHelper.CameraFacing cameraFacing =
        applicationInfo.metaData.getBoolean("cameraFacingFront", false)
            ? CameraHelper.CameraFacing.FRONT
            : CameraHelper.CameraFacing.BACK;
    cameraHelper.startCamera(
        this, cameraFacing, /*unusedSurfaceTexture=*/ null, cameraTargetResolution());
  }

  protected Size computeViewSize(int width, int height) {
    Log.e(TAG, "lolcat: computeViewSize width=" + width + " height=" + height);
    return new Size(width, height);
  }

  protected void onPreviewDisplaySurfaceChanged(
      SurfaceHolder holder, int format, int width, int height) {
    // (Re-)Compute the ideal size of the camera-preview display (the area that the
    // camera-preview frames get rendered onto, potentially with scaling and rotation)
    // based on the size of the SurfaceView that contains the display.
    Size viewSize = computeViewSize(width, height);
    Size displaySize = cameraHelper.computeDisplaySizeFromViewSize(viewSize);
    boolean isCameraRotated = cameraHelper.isCameraRotated();

    // Connect the converter to the camera-preview frames as its input (via
    // previewFrameTexture), and configure the output width and height as the computed
    // display size.
    converter.setSurfaceTextureAndAttachToGLContext(
        previewFrameTexture,
        isCameraRotated ? displaySize.getHeight() : displaySize.getWidth(),
        isCameraRotated ? displaySize.getWidth() : displaySize.getHeight());
  }

  private void setupPreviewDisplayView() {
    previewDisplayView.setVisibility(View.GONE);
    ViewGroup viewGroup = findViewById(R.id.preview_display_layout);
    viewGroup.addView(previewDisplayView);

    previewDisplayView
        .getHolder()
        .addCallback(
            new SurfaceHolder.Callback() {
              @Override
              public void surfaceCreated(SurfaceHolder holder) {
                processor.getVideoSurfaceOutput().setSurface(holder.getSurface());
              }

              @Override
              public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                onPreviewDisplaySurfaceChanged(holder, format, width, height);
              }

              @Override
              public void surfaceDestroyed(SurfaceHolder holder) {
                processor.getVideoSurfaceOutput().setSurface(null);
              }
            });
  }
}
