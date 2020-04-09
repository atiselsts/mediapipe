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

#include "mediapipe/calculators/util/landmarks_to_text_calculator.pb.h"
#include "mediapipe/framework/calculator_framework.h"
#include "mediapipe/framework/formats/landmark.pb.h"
#include "mediapipe/framework/port/ret_check.h"

namespace mediapipe {

namespace {

constexpr char kLandmarksTag[] = "NORM_LANDMARKS";

}  // namespace

// A calculator that converts NormalizedLandmarkList proto to Text proto and saves to a file.
//
// Example config:
// node {
//   calculator: "LandmarkToTextCalculator"
//   input_stream: "LANDMARKS:multi_hand_landmarks"
//   input_side_packet: "OUTPUT_FILE_PATH:output_annot_path_landmarks"
// }
class LandmarkToTextCalculator : public CalculatorBase {
 public:
  LandmarkToTextCalculator() {}
  ~LandmarkToTextCalculator() override {}
  LandmarkToTextCalculator(const LandmarkToTextCalculator&) =
      delete;
  LandmarkToTextCalculator& operator=(
      const LandmarkToTextCalculator&) = delete;

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
REGISTER_CALCULATOR(LandmarkToTextCalculator);

::mediapipe::Status LandmarkToTextCalculator::GetContract(
    CalculatorContract* cc) {
  RET_CHECK(cc->Inputs().HasTag(kLandmarksTag))
      << "Input stream is not provided.";
  cc->Inputs().Tag(kLandmarksTag).Set<std::vector<NormalizedLandmarkList>>();

  RET_CHECK(cc->InputSidePackets().HasTag("OUTPUT_FILE_PATH"));
  cc->InputSidePackets().Tag("OUTPUT_FILE_PATH").Set<std::string>();

  return ::mediapipe::OkStatus();
}

::mediapipe::Status LandmarkToTextCalculator::Open(
    CalculatorContext* cc) {
  output_file_path_ =
      cc->InputSidePackets().Tag("OUTPUT_FILE_PATH").Get<std::string>();

  return SetUpFileWriter();
}

::mediapipe::Status LandmarkToTextCalculator::Process(
    CalculatorContext* cc) {
  char output_data[200];

  output_file_ = fopen(output_file_path_.c_str(), "a+");
  if (output_file_ == NULL) {
      return ::mediapipe::UnavailableErrorBuilder(MEDIAPIPE_LOC)
           << "Failed to open file at " << output_file_path_;
  }

  double ts = cc->InputTimestamp().Seconds();

//  const NormalizedLandmarkList& landmarks =
//          cc->Inputs().Tag(kLandmarksTag).Get<NormalizedLandmarkList>();

  const auto& landmark_lists =
          cc->Inputs().Tag(kLandmarksTag).Get<std::vector<NormalizedLandmarkList>>();
  for (auto& landmarks : landmark_lists) {

      for (int i = 0; i < landmarks.landmark_size(); ++i) {
          const NormalizedLandmark& landmark = landmarks.landmark(i);
          snprintf(output_data, sizeof(output_data), "ts=%f x=%f y=%f z==%f\n",
                  ts, landmark.x(), landmark.y(), landmark.z());
          fputs(output_data, output_file_);
      }
      fputs("++++++++++++++++++++\n", output_file_);
  }

  fputs("====================\n", output_file_);
  counter++;

  fclose(output_file_);

  return ::mediapipe::OkStatus();
}

::mediapipe::Status LandmarkToTextCalculator::Close(
    CalculatorContext* cc) {
  output_file_ = fopen(output_file_path_.c_str(), "a+");
  if (output_file_) {
      fwrite("\n", 1, 1, output_file_);
      fclose(output_file_);
      output_file_ = NULL;
  }
  return ::mediapipe::OkStatus();
}

::mediapipe::Status LandmarkToTextCalculator::SetUpFileWriter() {
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
