package backend.server.service;

import backend.server.service.Service.CategorieService;
import backend.server.service.Service.DossierService;
import backend.server.service.Service.FichierService;
import backend.server.service.Service.LabelService;
import backend.server.service.security.entities.EROLE;
import backend.server.service.security.entities.Role;
import backend.server.service.security.entities.User;
import backend.server.service.security.repositories.RoleRepository;
import backend.server.service.security.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.beans.Encoder;
import java.util.*;

@SpringBootApplication
@Slf4j
public class ServiceApplication {



    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    // this command line runner creates new roles and new users for testing
    @Bean
    CommandLineRunner run(RoleRepository roleRepository, DossierService dossierService, FichierService fichierService, LabelService labelService, CategorieService categorieService, PasswordEncoder encoder, UserRepository userRepository) {
        return args -> {
            try{
                roleRepository.save(new Role(null, EROLE.ROLE_USER));
                roleRepository.save(new Role(null, EROLE.ROLE_COMPAGNIE));
                roleRepository.save(new Role(null, EROLE.ROLE_SUPERADMIN));


                Role superAdminRole = roleRepository.findByName(EROLE.ROLE_SUPERADMIN)
                        .orElseThrow(() -> new RuntimeException("Superadmin role not found"));
                User user = new User();
                user.setEmail("superAdmin@gmail.com");
                user.setUsername("superAdmin");
                user.setPassword(encoder.encode("password"));
                user.setRoles(new HashSet<>(Collections.singletonList(superAdminRole)));
                userRepository.save(user);
            }
            catch (Exception e){
                log.info("roles already created");
            }
        };
    }
}
