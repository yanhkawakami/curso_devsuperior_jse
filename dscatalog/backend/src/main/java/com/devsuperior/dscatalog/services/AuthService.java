package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.EmailDTO;
import com.devsuperior.dscatalog.dto.NewPasswordDTO;
import com.devsuperior.dscatalog.entities.PasswordRecover;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.PasswordRecoverRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
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

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

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

    public void newPassword(@Valid NewPasswordDTO body) {
        List<PasswordRecover> validToken = repository.searchValidTokens(body.getToken(), Instant.now());
        if (validToken.isEmpty()) {
            throw new ResourceNotFoundException("Token inválido ou expirado");
        }

        User user = userRepository.findByEmail(validToken.get(0).getEmail());
        if (user == null) {
            throw new ResourceNotFoundException("Usuário não encontrado para o email: " + validToken.get(0).getEmail());
        }
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        userRepository.save(user);
    }

    protected User authenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");
            return userRepository.findByEmail(username);
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("Invalid user");
        }
    }

}
