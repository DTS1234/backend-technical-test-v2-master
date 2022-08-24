package com.tui.proof;

import com.tui.proof.persistence.model.User;
import com.tui.proof.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandLineRunnerExecutor implements CommandLineRunner {

    public static final String PASSWORD = "pass123";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsById(1L)){
            User user = new User();
            user.setEnabled(true);
            user.setPassword(this.passwordEncoder.encode(PASSWORD));
            user.setUsername("user");
            user.addAuthority("NORMAL");
            userRepository.save(user);
        }
    }
}
