import sys
import cv2
import json
import mediapipe as mp
import math
import random

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


def detect_sport_with_pose(elbow_angle, knee_angle, left_shoulder, left_wrist, left_hip):
    """
    Very basic heuristic sport suggestion using pose data only.
    (In real scenarios, you'd combine this with object detection.)
    """
    detected_sport = "Unknown"

    # Cricket batting stance (elbow bent + knees relatively straight)
    if 60 <= elbow_angle <= 120 and knee_angle > 140:
        detected_sport = "Cricket"

    # Hockey stance (knees bent, upper body leaning forward)
    elif 100 <= knee_angle <= 140 and elbow_angle < 100:
        detected_sport = "Hockey"

    # Badminton (overhead reach, straight arm)
    elif elbow_angle > 150 and abs(left_wrist[1] - left_shoulder[1]) < 0.2:
        detected_sport = "Badminton"

    return detected_sport


def analyze_video_with_mediapipe(video_path, athlete_id):
    # Internal tracking
    total_frames = 0
    evaluated_frames = 0
    good_form_frames = 0
    detected_sport = "Unknown"

    with mp_pose.Pose(min_detection_confidence=0.5, min_tracking_confidence=0.5) as pose:
        cap = cv2.VideoCapture(video_path)
        if not cap.isOpened():
            print(f"Error: Could not open video file at {video_path}", file=sys.stderr)
            sys.exit(1)

        while cap.isOpened():
            ret, frame = cap.read()
            if not ret:
                break

            total_frames += 1
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

                evaluated_frames += 1

                # Detect sport
                sport_guess = detect_sport_with_pose(
                    elbow_angle, knee_angle, left_shoulder, left_wrist, left_hip
                )
                if detected_sport == "Unknown" and sport_guess != "Unknown":
                    detected_sport = sport_guess

                # Check good form
                if 70 <= elbow_angle <= 110 or 80 <= knee_angle <= 100:
                    good_form_frames += 1

        cap.release()

    # Compute performance metrics
    if evaluated_frames > 0:
        form_ratio = good_form_frames / evaluated_frames
        percentile = round(form_ratio * 100, 2)  # [0-100]
    else:
        form_ratio = 0
        percentile = 0.0

    # Injury risk estimation
    if form_ratio < 0.3:
        injury_risk = "high"
    elif form_ratio < 0.6:
        injury_risk = "medium"
    else:
        injury_risk = "low"

    # Recommendations
    if form_ratio < 0.4:
        recommendations = "Focus on basic posture and controlled movements."
        comments = "Form consistency is low. Training adjustments recommended."
    elif form_ratio < 0.7:
        recommendations = "Good effort, but aim for more stability."
        comments = "Moderate consistency. Keep improving alignment."
    else:
        recommendations = "Excellent form, maintain intensity."
        comments = "Great performance with consistent form."

    # Badges: scale performance to [0-10]
    badges = min(10, max(0, round(form_ratio * 10)))

    # Final JSON response mapped to Java entity fields
    result = {
        "percentile": percentile,
        "injuryRisk": injury_risk,
        "recommendations": recommendations,
        "comments": comments,
        "detected_sports": detected_sport,
        "badges": badges
    }

    return result


if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python video_analyzer.py <video_file_path> <athlete_id>", file=sys.stderr)
        sys.exit(1)

    video_file_path = sys.argv[1]
    athlete_id = sys.argv[2]

    report = analyze_video_with_mediapipe(video_file_path, athlete_id)
    print(json.dumps(report, indent=4))
