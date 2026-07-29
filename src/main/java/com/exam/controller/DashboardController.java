package com.exam.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class DashboardController {

    // Hardcoded list of exams for simplicity
    private final List<ExamDto> availableExams = Arrays.asList(
            new ExamDto("DBMS", "Database Management Systems", "2026-06-15", "2026-05-30"),
            new ExamDto("Algorithm", "Design and Analysis of Algorithms", "2026-06-18", "2026-05-30"),
            new ExamDto("Data Science", "Introduction to Data Science", "2026-06-20", "2026-05-30"),
            new ExamDto("Operating System", "Operating Systems Principles", "2026-06-22", "2026-05-30"),
            new ExamDto("Web Technology", "Web Technology and Applications", "2026-06-25", "2026-05-30"),
            new ExamDto("AI & ML", "Artificial Intelligence & Machine Learning", "2026-06-28", "2026-05-30"),
            new ExamDto("DevOps", "DevOps Practices and Tools", "2026-07-02", "2026-05-30"),
            new ExamDto("Computer Networks", "Computer Networks and Security", "2026-07-05", "2026-05-30")
    );

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("exams", availableExams);
        return "dashboard";
    }

    // Inner class for Exam Data Transfer
    public static class ExamDto {
        private String id;
        private String name;
        private String examDate;
        private String dueDate;

        public ExamDto(String id, String name, String examDate, String dueDate) {
            this.id = id;
            this.name = name;
            this.examDate = examDate;
            this.dueDate = dueDate;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getExamDate() { return examDate; }
        public String getDueDate() { return dueDate; }
    }
}