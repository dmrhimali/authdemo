package com.dmrhimali.auth.ssoauth.controller


import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginController {
    @GetMapping("/")
    fun home(): String {
        return "index" // Display the user's info on the home page
    }

    @GetMapping("/dashboard")
    fun dashboard(@AuthenticationPrincipal principal: OAuth2User, model: Model): String? {
        // Extract user details from OAuth2User
        val username = principal.getAttribute<String>("name")
        val email = principal.getAttribute<String>("email")

        // Add user details to the model
        model.addAttribute("username", username)
        model.addAttribute("email", email)

        // Return the dashboard view
        return "dashboard"  // This will render the dashboard.html page from /src/main/resources/templates/
    }

    @GetMapping("/login")
    fun login(): String {
        return "login"  // This will render the login.html page from /src/main/resources/templates/
    }

    // Logout Success Page
    @GetMapping("/logout-success")
    fun logoutSuccess(): String {
        return "logout-success"
    }

    // Logout Success Page
    @GetMapping("/logout")
    fun logout(): String {
        return "logout"
    }

    @GetMapping("/error")
    fun error(): String {
        return "error"  // Ensure you have a template for "error.html"
    }
}
