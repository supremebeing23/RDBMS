package mongodb.mongolabs.service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import mongodb.mongolabs.persistence.entity.Chair;
import mongodb.mongolabs.persistence.entity.Institute;
import mongodb.mongolabs.persistence.entity.Speciality;
import mongodb.mongolabs.persistence.repository.InstituteRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@DependsOn("chairsImporter")
public class SpecialitiesImporter {
    private static final String SPECIALITIES_CSV_FILE_PATH = "import/Specialties.csv";

    private final MongoTemplate mongoTemplate;
    private final InstituteRepository instituteRepository;

    public SpecialitiesImporter(InstituteRepository instituteRepository, MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.instituteRepository = instituteRepository;
    }

    @PostConstruct
    public void importData() throws IOException, URISyntaxException {
        final Path filePath = Paths.get(ClassLoader.getSystemResource(SPECIALITIES_CSV_FILE_PATH).toURI());
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = getCsvReader(reader)) {
                final List<String[]> rows = csvReader.readAll();
                importSpecialitiesFromRows(rows);
            }
        }
    }

    private CSVReader getCsvReader(final Reader reader) {
        final CSVParser parser = getParser();
        return new CSVReaderBuilder(reader)
                .withSkipLines(1)
                .withCSVParser(parser)
                .build();
    }

    private CSVParser getParser() {
        return new CSVParserBuilder()
                .withSeparator(';')
                .withIgnoreQuotations(true)
                .build();
    }

    private void importSpecialitiesFromRows(final List<String[]> rows) {
        final Map<String, List<SpecialityImportDto>> chairsByInstitute = rows
                .stream()
                .map(row -> new SpecialityImportDto(row[0], row[1], row[2]))
                .collect(Collectors.groupingBy(dto -> dto.chair));
        chairsByInstitute.forEach(this::addSpecialitiesToChairs);
    }

    private void addSpecialitiesToChairs(final String chairName, List<SpecialityImportDto> specialityImportDtos) {
        final Institute institute = findInstituteByChair(chairName);
        if (institute == null) {
            return;
        }

        final Optional<Chair> chairOptional = institute.getChairs().stream()
                .filter(chair -> chair.getName().equals(chairName))
                .findFirst();
        if (chairOptional.isEmpty()) {
            return;
        }

        addSpecialitiesToChair(chairOptional.get(), specialityImportDtos);
        instituteRepository.save(institute);
    }

    private Institute findInstituteByChair(final String chair) {
        final Query query = Query.query(Criteria.where("chairs.name").is(chair));
        return mongoTemplate.findOne(query, Institute.class);
    }

    private void addSpecialitiesToChair(final Chair chair, final List<SpecialityImportDto> specialityImportDtos) {
        final List<Speciality> specialities = specialityImportDtos
                .stream()
                .map(speciality -> new Speciality(speciality.code, speciality.name))
                .toList();
        chair.getSpecialities().addAll(specialities);
    }

    private static class SpecialityImportDto {
        String code;
        String name;
        String chair;

        public SpecialityImportDto(String code, String name, String chair) {
            this.code = code;
            this.name = name;
            this.chair = chair;
        }
    }
}
