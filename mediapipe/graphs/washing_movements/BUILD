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
    name = "washing_movements_mobile_calculators",
    deps = [
        "//mediapipe/calculators/core:flow_limiter_calculator",
        "//mediapipe/calculators/util:annotation_overlay_calculator",
        "//mediapipe/calculators/util:labels_to_render_data_calculator",
        "//mediapipe/calculators/image:image_cropping_calculator",
        "//mediapipe/gpu:gpu_buffer_to_image_frame_calculator",
        "//mediapipe/modules/hand_washing:washing_movements_gpu"
    ],
)

mediapipe_binary_graph(
    name = "washing_movements_mobile_gpu_binary_graph",
    graph = "washing_movements_mobile.pbtxt",
    output_name = "washing_movements_mobile_gpu.binarypb",
    deps = [":washing_movements_mobile_calculators"],
)
