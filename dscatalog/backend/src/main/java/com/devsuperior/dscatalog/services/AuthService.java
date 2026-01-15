package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.EmailDTO;
import com.devsuperior.dscatalog.entities.PasswordRecover;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.PasswordRecoverRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${email.password-recover.token.minutes}")
    private Long tokenExpirationMinutes;

    @Value("${email.password-recover.uri}")
    private String recoverUri;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordRecoverRepository repository;

    @Autowired
    EmailService emailService;

    @Transactional
    public void createRecoverToken(EmailDTO body) {
        // Lógica para criar o token de recuperação e enviar o e-mail
        // Valida se o usuário existe
        User user = userRepository.findByEmail(body.getEmail());
        if (user == null) {
            throw new ResourceNotFoundException("Email não encontrado: " + body.getEmail());
        }

        String token = UUID.randomUUID().toString();

        PasswordRecover passwordRecover = new PasswordRecover();
        passwordRecover.setEmail(body.getEmail());
        passwordRecover.setToken(token);
        passwordRecover.setExpiration(Instant.now().plusSeconds(tokenExpirationMinutes * 60));

        repository.save(passwordRecover);

        String emailBody = "Para recuperar sua senha, clique no link abaixo:\n"
                + recoverUri + token
                + "\n\nEste link expira em " + tokenExpirationMinutes + " minutos.";

        emailService.sendEmail(body.getEmail(), "Recuperação de Senha - DSCatalog", emailBody);

    }
}
