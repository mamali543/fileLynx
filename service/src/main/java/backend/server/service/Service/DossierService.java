package backend.server.service.Service;

import backend.server.service.Literals;
import backend.server.service.Repository.DossierRepository;
import backend.server.service.Repository.FichierRepository;
import backend.server.service.Repository.LogRepository;
import backend.server.service.domain.*;
import backend.server.service.enums.LogType;
import backend.server.service.payloads.CurrentAuth;
import backend.server.service.payloads.FileFilterRequest;
import backend.server.service.security.POJOs.responses.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class DossierService implements IDossierService {

    private final DossierRepository dossierRepository;
    private final FichierRepository fichierRepository;
//    private final IFichierService fichierService;
    private final IMembreService membreService;
    private final ICompagnieService compagnieService;
    private final IAuthotisationService authotisationService;
    @Autowired
    private LogRepository logRepository;

    /**
     * Ajoute un dossier, le persiste et l'ajoute à la liste des dossiers du dossier parent, si le dossier parent est null, le dossier ajouté est un dossier racine, il n'est pas possible d'ajouter un dossier au dossier racine
     * @param d dossier à ajouter
     * @param parentFolderId id du dossier parent
     * @return le dossier ajouté
     */
    public Dossier addDossier(Dossier d, Long parentFolderId)
    {

        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        String compagnieNom;
        Compagnie compagnie;
        if(ressourceAccessor instanceof Membre){
            compagnieNom = ((Membre) ressourceAccessor).getGroupe().getCompagnie().getNom();
            compagnie = ((Membre) ressourceAccessor).getGroupe().getCompagnie();
        }
        else{
            compagnieNom = ((Compagnie) ressourceAccessor).getNom();
            compagnie = (Compagnie) ressourceAccessor;
        }
        Dossier dossierParent = parentFolderId!=null ? dossierRepository.findById(parentFolderId).orElseThrow(()-> new RuntimeException("Folder not found")): null;
        d.setCompagnie(compagnieService.getCompagnie(compagnieNom));
        d.setRacine(dossierParent);
        d= dossierRepository.save(d);
        if (parentFolderId!=null) {
            dossierParent.getDossiers().add(d);
            dossierRepository.save(dossierParent);
        }
        RessourceAccessor trigger = authotisationService.extractResourceAccessorFromSecurityContext();
        Log logMessage = Log.builder().message("Dossier '" + d.getNom()+ "' ajouté dans " + dossierParent.getFullPath()).type(LogType.CRÉER).date(new Date()).trigger(trigger).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return d;
    }

    /**
     * Ajouter un dossier dans le parent spécifié et ajoute les autorisations par défaut si skipAuthCreation est false
     * @param d dossier à supprimer
     * @param parentFolderId id du dossier parent
     * @param compagnie compagnie à laquelle appartient le dossier
     * @param skipAuthCreation true si on ne veut pas ajouter les autorisations par défaut
     * @return le dossier ajouté
     */
    public Dossier addDossier(Dossier d, Long parentFolderId, Compagnie compagnie, boolean skipAuthCreation)
    {
        //determine si un dossier portant le méme nom existe déja
        List<Dossier> dossiers = compagnie.getDossiers();
        for (Dossier dossier: dossiers)
        {
            if (dossier.getNom().equals(d.getNom()))
                throw new RuntimeException("Dossier: "+d.getNom()+" existe déjà");
        }
        //determine le dossier parent
        Dossier dossierParent = parentFolderId!=null ? dossierRepository.findById(parentFolderId).orElseThrow(()-> new RuntimeException("Folder not found")): null;
        //ajoute le dossier a la base de donnée
        d.setCompagnie(compagnie);
        d.setRacine(dossierParent);
        //determine le chemin du dossier

        //ajoute les autorisations par défaut
        if(!skipAuthCreation){
            authotisationService.generateDefaultAuths(authotisationService.extractResourceAssessorIdFromSecurityContext(),d);
        }

        if (parentFolderId!=null) {
            dossierParent.getDossiers().add(d);
            dossierRepository.save(dossierParent);
        }
        log.info("File created at {}", d.getFullPath());
        return d;
    }

    /**
     * Renomme un dossier
     * @param dossierId id du dossier à renommer
     * @param name nouveau nom du dossier
     * @return le dossier renommé
     */
    public Dossier renameDossier(Long dossierId,String name)
    {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Dossier dossier = dossierRepository.findById(dossierId).orElseThrow(()-> new RuntimeException("Folder not found"));
        String oldName = dossier.getNom();
        List<Dossier> dossiers = compagnie.getDossiers();
        for (Dossier d: dossiers)
        {
            if (d.getNom().equals(name))
                throw new RuntimeException("Dossier: "+name+" existe déjà");
        }
        dossier.setNom(name);
        for(Dossier d : dossier.getDossiers())
        {
            d.setRacine(dossier);
        }
        dossierRepository.saveAll(dossier.getDossiers());
        for(Fichier f : dossier.getFichiers())
        {
            f.setRacine(dossier);
        }
        fichierRepository.saveAll(dossier.getFichiers());
        log.info("File Renamed at {}", dossier.getFullPath());
        RessourceAccessor trigger = authotisationService.extractResourceAccessorFromSecurityContext();
        Log logMessage = Log.builder().message("Dossier '"+oldName+"' renommé à '"+dossier.getNom()+"'").type(LogType.MODIFIER).date(new Date()).trigger(trigger).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return dossierRepository.save(dossier);
    }
    // cette method a été créé initialement pour tester l'arborescence du system de fichiers sur console, elle n'est plus utilisée
    // fix the display here
    public void fileTree(Long DossierId, Long level) {
        Dossier dossier = dossierRepository.findById(DossierId).orElseThrow(() -> new RuntimeException("Folder not found"));
        for (int i = 0; i < level - 1; i++) {
            System.out.print("|   ");
        }
        System.out.println("└── " +"[d]"+ dossier.getNom());
        for (Dossier d : dossier.getDossiers()) {
            fileTree(d.getId(), level + 1);
        }
        for (Fichier f : dossier.getFichiers()) {
            fileTreeFiles(f.getId(), level + 1);
        }
    }
    // cette method a été créé initialement pour tester l'arborescence du system de fichiers sur console, elle n'est plus utilisée

    public void fileTreeFiles(Long fichierId, Long level) {
        Fichier fichier = fichierRepository.findById(fichierId).orElseThrow(() -> new RuntimeException("Folder not found"));
        for (int i = 0; i < level - 1; i++) {
            System.out.print("|   ");
        }
        System.out.println("└── " + fichier.getNom()+"."+fichier.getExtension());
    }

    /**
     * Supprime un dossier et ses sous-dossiers et fichiers
     * @param DossierId id du dossier à supprimer
     */
    public void delete(Long DossierId) {
        log.info("Deleting dossier with ID: {}", DossierId);
        Dossier dossier = dossierRepository.findById(DossierId).orElseThrow(() -> new RuntimeException("Folder not found"));
        RessourceAccessor trigger = authotisationService.extractResourceAccessorFromSecurityContext();
        Compagnie compagnie = authotisationService.extractCompagnieFromResourceAccessor();
        Log logMessage = Log.builder().message("Dossier " + dossier.getFullPath()+ " supprimé avec son contenu").type(LogType.SUPPRIMER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        deleteRecursively(dossier);
    }

    /**
     * supprime les sous-dossiers et fichiers d'un dossier
     * @param dossier dossier à vider
     */
    private void deleteRecursively(Dossier dossier) {
        log.info("Deleting recursively dossier with ID: {}", dossier.getId());
        List<Dossier> childDossiers = new ArrayList<>(dossier.getDossiers());

        for (Dossier child : childDossiers) {
            dossier.getDossiers().remove(child);
            child.setRacine(null);
            deleteRecursively(child);
        }

        dossierRepository.delete(dossier);
        log.info("Deleted dossier with ID: {}", dossier.getId());
    }

    /**
     * Déplace un dossier vers un autre dossier
     * @param dossierId dossier à déplacer
     * @param dossierCibleId dossier cible
     * @return le dossier déplacé
     */
    public Dossier changerEmplacement(Long dossierId,Long dossierCibleId ) {
        Dossier dossier = dossierRepository.findById(dossierId).orElseThrow(() -> new RuntimeException("Folder not found"));
        Dossier dossierCible = dossierRepository.findById(dossierCibleId).orElseThrow(() -> new RuntimeException("Folder not found"));
        String oldPath = dossier.getFullPath();
        dossier.setRacine(dossierCible);
        RessourceAccessor trigger = authotisationService.extractResourceAccessorFromSecurityContext();
        Compagnie compagnie = authotisationService.extractCompagnieFromResourceAccessor();
        Log logMessage = Log.builder().message("Dossier '"+oldPath+"' deplacé vers '"+dossier.getFullPath()+"'" ).type(LogType.MODIFIER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return dossierRepository.save(dossier);
    }

    /**
     * Retourne les sous-dossiers d'un dossier
     * @param dossierId id du dossier
     * @return les sous-dossiers
     */
    public List<Dossier> getChildrenDossiers(Long dossierId){
        Dossier dossier = dossierRepository.findById(dossierId).orElseThrow(() -> new RuntimeException("Folder not found"));
        return dossier.getDossiers();
    }

    /**
     * retourne un dossier
     * @param dossierId id du dossier
     * @return le dossier
     */
    public Dossier getDossier(Long dossierId) {
        Compagnie compagnie;
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        if(ressourceAccessor instanceof Membre){
            authotisationService.authorize(ressourceAccessor.getId(), dossierId, "lecture");
        }

        return dossierRepository.findById(dossierId).orElseThrow(() -> new RuntimeException("Folder not found"));
    }

    /**
     * retourne le dossier racine de la compagnie
     * @return le dossier racine de la compagnie
     */
    @Override
    public Dossier getRootDossier() {
        Compagnie compagnie;
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        if(ressourceAccessor instanceof Membre){
            compagnie = ((Membre) ressourceAccessor).getGroupe().getCompagnie();
        }
        else{
            compagnie = (Compagnie) ressourceAccessor;
        }
        return dossierRepository.findByCompagnieNomAndRacineIsNull(compagnie.getNom());
    }

    /**
     * Retourne le groupe à qui appartient le dossier
     * @param dossierId id du dossier
     * @return le groupe à qui appartient le dossier
     */
    @Override
    public Groupe getGroupRootGroupe(Long dossierId) {
        Dossier dossier = dossierRepository.findById(dossierId).orElseThrow(() -> new RuntimeException("Folder not found"));
        if(dossier.getRacine()==null){
            return null;
        }
        while(!dossier.isGroupRoot()){
            dossier = dossier.getRacine();
        }
        return dossier.getGroupe();
    }

    /**
     * Retourne le dossier racine d'un groupe
     * @param groupe groupe
     * @return le dossier racine d'un groupe
     */
    @Override
    public Dossier getGroupRoot(Groupe groupe) {
        return dossierRepository.findByGroupeIdAndRacineIsNotNullAndIsGroupRootTrue(groupe.getId());
    }

    @Override
    public Dossier addDossierCtrl(Dossier d, Long parentFolderId) {
        Dossier parent = dossierRepository.findById(parentFolderId).orElseThrow(() -> new RuntimeException("Folder not found"));
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        String compagnieNom;
        Compagnie compagnie;
        if(ressourceAccessor instanceof Membre){
            authotisationService.authorize(ressourceAccessor.getId(), parent.getId(), "creationDossier");
            compagnieNom = ((Membre) ressourceAccessor).getGroupe().getCompagnie().getNom();
            compagnie = ((Membre) ressourceAccessor).getGroupe().getCompagnie();
        }
        else{
            compagnieNom = ((Compagnie) ressourceAccessor).getNom();
            compagnie = (Compagnie) ressourceAccessor;
        }


        if(getRootDossier().getId() == parentFolderId){
            throw new RuntimeException("Vous ne pouvez pas ajouter un dossier à la racine");
        }
        d.setGroupe(getGroupRootGroupe(parentFolderId));
        d.setCompagnie(compagnie);
        authotisationService.generateDefaultAuths(authotisationService.extractResourceAssessorIdFromSecurityContext(), d);
        d= addDossier(d, parentFolderId);
        Log logMessage = Log.builder().message("Dossier "+d.getNom()+" ajouté à la société "+compagnieNom).type(LogType.CRÉER).date(new Date()).trigger(ressourceAccessor).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return d;

    }

    @Override
    public void deleteDossierCtrl(Long dossierId) {
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        if(ressourceAccessor instanceof Membre){
            authotisationService.authorize(ressourceAccessor.getId(), dossierId, "suppression");
        }
        if(getRootDossier().getId() == dossierId){
            throw new RuntimeException(Literals.CANT_DELETE_DEFAULT_ROOT);
        }
        if(getDossier(dossierId).isGroupRoot()){
            throw new RuntimeException(Literals.CANT_DELETE_DEFAULT_GROUP_ROOT);
        }
        delete(dossierId);
    }

    @Override
    public Dossier renameDossierCtrl(Long dossierId, String name) {
        Compagnie compagnie;
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        if(ressourceAccessor instanceof Membre){
            authotisationService.authorize(ressourceAccessor.getId(), dossierId, "modification");
            compagnie = ((Membre) ressourceAccessor).getGroupe().getCompagnie();
        }
        else{
            compagnie = (Compagnie) ressourceAccessor;
        }

            Dossier dossier = renameDossier(dossierId, name);
            Log logMessage = Log.builder().message("Dossier "+name+" ajouté à la société "+compagnie.getNom()).type(LogType.MODIFIER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
            logRepository.save(logMessage);
            return dossier;

    }

    @Override
    public Dossier getGroupRootForUser(Groupe groupe) {
        Dossier root = dossierRepository.findByGroupeIdAndRacineIsNotNullAndIsGroupRootTrue(groupe.getId());
        Dossier dossierAfterAuthFilter = new Dossier();
        List<Dossier> dossiersAfterAuthFilter = new ArrayList<>(root.getDossiers());
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        Membre membre = membreService.getMembre(ressourceAccessor.getId());
        Iterator<Dossier> iterator = dossiersAfterAuthFilter.iterator();
        while (iterator.hasNext()) {
            Dossier dossier = iterator.next();
            if (!authotisationService.hasAuth(membre.getId(), dossier.getId(), "lecture")) {
                iterator.remove();
            }
            else{
                Authorisation auth = authotisationService.getAuthorisation(membre.getId(), dossier.getId());
                dossier.setCurrentAuth(new CurrentAuth(auth));
            }
        }
        root.setDossiers(dossiersAfterAuthFilter);
        Authorisation auth = authotisationService.getAuthorisation(membre.getId(), root.getId());
        root.setCurrentAuth(new CurrentAuth(auth));
        return root;
    }

    @Override
    public Dossier getDossierByIdAsUser(Long id) {
        Membre membre = membreService.getMembre(authotisationService.extractResourceAssessorIdFromSecurityContext());
        authotisationService.hasAuth(membre.getId(), id, "lecture");
        return dossierRepository.findById(id).orElseThrow(() -> new RuntimeException("Folder not found"));
    }


}
