import sys
import cv2
import json
import mediapipe as mp
import math

mp_pose = mp.solutions.pose

def calculate_angle(a, b, c):
    """
    Calculates angle between three points (a=first joint, b=center joint, c=end joint).
    Each point is (x, y).
    """
    ang = math.degrees(
        math.atan2(c[1]-b[1], c[0]-b[0]) - math.atan2(a[1]-b[1], a[0]-b[0])
    )
    ang = abs(ang)
    if ang > 180:
        ang = 360 - ang
    return ang

def analyze_video_with_mediapipe(video_path, athlete_id):
    analysis_results = {
        "athlete_id": athlete_id,
        "total_frames": 0,
        "evaluated_frames": 0,
        "good_form_frames": 0,
        "average_score": 0.0,
        "key_moments": [],
        "suggestions": []
    }

    with mp_pose.Pose(min_detection_confidence=0.5, min_tracking_confidence=0.5) as pose:
        cap = cv2.VideoCapture(video_path)
        if not cap.isOpened():
            print(f"Error: Could not open video file at {video_path}", file=sys.stderr)
            sys.exit(1)

        while cap.isOpened():
            ret, frame = cap.read()
            if not ret:
                break

            analysis_results["total_frames"] += 1
            image = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            results = pose.process(image)

            if results.pose_landmarks:
                landmarks = results.pose_landmarks.landmark

                # Get joint coordinates
                left_shoulder = [landmarks[mp_pose.PoseLandmark.LEFT_SHOULDER.value].x,
                                 landmarks[mp_pose.PoseLandmark.LEFT_SHOULDER.value].y]
                left_elbow = [landmarks[mp_pose.PoseLandmark.LEFT_ELBOW.value].x,
                              landmarks[mp_pose.PoseLandmark.LEFT_ELBOW.value].y]
                left_wrist = [landmarks[mp_pose.PoseLandmark.LEFT_WRIST.value].x,
                              landmarks[mp_pose.PoseLandmark.LEFT_WRIST.value].y]

                left_hip = [landmarks[mp_pose.PoseLandmark.LEFT_HIP.value].x,
                            landmarks[mp_pose.PoseLandmark.LEFT_HIP.value].y]
                left_knee = [landmarks[mp_pose.PoseLandmark.LEFT_KNEE.value].x,
                             landmarks[mp_pose.PoseLandmark.LEFT_KNEE.value].y]
                left_ankle = [landmarks[mp_pose.PoseLandmark.LEFT_ANKLE.value].x,
                              landmarks[mp_pose.PoseLandmark.LEFT_ANKLE.value].y]

                # Calculate angles
                elbow_angle = calculate_angle(left_shoulder, left_elbow, left_wrist)
                knee_angle = calculate_angle(left_hip, left_knee, left_ankle)

                analysis_results["evaluated_frames"] += 1

                # Define simple "good form" conditions (example)
                good_form = False
                if 70 <= elbow_angle <= 110:  # Example range for push-up position
                    good_form = True
                if 80 <= knee_angle <= 100:  # Example range for squat depth
                    good_form = True

                if good_form:
                    analysis_results["good_form_frames"] += 1
                    if analysis_results["evaluated_frames"] % 50 == 0:
                        analysis_results["key_moments"].append({
                            "frame": analysis_results["total_frames"],
                            "description": f"Good form detected (Elbow={elbow_angle:.1f}, Knee={knee_angle:.1f})"
                        })

        cap.release()

    # Final score = % of frames with good form
    if analysis_results["evaluated_frames"] > 0:
        ratio = analysis_results["good_form_frames"] / analysis_results["evaluated_frames"]
        analysis_results["average_score"] = ratio * 100

        if ratio < 0.4:
            analysis_results["suggestions"].append("Form consistency is low. Focus on stability and alignment.")
        elif ratio < 0.7:
            analysis_results["suggestions"].append("Decent form, but can improve consistency.")
        else:
            analysis_results["suggestions"].append("Great form! Keep practicing with intensity.")

    else:
        analysis_results["suggestions"].append("No pose landmarks detected in the video.")

    return analysis_results

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python video_analyzer.py <video_file_path> <athlete_id>", file=sys.stderr)
        sys.exit(1)

    video_file_path = sys.argv[1]
    athlete_id = sys.argv[2]

    report = analyze_video_with_mediapipe(video_file_path, athlete_id)
    print(json.dumps(report, indent=4))
