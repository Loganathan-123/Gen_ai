package com.exam.controller;

import com.exam.model.Registration;
import com.exam.model.User;
import com.exam.repository.RegistrationRepository;
import com.exam.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Controller
public class RegistrationController {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/register/{examId}")
    public String showRegistrationForm(@PathVariable String examId, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        // Simple validation to prevent multiple registrations for same exam
        if (registrationRepository.existsByUserIdAndExamName(userId, examId)) {
            return "redirect:/dashboard?error=already_registered";
        }

        Registration registration = new Registration();
        registration.setExamName(examId);
        // Default exam date logic or we can fetch it from DB if we had it, but passing from controller or form is easier
        // Let's set a dummy date for now, in a real app this would be queried from an Exam entity
        registration.setExamDate(LocalDate.now().plusMonths(2));

        model.addAttribute("registration", registration);
        model.addAttribute("examId", examId);
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid @ModelAttribute("registration") Registration registration,
                                      BindingResult bindingResult,
                                      HttpSession session,
                                      Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("examId", registration.getExamName());
            return "register";
        }

        // Validate Age (must be 18+)
        if (registration.getDateOfBirth() != null) {
            int age = Period.between(registration.getDateOfBirth(), LocalDate.now()).getYears();
            if (age < 18) {
                model.addAttribute("ageError", "You must be at least 18 years old to register.");
                model.addAttribute("examId", registration.getExamName());
                return "register";
            }
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        registration.setUser(user);
        Registration savedRegistration = registrationRepository.save(registration);

        return "redirect:/hall-ticket/" + savedRegistration.getId();
    }

    @GetMapping("/hall-ticket/{id}")
    public String showHallTicket(@PathVariable Long id, HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<Registration> optionalRegistration = registrationRepository.findById(id);
        if (optionalRegistration.isEmpty()) {
            return "redirect:/dashboard?error=not_found";
        }

        Registration registration = optionalRegistration.get();
        // Ensure user can only view their own hall ticket
        if (!registration.getUser().getId().equals(userId)) {
            return "redirect:/dashboard?error=unauthorized";
        }

        model.addAttribute("registration", registration);
        model.addAttribute("location", "Anna University Regional Campus, Tirunelveli");
        return "hall_ticket";
    }
}