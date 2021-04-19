#include "mediapipe/calculators/image/image_merge_calculator.h"

#include <cmath>

#include "mediapipe/framework/formats/image_frame.h"
#include "mediapipe/framework/formats/image_frame_opencv.h"
#include "mediapipe/framework/port/opencv_core_inc.h"
#include "mediapipe/framework/port/opencv_imgproc_inc.h"
#include "mediapipe/framework/port/ret_check.h"
#include "mediapipe/framework/port/status.h"

namespace {

const float FPS = 5; // use 5 frames per second
const float MIN_TIMESTAMP_DIFF_SEC = 1.f / FPS;

}  // namespace

namespace mediapipe {

namespace {

constexpr char kImageTag[] = "IMAGE";

}  // namespace

REGISTER_CALCULATOR(ImageMergeCalculator);

absl::Status ImageMergeCalculator::GetContract(CalculatorContract* cc) {
  RET_CHECK(cc->Inputs().HasTag(kImageTag));

  RET_CHECK(cc->Outputs().HasTag(kImageTag));

  cc->Inputs().Tag(kImageTag).Set<ImageFrame>();
  cc->Outputs().Tag(kImageTag).Set<ImageFrame>();

  return absl::OkStatus();
}

absl::Status ImageMergeCalculator::Open(CalculatorContext* cc) {
  cc->SetOffset(TimestampDiff(0));

  options_ = cc->Options<mediapipe::ImageMergeCalculatorOptions>();
  num_frames_ = options_.has_num_frames() ? options_.num_frames() : 5;

  cache = NULL;
  last_timestamp = 0;

  return absl::OkStatus();
}

absl::Status ImageMergeCalculator::Process(CalculatorContext* cc) {
  MP_RETURN_IF_ERROR(RenderCpu(cc));

  return absl::OkStatus();
}

absl::Status ImageMergeCalculator::Close(CalculatorContext* cc) {
  delete[] cache;

  return absl::OkStatus();
}

absl::Status ImageMergeCalculator::RenderCpu(CalculatorContext* cc) {
  const auto& input_img = cc->Inputs().Tag(kImageTag).Get<ImageFrame>();
  cv::Mat input_mat = formats::MatView(&input_img);

  if (cache == NULL) {
      cache = new cv::Mat[num_frames_];
      // first-time initialization
      for (int i = 0; i < num_frames_; ++i) {
          input_mat.copyTo(cache[i]);
      }
      last_timestamp = cc->InputTimestamp().Seconds();
  } else if (cc->InputTimestamp().Seconds() - last_timestamp > MIN_TIMESTAMP_DIFF_SEC) {
      // make the first element the oldest
      for (int i = 0; i < num_frames_ - 1; ++i) {
          cache[i + 1].copyTo(cache[i]);
      }
      input_mat.copyTo(cache[num_frames_ - 1]);
      last_timestamp = cc->InputTimestamp().Seconds();
  }

  // cv::Mat merged_mat; // XXX: could get rid of this???
  // cv::vconcat(cache, num_frames_, merged_mat);

  std::unique_ptr<ImageFrame> output_frame(new ImageFrame(
                  input_img.Format(), input_img.Width(), input_img.Height() * num_frames_));
  
  cv::Mat output_mat = formats::MatView(output_frame.get());
//  merged_mat.copyTo(output_mat);

  cv::vconcat(cache, num_frames_, output_mat);

  cc->Outputs().Tag(kImageTag).Add(output_frame.release(),
                                   cc->InputTimestamp());

  return absl::OkStatus();
}

}  // namespace mediapipe
