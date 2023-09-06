package backend.server.service.controller;

import backend.server.service.Literals;
import backend.server.service.POJO.PageResponse;
import backend.server.service.POJO.Quota;
import backend.server.service.Repository.*;
import backend.server.service.Service.*;
import backend.server.service.domain.*;
import backend.server.service.enums.AuthLevel;
import backend.server.service.enums.LogType;
import backend.server.service.payloads.*;
import backend.server.service.security.POJOs.responses.MessageResponse;
import backend.server.service.security.entities.EROLE;
import backend.server.service.security.entities.User;
import backend.server.service.security.repositories.RoleRepository;
import backend.server.service.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/compagnie")
@CrossOrigin(origins = "*", maxAge = 3600) // Allow requests from any origin for one hour
@RequiredArgsConstructor @Slf4j
public class CompagnieController {
    private final ICompagnieService compagnieService;
    private final CompagnieRepository compagnieRepository;
    private final PasswordEncoder encoder;

    private final RoleRepository roleRepository;
    private final IGroupeService groupeService;
    private final GroupeRepository groupeRepository;
    private final IMembreService membreService;
    private final LogRepository logRepository;
    private final CategorieRepository categorieRepository;
    private final LabelRepository labelRepository;
    private final ILogService logService;
    private final ICategorieService categorieService;
    private final ILabelService labelService;
    private final IDossierService dossierService;
    private final QuotaService quotaService;
    private final IAuthotisationService authotisationService;

    /**
     * Ajoute un nouveau membre à la compagnie actuelle.
     *
     * @param membre Les informations du membre à ajouter.
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/RegisterMembre")
    public ResponseEntity<?> addMembre(@RequestBody RegisterUserRequest membre) {
        membreService.registerMembre(membre);
        return ResponseEntity.ok(new MessageResponse(Literals.MEMBER_CREATE_SUCCESS));
    }

    /**
     * Crée un nouveau groupe dans la compagnie actuelle.
     * @param group Le nom du groupe à créer.
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/createGroup/{group}")
    public ResponseEntity<?> createGroup(@PathVariable String group) {
        groupeService.createGroupe(group);
        return ResponseEntity.ok(new MessageResponse(Literals.GROUPE_CREATE_SUCCESS));
    }

    /**
     * Transfére un membre d'un groupe à un autre.
     * @param username Le nom d'utilisateur du membre à transférer.
     * @param group Le nom du groupe dans lequel le membre doit être transféré.
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PutMapping("/ChangeMemberGroup/{username}/{group}")
    public ResponseEntity<?> changeMemberGroup(@PathVariable String username, @PathVariable String group) {
        membreService.changeMemberGroup(username,group);
        return ResponseEntity.ok(new MessageResponse(Literals.MEMBRE_TRANSTER_SUCCESS));
    }

    /**
     * retourne l'etat d'utilisation du quota de la compagnie
     * @param
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getQuotaStatus")
    public ResponseEntity<?> getQuotaStatus() {

        return ResponseEntity.ok(quotaService.getQuotaStatus());
    }

    /**
     * retourne les logs de la compagnie
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getCompagnieLogs")
    public List<Log> getCompagnieLogs() {
        return compagnieService.getLogs();
    }

    /**
     * retourne les logs de la compagnie, paginés
     * @param page nombre de la page
     * @param size taille de la page
     * @param sortBy propriété utilisée pour le tri
     * @return la page de logs demandée
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getLogsPagination")
    public  PageResponse<Log> getAllLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            @RequestParam(defaultValue = "date") String sortBy
    )
    {
        return logService.getLogsPage(page, size, sortBy);
    }

    /**
     * retourne les Membres (collaborateurs) de la compagnie, paginés et filtrés
     * @param page nombre de la page
     * @param size taille de la page
     * @param sortBy propriété utilisée pour le tri
     * @param sortOrder ordre de tri
     * @param searchQuery chaine de caractère utilisée pour la recherche
     * @param groupFilter filtre sur le groupe
     * @return la page de Membres demandée
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getUsers")
    public PageResponse<Membre> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortOrder,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String groupFilter
    ) {
        return membreService.getMembresPage(page, size, sortBy, sortOrder, searchQuery, groupFilter);
    }

    /**
     * retourne les groupes de la compagnie, paginés et filtrés
     * @param page nombre de la page
     * @param size taille de la page
     * @param sortBy propriété utilisée pour le tri
     * @param sortOrder ordre de tri
     * @param searchQuery chaine de caractère utilisée pour la recherche
     * @return la page de groupes demandée
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getGroups")
    public PageResponse<Groupe> getAllGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortOrder,
            @RequestParam(required = false) String searchQuery
    ) {
        return groupeService.getGroupesPage(page, size, sortBy, sortOrder, searchQuery);
    }

    /**
     * supprime un groupe de la compagnie (sauf le groupe par défaut) et remet les membres dans le groupe par défaut, le dossier du groupe est supprimé
     * @param group nom du groupe à supprimer
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @DeleteMapping("/deleteGroupe/{group}")
    public ResponseEntity<?> deleteGroup(@PathVariable String group) {
        groupeService.deleteGroup(group);
        return ResponseEntity.ok(new MessageResponse(Literals.GROUPE_DELETE_SUCCESS));
    }

    /**
     * supprime un membre de la compagnie
     * @param membreId id du membre à supprimer
     * @param username nom d'utilisateur du membre à supprimer
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @DeleteMapping("/deleteMembre/{membreId}/{username}")
    public ResponseEntity<?> deleteMembre(@PathVariable Long membreId, @PathVariable String username) {
        membreService.deleteMembre(membreId, username);
        return ResponseEntity.ok(new MessageResponse(Literals.MEMBER_DELETE_SUCCESS));
    }

    /**
     * Modifie le nom d'un groupe, le dossier du groupe est renommé également avec le nouveau nom, les membres du groupe ne sont pas modifiés
     * @param groupeId id du groupe à modifier
     * @param newName nouveau nom du groupe
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PutMapping("/updateGroupe/{groupeId}/{newName}")
    public ResponseEntity<?> updateGroup(@PathVariable Long groupeId, @PathVariable String newName) {
        groupeService.updateGroupe(groupeId, newName);
        return ResponseEntity.ok(new MessageResponse(Literals.GROUPE_EDIT_SUCCESS));
    }

    /**
     * Modifie les informations d'un membre
     * @param membre membre à modifier
     * @return  Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PutMapping("/updateMembre")
    public ResponseEntity<?> updateMembre(@RequestBody Membre membre) {
        compagnieService.updateMembre(membre);
        return ResponseEntity.ok(new MessageResponse(Literals.MEMBER_EDIT_SUCCESS));
    }

    /**
     * Modifie le nom d'une catégorie
     * @param categorieId id de la catégorie à modifier
     * @param newName nouveau nom de la catégorie
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PutMapping("/updateCategorie/{categorieId}/{newName}")
    public ResponseEntity<?> updateCategorie(@PathVariable Long categorieId, @PathVariable String newName ) {
        categorieService.updateCategorie(categorieId, newName);
        return ResponseEntity.ok(new MessageResponse(Literals.CATEGORY_EDIT_SUCCESS));
    }

    /**
     * Supprime une catégorie
     * @param categorieId id de la catégorie à supprimer
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @DeleteMapping("/deleteCategorie/{categorieId}")
    public ResponseEntity<?> deleteCategorie(@PathVariable Long categorieId) {
        categorieService.deleteCategorie(categorieId);
        return ResponseEntity.ok(new MessageResponse(Literals.CATEGORY_DELETE_SUCCESS));
    }

    /**
     * Modifie le nom d'une étiquette
     * @param labelId id de l'étiquette à modifier
     * @param newName nouveau nom de l'étiquette
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PutMapping("/updateLabel/{labelId}/{newName}")
    public ResponseEntity<?> updateLabel(@PathVariable Long labelId, @PathVariable String newName ) {
        labelService.updateLabel(labelId, newName);
        return ResponseEntity.ok(new MessageResponse(Literals.LABEL_EDIT_SUCCESS));
    }

    /**
     * Supprime une étiquette
     * @param labelId id de l'étiquette à supprimer
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @DeleteMapping("/deleteLabel/{labelId}")
    public ResponseEntity<?> deleteLabel(@PathVariable Long labelId) {
        labelService.deleteLabel(labelId);
        return ResponseEntity.ok(new MessageResponse(Literals.LABEL_DELETE_SUCCESS));
    }

    /**
     * @return une liste des groupes de la compagnie ou au moins un membre est affecté
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/distinctGroups")
    public List<String> getAllUniqueGroupes() {
        return compagnieService.getAllUniqueGroups();
    }

    /**
     * @return une liste des labels de la compagnie
     */

    @PreAuthorize("hasRole('ROLE_COMPAGNIE') or hasRole('ROLE_USER')")
    @GetMapping("/labels")
    public List<String> getAllLabels() {
         return compagnieService.getAllLabels();
    }

    /**
     * @return une liste des catégories de la compagnie
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE') or hasRole('ROLE_USER')")
    @GetMapping("/categories")
    public List<String> getAllCategories() {
        return compagnieService.getAllCategories();
    }


    /**
     * @param page numéro de la page
     * @param size nombre d'éléments par page
     * @param sortBy nom de la colonne à trier
     * @param sortOrder ordre de tri
     * @param searchQuery mot clé de recherche
     * @return une liste des catégories de la compagnie, paginé et triable, avec un mot clé de recherche
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getCategories")
    public PageResponse<Categorie> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortOrder,
            @RequestParam(required = false) String searchQuery
    ) {
        return categorieService.getCategoriesPage(page, size, sortBy, sortOrder, searchQuery);
    }

    /**
     * @param page numéro de la page
     * @param size nombre d'éléments par page
     * @param sortBy nom de la colonne à trier
     * @param sortOrder ordre de tri
     * @param searchQuery mot clé de recherche
     * @return une liste des étiquettes de la compagnie, paginé et triable, avec un mot clé de recherche
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getLabels")
    public PageResponse<Label> getAllLabels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortOrder,
            @RequestParam(required = false) String searchQuery
    ) {
        return labelService.getLabelsPage(page, size, sortBy, sortOrder, searchQuery);
    }

    /**
     * ajoute une étiquette
     * @param label étiquette à ajouter
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/addLabel")
    public ResponseEntity<?> addLabel(@RequestBody Label label) {
            labelService.addLabel(label);
            return ResponseEntity.ok(new MessageResponse(Literals.LABEL_CREATE_SUCCESS));
    }

    /**
     * ajoute une catégorie
     * @param categorie catégorie à ajouter
     * @return Une réponse HTTP contenant un message de réussite ou d'erreur.
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/addCategorie")
    public ResponseEntity<?> addCategorie(@RequestBody Categorie categorie) {
            categorieService.addCategorie(categorie);
            return ResponseEntity.ok(new MessageResponse(Literals.CATEGORY_CREATE_SUCCESS));
    }

    /**
     * @param groupe nom du groupe
     * @return une liste des membres de la compagnie affectés au groupe
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getMembresByGroupe/{groupe}")
    public List<Membre> getMembresByGroupe(@PathVariable Long groupe) {
        return membreService.getMembresByGroupeId(groupe);
    }

    /**
     *
     * @return un objet contenant le nombre de membres, le nombre de groupes, le nombre de dossiers et le nombre de fichiers de la compagnie
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getEntitiesCount")
    public EntitiesCountResponse getEntitiesCount() {
        return compagnieService.getEntitiesCount();
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getQuotaUsedToday")
    public ResponseEntity<?> getQuotaUsedToday(){
        return ResponseEntity.ok(compagnieService.getQuotaUsedToday());
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getTotalAllocatedQuota")
    public ResponseEntity<?> getTotalAllocatedQuota(){
        return ResponseEntity.ok(quotaService.getTotalAllocatedQuota());
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getQuotaUsedByDay")
    public ConsumptionHistoryChart getQuotaUsedByDay(){
        return compagnieService.getQuotaUsedByDay();
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getAllGroupsConsumption")
    public List<GroupConsumption> getAllGroupsConsumption(){
        return compagnieService.getAllGroupsConsumption();
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getCompagnieName")
    public ResponseEntity<CompagnieName> getCompagnieName(){

        return ResponseEntity.ok(compagnieService.getCompagnieName());
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getGroupe/{id}")
    public ResponseEntity<Groupe> getGroupe(@PathVariable Long id){
        return ResponseEntity.ok(groupeService.getGroupe(id));
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getCompagnieUnallocatedQuota")
    public ResponseEntity<Double> getCompagnieUnallocatedQuota(){
        return ResponseEntity.ok(quotaService.getCompagnieUnallocatedQuota());
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PutMapping("/updateGroupeQuota/{id}")
    public ResponseEntity<?> updateGroupeQuota(@PathVariable Long id, @RequestBody Double quota){
        quotaService.updateGroupeQuota(id, quota);
        return ResponseEntity.ok(new MessageResponse(Literals.GROUPE_EDIT_SUCCESS));
    }


    @PreAuthorize("hasRole('ROLE_SUPERADMIN')")
    @PutMapping("/updateCompagnieQuota/{id}/{name}")
    public ResponseEntity<?> updateCompagnieQuota(@PathVariable Long id, @PathVariable String name,@RequestBody Double quota){
        compagnieService.updateCompagnieQuota(id, name, quota);
        return ResponseEntity.ok(new MessageResponse(Literals.COMPAGNIEQUOTA_EDIT_SUCCESS));
    }

    @PreAuthorize("hasRole('ROLE_SUPERADMIN')")
    @GetMapping("/getCompagnies")
    public PageResponse<Compagnie> getAllCompagnies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size,
            @RequestParam(defaultValue = "nom") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortOrder,
            @RequestParam(required = false) String searchQuery
    ) {
        System.out.println("here pleaaaase");
        return compagnieService.getCompagniesPage(page, size, sortBy, sortOrder, searchQuery);
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getMembresWithAuth/{folderId}")
    public ResponseEntity<List<Membre>> getMembresWithAuth(@PathVariable Long folderId){
        return ResponseEntity.ok(authotisationService.getMembresWithAuthObjects(folderId));
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getMembresWithoutAuth/{folderId}")
    public ResponseEntity<List<Membre>> getMembresWithoutAuth(@PathVariable Long folderId){
        return ResponseEntity.ok(authotisationService.getMembresWithoutAuthObjects(folderId));
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/giveMemberAccessToDossier/{folderId}/{membreId}")
    public ResponseEntity<?> giveMemberAccessToDossier(@PathVariable Long folderId, @PathVariable Long membreId){
        authotisationService.giveMemberAccessToDossier(folderId, membreId);
        return ResponseEntity.ok(new MessageResponse(Literals.ACCESS_GRANTED));
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/getAuthObject/{folderId}/{resourceAccessorId}")
    public ResponseEntity<Authorisation> getMembreAuthObject(@PathVariable Long folderId, @PathVariable Long resourceAccessorId){
        return ResponseEntity.ok(authotisationService.getAuthorisation(resourceAccessorId, folderId));
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PutMapping("/updateAuth")
    public ResponseEntity<?> updateAuth(@RequestBody Authorisation authorisation){
        log.info("update auth");
        authotisationService.updateAuthorisation(authorisation);
        return ResponseEntity.ok(new MessageResponse(Literals.ACCESS_GRANTED));
    }

}