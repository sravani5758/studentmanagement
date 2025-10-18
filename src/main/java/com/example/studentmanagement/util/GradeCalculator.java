package com.example.studentmanagement.util;

import com.example.studentmanagement.entity.Enrollment;
import com.example.studentmanagement.entity.Submission;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GradeCalculator {

    public String calculateCourseGrade(List<Submission> submissions, Double assignmentWeight) {
        if (submissions == null || submissions.isEmpty()) {
            return "N/A";
        }

        double totalMarks = submissions.stream()
                .filter(s -> "GRADED".equals(s.getStatus()) && s.getMarksObtained() != null)
                .mapToDouble(s -> (s.getMarksObtained() / s.getAssignment().getMaxMarks()) * assignmentWeight)
                .sum();

        return convertMarksToGrade(totalMarks);
    }

    public String convertMarksToGrade(double marks) {
        if (marks >= 90) return "A";
        else if (marks >= 80) return "B";
        else if (marks >= 70) return "C";
        else if (marks >= 60) return "D";
        else return "F";
    }

    public double calculateGPA(List<Enrollment> enrollments) {
        if (enrollments == null || enrollments.isEmpty()) {
            return 0.0;
        }

        double totalGradePoints = 0;
        int count = 0;

        for (Enrollment enrollment : enrollments) {
            if (enrollment.getGrade() != null) {
                totalGradePoints += convertGradeToPoints(enrollment.getGrade());
                count++;
            }
        }

        return count > 0 ? Math.round((totalGradePoints / count) * 100.0) / 100.0 : 0.0;
    }

    private double convertGradeToPoints(String grade) {
        switch (grade.toUpperCase()) {
            case "A": return 4.0;
            case "B": return 3.0;
            case "C": return 2.0;
            case "D": return 1.0;
            case "F": return 0.0;
            default: return 0.0;
        }
    }
}