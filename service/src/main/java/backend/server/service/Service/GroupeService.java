package backend.server.service.Service;

import backend.server.service.Literals;
import backend.server.service.POJO.PageResponse;
import backend.server.service.Repository.LogRepository;
import backend.server.service.domain.*;
import backend.server.service.Repository.GroupeRepository;
import backend.server.service.enums.AuthLevel;
import backend.server.service.enums.LogType;
import backend.server.service.security.POJOs.responses.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service @Transactional @Slf4j
public class GroupeService implements IGroupeService{

    private final GroupeRepository groupeRepository;
    private final DossierService dossierService;
    private final CompagnieService compagnieService;
    private final IMembreService membreService;
    @Autowired
    private LogRepository logRepository;
    private final QuotaService quotaService;
    public GroupeService(GroupeRepository groupeRepository, @Lazy CompagnieService compagnieService,@Lazy DossierService dossierService, @Lazy IMembreService membreService, QuotaService quotaService) {
        this.groupeRepository = groupeRepository;
        this.compagnieService = compagnieService;
        this.dossierService = dossierService;
        this.membreService = membreService;
        this.quotaService = quotaService;
    }
    @Override
    public Groupe addGroupe(Groupe groupe) {
        return groupeRepository.save(groupe);
    }

    @Override
    public Groupe getGroupe(String nom, String compagnieNom) {
        return groupeRepository.findByNomAndCompagnieNom(nom, compagnieNom);
    }
    @Override
    public Groupe getGroupe(Long id) {
        return groupeRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteGroupe(Long id) {
        groupeRepository.deleteById(id);
    }

    @Override
    public Groupe updateGroupe(Groupe groupe) {
        return groupeRepository.save(groupe);
    }
   @Override
    public PageResponse<Groupe> getGroupesPage(int page, int size, String sortBy, String sortOrder, String searchQuery ){

        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Sort sort = Sort.by(direction, sortBy);
        int start = page * size;
        int end = Math.min(start + size, (int) groupeRepository.count());
        List<Groupe> groupes = groupeRepository.findAllByCompagnieNom(compagnieName,sort);
        if (searchQuery != null && !searchQuery.isEmpty()){
            groupes = groupes.stream()
                    .filter(groupe -> groupe.getNom().toLowerCase().contains(searchQuery.toLowerCase()))
                    .collect(Collectors.toList());
        }
        for (Groupe groupe : groupes) {
            groupe.setQuotaUsed(quotaService.getTotalQuotaOfGroup(groupe.getId()));
        }
        List<Groupe> pageContent = groupes.subList(start, Math.min(end, groupes.size()));
        return new PageResponse<>(pageContent, groupes.size());
    }

    @Override
    public Groupe updateGroupe(Long groupeId, String newName) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Groupe grp = groupeRepository.findByIdAndCompagnieNom(groupeId, compagnieNom);
        String nom = grp.getNom();
        Dossier dossier = dossierService.getGroupRoot(grp);

        grp = compagnieService.updateGroupe(groupeId, newName);
            dossierService.renameDossier(dossier.getId(), newName);
            // Ajouter un message de log pour l'ajout du nouveau membre
            Log logMessage = Log.builder().message("Groupe " + nom + " de la Société " + compagnieNom + " a été mis à jour ").type(LogType.MODIFIER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
            logRepository.save(logMessage);
            return grp;

    }

    @Override
    public void deleteGroup(String group) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
            Groupe groupe = getGroupe(group,compagnieNom);
            Groupe defaultGroup = getGroupe(compagnieNom,compagnieNom);
            if(groupe.getNom().equals(compagnieNom))
                throw new RuntimeException(Literals.CANT_DELETE_DEFAULT_GROUP);
            for (Membre membre : groupe.getMembres()) {
                membre.setGroupe(defaultGroup);
                membreService.updateMembre(membre);
                Log logMessage = Log.builder().message("Membre " + membre.getUsername() + " Déplacé vers le groupe " + compagnieNom).type(LogType.DÉPLACER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
                logRepository.save(logMessage);
            }
            groupe.getMembres().clear();
            Dossier dossierGroupe = dossierService.getGroupRoot(groupe);
            dossierService.delete(dossierGroupe.getId());
            compagnieService.deleteGroupe(group);
            // Ajouter un message de log pour l'ajout du nouveau membre
            Log logMessage = Log.builder().message("Groupe " + group + " retiré de la Société " + compagnieNom).type(LogType.SUPPRIMER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
            logRepository.save(logMessage);
    }

    @Override
    public Groupe createGroupe(String group) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);

            Groupe groupe =  compagnieService.createGroupe(group, 1024.*1024.*1024.*5,compagnie.getId());
            Log logMessage = Log.builder().message("Groupe " + group + " créé").type(LogType.CRÉER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
            logRepository.save(logMessage);
            Dossier dossier = new Dossier();
            dossier.setNom(group);
            dossier.setCompagnie(compagnie);
            dossier.setGroupRoot(true);
            dossier.setGroupe(groupe);
            Authorisation authorisation = Authorisation.generateFullAccess();
            authorisation.setAuthLevel(AuthLevel.GROUPE);
            authorisation.setRessourceAccessor(getGroupe(group,compagnieNom));
            authorisation.setDossier(dossier);
            dossier.getAuthorisations().add(authorisation);
            dossierService.addDossier(dossier,dossierService.getRootDossier().getId());
            return groupe;

    }

}
