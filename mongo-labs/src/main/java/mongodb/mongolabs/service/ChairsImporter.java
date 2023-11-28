package mongodb.mongolabs.service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import mongodb.mongolabs.persistence.entity.Chair;
import mongodb.mongolabs.persistence.entity.Institute;
import mongodb.mongolabs.persistence.repository.InstituteRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@DependsOn("institutesImporter")
public class ChairsImporter {
    private static final String CHAIRS_CSV_FILE_PATH = "import/Chairs.csv";

    private final InstituteRepository instituteRepository;

    public ChairsImporter(InstituteRepository instituteRepository) {
        this.instituteRepository = instituteRepository;
    }

    @PostConstruct
    public void importData() throws IOException, URISyntaxException {
        final Path filePath = Paths.get(ClassLoader.getSystemResource(CHAIRS_CSV_FILE_PATH).toURI());
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = getCsvReader(reader)) {
                final List<String[]> rows = csvReader.readAll();
                importChairsFromRows(rows);
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

    private void importChairsFromRows(final List<String[]> rows) {
        final Map<String, List<ChairImportDto>> chairsByInstitute = rows
                .stream()
                .map(row -> new ChairImportDto(row[0], row[1], row[2], row[3]))
                .collect(Collectors.groupingBy(dto -> dto.institute));
        chairsByInstitute.forEach(this::addChairsToInstitute);
    }

    private void addChairsToInstitute(final String instituteName, final List<ChairImportDto> chairImportDtos) {
        final Institute institute = instituteRepository.findByName(instituteName);
        if (institute == null) {
            return;
        }

        final List<Chair> chairs = chairImportDtos
                .stream()
                .map(chair -> new Chair(chair.name, chair.code, chair.phone))
                .toList();

        institute.getChairs().addAll(chairs);
        instituteRepository.save(institute);
    }

    private static class ChairImportDto {
        String name;
        String code;
        String phone;
        String institute;

        public ChairImportDto(String name, String code, String phone, String institute) {
            this.name = name;
            this.code = code;
            this.phone = phone;
            this.institute = institute;
        }
    }
}
