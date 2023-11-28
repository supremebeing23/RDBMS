package mongodb.mongolabs.service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import mongodb.mongolabs.persistence.entity.Institute;
import mongodb.mongolabs.persistence.repository.InstituteRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class InstitutesImporter {
    public static final String INSTITUTES_CSV_FILE_PATH = "import/Institutes.csv";


    private final InstituteRepository instituteRepository;

    public InstitutesImporter(InstituteRepository instituteRepository) {
        this.instituteRepository = instituteRepository;
    }

    @PostConstruct
    public void importData() throws IOException, URISyntaxException {
        final Path filePath = Paths.get(ClassLoader.getSystemResource(INSTITUTES_CSV_FILE_PATH).toURI());
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReader csvReader = getCsvReader(reader)) {
                final List<String[]> rows = csvReader.readAll();
                importInstitutesFromRows(rows);
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

    private void importInstitutesFromRows(final List<String[]> rows) {
        final List<Institute> institutes = rows.stream()
                .map(row -> row[0])
                .filter(instituteName -> !instituteExists(instituteName))
                .map(Institute::new)
                .toList();
        instituteRepository.saveAll(institutes);
    }

    private boolean instituteExists(final String instituteName) {
        return instituteRepository.findByName(instituteName) != null;
    }
}
