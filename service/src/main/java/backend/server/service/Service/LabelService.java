package backend.server.service.Service;

import backend.server.service.Literals;
import backend.server.service.POJO.PageResponse;
import backend.server.service.Repository.LabelRepository;
import backend.server.service.Repository.LogRepository;
import backend.server.service.domain.Compagnie;
import backend.server.service.domain.Label;
import backend.server.service.domain.Log;
import backend.server.service.enums.LogType;
import backend.server.service.security.POJOs.responses.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
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
public class LabelService implements ILabelService{

    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private ICompagnieService compagnieService;
    @Autowired
    private LogRepository logRepository;

    @Override
    public Label addLabel(Label label)
    {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);

            label.setCompagnie(compagnie);
            label = labelRepository.save(label);
            // Ajouter un message de log pour l'ajout du nouveau membre
            Log logMessage = Log.builder().message("Label " + label.getNom() + " ajouté.").type(LogType.CRÉER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
            logRepository.save(logMessage);
            return label;

    }
    @Override
    public void deleteLabel(Long labelId)
    {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Label label = labelRepository.findByIdAndCompagnieNom(labelId, compagnieNom);
        String labelName = label.getNom();
        labelRepository.deleteById(labelId);
        Log logMessage = Log.builder().message("Étiquete " + labelName + " retiré de la Société " + compagnieNom).type(LogType.SUPPRIMER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
    }
    @Override
    public Label updateLabel(Long labelId, String newName)
    {
        String compagnieNom = SecurityContextHolder.getContext().getAuthentication().getName();
        Compagnie compagnie = compagnieService.getCompagnie(compagnieNom);
        Label label = labelRepository.findByIdAndCompagnieNom(labelId, compagnieNom);
        label.setNom(newName);
        String labelName = label.getNom();
        label = labelRepository.save(label);
        Log logMessage = Log.builder().message("Étiquete " + labelName + " de la Société " + compagnieNom + " a été mis à jour").type(LogType.MODIFIER).date(new Date()).trigger(compagnie).compagnie(compagnie).build();
        logRepository.save(logMessage);
        return label;

    }
    @Override
    public List<Label> getAllLabels()
    {
        return labelRepository.findAll();
    }

    @Override
    public Label getLabel(Long id)
    {
        return labelRepository.findById(id).orElseThrow(()-> new RuntimeException(Literals.LABEL_NOT_FOUND));
    }

    @Override
    public PageResponse<Label> getLabelsPage(int page, int size, String sortBy, String sortOrder, String searchQuery) {
        String compagnieName = SecurityContextHolder.getContext().getAuthentication().getName();
        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Sort sort = Sort.by(direction, sortBy);
        int start = page * size;
        int end = Math.min(start + size, (int) labelRepository.count());
        List<Label> labels = labelRepository.findAllByCompagnieNom(compagnieName,sort);
        if (searchQuery != null && !searchQuery.isEmpty()){
            labels = labels.stream()
                    .filter(label -> label.getNom().toLowerCase().contains(searchQuery.toLowerCase()))
                    .collect(Collectors.toList());
        }
        List<Label> pageContent = labels.subList(start, Math.min(end, labels.size()));
        return new PageResponse<>(pageContent, labels.size());
    }
}
