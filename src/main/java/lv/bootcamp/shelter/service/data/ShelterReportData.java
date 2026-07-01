package lv.bootcamp.shelter.service.data;

import lv.bootcamp.shelter.model.Animal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public record ShelterReportData(
        List<Animal> allAnimals,
        Set<String> uniqueSpecies,
        Map<String, List<Animal>> animalsBySpecies,
        Map<String, Long> animalCountBySpecies,
        List<String> animalsNeedingVetInput,
        Map<String, Long> vaccinatedCountBySpecies,
        Map<String, Long> unvaccinatedCountBySpecies,
        Map<String, Optional<Animal>> oldestAnimalBySpecies,
        ImportResult importResult
) { }
