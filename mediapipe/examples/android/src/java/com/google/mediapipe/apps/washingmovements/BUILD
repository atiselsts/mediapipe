licenses(["notice"])

package(default_visibility = ["//visibility:private"])

cc_binary(
    name = "libmediapipe_jni.so",
    linkshared = 1,
    linkstatic = 1,
    deps = [
        "//mediapipe/graphs/washing_movements:washing_movements_mobile_calculators",
        "//mediapipe/java/com/google/mediapipe/framework/jni:mediapipe_framework_jni",
    ],
)

cc_library(
    name = "mediapipe_jni_lib",
    srcs = [":libmediapipe_jni.so"],
    alwayslink = 1,
)

android_binary(
    name = "washingmovements",
    srcs = glob(["*.java"]),
    resource_files = glob(["res/**"]),
    assets = [
        "//mediapipe/graphs/washing_movements:washing_movements_mobile_gpu.binarypb",
        "//mediapipe/modules/hand_washing:washing_movements.tflite",
        "//mediapipe/modules/hand_washing:is_washing.tflite",
        "//mediapipe/modules/hand_washing:washing_status.txt",
        "//mediapipe/modules/hand_washing:washing_movements.txt",
    ],
    assets_dir = "",
    manifest = "//mediapipe/examples/android/src/java/com/google/mediapipe/apps/washingmovements:AndroidManifest.xml",
    manifest_values = {
        "applicationId": "com.google.mediapipe.apps.washingmovements",
        "appName": "Washing Movements",
        "mainActivity": "com.google.mediapipe.apps.washingmovements.MainActivity",
        "cameraFacingFront": "True",
        "binaryGraphName": "washing_movements_mobile_gpu.binarypb",
        "inputVideoStreamName": "input_video",
        "outputVideoStreamName": "output_video",
        "flipFramesVertically": "True",
        "converterNumBuffers": "2",
    },
    multidex = "native",
    deps = [
        ":mediapipe_jni_lib",

        "//mediapipe/java/com/google/mediapipe/components:android_camerax_helper",
        "//mediapipe/java/com/google/mediapipe/components:android_components",
        "//mediapipe/java/com/google/mediapipe/framework:android_framework",
        "//mediapipe/java/com/google/mediapipe/glutil",

        "//mediapipe/framework/formats:classification_java_proto_lite",

        "//third_party:androidx_appcompat",
        "//third_party:androidx_constraint_layout",
        "//third_party:opencv",
        "@maven//:androidx_concurrent_concurrent_futures",
        "@maven//:com_google_guava_guava",

        "@com_google_protobuf//:protobuf_javalite",
    ],
)
