package backend.server.service.Service;

import backend.server.service.POJO.PageResponse;
import backend.server.service.domain.*;
import backend.server.service.payloads.*;

import java.util.List;

public interface ICompagnieService {

    Compagnie getCompagnie(Long id);
    Compagnie getCompagnie(String nom);
    List<Log> getLogs();
    List<Compagnie> getAllCompagnies();
    Compagnie createCompagnie(Compagnie compagnie);
    Compagnie updateCompagnie(Compagnie compagnie);
    void deleteCompagnie(Long id);
    void deleteCompagnie(String nom);
    Groupe createGroupe(String nom, double quota);
    Groupe createGroupe(String nom, double quota, Long CompagnieId);
    void deleteGroupe(String nom);
    Groupe updateGroupe(Long groupeId, String newName);
    void deleteMembre(Long membreId, String username);
    Membre updateMembre(Membre membre);
    List<String> getAllUniqueGroups();
    EntitiesCountResponse getEntitiesCount();
    QuotaUsedToday getQuotaUsedToday();
    List<String> getAllLabels();
    List<String> getAllCategories();
    ConsumptionHistoryChart getQuotaUsedByDay();
    List<GroupConsumption> getAllGroupsConsumption();
    CompagnieName getCompagnieName();

    PageResponse<Compagnie> getCompagniesPage(int page, int size, String sortBy, String sortOrder, String searchQuery);

    void updateCompagnieQuota(Long id, String name, Double quota);
}
