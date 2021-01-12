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

#include <cmath>
#include <vector>

#include "mediapipe/calculators/util/landmark_to_string_calculator.pb.h"
#include "mediapipe/framework/calculator_framework.h"
#include "mediapipe/framework/formats/landmark.pb.h"
#include "mediapipe/framework/port/ret_check.h"

namespace mediapipe {

namespace {

constexpr char kLandmarksTag[] = "NORM_LANDMARKS";
constexpr char kStringTag[] = "STRING";

}  // namespace

// Converts a vector of landmarks to a vector of floats or a matrix.
// Input:
//   NORM_LANDMARKS: A NormalizedLandmarkList proto.
//
// Output:
//   STRING: String from flattened landmarks.
//
// Usage example:
// node {
//   calculator: "LandmarkToStringCalculator"
//   input_stream: "NORM_LANDMARKS:landmarks"
//   output_stream: "STRING:landmarks"
// }
class LandmarkToStringCalculator : public CalculatorBase {
    const int num_dimensions = 3; // TODO: make this an option

 public:
  static ::mediapipe::Status GetContract(CalculatorContract* cc) {
    cc->Inputs().Tag(kLandmarksTag).Set<NormalizedLandmarkList>();

    RET_CHECK(cc->Outputs().HasTag(kStringTag));
    cc->Outputs().Tag(kStringTag).Set<std::string>();

    return ::mediapipe::OkStatus();
  }

  ::mediapipe::Status Open(CalculatorContext* cc) override {
    cc->SetOffset(TimestampDiff(0));
    // const auto& options =
    //     cc->Options<::mediapipe::LandmarkToStringCalculatorOptions>();
    // num_dimensions_ = options.num_dimensions();
    // // Currently number of dimensions must be within [1, 3].
    // RET_CHECK_GE(num_dimensions_, 1);
    // RET_CHECK_LE(num_dimensions_, 3);
    return ::mediapipe::OkStatus();
  }

  ::mediapipe::Status Process(CalculatorContext* cc) override {
    // Only process if there's input landmarks.
    if (cc->Inputs().Tag(kLandmarksTag).IsEmpty()) {
      return ::mediapipe::OkStatus();
    }

    const auto& input_landmarks =
        cc->Inputs().Tag(kLandmarksTag).Get<NormalizedLandmarkList>();

    auto output_string = absl::make_unique<std::string>();
    for (int i = 0; i < input_landmarks.landmark_size(); ++i) {
        const NormalizedLandmark& landmark = input_landmarks.landmark(i);
        char output_buf[100];
        sprintf(output_buf, "x=%f y=%f z=%f", landmark.x(), landmark.y(), landmark.z());
        output_string->append(output_buf);
    }

    cc->Outputs()
            .Tag(kStringTag)
            .Add(output_string.release(), cc->InputTimestamp());
    return ::mediapipe::OkStatus();
  }

 private:
  int num_dimensions_ = 0;
};
REGISTER_CALCULATOR(LandmarkToStringCalculator);

}  // namespace mediapipe
