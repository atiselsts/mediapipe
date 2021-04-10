licenses(["notice"])

package(default_visibility = ["//visibility:private"])

cc_binary(
    name = "libmediapipe_jni.so",
    linkshared = 1,
    linkstatic = 1,
    deps = [
        "//mediapipe/graphs/is_washing:is_washing_mobile_calculators",
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
    resource_files = glob(["res/**"]),
    assets = [
        "//mediapipe/graphs/is_washing:is_washing_mobile_gpu.binarypb",
        "//mediapipe/modules/is_washing:is_washing.tflite",
        "//mediapipe/modules/is_washing:washing_status.txt",
    ],
    assets_dir = "",
    manifest = "//mediapipe/examples/android/src/java/com/google/mediapipe/apps/iswashing:AndroidManifest.xml",
    manifest_values = {
        "applicationId": "com.google.mediapipe.apps.iswashing",
        "appName": "Washing Detection",
        "mainActivity": "com.google.mediapipe.apps.iswashing.MainActivity",
        "cameraFacingFront": "True",
        "binaryGraphName": "is_washing_mobile_gpu.binarypb",
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
