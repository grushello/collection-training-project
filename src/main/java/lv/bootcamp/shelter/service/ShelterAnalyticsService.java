package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.util.*;
import java.util.stream.Collectors;

public class ShelterAnalyticsService {

    public ShelterReportData buildReportData(ImportResult importResult) {
        List<Animal> allAnimals = importResult.allAnimals();

        Set<String> uniqueSpecies = allAnimals.stream()
                .map(a -> a.getSpecies())
                .collect(Collectors.toCollection(TreeSet::new));

        Map<String, List<Animal>> animalsBySpecies = allAnimals.stream()
                .collect(Collectors.groupingBy(Animal::getSpecies));

        List<String> animalsNeedingVetInput = allAnimals.stream()
                .filter(a -> !a.isVaccinated())
                .map(a -> a.getName() + "(" + a.getSpecies() + ")")
                .collect(Collectors.toList());

        Map<String, Long> animalCountBySpecies = new java.util.HashMap<>();

        for (Map.Entry<String, java.util.List<Animal>> entry : animalsBySpecies.entrySet()) {
            animalCountBySpecies.put(entry.getKey(), (long) entry.getValue().size());
        }

        Map<String, Long> vaccinatedCountBySpecies = allAnimals.stream()
                .filter(a -> a.isVaccinated())
                .collect(Collectors.groupingBy(
                        Animal::getSpecies,
                        Collectors.counting()
                ));
        Map<String, Long> unvaccinatedCountBySpecies = allAnimals.stream()
                .filter(a -> !a.isVaccinated())
                .collect(Collectors.groupingBy(
                        Animal::getSpecies,
                        Collectors.counting()
                ));
        Map<String, Optional<Animal>> oldestAnimalBySpecies = allAnimals.stream()
                .filter(a -> a.getAge() != null && a.getAge() > 0)
                .collect(Collectors.groupingBy(
                        Animal::getSpecies,
                        Collectors.maxBy(Comparator.comparingInt(Animal::getAge))
                ));

        return new ShelterReportData(
                allAnimals,
                uniqueSpecies,
                animalsBySpecies,
                animalCountBySpecies,
                animalsNeedingVetInput,
                vaccinatedCountBySpecies,
                unvaccinatedCountBySpecies,
                oldestAnimalBySpecies,
                importResult
        );
    }
}
