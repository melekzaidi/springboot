package com.formation.config;

import com.formation.entity.AppUser;
import com.formation.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create Default Admin if not exists
        if (appUserRepository.findByEmail("admin@gmail.com").isEmpty()) {
            AppUser admin = new AppUser();
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole("ADMIN");
            appUserRepository.save(admin);
            System.out.println("Default Admin User created: admin@gmail.com / admin");
        }
    }
}
