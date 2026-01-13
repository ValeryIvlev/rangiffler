package io.student.rangiffler.controller;

import io.student.rangiffler.model.RegistrationForm;
import io.student.rangiffler.service.UserService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegisterController {
  private static final Logger LOG = LoggerFactory.getLogger(RegisterController.class);

  private static final String REGISTRATION_VIEW_NAME = "register";
  private static final String MODEL_USERNAME_ATTR = "username";
  private static final String MODEL_REG_FORM_ATTR = "registrationForm";
  private static final String MODEL_FRONT_URI_ATTR = "frontUri";
  private static final String BINDING_RESULT_KEY = "org.springframework.validation.BindingResult.registrationForm";

  private final UserService userService;
  private final String frontUri;

  @Autowired
  public RegisterController(UserService userService,
                            @Value("${rangiffler-front.base-uri}") String frontUri) {
    this.userService = userService;
    this.frontUri = frontUri;
  }

  @GetMapping("/register")
  public String getRegisterPage(@Nonnull Model model) {
    LOG.debug("Loading registration page");
    if (!model.containsAttribute(MODEL_REG_FORM_ATTR)) {
      model.addAttribute(MODEL_REG_FORM_ATTR, new RegistrationForm(null, null, null));
    }
    model.addAttribute(MODEL_FRONT_URI_ATTR, frontUri);
    return REGISTRATION_VIEW_NAME;
  }

  @PostMapping(value = "/register")
  public String registerUser(@Valid @ModelAttribute(MODEL_REG_FORM_ATTR) RegistrationForm registrationForm,
                             BindingResult bindingResult,  // Используем BindingResult вместо Errors
                             Model model,
                             HttpServletResponse response) {

    LOG.debug("Starting registration for username: {}", registrationForm.username());
    LOG.debug("BindingResult has errors: {}", bindingResult.hasErrors());

    if (!bindingResult.hasErrors()) {
      LOG.debug("No validation errors, attempting to register user");
      final String registeredUserName;
      try {
        registeredUserName = userService.registerUser(
                registrationForm.username(),
                registrationForm.password()
        );
        LOG.info("User registered successfully: {}", registeredUserName);
        response.setStatus(HttpServletResponse.SC_CREATED);
        model.addAttribute(MODEL_USERNAME_ATTR, registeredUserName);

        // После успешной регистрации можно редиректнуть
        // return "redirect:/login?registered";

      } catch (DataIntegrityViolationException e) {
        LOG.warn("Username '{}' already exists", registrationForm.username(), e);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        // Ключевое исправление: добавляем ошибку в bindingResult
        bindingResult.addError(new FieldError(
                MODEL_REG_FORM_ATTR,    // object name - должно совпадать с именем модели
                "username",             // field name
                registrationForm.username(), // rejected value
                true,                   // binding failure
                new String[]{"duplicate.username"}, // error codes
                new Object[]{registrationForm.username()}, // arguments
                "Username '" + registrationForm.username() + "' already exists" // default message
        ));

        LOG.debug("Added field error to bindingResult. Has errors now: {}", bindingResult.hasErrors());

      } catch (Exception e) {
        LOG.error("Unexpected error during registration", e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        bindingResult.reject("registration.error", "Registration failed due to server error");
      }
    } else {
      LOG.warn("Form validation failed. Error count: {}", bindingResult.getErrorCount());
      bindingResult.getAllErrors().forEach(error ->
              LOG.debug("Validation error: {}", error.getDefaultMessage()));
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    // Критически важно: добавляем форму обратно в модель
    model.addAttribute(MODEL_REG_FORM_ATTR, registrationForm);
    model.addAttribute(MODEL_FRONT_URI_ATTR, frontUri);

    // Добавляем bindingResult в модель (Spring делает это автоматически, но на всякий случай)
    model.addAttribute(BINDING_RESULT_KEY, bindingResult);

    LOG.debug("Returning to registration view with {} errors", bindingResult.getErrorCount());
    return REGISTRATION_VIEW_NAME;
  }

}
