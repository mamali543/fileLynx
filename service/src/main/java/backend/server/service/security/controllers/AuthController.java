package backend.server.service.security.controllers;

import backend.server.service.Literals;
import backend.server.service.Repository.AuthorisationRepository;
import backend.server.service.Repository.DossierRepository;
import backend.server.service.Repository.LogRepository;
import backend.server.service.Service.CompagnieService;
import backend.server.service.Service.DossierService;
import backend.server.service.domain.*;
import backend.server.service.enums.AuthLevel;
import backend.server.service.enums.LogType;
import backend.server.service.security.JwtUtils;
import backend.server.service.security.POJOs.requests.LoginRequest;
import backend.server.service.security.POJOs.requests.SignupRequest;
import backend.server.service.security.POJOs.requests.TokenRefreshRequest;
import backend.server.service.security.POJOs.responses.JwtResponse;
import backend.server.service.security.POJOs.responses.MessageResponse;
import backend.server.service.security.POJOs.responses.TokenRefreshResponse;
import backend.server.service.security.entities.EROLE;
import backend.server.service.security.entities.RefreshToken;
import backend.server.service.security.entities.Role;
import backend.server.service.security.entities.User;
import backend.server.service.security.exceptions.TokenRefreshException;
import backend.server.service.security.repositories.RoleRepository;
import backend.server.service.security.repositories.UserRepository;
import backend.server.service.security.services.RefreshTokenService;
import backend.server.service.security.services.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// This is a REST controller that handles user authentication and registration requests
//codegpt do : generate an importable minified json file to postman for this controller, the root is http://localhost:8080
@CrossOrigin(origins = "*", maxAge = 3600) // Allow requests from any origin for one hour
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    LogRepository logRepository;
    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    CompagnieService compagnieService;
    @Autowired
    DossierService dossierService;
    @Autowired
    AuthorisationRepository authorisationRepository;
    @Autowired
    DossierRepository dossierRepository;

    /**
     * Authenticates a user and returns a JWT token if successful
     *
     * @param loginRequest the login request object containing the user's credentials
     * @return a JWT response object containing the token and user details
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String jwt = jwtUtils.generateJwtToken(userDetails);
            List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
            return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
                    userDetails.getUsername(), userDetails.getEmail(), roles));
        }
        catch (Exception e)
        {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, Literals.INVALID_USERNAME_OR_PASSWORD));
        }
    }
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        Literals.REFRESH_TOKEN_NOT_FOUND));
    }

    /**
     * Registers a new user account
     *
     * @param signUpRequest the sign-up request object containing the user's details
     * @return a message response object indicating the success or failure of the registration
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        // Check if username is already taken
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse(Literals.USERNAME_ALREADY_EXISTS));
        }

        // Check if email is already in use
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse(Literals.EMAIL_ALREADY_EXISTS));
        }
        
        // Create new user's account
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));

        // Set user roles
        Set<Role> roles = new HashSet<>();
        Role CompagnieRole = roleRepository.findByName(EROLE.ROLE_COMPAGNIE).orElseThrow(() -> new RuntimeException(Literals.ROLE_NOT_FOUND));
        roles.add(CompagnieRole);
        user.setRoles(roles);
        userRepository.save(user);

        // Create new compagnie
        Compagnie compagnie = new Compagnie();
        compagnie.setNom(signUpRequest.getUsername());
        compagnie.setQuota(1024.*1024.*1024.*50);
        compagnie.setUsedQuota(0.);
        compagnie = compagnieService.createCompagnie(compagnie);

        Log logMessage = Log.builder().message("Société " + user.getUsername() + " créée").type(LogType.CRÉER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);

        // Creating a default groupe for the compagnie
        Groupe groupe = compagnieService.createGroupe(compagnie.getNom(), 1024.*1024.*1024.*5, compagnie.getId());
        logMessage = Log.builder().message("Groupe " + groupe.getNom() + " créée").type(LogType.CRÉER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        Dossier root = new Dossier();
        root.setNom("root");
        root.setCompagnie(compagnie);
        Authorisation authorisation = Authorisation.generateFullAccess();
        authorisation.setRessourceAccessor(compagnie);
        authorisation.setDossier(root);

        root = dossierService.addDossier(root, null, compagnie,true);
        logMessage = Log.builder().message("Dossier root créée").type(LogType.CRÉER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        Dossier dossierGroupe = new Dossier();
        Authorisation authorisationGroupe = Authorisation.generateFullAccess();
        authorisationGroupe.setAuthLevel(AuthLevel.GROUPE);
        Authorisation authorisationCompagnie = Authorisation.generateReadOnly();
        authorisationCompagnie.setAuthLevel(AuthLevel.COMPAGNIE);
        authorisation.setAuthLevel(AuthLevel.COMPAGNIE);
        dossierGroupe.setNom(compagnie.getNom());
        dossierGroupe.setRacine(root);
        dossierGroupe.setGroupRoot(true);
        dossierGroupe.setGroupe(groupe);
        authorisation.setRessourceAccessor(compagnie);
        authorisation.setDossier(root);
        authorisationGroupe.setDossier(dossierGroupe);
        authorisationGroupe.setRessourceAccessor(groupe);
        authorisationCompagnie.setDossier(dossierGroupe);
        authorisationCompagnie.setRessourceAccessor(compagnie);
        dossierGroupe.getAuthorisations().add(authorisationGroupe);
        dossierGroupe.getAuthorisations().add(authorisationCompagnie);
        logMessage = Log.builder().message("Dossier "+dossierGroupe.getNom()+" créée dans /root").type(LogType.CRÉER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        root.getAuthorisations().add(authorisation);
        dossierRepository.save(root);
        dossierGroupe = dossierService.addDossier(dossierGroupe, root.getId(), compagnie,true);
        return ResponseEntity.ok(new MessageResponse(Literals.COMPAGNIE_REGISTRATION_SUCCESSFUL));
    }
}