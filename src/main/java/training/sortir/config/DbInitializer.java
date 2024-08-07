package training.sortir.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import training.sortir.entities.Campus;
import training.sortir.entities.City;
import training.sortir.repository.CampusRepository;
import training.sortir.repository.CityRepository;

@Component
@RequiredArgsConstructor
public class DbInitializer {
    private final CityRepository cityRepository;
    private final CampusRepository campusRepository;

    @PostConstruct
    public void initialize() {
        System.out.println("Initialisation des données...");


        if (cityRepository.count() == 0) {
            System.out.println("Ajout des villes...");

            City[] cities = new City[]{
                    new City("Paris", "75000"),
                    new City("Marseille", "13000"),
                    new City("Lyon", "69000"),
                    new City("Toulouse", "31000"),
                    new City("Nice", "06000"),
                    new City("Nantes", "44000"),
                    new City("Strasbourg", "67000"),
                    new City("Montpellier", "34000"),
                    new City("Bordeaux", "33000"),
                    new City("Lille", "59000"),
                    new City("Rennes", "35000"),
                    new City("Reims", "51100"),
                    new City("Le Havre", "76600"),
                    new City("Saint-Étienne", "42000"),
                    new City("Toulon", "83000"),
                    new City("Angers", "49000"),
                    new City("Clermont-Ferrand", "63000"),
                    new City("Le Mans", "72000"),
                    new City("Aix-en-Provence", "13090"),
                    new City("La Rochelle", "17000")
            };

            for (City city : cities) {
                cityRepository.save(city);
            }
        }

        if (campusRepository.count() == 0) {
            System.out.println("Ajout des campus...");

            Campus[] campuses = new Campus[]{
                    new Campus("Campus Lumière"),
                    new Campus("Campus Horizon"),
                    new Campus("Campus Étoile"),
                    new Campus("Campus Horizon Bleu"),
                    new Campus("Campus Verger"),
                    new Campus("Campus Zenith"),
                    new Campus("Campus Terra Nova"),
                    new Campus("Campus Albatros"),
                    new Campus("Campus Nova"),
                    new Campus("Campus Métropole"),
                    new Campus("Campus Aurora"),
                    new Campus("Campus Émeraude"),
                    new Campus("Campus Atlas"),
                    new Campus("Campus Arcadia"),
                    new Campus("Campus Solstice"),
                    new Campus("Campus Orléans"),
                    new Campus("Campus Vortex"),
                    new Campus("Campus Montaigne"),
                    new Campus("Campus Élysée"),
                    new Campus("Campus Phoenix")
            };

            for (Campus campus : campuses) {
                campusRepository.save(campus);
            }
        }
    }
}