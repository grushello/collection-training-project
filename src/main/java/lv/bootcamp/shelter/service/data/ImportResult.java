package lv.bootcamp.shelter.service.data;

import lv.bootcamp.shelter.model.Animal;

import java.util.ArrayList;
import java.util.List;

public record ImportResult(List<Animal> allAnimals, ArrayList<Integer> invalidLineNumbers) {

}
