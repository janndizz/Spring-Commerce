package vn.edu.tdtu.javatech.springcommerce.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import vn.edu.tdtu.javatech.springcommerce.model.User;
import vn.edu.tdtu.javatech.springcommerce.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginPage(Model model, @RequestParam(required = false) String logout) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return "redirect:/admin";
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_REALTOR"))) {
                return "redirect:/realtor";
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENT"))) {
                return "redirect:/client";
            }
        }

        if (logout != null) {
            model.addAttribute("logoutMessage", "Đăng xuất thành công!");
        }
        return "login";
    }
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user,
                               @RequestParam("confirmPassword") String confirmPassword,
                               Model model) {

        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Mật khẩu không khớp.");
            return "register";
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
            return "register";
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("errorMessage", "Email đã được sử dụng.");
            return "register";
        }

        // Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.CLIENT); // mặc định là CLIENT

        userRepository.save(user);
        model.addAttribute("successMessage", "Đăng ký thành công! Hãy đăng nhập.");
        return "redirect:/login";
    }

    // Removed the conflicting forgot-password method since it's now in ForgotPasswordController

//    @PostMapping("/forgot-password")
//    public String handleForgotPassword(
//            @RequestParam("email") String email,
//            Model model
//    ) {
//        // Gọi service xử lý gửi email hoặc thông báo lỗi nếu không tồn tại
//        boolean success = yourResetPasswordService.sendResetLink(email);
//        if (success) {
//            model.addAttribute("successMessage", "Reset link has been sent to your email.");
//        } else {
//            model.addAttribute("errorMessage", "Email address not found.");
//        }
//        return "forgot-password";
//    }
}
