package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.entities.BlackList;
import com.example.MyBookShopApp.data.entities.SmsCode;
import com.example.MyBookShopApp.data.api.PaymentRequest;
import com.example.MyBookShopApp.data.api.UploadProfileRequest;
import com.example.MyBookShopApp.data.repository.BlackListRepository;
import com.example.MyBookShopApp.errs.BookstoreUserExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.NoSuchAlgorithmException;

@Controller
public class AuthUserController {

    private final BookstoreUserRegister userRegister;
    private final SmsService smsService;
    private final JavaMailSender javaMailSender;
    private final ProfileService uploadService;
    private final BlackListRepository blackListRepository;

    @Autowired
    public AuthUserController(BookstoreUserRegister userRegister, SmsService smsService, JavaMailSender javaMailSender, ProfileService uploadService, BlackListRepository blackListRepository) {
        this.userRegister = userRegister;
        this.smsService = smsService;
        this.javaMailSender = javaMailSender;
        this.uploadService = uploadService;
        this.blackListRepository = blackListRepository;
    }

    @GetMapping("/signin")
    public String handleSignin() {
        return "signin";
    }

    @GetMapping("/signup")
    public String handleSignUp(Model model) {
        model.addAttribute("regForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestContactConfirmation(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");

        if(payload.getContact().contains("@")){
            return response;
        }else{
            String smsCodeString = smsService.sendSecretCodeSms(payload.getContact());
            smsService.saveNewCode(new SmsCode(smsCodeString,60)); //expires in 1 min.
            return response;
        }
    }

    @PostMapping("/requestEmailConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestEmailConfirmation(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("bookstore2205@gmail.com");
        message.setTo(payload.getContact());
        SmsCode smsCode = new SmsCode(smsService.generateCode(),300); //5 minutes
        smsService.saveNewCode(smsCode);
        message.setSubject("Bookstore email verification!");
        message.setText("Verification code is: "+smsCode.getCode());
        javaMailSender.send(message);
        response.setResult("true");
        return response;
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContact(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();

        if(smsService.verifyCode(payload.getCode())){
            response.setResult("true");
        }

        return response;
    }

    @PostMapping("/reg")
    public String handleUserRegistration(RegistrationForm registrationForm, Model model) throws BookstoreUserExistException {
        userRegister.registerNewUser(registrationForm);
        model.addAttribute("regOk", true);
        return "signin";
    }

    @PostMapping("/login")
    @ResponseBody
    public ContactConfirmationResponse handleLogin(@RequestBody ContactConfirmationPayload payload,
                                                   HttpServletResponse httpServletResponse) {
        ContactConfirmationResponse loginResponse = userRegister.jwtLogin(payload);
        Cookie cookie = new Cookie("token", loginResponse.getResult());
        httpServletResponse.addCookie(cookie);
        return loginResponse;
    }

    @PostMapping("/login-by-phone-number")
    @ResponseBody
    public ContactConfirmationResponse handleLoginByPhoneNumber(@RequestBody ContactConfirmationPayload payload,
                                                   HttpServletResponse httpServletResponse) throws BookstoreUserExistException {
        if(smsService.verifyCode(payload.getCode())) {
            ContactConfirmationResponse loginResponse = userRegister.jwtLoginByPhoneNumber(payload);
            Cookie cookie = new Cookie("token", loginResponse.getResult());
            httpServletResponse.addCookie(cookie);
            return loginResponse;
        }else {
            return null;
        }
    }

    @GetMapping("/my")
    public String handleMy(Model model) {
        model.addAttribute("curUsr", userRegister.getCurrentUser());
        return "my";
    }

    @GetMapping("/profile")
    public String handleProfile(Model model) {
        model.addAttribute("curUsr", userRegister.getCurrentUser());
        return "profile";
    }

    @PostMapping("/profile")
    public String upload(Model model, UploadProfileRequest request, HttpServletRequest servletRequest) throws InterruptedException {
        uploadService.upload(request, model, servletRequest);
        return "profile";
    }

    @PostMapping("profile/topUp")
    public RedirectView topUp (PaymentRequest request, Model model) throws NoSuchAlgorithmException, InterruptedException {
        return new RedirectView(uploadService.toUp(request, model));
    }

    @GetMapping("recovery/{id}")
    public String uploadConfirm (@PathVariable String id, Model model){
        return uploadService.uploadConfirm(id, model);
    }

    @GetMapping("/logout")
    public String handleLogout(HttpServletRequest request){
        HttpSession session = request.getSession();
        SecurityContextHolder.clearContext();

        if (session != null){
            session.invalidate();
        }

        for (Cookie cookie : request.getCookies()){
            if (cookie.getName().equals("token")) {
                BlackList item = new BlackList();
                item.setId(cookie.getValue());
                blackListRepository.save(item);
            }
            cookie.setMaxAge(0);
        }

        return "redirect:/";
    }
}
