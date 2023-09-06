package backend.server.service.Service;

import backend.server.service.Literals;
import backend.server.service.POJO.PageResponse;
import backend.server.service.Repository.CompagnieRepository;
import backend.server.service.Repository.LogRepository;
import backend.server.service.Repository.MembreRepository;
import backend.server.service.domain.Compagnie;
import backend.server.service.domain.Groupe;
import backend.server.service.domain.Log;
import backend.server.service.domain.Membre;
import backend.server.service.enums.LogType;
import backend.server.service.payloads.RegisterUserRequest;
import backend.server.service.security.POJOs.responses.MessageResponse;
import backend.server.service.security.entities.EROLE;
import backend.server.service.security.entities.User;
import backend.server.service.security.repositories.RoleRepository;
import backend.server.service.security.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional @Slf4j
public class MembreService implements IMembreService {
    private final MembreRepository membreRepository;
    private final ICompagnieService compagnieService;
    private final IGroupeService groupeService;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CompagnieRepository compagnieRepository;
    @Autowired
    private LogRepository logRepository;
    public MembreService(MembreRepository membreRepository, @Lazy ICompagnieService compagnieService, IGroupeService groupeService, UserRepository userRepository, PasswordEncoder encoder) {
        this.membreRepository = membreRepository;
        this.compagnieService = compagnieService;
        this.groupeService = groupeService;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }
    @Override
    public Membre getMembre(Long id){
        return membreRepository.findById(id).orElseThrow(()-> new RuntimeException(Literals.MEMBER_NOT_FOUND) );
    }
    @Override
    public Membre addMembre(Membre membre){
        return membreRepository.save(membre);
    }
    @Override
    public Membre updateMembre(Membre membre){
        return membreRepository.save(membre);
    }
    @Override
    public void deleteMembre(Long id){
        membreRepository.deleteById(id);
    }
    @Override
    public Membre getMembre(String username){
        return membreRepository.findByUsername(username);
    }
    @Override
    public PageResponse<Membre> getMembresPage(int page, int size, String sortBy, String sortOrder, String searchQuery, String groupFilter ){

        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Sort sort = Sort.by(direction, sortBy);
        int start = page * size;
        int end = Math.min(start + size, (int) membreRepository.count());
        List<Membre> membres = membreRepository.findAllByCompagnieNom(compagnieName,sort);

        if (searchQuery != null && !searchQuery.isEmpty()){
            membres = membres.stream()
                    .filter(membre -> membre.getUsername().toLowerCase().contains(searchQuery.toLowerCase())
                            || membre.getNom().toLowerCase().contains(searchQuery.toLowerCase())
                            || membre.getPrenom().toLowerCase().contains(searchQuery.toLowerCase())
                            || membre.getEmail().toLowerCase().contains(searchQuery.toLowerCase())
                    )
                    .collect(Collectors.toList());
        }
        if (groupFilter != null && !groupFilter.isEmpty()) {
            membres = membres.stream()
                    .filter(professor -> professor.getGroupe().getNom().equalsIgnoreCase(groupFilter))
                    .collect(Collectors.toList());
        }
        List<Membre> pageContent = membres.subList(start, Math.min(end, membres.size()));
        System.out.println(pageContent);
        return new PageResponse<>(pageContent, membres.size());
    }

    @Override
    public List<Membre> getMembresByGroupeId(Long groupeId) {
        return membreRepository.findAllByGroupeId(groupeId);
    }

    @Override
    public void deleteMembre(Long membreId, String username) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        compagnieService.deleteMembre(membreId, username);
        // Ajouter un message de log pour l'ajout du nouveau membre
        Log logMessage = Log.builder().message("Membre " + username + " retiré de la Société " + compagnieNom).type(LogType.SUPPRIMER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
    }

    @Override
    public Membre changeMemberGroup(String username, String group) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Membre membre = getMembre(username);
        Groupe oldGroupe = membre.getGroupe();
        membre.setGroupe(groupeService.getGroupe(group,compagnieNom));
        membre = updateMembre(membre);
        Log logMessage = Log.builder().message("Membre " + membre.getUsername() + " à changer de groupe de " + oldGroupe + " vers " + group).type(LogType.MODIFIER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return membre;
    }

    @Override
    public Membre registerMembre(RegisterUserRequest membre) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieRepository.findByNom(compagnieNom);
        // Vérifier si la compagnie existe
        if(compagnie == null){
            throw new RuntimeException(Literals.COMPAGNIE_NOT_FOUND);

        }
//         Vérifier si le nom d'utilisateur est disponible
        if (Boolean.TRUE.equals(userRepository.existsByUsername(membre.getUsername()))) {
            throw new RuntimeException(Literals.USERNAME_ALREADY_EXISTS);
        }
        // Vérifier si l'adresse email est disponible
        if (Boolean.TRUE.equals(userRepository.existsByEmail(membre.getEmail()))) {
            throw new RuntimeException(Literals.EMAIL_ALREADY_EXISTS);
        }

        // Créer un nouvel utilisateur avec le rôle ROLE_USER
        User user = new User(membre.getUsername(), membre.getEmail(), encoder.encode(membre.getPassword()));
        user.setRoles(Collections.singleton(roleRepository.findByName(EROLE.ROLE_USER).orElseThrow(() -> new RuntimeException(Literals.ROLE_NOT_FOUND))));
        userRepository.save(user);

        // Ajouter le nouveau membre à la base de données
        Membre newMembre = Membre.builder()
                .nom(membre.getNom())
                .prenom(membre.getPrenom())
                .email(membre.getEmail())
                .username(membre.getUsername())
                .groupe(groupeService.getGroupe(membre.getGroupe(),compagnieNom))
                .compagnie(compagnie)
                .build();
        newMembre = addMembre(newMembre);
        // Ajouter un message de log pour l'ajout du nouveau membre
        Log logMessage = Log.builder().message("Membre " + membre.getUsername() + " créé et ajouté au groupe " + membre.getGroupe()).type(LogType.CRÉER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);

        // Retourner une réponse HTTP avec un message de réussite
        return newMembre;
    }
}
