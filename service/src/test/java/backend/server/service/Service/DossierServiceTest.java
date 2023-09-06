package backend.server.service.Service;

import backend.server.service.Repository.DossierRepository;
import backend.server.service.domain.Compagnie;
import backend.server.service.domain.Dossier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DossierServiceTest {

    @Mock
    private DossierRepository dossierRepository;

    @Autowired
    private DossierService dossierService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dossierService = new DossierService(dossierRepository);
    }

    @Test
    void testAddDossier() {
        // Mock data
        Dossier dossier = new Dossier();
        Long parentFolderId = 1L;
        Compagnie compagnie = new Compagnie();

        Dossier dossierParent = new Dossier();
        dossierParent.setId(parentFolderId);

        // Mock repository behavior
        when(dossierRepository.findById(parentFolderId)).thenReturn(Optional.of(dossierParent));
        when(dossierRepository.save(dossier)).thenReturn(dossier);

        // Invoke the method
        Dossier result = dossierService.addDossier(dossier, parentFolderId, compagnie);

        // Verify the interactions
        verify(dossierRepository).findById(parentFolderId);
        verify(dossierRepository).save(dossier);

        // Assertions
        Assertions.assertEquals(compagnie, dossier.getCompagnie());
        Assertions.assertEquals(dossierParent, dossier.getRacine());
        Assertions.assertTrue(dossierParent.getDossiers().contains(dossier));
        Assertions.assertNotNull(result);
    }
    @Test
    void addDossier() {
    }


    @Test
    void renameDossier() {
    }

    @Test
    void fileTree() {
    }

    @Test
    void fileTreeFiles() {
    }

    @Test
    void delete() {
    }

    @Test
    void changerEmplacement() {
    }
}