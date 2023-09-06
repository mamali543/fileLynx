package backend.server.service.controller;

import backend.server.service.Literals;
import backend.server.service.Service.*;
import backend.server.service.domain.*;
import backend.server.service.payloads.FileResponse;
import backend.server.service.security.POJOs.responses.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fichier")
@CrossOrigin(origins = "*", maxAge = 3600) // Allow requests from any origin for one hour
public class FileController {

    private static final String pathReda = "/Users/macbookpro/Desktop/files/upload/";
    private static final String pathd = "/Users/macbookpro/Desktop/files/download/";
    private static final String pathDiae = "C:/Users/stagiaire7/Documents/GitHub/filelynx/files/upload/";

    @Autowired
    private IFichierService fichierService;
    @Autowired
    private QuotaService quotaService;
    @Autowired
    private AuthotisationService authotisationService;
    /**
     * ajoute un fichier dans le dossier parent spécifié
     * @param f fichier à ajouter
     * @param parentFolderId id du dossier parent
     * @return Réponse HTTP contenenant un message de succès ou d'erreur en cas d'échec
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/add/{parentFolderId}")
    public ResponseEntity<?> addFile(@RequestBody Fichier f,@PathVariable Long parentFolderId) {
        fichierService.addFichier(f, parentFolderId);
        return ResponseEntity.ok(new MessageResponse(Literals.FILE_CREATE_SUCCESS));

    }

    /**
     * Supprime un fichier
     * @param fileId id du fichier à supprimer
     * @return Réponse HTTP contenenant un message de succès ou d'erreur en cas d'échec
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE') or hasRole('ROLE_USER')")
    @DeleteMapping("/admin/delete/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Long fileId) {
        fichierService.deleteFichier(fileId);
        return ResponseEntity.ok(new MessageResponse(Literals.FILE_DELETE_SUCCESS));

    }

    /**
     * Renomme un fichier
     * @param fileId id du fichier à renommer
     * @param name nouveau nom du fichier
     * @return Réponse HTTP contenenant un message de succès ou d'erreur en cas d'échec
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE') or hasRole('ROLE_USER')")
    @PostMapping("/admin/rename/{fileId}")
    public ResponseEntity<?> renameFile(@PathVariable Long fileId,@RequestParam String name) {
        fichierService.rename(fileId, name);
        return ResponseEntity.ok(new MessageResponse(Literals.FILE_EDIT_SUCCESS));

    }

    /**
     * Mis à jour d'un fichier
     * @param f fichier à mettre à jour
     * @return Réponse HTTP contenenant un message de succès ou d'erreur en cas d'échec
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/update")
    public ResponseEntity<?> updateFile(@RequestBody Fichier f) {
        fichierService.updateFichier(f);
        return ResponseEntity.ok(new MessageResponse(Literals.FILE_EDIT_SUCCESS));

    }

    /**
     * deplace un fichier vers un dossier cible
     * @param fileId id du fichier à déplacer
     * @param targetFolderId id du dossier cible
     * @return Réponse HTTP contenenant un message de succès ou d'erreur en cas d'échec
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @PostMapping("/admin/move/{fileId}")
    public ResponseEntity<?> moveFile(@PathVariable Long fileId,@RequestParam Long targetFolderId) {
        fichierService.changerEmplacement(fileId, targetFolderId);
        return ResponseEntity.ok(new MessageResponse(Literals.FILE_EDIT_SUCCESS));
    }

    /**
     * retourne un fichier
     * @param fileId id du fichier à retourner
     * @return le fichier demandé
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE') or hasRole('ROLE_USER')")
    @GetMapping("/admin/get/{fileId}")
    public Fichier getFile(@PathVariable Long fileId) {
        return fichierService.getFichier(fileId);
    }

    /**
     * Change la catégorie d'un fichier
     * @param fileId id du fichier à modifier
     * @param categoryId id de la nouvelle catégorie
     * @return Réponse HTTP contenenant un message de succès ou d'erreur en cas d'échec
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/admin/changeCategory/{fileId}")
    public ResponseEntity<?> changeCategory(@PathVariable Long fileId,@RequestBody Long categoryId) {
        fichierService.changeCategory(fileId, categoryId);
        return ResponseEntity.ok(new MessageResponse(Literals.FILE_EDIT_SUCCESS));
    }

    /**
     * Change les labels d'un fichier
     * @param fileId id du fichier à modifier
     * @param labels liste des nouveaux labels
     * @return Réponse HTTP contenenant un message de succès ou d'erreur en cas d'échec
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE')")
    @GetMapping("/admin/changeLabel/{fileId}")
    public ResponseEntity<?> changeLabel(@PathVariable Long fileId,@RequestBody List<Label> labels) {
        fichierService.updateLabels(fileId, labels);
        return ResponseEntity.ok(new MessageResponse(Literals.FILE_EDIT_SUCCESS));
    }

    /**
     * retourne la liste des fichiers d'un dossier
     * @param parentFolderId id du dossier parent
     * @return la liste des fichiers du dossier parent
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE') or hasRole('ROLE_USER')")
    @GetMapping("/admin/getFromParent")
    public List<Fichier> getFilesFromParent(@RequestBody Long parentFolderId) {
        return fichierService.getFichiersByParent(parentFolderId);
    }

    /**
     * Charge un fichier
     * @param file fichier à charger
     * @return Réponse HTTP contenenant un message de succès ou d'erreur en cas d'échec
     * @throws Exception exception
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE') or hasRole('ROLE_USER')")
    @PostMapping("/upload")
    public ResponseEntity<List<String>> fileUpload
            (@RequestParam("file") MultipartFile file,
             @RequestParam("selectedLabels") List<String> selectedLabels,
             @RequestParam("selectedCategorie") String selectedCategorie,
             @RequestParam("folderId") Long folderId)
            throws Exception {
        quotaService.QuotaAuthFilter(file.getSize(), folderId);
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        if(ressourceAccessor instanceof Membre)
            authotisationService.authorize(ressourceAccessor.getId(), folderId, "telechargement");
        return new ResponseEntity<>(fichierService.uploadFile(file, folderId, selectedLabels, selectedCategorie),
                HttpStatus.OK);

    }

    /**
     * Retourne l'entête de la réponse HTTP pour le téléchargement d'un fichier
     * @param name nom du fichier
     * @return l'entête de la réponse HTTP pour le téléchargement d'un fichier
     */
    private HttpHeaders headers(String name) {

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + name);
        header.add("Cache-Control",
                "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        return header;

    }

    /**
     * Télécharge un fichier
     * @param name nom du fichier à télécharger
     * @return le fichier demandé
     * @throws IOException exception
     */
    @PreAuthorize("hasRole('ROLE_COMPAGNIE') or hasRole('ROLE_USER')")
    @GetMapping(path = "/download/{name}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable("name") String name) throws IOException {
        fichierService.dowloadFile(name);
        // 1. Construct the File object representing the file to be downloaded
        File file = new File(pathDiae + name);
        // 2. Create a Path object from the File's absolute path
        Path filePath = Paths.get(file.getAbsolutePath());
        // 3. Read the file's content into a ByteArrayResource
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(filePath));
        BufferedOutputStream outputStream =
                new BufferedOutputStream(
                        new FileOutputStream(new File(pathd,
                                "tswira.png")));
        outputStream.write(resource.getByteArray());
        outputStream.flush();
        outputStream.close();
        // 4. Prepare and return the ResponseEntity with the file content
        return ResponseEntity
                .ok()
                .headers(this.headers(name)) // Add custom headers for the response
                .contentLength(file.length()) // Set the Content-Length header
                .contentType(MediaType.parseMediaType("application/octet-stream")) // Set the Content-Type header
                .body(resource); // Set the response body with the file content
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE') or hasRole('ROLE_USER')")
    @GetMapping("/getImage/{fichierId}")
    public ResponseEntity<org.springframework.core.io.Resource> getImage(@PathVariable Long fichierId) throws IOException {
        Fichier f = fichierService.getFichier(fichierId);
        String path = f.getRealPath();
        File file = new File(path);
        Path filePath = Paths.get(file.getAbsolutePath());
        org.springframework.core.io.Resource res = new UrlResource(file.toURI());
        if (res.exists()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE,Files.probeContentType(filePath))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + res.getFilename() + "\"")
                    .body(res);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // This method handles the download of a file based on its fichierId
    @PreAuthorize("hasRole('ROLE_COMPAGNIE') or hasRole('ROLE_USER')")
    @GetMapping("/downloadFile/{fichierId}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(@PathVariable Long fichierId) throws IOException {

        // Retrieve the Fichier (file) based on the given fichierId
        Fichier f = fichierService.getFichier(fichierId);
        RessourceAccessor ressourceAccessor = authotisationService.extractResourceAccessorFromSecurityContext();
        if(ressourceAccessor instanceof Membre)
            authotisationService.authorize(ressourceAccessor.getId(), f.getRacine().getId(), "telechargement");
        // Get the real path of the file
        String path = f.getRealPath();

        // Create a File object from the path
        File file = new File(path);

        // Get the absolute file path
        Path filePath = Paths.get(file.getAbsolutePath());

        // Create a Spring Resource from the File's URI
        org.springframework.core.io.Resource res = new UrlResource(file.toURI());

        // Check if the resource (file) exists
        if (res.exists()) {
            // If the resource exists, build a ResponseEntity with the file content for download
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE,Files.probeContentType(filePath)) // Set the content type based on file type
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + res.getFilename() + "\"") // Set the filename for download
                    .body(res); // Set the file content as the response body
        } else {
            // If the resource does not exist, return a not found response
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ROLE_COMPAGNIE') or hasRole('ROLE_USER')")
    @PostMapping("/updateFile")
    public ResponseEntity<?> updateFile(@RequestParam("selectedLabels") List<String> selectedLabels,
                                        @RequestParam("selectedCategorie") String selectedCategorie,
                                        @RequestParam("fileName") String fileName,
                                        @RequestParam("fileId") Long fileId){
        fichierService.updateFile(fileName, selectedLabels, selectedCategorie, fileId);
        return ResponseEntity.ok(new MessageResponse(Literals.FILE_EDIT_SUCCESS));
    }
}
