licenses(["notice"])

package(default_visibility = ["//visibility:private"])

cc_binary(
    name = "libmediapipe_jni.so",
    linkshared = 1,
    linkstatic = 1,
    deps = [
        "//mediapipe/graphs/hand_tracking:detection_mobile_calculators",
        "//mediapipe/java/com/google/mediapipe/framework/jni:mediapipe_framework_jni",
    ],
)

cc_library(
    name = "mediapipe_jni_lib",
    srcs = [":libmediapipe_jni.so"],
    alwayslink = 1,
)

android_binary(
    name = "iswashing",
    srcs = glob(["*.java"]),
    assets = [
        "//mediapipe/graphs/is_washing:hand_detection_mobile_gpu.binarypb",
        "//mediapipe/modules/handwashing:is_washing.tflite",
        "//mediapipe/modules/hand_landmark:handedness.txt",
        "//mediapipe/modules/handwashing:washing_status.txt",
        "//mediapipe/modules/palm_detection:palm_detection.tflite",
    ],
    assets_dir = "",
    manifest = "//mediapipe/examples/android/src/java/com/google/mediapipe/apps/basic:AndroidManifest.xml",
    manifest_values = {
        "applicationId": "com.google.mediapipe.apps.iswashing",
        "appName": "Hand Washing Detection",
        "mainActivity": "com.google.mediapipe.apps.basic.MainActivity",
        "cameraFacingFront": "True",
        "binaryGraphName": "hand_detection_mobile_gpu.binarypb",
        "inputVideoStreamName": "input_video",
        "outputVideoStreamName": "output_video",
        "flipFramesVertically": "True",
        "converterNumBuffers": "2",
    },
    multidex = "native",
    deps = [
        ":mediapipe_jni_lib",
        "//mediapipe/examples/android/src/java/com/google/mediapipe/apps/basic:basic_lib",
    ],
)
