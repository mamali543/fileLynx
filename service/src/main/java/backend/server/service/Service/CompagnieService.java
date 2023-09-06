package backend.server.service.Service;

import backend.server.service.Literals;
import backend.server.service.POJO.PageResponse;
import backend.server.service.Repository.*;
import backend.server.service.domain.*;
import backend.server.service.enums.LogType;
import backend.server.service.payloads.*;
import backend.server.service.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service @Slf4j @Transactional @RequiredArgsConstructor
public class CompagnieService implements ICompagnieService{
     private final GroupeRepository groupeRepository;
     private final MembreRepository membreRepository;
     private final UserRepository userRepository;
     private final LabelRepository labelRepository;
     private final CategorieRepository categorieRepository;
     private final CompagnieRepository compagnieRepository;
     private final DossierRepository dossierRepository;
     private final FichierRepository fichierRepository;
     private final QuotaService quotaService;
     private final LogRepository logRepository;

    private final GroupeService groupeService;
    @Override
    public Compagnie getCompagnie(Long id){
        return compagnieRepository.findById(id).orElseThrow(()-> new RuntimeException("Compagnie not found") );
    }
    @Override
    public Compagnie getCompagnie(String nom){
        return compagnieRepository.findByNom(nom);
    }

    @Override
    public List<Log> getLogs() {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        return getCompagnie(compagnieNom).getLogs();
    }

    @Override
    public List<Compagnie> getAllCompagnies(){
        return compagnieRepository.findAll();
    }
    @Override
    public Compagnie createCompagnie(Compagnie compagnie){
        return compagnieRepository.save(compagnie);
    }
    @Override
    public Compagnie updateCompagnie(Compagnie compagnie){
        return compagnieRepository.save(compagnie);
    }
    @Override
    public void deleteCompagnie(Long id){
        compagnieRepository.deleteById(id);
    }
    @Override
    public void deleteCompagnie(String nom){
        compagnieRepository.deleteByNom(nom);
    }

     /**
      * Crée un nouveau groupe, l'ajoute à la compagnie et le persiste si le quota de la compagnie n'est pas dépassé, un dossier est créé pour le groupe
      * @param nom nom du groupe
      * @param quota quota du groupe (espace disque)
      * @return le groupe créé
      */
    @Override
    public Groupe createGroupe(String nom, double quota){
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        //System.out.println("compagnie name: "+ compagnieName+" nom de groupe: "+ nom);
        //check if the compagnie has a groupe with the same name
        if(groupeService.getGroupe(nom, compagnieName) != null){
            throw new RuntimeException(Literals.GROUPE_ALREADY_EXISTS);
        }
        Groupe groupe = Groupe.builder().nom(nom).quota(quota).build();
        //get the id of the current authenticated user via the security context holder
        Compagnie compagnie = compagnieRepository.findByNom(compagnieName);
        if(groupeRepository.sumQuotasByCompagnieNom(compagnieName) + quota > compagnie.getQuota()){
            throw new RuntimeException(Literals.QUOTA_ALLOCATION_EXCEEDED);
        }
        groupe.setCompagnie(compagnie);
        compagnie.getGroupes().add(groupe);
        compagnieRepository.save(compagnie);
        return groupeService.getGroupe(nom, compagnieName);
    }

     /**
      * Crée un nouveau groupe, l'ajoute à la compagnie et le persiste si le quota de la compagnie n'est pas dépassé, un dossier est créé pour le groupe
      * @param nom nom du groupe
      * @param quota quota du groupe (espace disque)
      * @param CompagnieId id de la compagnie
      * @return le groupe créé
      */
    @Override
    public Groupe createGroupe(String nom, double quota, Long CompagnieId){
        //check if the compagnie has a groupe with the same name
        if(groupeService.getGroupe(nom, SecurityContextHolder.getContext().getAuthentication().getName()) != null){
            throw new RuntimeException(Literals.GROUPE_ALREADY_EXISTS);
        }
        Groupe groupe = Groupe.builder().nom(nom).quota(quota).build();

        Compagnie compagnie = compagnieRepository.findById(CompagnieId).orElseThrow(()-> new RuntimeException("Compagnie not found") );
        if(groupeRepository.sumQuotasByCompagnieNom(compagnie.getNom()) + quota > compagnie.getQuota()){
            throw new RuntimeException(Literals.QUOTA_ALLOCATION_EXCEEDED);
        }
        groupe.setCompagnie(compagnie);
        compagnie.getGroupes().add(groupe);
        compagnieRepository.save(compagnie);
        return groupeService.getGroupe(nom, compagnie.getNom());
    }

     /**
      * Supprime un groupe de la compagnie si le groupe existe et si l'utilisateur est autorisé à le faire
      * @param nom nom du groupe
      */
    @Override
    public void deleteGroupe(String nom){
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        Groupe groupe = groupeRepository.findByNomAndCompagnieNom(nom, compagnieName);
        if(groupe == null){
            throw new RuntimeException(Literals.GROUPE_NOT_FOUND);
        }
        if(!(compagnieName.equals(groupe.getCompagnie().getNom()))){
            throw new RuntimeException(Literals.UNAUTHORIZED);
        }
        if(groupe.getNom().equalsIgnoreCase(compagnieName)){
            throw new RuntimeException(Literals.CANT_DELETE_DEFAULT_GROUP);
        }
        log.info("groupe name: "+groupe.getNom()+" compagnie name: "+groupe.getCompagnie().getNom());
        groupeRepository.delete(groupe);
    }
    /**
     * Supprime un groupe de la compagnie si le groupe existe et si l'utilisateur est autorisé à le faire
     * @param groupeId id du groupe
     */
    @Override
    public Groupe updateGroupe(Long groupeId, String newName) {

        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();

        Groupe grp = groupeRepository.findByIdAndCompagnieNom(groupeId, compagnieName);
        if(grp.getNom().equalsIgnoreCase(compagnieName)){
            throw new RuntimeException(Literals.CANT_EDIT_DEFAULT_GROUP);
        }
        grp.setNom(newName);
        return groupeRepository.save(grp);
    }

     /**
      * Supprime un Memebre de la compagnie si le membre existe et si l'utilisateur est autorisé à le faire
      * @param membreId id du membre
      * @param username nom d'utilisateur du membre
      */
    @Override
    public void deleteMembre(Long membreId, String username){
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        Membre membre = membreRepository.findByUsername(username);
        if(membre == null){
            throw new RuntimeException(Literals.MEMBER_NOT_FOUND);
        }
        if(!(compagnieName.equals(membre.getCompagnie().getNom()))){
            throw new RuntimeException(Literals.UNAUTHORIZED);
        }
        log.info("membre name: "+membre.getNom()+" compagnie name: "+membre.getCompagnie().getNom());
        membreRepository.deleteById(membreId);
        userRepository.deleteByUsername(username);
    }

     /**
      * Mis à jour un membre passé en paramètre
      * @param membre membre à mettre à jour
      * @return le membre mis à jour
      */
    @Override
    public Membre updateMembre(Membre membre) {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = getCompagnie(compagnieNom);
        String username= membre.getUsername();
        Optional<Membre> optionalExistingMembre = membreRepository.findById(membre.getId());

        if (optionalExistingMembre.isPresent()) {
            Membre existingMembre = optionalExistingMembre.get();

            // Update the desired fields
            existingMembre.setNom(membre.getNom());
            existingMembre.setPrenom(membre.getPrenom());
            existingMembre.setEmail(membre.getEmail());
            existingMembre.setUsername(membre.getUsername());

            // Save the updated entity back to the database
            Log logMessage = Log.builder().message("Membre " + username + " de la Société " + compagnieNom + " a été mis à jour").type(LogType.MODIFIER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
            logRepository.save(logMessage);
            return membreRepository.save(existingMembre);
        } else {
            throw new RuntimeException(Literals.MEMBER_NOT_FOUND);
        }




    }

     /**
      * Récupère tous les groupes de la compagnie
      * @return la liste des groupes de la compagnie
      */
    @Override
    public List<String> getAllUniqueGroups() {
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        return groupeRepository.findAllUniqueGroupes(compagnieName);
    }

     /**
      * Récupére le nombre des membres, groupes, fichiers et dossiers de la compagnie
      * @return Un objet de type EntitiesCountResponse contenant le nombre des membres, groupes, fichiers et dossiers
      */
     @Override
     public EntitiesCountResponse getEntitiesCount() {
         EntitiesCountResponse entitiesCountResponse = new EntitiesCountResponse();
         String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
         entitiesCountResponse.setDossiers(dossierRepository.countByCompagnieNom(compagnieName));
         entitiesCountResponse.setFichiers(fichierRepository.countByCompagnieNom(compagnieName));
         entitiesCountResponse.setGroupes(groupeRepository.countByCompagnieNom(compagnieName));
         entitiesCountResponse.setMembres(membreRepository.countByCompagnieNom(compagnieName));
         entitiesCountResponse.setCategories(categorieRepository.countByCompagnieNom(compagnieName));
         entitiesCountResponse.setLabels(labelRepository.countByCompagnieNom(compagnieName));
         return entitiesCountResponse;
     }

     /**
      * Récupére toutes les étiquettes de la compagnie et les retourne sous forme d'une liste de chaînes de caractères
      * @return la liste des étiquettes de la compagnie
      */
     @Override
     public List<String> getAllLabels() {
         List<Label> l = labelRepository.findAll();
         List<String> listLabels = new ArrayList<>();
         for (Label n: l)
         {
             listLabels.add(n.getNom());
         }
         return (listLabels);
     }

     /**
      * Récupére tous les groupes de la compagnie et les retourne sous forme d'une liste de chaînes de caractères
      * @return la liste des groupes de la compagnie
      */
     @Override
     public List<String> getAllCategories() {
         List<Categorie> l = categorieRepository.findAll();
         List<String> listCategories = new ArrayList<>();
         for (Categorie n: l)
         {
             listCategories.add(n.getNom());
         }
         return (listCategories);
     }

    @Override
    public ConsumptionHistoryChart getQuotaUsedByDay() {
        ConsumptionHistoryChart consumptionHistoryChart = new ConsumptionHistoryChart();
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Fichier> fichiers = fichierRepository.findAllByCompagnieNom(compagnieName);
        //seperates the files by day in a List of Lists
        List<List<Fichier>> fichiersByDay = new ArrayList<>();
        for (Fichier fichier : fichiers) {
            Date date = fichier.getDateCreation();
            boolean found = false;
            for (List<Fichier> fichiers1 : fichiersByDay) {
                if (fichiers1.get(0).getDateCreation().getDay() == date.getDay()) {
                    fichiers1.add(fichier);
                    found = true;
                    break;
                }
            }
            if (!found) {
                List<Fichier> newFichiers = new ArrayList<>();
                newFichiers.add(fichier);
                fichiersByDay.add(newFichiers);
            }
        }
        //reduce the content of the lists to the sum of the size of the files
        List<Double> sizeByDay = new ArrayList<>();
        for (List<Fichier> fichiers1 : fichiersByDay) {
            double size = 0;
            for (Fichier fichier : fichiers1) {
                size += fichier.getTaille();
            }
            sizeByDay.add(size);
        }
        consumptionHistoryChart.setConsumptionHistory(sizeByDay);
        // get the dates of the days as Date objects
        List<Date> dates = new ArrayList<>();
        for (List<Fichier> fichiers1 : fichiersByDay) {
            dates.add(fichiers1.get(0).getDateCreation());
        }
        consumptionHistoryChart.setLabels(dates);

        return consumptionHistoryChart;
    }

    @Override
    public List<GroupConsumption> getAllGroupsConsumption() {
        Compagnie compagnie = compagnieRepository.findByNom(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Groupe> groupes = groupeRepository.findAllByCompagnieNom(compagnie.getNom());
        List<GroupConsumption> groupConsumptions = new ArrayList<>();
        double consumed = 0;
        for(Groupe groupe : groupes){
            GroupConsumption groupConsumption = new GroupConsumption();
            groupConsumption.setConsumption(quotaService.getTotalQuotaOfGroup(groupe.getId()));
            groupConsumption.setName(groupe.getNom());
            consumed += groupConsumption.getConsumption();
            groupConsumptions.add(groupConsumption);
        }
        GroupConsumption groupConsumption = new GroupConsumption();
        groupConsumption.setName("Libre");
        groupConsumption.setConsumption(compagnie.getQuota() - consumed);
        groupConsumptions.add(groupConsumption);
        return groupConsumptions;
    }

    @Override
    public CompagnieName getCompagnieName() {
        CompagnieName compagnieName = new CompagnieName();
        compagnieName.setName(SecurityContextHolder.getContext().getAuthentication().getName());
        return compagnieName;
    }

    @Override
    public PageResponse<Compagnie> getCompagniesPage(int page, int size, String sortBy, String sortOrder, String searchQuery) {
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Sort sort = Sort.by(direction, sortBy);
        int start = page * size;
        int end = Math.min(start + size, (int) compagnieRepository.count());
        List<Compagnie> compagnies = compagnieRepository.findAll(sort);
        if (searchQuery != null && !searchQuery.isEmpty()){
            compagnies = compagnies.stream()
                    .filter(compagnie -> compagnie.getNom().toLowerCase().contains(searchQuery.toLowerCase()))
                    .collect(Collectors.toList());
        }
//        for (Compagnie compagnie : compagnies) {
//            groupe.setQuotaUsed(quotaService.getTotalQuotaOfGroup(groupe.getId()));
//        }
        List<Compagnie> pageContent = compagnies.subList(start, Math.min(end, compagnies.size()));
        return new PageResponse<>(pageContent, compagnies.size());
     }

    @Override
    public void updateCompagnieQuota(Long id, String name, Double quota) {
        Compagnie compagnie = getCompagnie(id);
        compagnie.setQuota(quota);
        compagnie.setNom(name);
        compagnieRepository.save(compagnie);
    }

    public QuotaUsedToday getQuotaUsedToday() {
        QuotaUsedToday quotaUsedToday = new QuotaUsedToday();
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Fichier> fichiers = fichierRepository.findAllByCompagnieNom(compagnieName);
        //filter the files that were created today after midnight
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0); //midnight
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date midnight = calendar.getTime();
        List<Fichier> fichiersToday = fichiers.stream().filter(fichier -> fichier.getDateCreation().after(midnight)).collect(Collectors.toList());
        //sum the size of the files
        double size = 0;
        for (Fichier fichier : fichiersToday) {
            size += fichier.getTaille();
        }
        quotaUsedToday.setQuotaUsedToday(size);
        quotaUsedToday.setFileCount((long) fichiersToday.size());

        return quotaUsedToday;
     }

}
