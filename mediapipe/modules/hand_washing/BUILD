load(
    "//mediapipe/framework/tool:mediapipe_graph.bzl",
    "mediapipe_simple_subgraph",
)

licenses(["notice"])

package(default_visibility = ["//visibility:public"])

mediapipe_simple_subgraph(
    name = "is_washing_gpu",
    graph = "is_washing_gpu.pbtxt",
    register_as = "IsWashingGpu",
    deps = [
        "//mediapipe/calculators/tensor:image_to_tensor_calculator",
        "//mediapipe/calculators/tensor:inference_calculator",

        "//mediapipe/calculators/tensor:tensors_to_classification_calculator",
        "//mediapipe/calculators/util:detection_letterbox_removal_calculator",

        "//mediapipe/calculators/image:image_cropping_calculator",
        "//mediapipe/calculators/image:image_transformation_calculator",
    ],
)

mediapipe_simple_subgraph(
    name = "washing_movements_gpu",
    graph = "washing_movements_gpu.pbtxt",
    register_as = "WashingMovementsGpu",
    deps = [
        "//mediapipe/calculators/tensor:image_to_tensor_calculator",
        "//mediapipe/calculators/tensor:inference_calculator",

        "//mediapipe/calculators/tensor:tensors_to_classification_calculator",
        "//mediapipe/calculators/util:detection_letterbox_removal_calculator",

        "//mediapipe/calculators/image:image_cropping_calculator",
        "//mediapipe/calculators/image:image_transformation_calculator",
    ],
)

exports_files([
        "is_washing.tflite",
        "washing_status.txt",
        "washing_movements.tflite",
        "washing_movements.txt",
])
