package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class ReportExportService {

    public void writeReport(Path outputPath, ShelterReportData reportData) {
        LocalDateTime dateTime = java.time.LocalDateTime.now();
        String timestamp = dateTime
                .format(java.time.format.DateTimeFormatter.ofPattern("dd_MM_yyyy__HH_mm_ss"));

        String fileName = outputPath.getFileName().toString();
        String newFileName = fileName.replace(".txt", "") + "_" + timestamp + ".txt";

        outputPath = outputPath.getParent().resolve(newFileName);

        try (BufferedWriter bw = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            bw.write("SHELTER REPORT\n");
            bw.write("Generated at: " + dateTime + "\n\n");

            bw.write("\nIMPORTED: " + reportData.allAnimals().size());

            bw.write("\n\nUNIQUE SPECIES:\n");
            for (String species : reportData.uniqueSpecies()) {
                bw.write(" - " + species + "\n");
            }
            bw.write("\n\nANIMAL COUNT BY SPECIES:\n");
            bw.write(formatCountMap(reportData.animalCountBySpecies()));
            bw.write("\n\nVACCINATED COUNT BY SPECIES:\n");
            bw.write(formatCountMap(reportData.vaccinatedCountBySpecies()));
            bw.write("\n\nUNVACCINATED COUNT BY SPECIES:\n");
            bw.write(formatCountMap(reportData.unvaccinatedCountBySpecies()));
            bw.write("\n\nANIMALS NEEDING VET INPUT:\n");
            for (String animal : reportData.animalsNeedingVetInput()) {
                bw.write(" - " + animal + "\n");
            }
            bw.write("\n\nOLDEST ANIMAL BY SPECIES:\n");
            for (Map.Entry<String, Optional<Animal>> entry :
                    reportData.oldestAnimalBySpecies().entrySet()) {

                bw.write(formatOldest(entry.getKey(), entry.getValue()) + "\n");
            }
            bw.write("\nSKIPPED: " + reportData.importResult().invalidLineNumbers().size() + "\n\n");
            bw.write("SKIPPED LINES:\n");
            bw.write(formatInvalidLines(reportData.importResult().invalidLineNumbers()));
        }
        catch (IOException e){
            log.error("Failed to write to file {}", outputPath);
        }
    }
    private String formatCountMap(Map<String, Long> map) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, Long> entry : map.entrySet()) {
            sb.append(" - ")
                    .append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }

        return sb.toString();
    }

    private String formatOldest(String species, Optional<Animal> animal){
        return "Species: " + species + " -> " + (animal.isEmpty()? "None" :
                animal.get().getName() + "(" + animal.get().getAge() + ")");
    }
    private String formatInvalidLines(ArrayList<Integer> lines) {
        StringBuilder sb = new StringBuilder();
        for (Integer line : lines) {
            sb.append(line).append(" ");
        }
        return sb.toString();
    }
}
