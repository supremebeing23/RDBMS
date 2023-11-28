package mongodb.mongolabs.api;

import mongodb.mongolabs.persistence.entity.Chair;
import mongodb.mongolabs.persistence.entity.Institute;
import mongodb.mongolabs.persistence.entity.Speciality;
import mongodb.mongolabs.persistence.repository.InstituteRepository;
import mongodb.mongolabs.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/institutes")
public class InstituteController {

    private final InstituteRepository instituteRepository;
    private final SearchService searchService;

    public InstituteController(InstituteRepository instituteRepository, SearchService searchService) {
        this.instituteRepository = instituteRepository;
        this.searchService = searchService;
    }

    @GetMapping
    public List<Institute> getInstitutes() {
        return instituteRepository.findAll();
    }

    @GetMapping("/chairs")
    public Set<Chair> getChairs() {
        return instituteRepository.findAll()
                .stream()
                .map(Institute::getChairs)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @GetMapping("/specialities")
    public Set<Speciality> getSpecialities() {
        return instituteRepository.findAll()
                .stream()
                .map(Institute::getChairs)
                .flatMap(Collection::stream)
                .map(Chair::getSpecialities)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @GetMapping("/search")
    public Institute getInstitute(@RequestParam final String name) {
        return instituteRepository.findByName(name);
    }

    @GetMapping("/search-speciality")
    public List<Institute> getInstitutesBySpeciality(@RequestParam final String specialityName) {
        return searchService.findInstituteBySpeciality(specialityName);
    }
}
