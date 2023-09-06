package backend.server.service.Service;

import backend.server.service.Literals;
import backend.server.service.POJO.Quota;
import backend.server.service.Repository.AuthorisationRepository;
import backend.server.service.Repository.GroupeRepository;
import backend.server.service.Repository.RessourceAccessorRepository;
import backend.server.service.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service @Transactional @Slf4j
public class QuotaService implements IQuotaService{

    private final AuthorisationRepository authorisationRepository;
    private final AuthotisationService  authotisationService;
    private final RessourceAccessorRepository ressourceAccessorRepository;
    private final GroupeRepository groupeRepository;
    private final ICompagnieService compagnieService;
    private final IDossierService dossierService;

    public QuotaService(AuthorisationRepository authorisationRepository, AuthotisationService authotisationService, RessourceAccessorRepository ressourceAccessorRepository, GroupeRepository groupeRepository,
                        @Lazy ICompagnieService compagnieService,
                        @Lazy IDossierService dossierService) {
        this.authorisationRepository = authorisationRepository;
        this.authotisationService = authotisationService;
        this.ressourceAccessorRepository = ressourceAccessorRepository;
        this.groupeRepository = groupeRepository;
        this.compagnieService = compagnieService;
        this.dossierService = dossierService;
    }
    public Double getTotalQuotaOfGroup(Long ressourceAccessorId){
        RessourceAccessor ressourceAccessor = ressourceAccessorRepository.findById(ressourceAccessorId).orElseThrow(()-> new RuntimeException(Literals.RESOURCE_ACCESSOR_NOT_FOUND));
        if(ressourceAccessor instanceof Membre){
            ressourceAccessorId = ((Membre) ressourceAccessor).getGroupe().getId();
        }
        else if(ressourceAccessor instanceof Groupe){
            ressourceAccessorId = ressourceAccessor.getId();
        }
        else{
            throw new RuntimeException(Literals.RESSOURCE_ACCESSOR_IS_NOT_A_GROUP);
        }
        List<Authorisation> authorisations = authorisationRepository.findAllByRessourceAccessorId(ressourceAccessorId);
        Double totalQuota = 0.0;
        for (Authorisation authorisation : authorisations) {
            for(Fichier dossier : authorisation.getDossier().getFichiers()){
                totalQuota += dossier.getTaille();
            }
        }
        return totalQuota;
    }

    public Double getTotalQuotaOfCompagnie(){
        Compagnie compagnie;
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        if(ressourceAccessor instanceof Membre){
            compagnie = ((Membre) ressourceAccessor).getGroupe().getCompagnie();
        }
        else{
            compagnie = (Compagnie) ressourceAccessor;
        }
        Double totalQuota = 0.0;
        for (Groupe groupe : compagnie.getGroupes()) {
            totalQuota += getTotalQuotaOfGroup(groupe.getId());
        }
        return totalQuota;
    }

    public void CheckQuotaOfGroupe(Fichier f){
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
            ressourceAccessor = f.getRacine().getGroupe();
            Double totalQuota = getTotalQuotaOfGroup(ressourceAccessor.getId());
            if(totalQuota + f.getTaille() > ((Groupe) ressourceAccessor).getQuota()){
                throw new RuntimeException(Literals.QUOTA_ALLOCATION_EXCEEDED);

        }
    }

    public void checkQuotaOfCompagnie(Fichier f){
        Compagnie compagnie;
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        if(ressourceAccessor instanceof Membre){
            compagnie = ((Membre) ressourceAccessor).getGroupe().getCompagnie();
        }
        else{
            compagnie = (Compagnie) ressourceAccessor;
        }

        Double totalQuota = getTotalQuotaOfCompagnie();
        log.info("total used quota of compagnie : " + totalQuota);
        log.info("quota of compagnie : " + compagnie.getQuota());
        log.info("used quota after adding the file : " + (totalQuota + f.getTaille()));

        if(totalQuota + f.getTaille() > compagnie.getQuota()){
            throw new RuntimeException(Literals.QUOTA_ALLOCATION_EXCEEDED);
        }
    }

    public void QuotaAuthFilter(Fichier f){
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        if(ressourceAccessor instanceof Membre){
            checkQuotaOfCompagnie(f);
            CheckQuotaOfGroupe(f);
        }
        else{
            checkQuotaOfCompagnie(f);
            CheckQuotaOfGroupe(f);
        }
    }

    public void QuotaAuthFilter(Long size, Long FolderId){
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        Fichier f = new Fichier();
        Dossier dossier = dossierService.getDossier(FolderId);
        f.setRacine(dossier);
        f.setTaille((size.doubleValue()));
        log.info("size of the entering file : " + size);
        if(ressourceAccessor instanceof Membre){
            checkQuotaOfCompagnie(f);
            CheckQuotaOfGroupe(f);
        }
        else{
            checkQuotaOfCompagnie(f);
            CheckQuotaOfGroupe(f);
        }
    }

    public double getTotalAllocatedQuota(){
        String compangnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Groupe> groupes = groupeRepository.findAllByCompagnieNom(compangnieName);
        double totalAllocatedQuota = 0;
        for (Groupe groupe : groupes) {
            totalAllocatedQuota += groupe.getQuota();
        }
        return totalAllocatedQuota;
    }

    @Override
    public Quota getQuotaStatus() {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Quota quota1 = new Quota();
        quota1.setQuota(compagnie.getQuota());
        quota1.setUsedQuota(getTotalQuotaOfCompagnie());
        quota1.setQuotaLeft(compagnie.getQuota() - getTotalQuotaOfCompagnie());
        return quota1;
    }

    @Override
    public Double getCompagnieUnallocatedQuota() {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        return compagnie.getQuota() - getTotalAllocatedQuota();
    }


    public void updateGroupeQuota(Long groupe, Double quota) {
        Groupe groupe1 = groupeRepository.findById(groupe).orElseThrow(()-> new RuntimeException(Literals.GROUPE_NOT_FOUND));
        if(quota < groupe1.getQuotaUsed())
            throw new RuntimeException(Literals.QUOTA_ALLOCATION_UNDER_ALLOWED);
        if(quota > getCompagnieUnallocatedQuota()+groupe1.getQuota())
            throw new RuntimeException(Literals.QUOTA_ALLOCATION_OVER_ALLOWED);
        groupe1.setQuota(quota);
        groupeRepository.save(groupe1);
    }
}
