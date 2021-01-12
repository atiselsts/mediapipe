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

#include "mediapipe/calculators/util/rect_to_string_calculator.pb.h"
#include "mediapipe/framework/calculator_framework.h"
#include "mediapipe/framework/formats/rect.pb.h"
#include "mediapipe/framework/port/ret_check.h"
namespace mediapipe {

namespace {

constexpr char kNormRectsTag[] = "NORM_RECTS";
constexpr char kStringTag[] = "STRING";

}  // namespace

// A calculator that converts NormRects proto to strings.
//
// Example config:
// node {
//   calculator: "RectToStringCalculator"
//   input_stream: "NORM_RECTS:multi_palm_detections"
//   output_stream: "STRING:multi_palm_detection_strings"
// }
class RectToStringCalculator : public CalculatorBase {
 public:
  RectToStringCalculator() {}
  ~RectToStringCalculator() override {}
  RectToStringCalculator(const RectToStringCalculator&) =
      delete;
  RectToStringCalculator& operator=(
      const RectToStringCalculator&) = delete;

  static ::mediapipe::Status GetContract(CalculatorContract* cc);

  ::mediapipe::Status Open(CalculatorContext* cc) override;

  ::mediapipe::Status Process(CalculatorContext* cc) override;

  ::mediapipe::Status Close(CalculatorContext* cc) override;

 private:
};
REGISTER_CALCULATOR(RectToStringCalculator);

::mediapipe::Status RectToStringCalculator::GetContract(
    CalculatorContract* cc) {
  RET_CHECK(cc->Inputs().HasTag(kNormRectsTag))
      << "Input stream is not provided.";
  cc->Inputs().Tag(kNormRectsTag).Set<std::vector<NormalizedRect>>();

  cc->Outputs().Tag(kStringTag).Set<std::string>();

  return ::mediapipe::OkStatus();
}

::mediapipe::Status RectToStringCalculator::Open(
    CalculatorContext* cc) {
  return ::mediapipe::OkStatus();
}

::mediapipe::Status RectToStringCalculator::Process(
    CalculatorContext* cc) {

  auto s = absl::make_unique<std::string>();
  char output_data[1000];

  double ts = cc->InputTimestamp().Seconds();

  const auto& rects =
          cc->Inputs().Tag(kNormRectsTag).Get<std::vector<NormalizedRect>>();
  for (auto& rect : rects) {
      snprintf(output_data, sizeof(output_data), "ts=%f x=%f y=%f w=%f h=%f rotation=%f\n",
              ts, rect.x_center(), rect.y_center(), rect.width(), rect.height(), rect.rotation());
      s->append(output_data);
  }

  cc->Outputs()
      .Tag(kStringTag)
      .Add(s.release(), cc->InputTimestamp());

  return ::mediapipe::OkStatus();
}

::mediapipe::Status RectToStringCalculator::Close(
    CalculatorContext* cc) {
  return ::mediapipe::OkStatus();
}

}  // namespace mediapipe
