package backend.server.service.Service;

import backend.server.service.domain.*;

import java.util.List;

public interface IAuthotisationService {
    Long extractResourceAssessorIdFromSecurityContext();
    void generateDefaultAuths(Long resourceAccessorId, Dossier dossier);
    Authorisation getAuthorisation(Long ressourceAccessorId, Long dossierId);
    boolean hasAuth(Long resourceAccessorId, Long dossierId, String authType);
    void authorize(Long resourceAccessorId, Long dossierId, String authType);
    boolean determineResourceAssessor();
    Compagnie extractCompagnieFromResourceAccessor();
    RessourceAccessor extractResourceAccessorFromSecurityContext();
    List<Membre> getMembresWithAuthObjects(Long dossierId);
    List<Membre> getMembresWithoutAuthObjects(Long dossierId);
    Authorisation giveMemberAccessToDossier(Long dossierId, Long membreId);

    void updateAuthorisation(Authorisation authorisation);
}
