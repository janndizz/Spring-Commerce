package vn.edu.tdtu.javatech.springcommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vn.edu.tdtu.javatech.springcommerce.model.User;
import vn.edu.tdtu.javatech.springcommerce.service.*;
import vn.edu.tdtu.javatech.springcommerce.repository.UserRepository;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Hiển thị trang đăng nhập
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

    // Hiển thị form đăng ký
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String phonenumber,
            @RequestParam String role,
            Model model) {

        // Validate password match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Mật khẩu xác nhận không khớp");
            return "register";
        }

        // Check if username or email already exists
        if (userService.existsByUsername(username)) {
            model.addAttribute("errorMessage", "Tên đăng nhập đã được sử dụng");
            return "register";
        }

        if (userService.existsByEmail(email)) {
            model.addAttribute("errorMessage", "Email đã được sử dụng");
            return "register";
        }

        // Create new user
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password); // Note: Should be encoded in production
        newUser.setPhonenumber(phonenumber);
        newUser.setRole(User.Role.CLIENT);

        try {
            userService.save(newUser);
            model.addAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "redirect:/login?registerSuccess";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Có lỗi xảy ra khi đăng ký: " + e.getMessage());
            return "register";
        }
    }
}
