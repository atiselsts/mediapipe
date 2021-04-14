load(
    "//mediapipe/framework/tool:mediapipe_graph.bzl",
    "mediapipe_binary_graph",
)

licenses(["notice"])  # Apache 2.0

package(default_visibility = ["//visibility:public"])

exports_files(glob([
    "*.pbtxt",
]))

cc_library(
    name = "is_washing_mobile_calculators",
    deps = [
        "//mediapipe/calculators/core:flow_limiter_calculator",
        "//mediapipe/calculators/util:annotation_overlay_calculator",
        "//mediapipe/calculators/util:labels_to_render_data_calculator",
        "//mediapipe/calculators/image:image_cropping_calculator",
#        "//mediapipe/calculators/tensor:tensors_to_floats_calculator",
        "//mediapipe/gpu:gpu_buffer_to_image_frame_calculator",
        "//mediapipe/modules/hand_washing:is_washing_gpu"
    ],
)

mediapipe_binary_graph(
    name = "is_washing_mobile_gpu_binary_graph",
    graph = "is_washing_mobile.pbtxt",
    output_name = "is_washing_mobile_gpu.binarypb",
    deps = [":is_washing_mobile_calculators"],
)
