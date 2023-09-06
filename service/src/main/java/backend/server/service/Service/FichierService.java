package backend.server.service.Service;

import backend.server.service.Literals;
import backend.server.service.Repository.*;
import backend.server.service.domain.*;
import backend.server.service.enums.ETAT;
import backend.server.service.enums.LogType;
import backend.server.service.payloads.CurrentAuth;
import backend.server.service.payloads.FileFilterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor @Service @Slf4j
public class FichierService implements IFichierService{

    private static final String path = "C:/Users/stagiaire7/Documents/GitHub/filelynx/files/upload";
//    private static final String path = "/Users/macbookpro/Desktop/files/upload/";
    private FichierRepository fichierRepository;
    private DossierRepository dossierRepository;
    private CategorieService categorieService;
    private CategorieRepository categorieRepository;
    private LabelRepository labelRepository;
    private CompagnieService compagnieService;
    private LogRepository logRepository;
    private IAuthotisationService authotisationService;
    private IMembreService membreService;
    @Autowired
    public FichierService(@Lazy DossierService dossierService,
                          @Lazy IMembreService membreService,
                          FichierRepository fichierRepository,
                          DossierRepository dossierRepository,
                          CategorieRepository categorieRepository,
                          CategorieService categorieService,
                          LabelRepository labelRepository,
                          CompagnieService compagnieService,
                          LogRepository logRepository,
                          IAuthotisationService authotisationService) {
        this.fichierRepository = fichierRepository;
        this.dossierRepository = dossierRepository;
        this.categorieService = categorieService;
        this.categorieRepository = categorieRepository;
        this.labelRepository = labelRepository;
        this.compagnieService = compagnieService;
        this.logRepository = logRepository;
        this.authotisationService = authotisationService;
        this.membreService = membreService;

    }
    /**
     * ajoute et persiste un nouveau fichier
     * @param f fichier à ajouter
     * @param ParentFolderId id du dossier parent
     * @return le fichier ajouté
     */
    @Override
    public Fichier addFichier(Fichier f, Long ParentFolderId)
    {
        Dossier dossierParent = ParentFolderId!=null ? dossierRepository.findById(ParentFolderId).orElseThrow(()-> new RuntimeException(Literals.FILE_NOT_FOUND)): null;
        f.setRacine(dossierParent);
        f= fichierRepository.save(f);
        if (ParentFolderId!=null) {
            dossierParent.getFichiers().add(f);

            dossierRepository.save(dossierParent);
        }
        return f;
    }

    /**
     * Supprime un fichier
     * @param fichierId id du fichier à supprimer
     */
    @Override
    public void deleteFichier(Long fichierId)
    {
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException(Literals.FILE_NOT_FOUND));
        System.out.println(file.getNom());
        // Create a File object
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        if(ressourceAccessor instanceof Membre){
            authotisationService.authorize(ressourceAccessor.getId(), file.getRacine().getId(), "suppression");
        }
        File fileToDelete = new File(path + file.getNom()+"."+file.getExtension());
        System.out.println(fileToDelete);
        // Delete the file
        if(fileToDelete.delete()){
            System.out.println("File deleted successfully");
        }else{
            System.out.println("Failed to delete the file");
        }
        fichierRepository.delete(file);
    }

    /**
     * Rennome un fichier
     * @param fichierId id du fichier à rennomer
     * @param name nouveau nom du fichier
     * @return le fichier rennomé
     */
    @Override
    public Fichier rename(Long fichierId, String name)
    {
        RessourceAccessor trigger = authotisationService.extractResourceAccessorFromSecurityContext();
        Compagnie compagnie = authotisationService.extractCompagnieFromResourceAccessor();
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException(Literals.FILE_NOT_FOUND));
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        if(ressourceAccessor instanceof Membre){
            authotisationService.authorize(ressourceAccessor.getId(), file.getRacine().getId(), "modification");
        }
        String oldName = file.getNom();
        file.setNom(name);
        Log logMessage = Log.builder().message("Fichier '" + oldName+"."+file.getExtension() + "' Renommé à "+file.getNom()+"."+file.getExtension()).type(LogType.MODIFIER).date(new Date()).trigger(trigger).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return fichierRepository.save(file);
    }

    /**
     * Met à jour un fichier
     * @param f fichier à mettre à jour
     * @return le fichier mis à jour
     */
    @Override
    public Fichier updateFichier(Fichier f)
    {
        return fichierRepository.save(f);
    }

    /**
     * deplace un fichier vers un dossier cible
     * @param fichierId id du fichier à deplacer
     * @param dossierCibleId id du dossier cible
     * @return le fichier deplacé
     */
    @Override
    public Fichier changerEmplacement(Long fichierId,Long dossierCibleId )
    {
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException(Literals.FILE_NOT_FOUND));
        Dossier dossierCible = dossierRepository.findById(dossierCibleId).orElseThrow(()-> new RuntimeException(Literals.FOLDER_NOT_FOUND));
        file.setRacine(dossierCible);
        return fichierRepository.save(file);
    }

    /**
     * retourne un fichier
     * @param id id du fichier à retourner
     * @return le fichier
     */
    @Override
    public Fichier getFichier(Long id)
    {
        Fichier file = fichierRepository.findById(id).orElseThrow(()-> new RuntimeException(Literals.FILE_NOT_FOUND));
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        if(ressourceAccessor instanceof Membre){
            authotisationService.authorize(ressourceAccessor.getId(), file.getRacine().getId(), "lecture");
        }

        return file;
    }

    /**
     * Change l'etat d'un fichier
     * @param fichierId id du fichier à modifier
     * @param etat nouvel etat du fichier
     * @return le fichier modifié
     */
    @Override
    public Fichier updateEtat(Long fichierId, ETAT etat)
    {
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException(Literals.FILE_NOT_FOUND));
        file.setEtat(etat);
        return fichierRepository.save(file);
    }

    /**
     * Change la categorie d'un fichier
     * @param fichierId id du fichier à modifier
     * @param categorieId id de la nouvelle categorie
     * @return le fichier modifié
     */
    @Override
    public Fichier changeCategory(Long fichierId,Long categorieId){
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException(Literals.FILE_NOT_FOUND));
        Categorie categorie = categorieService.getCategorie(categorieId);
        file.setCategorie(categorie);
        return fichierRepository.save(file);
    }

    /**
     * Met ajour les labels d'un fichier
     * @param fichierId id du fichier à modifier
     * @param labels liste des nouveaux labels
     * @return le fichier modifié
     */
    @Override
    public Fichier updateLabels(Long fichierId, List<Label> labels){
        Fichier file = fichierRepository.findById(fichierId).orElseThrow(()-> new RuntimeException(Literals.FILE_NOT_FOUND));
        file.setLabels(labels);
        return fichierRepository.save(file);
    }

    /**
     * retourne la liste des fichiers d'un dossier
     * @param parentId id du dossier parent
     * @return la liste des fichiers
     */
    @Override
    public List<Fichier> getFichiersByParent(Long parentId){
        Dossier dossier = dossierRepository.findById(parentId).orElseThrow(()-> new RuntimeException(Literals.FOLDER_NOT_FOUND));
        return dossier.getFichiers();
    }

    /**
     * Charge un fichier vers le disque dur
     * @param file fichier à charger
     * @return le fichier chargé
     */
    /*a Spring-specific class used to handle file uploads.
    returns a List<String> containing the filenames of all
    files present in the directory where the new file will be uploaded*/

    @Override
    public List<String> uploadFile(MultipartFile file, Long folderId, List<String> selectedLabels, String selectedCategorie)
            throws Exception {
        // Save file on system
        if (!file.getOriginalFilename().isEmpty()) {
            BufferedOutputStream outputStream =
                    new BufferedOutputStream(
                            new FileOutputStream(new File(path,
                                    file.getOriginalFilename())));
            saveFile(file, folderId, selectedLabels, selectedCategorie);
            outputStream.write(file.getBytes());
            outputStream.flush();
            outputStream.close();
        } else {
            throw new Exception();
        }
        List<String> list = new ArrayList<String>();
        File files = new File(path);
        String[] fileList = files.list();
        for (String name : fileList) {
            list.add(name);
        }
        ;
        return list;
    }

    @Override
    public ByteArrayResource dowloadFile(String name) {
        return null;
    }

    @Override
    public void updateFile(String fileName, List<String> selectedLabels, String selectedCategorie, Long fileId) {
        Fichier f = fichierRepository.findById(fileId).orElseThrow(() -> new RuntimeException(Literals.FILE_NOT_FOUND));
        f.setLabels(getLabels(selectedLabels));
        Categorie categorie = categorieService.getCategorie(selectedCategorie);
        f.setCategorie(categorie);
        f.setNom(fileName);
        fichierRepository.save(f);
    }

    @Override
    public List<Fichier> getFilteredFiles(FileFilterRequest fileFilterRequest) {
        Membre membre = membreService.getMembre(authotisationService.extractResourceAssessorIdFromSecurityContext());
        List<Fichier> fichiers = fichierRepository.findAllByCompagnieNom(membre.getCompagnie().getNom());
        log.info("current file count no filters : {}", fichiers.size());
        // fileFilterRequest contains the file name to filter with, the category name and a list of labels names
        if (fileFilterRequest.getQuery() != null && !fileFilterRequest.getQuery().isEmpty() && !fileFilterRequest.getQuery().equals("null")) {
            fichiers = fichiers.stream().filter(fichier -> fichier.getNom().toLowerCase().contains(fileFilterRequest.getQuery().toLowerCase())).collect(Collectors.toList());
        }
        log.info("current file count after query filter : {}", fichiers.size());
        if (fileFilterRequest.getCategory() != null && !fileFilterRequest.getCategory().isEmpty() && !fileFilterRequest.getCategory().equals("null")) {
            fichiers = fichiers.stream().filter(fichier -> fichier.getCategorie().getNom().equalsIgnoreCase(fileFilterRequest.getCategory())).collect(Collectors.toList());
        }
        log.info("current file count after category filter : {}", fichiers.size());
        log.info("category : {}", fileFilterRequest.getCategory());
        if (fileFilterRequest.getLabels() != null && !fileFilterRequest.getLabels().isEmpty() && !fileFilterRequest.getLabels().equals("null")) {
            List<Label> labels = new ArrayList<>();
            for (String labelName : fileFilterRequest.getLabels()) {
                labels.add(labelRepository.findByNom(labelName));
            }
            fichiers = fichiers.stream().filter(fichier -> fichier.getLabels().containsAll(labels)).collect(Collectors.toList());
        }
        log.info("current file count after labels filter : {}", fichiers.size());
        log.info("labels : {}", fileFilterRequest.getLabels());
        //checks if the member has the lecture right
            fichiers = fichiers.stream().filter(fichier -> {
                return authotisationService.hasAuth(membre.getId(),fichier.getRacine().getId(),"lecture");
            }).collect(Collectors.toList());
        log.info("current file count after auth filter : {}", fichiers.size());
        for(Fichier f : fichiers){
           Authorisation a = authotisationService.getAuthorisation(membre.getId(),f.getRacine().getId());
           f.setCurrentAuth(new CurrentAuth(a));
        }
        return fichiers;
    }

    private void saveFile(MultipartFile file, Long folderId, List<String> selectedlabels, String selectedCategorie) {
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        Compagnie compagnie;
        if(ressourceAccessor instanceof  Membre){
            compagnie = ((Membre) ressourceAccessor).getCompagnie();
        }
        else{
            compagnie = ((Compagnie) ressourceAccessor);
        }

        Fichier fichier = new Fichier();
        Optional<Dossier> dossierOptional = dossierRepository.findById(folderId);
        Dossier dossier = dossierOptional.orElseThrow(() -> new NoSuchElementException(Literals.FOLDER_NOT_FOUND));
        int lastIndex = file.getOriginalFilename().lastIndexOf('.');
        fichier.setNom(lastIndex != -1 ? file.getOriginalFilename().substring(0, lastIndex) : file.getOriginalFilename());
        fichier.setExtension(lastIndex != -1 ? file.getOriginalFilename().substring(lastIndex+1, file.getOriginalFilename().length()) : file.getOriginalFilename());
        fichier.setTaille((double)file.getSize());
        fichier.setRealPath(path+"/"+fichier.getNom()+"."+fichier.getExtension());
        fichier.setCompagnie(compagnie);
        fichier.setRacine(dossier);
        fichier.setCategorie(getCategorie(selectedCategorie));
        fichier.setDateCreation(new Date());
        fichier.getLabels().addAll(getLabels(selectedlabels));;
        Log logMessage = Log.builder().message("Fichier '" + fichier.getNom()+"."+fichier.getExtension() + "' chargé dans " + dossier.getFullPath()).type(LogType.UPLOAD).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        fichierRepository.save(fichier);
    }

    private List<Label> getLabels(List<String> selectedlabels) {
        List<Label> labels = new ArrayList<>();

        for (String s: selectedlabels)
        {
            //removes "[", "]", """ and "," from the string
            s = s.replace("[", "").replace("]", "").replace("\"", "").replace(",", "");
            log.info("trying label: "+ s);
            log.info("label: "+ labelRepository.findByNom(s));
            labels.add(labelRepository.findByNom(s));
        }
        return labels;
    }

    private Categorie getCategorie(String selectedCategorie) {
        return (categorieRepository.findByNom(selectedCategorie));
    }

}
