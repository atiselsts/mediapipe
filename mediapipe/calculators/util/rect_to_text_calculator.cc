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

#include "mediapipe/calculators/util/rect_to_text_calculator.pb.h"
#include "mediapipe/framework/calculator_framework.h"
#include "mediapipe/framework/formats/rect.pb.h"
#include "mediapipe/framework/port/ret_check.h"
namespace mediapipe {

namespace {

constexpr char kNormRectsTag[] = "NORM_RECTS";

}  // namespace

// A calculator that converts NormRects proto to Text proto and saves to a file.
//
// Example config:
// node {
//   calculator: "RectToTextCalculator"
//   input_stream: "NORM_RECTS:multi_palm_detections"
//   input_side_packet: "OUTPUT_FILE_PATH:output_annot_path_palms"
// }
class RectToTextCalculator : public CalculatorBase {
 public:
  RectToTextCalculator() {}
  ~RectToTextCalculator() override {}
  RectToTextCalculator(const RectToTextCalculator&) =
      delete;
  RectToTextCalculator& operator=(
      const RectToTextCalculator&) = delete;

  static ::mediapipe::Status GetContract(CalculatorContract* cc);

  ::mediapipe::Status Open(CalculatorContext* cc) override;

  ::mediapipe::Status Process(CalculatorContext* cc) override;

  ::mediapipe::Status Close(CalculatorContext* cc) override;

 private:
  ::mediapipe::Status SetUpFileWriter();
  std::string output_file_path_;
  unsigned int counter = 0;

  FILE *output_file_;
};
REGISTER_CALCULATOR(RectToTextCalculator);

::mediapipe::Status RectToTextCalculator::GetContract(
    CalculatorContract* cc) {
  RET_CHECK(cc->Inputs().HasTag(kNormRectsTag))
      << "Input stream is not provided.";
  cc->Inputs().Tag(kNormRectsTag).Set<std::vector<NormalizedRect>>();

  RET_CHECK(cc->InputSidePackets().HasTag("OUTPUT_FILE_PATH"));
  cc->InputSidePackets().Tag("OUTPUT_FILE_PATH").Set<std::string>();

  return ::mediapipe::OkStatus();
}

::mediapipe::Status RectToTextCalculator::Open(
    CalculatorContext* cc) {
  output_file_path_ =
      cc->InputSidePackets().Tag("OUTPUT_FILE_PATH").Get<std::string>();

  return SetUpFileWriter();
}

::mediapipe::Status RectToTextCalculator::Process(
    CalculatorContext* cc) {
  char output_data[200];

  output_file_ = fopen(output_file_path_.c_str(), "a+");
  if (output_file_ == NULL) {
      return ::mediapipe::UnavailableErrorBuilder(MEDIAPIPE_LOC)
           << "Failed to open file at " << output_file_path_;
  }

  double ts = cc->InputTimestamp().Seconds();
  const auto& rects =
          cc->Inputs().Tag(kNormRectsTag).Get<std::vector<NormalizedRect>>();
  for (auto& rect : rects) {
      snprintf(output_data, sizeof(output_data), "ts=%f x=%f y=%f w=%f h=%f rotation=%f\n",
              ts, rect.x_center(), rect.y_center(), rect.width(), rect.height(), rect.rotation());
      fputs(output_data, output_file_);
  }

  fputs("====================\n", output_file_);
  counter++;

  fclose(output_file_);

  return ::mediapipe::OkStatus();
}

::mediapipe::Status RectToTextCalculator::Close(
    CalculatorContext* cc) {
  output_file_ = fopen(output_file_path_.c_str(), "a+");
  if (output_file_) {
      fwrite("\n", 1, 1, output_file_);
      fclose(output_file_);
      output_file_ = NULL;
  }
  return ::mediapipe::OkStatus();
}

::mediapipe::Status RectToTextCalculator::SetUpFileWriter() {
  output_file_ = fopen(output_file_path_.c_str(), "w"); /* clear the file */
  if (output_file_ == NULL) {
    return ::mediapipe::InvalidArgumentErrorBuilder(MEDIAPIPE_LOC)
           << "Fail to open file at " << output_file_path_;
  }
  fclose(output_file_);
  output_file_ = NULL;
  return ::mediapipe::OkStatus();
}

}  // namespace mediapipe
