#ifndef MEDIAPIPE_CALCULATORS_IMAGE_IMAGE_MERGE_CALCULATOR_H_
#define MEDIAPIPE_CALCULATORS_IMAGE_IMAGE_MERGE_CALCULATOR_H_

#include <float.h>

#include "mediapipe/calculators/image/image_merge_calculator.pb.h"
#include "mediapipe/framework/calculator_framework.h"

#include "mediapipe/framework/formats/image_frame_opencv.h"

// Merges an image with previous versions of the same image.
//
// Input:
//   IMAGE - ImageFrame representing the input image.
//
// Output:
//   IMAGE - Merged ImageFrame
//
namespace mediapipe {

class ImageMergeCalculator : public CalculatorBase {
 public:
  ImageMergeCalculator() = default;
  ~ImageMergeCalculator() override = default;

  static absl::Status GetContract(CalculatorContract* cc);
  absl::Status Open(CalculatorContext* cc) override;
  absl::Status Process(CalculatorContext* cc) override;
  absl::Status Close(CalculatorContext* cc) override;

 private:
  absl::Status RenderCpu(CalculatorContext* cc);

  mediapipe::ImageMergeCalculatorOptions options_;

  int32_t num_frames_;

  cv::Mat *cache;
  double last_timestamp;
};

}  // namespace mediapipe
#endif  // MEDIAPIPE_CALCULATORS_IMAGE_IMAGE_MERGE_CALCULATOR_H_
