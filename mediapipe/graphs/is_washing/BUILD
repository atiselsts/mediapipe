load(
    "//mediapipe/framework/tool:mediapipe_graph.bzl",
    "mediapipe_binary_graph",
)

licenses(["notice"])  # Apache 2.0

package(default_visibility = ["//visibility:public"])

exports_files(glob([
    "*.pbtxt",
]))

#cc_library(
#    name = "mobile_calculators",
#    deps = [
#        "//mediapipe/calculators/core:constant_side_packet_calculator",
#        "//mediapipe/calculators/core:flow_limiter_calculator",
#        "//mediapipe/graphs/is_washing/subgraphs:hand_renderer_gpu",
#        "//mediapipe/modules/handwashing:is_washing_gpu"
#    ],
#)

cc_library(
    name = "detection_mobile_calculators",
    deps = [
        "//mediapipe/calculators/core:flow_limiter_calculator",
        "//mediapipe/calculators/tflite:tflite_custom_op_resolver_calculator",
        "//mediapipe/calculators/util:annotation_overlay_calculator",
        "//mediapipe/modules/handwashing:is_washing_gpu"
    ],
)

mediapipe_binary_graph(
    name = "hand_detection_mobile_gpu_binary_graph",
    graph = "hand_detection_mobile.pbtxt",
    output_name = "hand_detection_mobile_gpu.binarypb",
    deps = [":detection_mobile_calculators"],
)
