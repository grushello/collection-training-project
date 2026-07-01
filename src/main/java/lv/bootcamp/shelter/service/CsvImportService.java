package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvImportService {

    public ImportResult importAnimals(Path inputPath) {
        log.info("Starting import from {}", inputPath);

        List<Animal> allAnimals = new ArrayList<>();
        ArrayList<Integer> invalidLineNumbers = new ArrayList<Integer>();

        try (BufferedReader br = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)){
            String line;
            br.readLine();
            int currentLineNumber = 1;

            while ((line = br.readLine()) != null){
                currentLineNumber++;
                String[] parts = line.split(",");

                if(!hasRequiredFields(parts)){
                    invalidLineNumbers.add(currentLineNumber);
                    log.warn("skipping malformed row {}. line: {}", line, currentLineNumber);
                    continue;
                }

                try {
                    String name = parts[0].trim();
                    String species = parts[1].trim();
                    Integer age = parseAge(parts[2], line);
                    boolean vaccinated = parseVaccinated(parts[3], line);
                    LocalDate intakeDate = parseIntakeDate(parts[4], line);

                    allAnimals.add(new Animal(name, species, age, vaccinated, intakeDate));
                }
                catch(IllegalArgumentException e){
                    invalidLineNumbers.add(currentLineNumber);
                    log.warn("skipping malformed row {}. line: {}", line, currentLineNumber);
                }
            }
        }
        catch(IOException e){
            log.error("Failed to read file {}", inputPath);
        }

        return new ImportResult(allAnimals, invalidLineNumbers);
    }

    // allowing age to be blank
    private boolean hasRequiredFields(String[] arr) {
        return arr.length == 5 &&
                !arr[0].isBlank() &&
                !arr[1].isBlank() &&
                !arr[3].isBlank() &&
                !arr[4].isBlank();
    }

    private boolean parseVaccinated(String val, String line) {
        if (val == null) {
            log.warn("Missing vaccinated value in row: {}", line);
            throw new IllegalArgumentException();
        }

        String value = val.trim().toLowerCase();

        if (value.equals("true") || value.equals("false")) {
            return Boolean.parseBoolean(value);
        }

        log.warn("Invalid vaccinated value '{}' in row: {}", val, line);
        throw new IllegalArgumentException("Invalid boolean");
    }

    private Integer parseAge(String val, String line) {
        if (val == null || val.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            log.warn("Invalid age value '{}' in row: {}", val, line);
            throw new IllegalArgumentException("Invalid age");
        }
    }

    private LocalDate parseIntakeDate(String val, String line) {
        if (val == null || val.trim().isEmpty()) {
            log.warn("Missing intakeDate value in row: {}", line);
            throw new IllegalArgumentException("Missing intakeDate");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        try {
            return LocalDate.parse(val.trim(), formatter);
        } catch (DateTimeParseException e) {
            log.warn("Invalid intakeDate '{}' in row: {}", val, line);
            throw new IllegalArgumentException("Invalid intakeDate");
        }
    }
}
