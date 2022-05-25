package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.entities.ChangeProfileEntity;
import com.example.MyBookShopApp.data.repository.ChangeProfileEntityRepository;
import com.example.MyBookShopApp.data.repository.TransactionRepository;
import com.example.MyBookShopApp.data.api.PaymentRequest;
import com.example.MyBookShopApp.data.api.UploadProfileRequest;
import com.example.MyBookShopApp.data.entities.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService {

    private final BookstoreUserRepository repository;
    private final BookstoreUserRegister register;
    private final PasswordEncoder encoder;
    private final TransactionRepository transactionRepository;
    private final ChangeProfileEntityRepository changeProfileEntityRepository;
    private final JavaMailSender javaMailSender;
    private final BookstoreUserRepository bookstoreUserRepository;
    @Value("${robokassa.merchant.login}")
    private String merchantLogin;
    @Value("${robokassa.pass.first.test}")
    private String firstTestPass;

    @Autowired
    public ProfileService(BookstoreUserRepository repository, BookstoreUserRegister register, PasswordEncoder encoder, TransactionRepository transactionRepository, ChangeProfileEntityRepository changeProfileEntityRepository, JavaMailSender javaMailSender, BookstoreUserRepository bookstoreUserRepository) {
        this.repository = repository;
        this.register = register;
        this.encoder = encoder;
        this.transactionRepository = transactionRepository;
        this.changeProfileEntityRepository = changeProfileEntityRepository;
        this.javaMailSender = javaMailSender;
        this.bookstoreUserRepository = bookstoreUserRepository;
    }

    public void upload (UploadProfileRequest request, Model model, HttpServletRequest servletRequest) throws InterruptedException {
        boolean success = true;
        BookstoreUser user = register.getCurrentUser();
        String newPassword = null;
        if (request.getPassword() != null && request.getPassword().equals(request.getPasswordReply())){
            newPassword = encoder.encode(request.getPassword());
        } else if (request.getPassword() != null) {
            model.addAttribute("uploadFailed", "пароли не совпадают");
            success = false;
        }
        if (newPassword != null
                || !user.getEmail().equals(request.getMail())
                || !user.getName().equals(request.getName())
                || !user.getPhone().equals(request.getPhone())) {
            String invId = UUID.randomUUID().toString().replace("-", "");
            ChangeProfileEntity changeProfileEntity = new ChangeProfileEntity(
                    invId,
                    request.getName(),
                    request.getMail(),
                    request.getPhone(),
                    newPassword,
                    60);
            changeProfileEntityRepository.save(changeProfileEntity);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("bookstore2205@gmail.com");
            message.setTo(user.getEmail());
            message.setSubject("Bookstore profile upload verification!");
            String address = servletRequest.getHeader("Origin");
            message.setText("Your link is: "+ address + "/recovery/" + invId);
            javaMailSender.send(message);
            model.addAttribute("success", success);
        }
        model.addAttribute("curUsr", user);
        repository.save(user);
    }

    public String toUp (PaymentRequest request, Model model) throws NoSuchAlgorithmException, InterruptedException {
        double sum = Double.parseDouble(request.getSum());
        BookstoreUser user = register.getCurrentUser();
        String invId = UUID.randomUUID().toString().replace("-", "");
        Transaction transaction = new Transaction(invId, sum, user, "Пополнение");
        transactionRepository.save(transaction);
        model.addAttribute("curUsr", user);

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update((merchantLogin + ":" + sum + ":" + invId + ":" + firstTestPass).getBytes());
        return "https://auth.robokassa.ru/Merchant/Index.aspx"+
                "?MerchantLogin="+merchantLogin+
                "&IndId="+invId+
                "&Culture=ru"+
                "&Encoding=utf-8"+
                "&OutSum="+sum+
                "&SignatureValue="+ DatatypeConverter.printHexBinary(md.digest()).toUpperCase()+
                "&IsTest=1";
    }

    public String uploadConfirm (String id, Model model){
        boolean result = true;
        BookstoreUser user;
        try {
            user = register.getCurrentUser();
        } catch (Exception ex){
            return "signin";
        }
        Optional <ChangeProfileEntity> changeProfileEntityOptional = changeProfileEntityRepository.findById(id);
        if (!changeProfileEntityOptional.isPresent()){
            result = false;
        } else {
            ChangeProfileEntity changeProfileEntity = changeProfileEntityOptional.get();
            if (changeProfileEntity.isExpired()){
                result = false;
            } else {
                if (changeProfileEntity.getPassword() != null){
                    user.setPassword(changeProfileEntity.getPassword());
                }
                user.setEmail(changeProfileEntity.getMail());
                user.setName(changeProfileEntity.getName());
                user.setPhone(changeProfileEntity.getPhone());
                bookstoreUserRepository.save(user);
                changeProfileEntityRepository.deleteById(id);
            }
        }
        if (!result){
            model.addAttribute("expired", true);
        }
        model.addAttribute("result", result);
        model.addAttribute("curUsr", user);
        return "profile";
    }
}
