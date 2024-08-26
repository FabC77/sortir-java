package training.sortir.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import training.sortir.entities.Campus;
import training.sortir.entities.City;
import training.sortir.entities.Location;
import training.sortir.entities.User;
import training.sortir.repository.CampusRepository;
import training.sortir.repository.CityRepository;
import training.sortir.repository.LocationRepository;
import training.sortir.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DbInitializer {
    private final CityRepository cityRepository;
    private final CampusRepository campusRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;
    @PostConstruct
    public void initialize() {
        System.out.println("Initialisation des données...");
        if (userRepository.count() == 0) {
            User[] users = new User[]{
                    new User("fab",
                            "Fabien",
                            "C",
                            "fab@gmail.com",
                            passwordEncoder.encode("1234"),
                            1
                    ),
                    new User("bob", "Bob","Morane","bob@gmail.com",passwordEncoder.encode("123"),2)
            };
            for (User user : users) {
                userRepository.save(user);
            }
        }

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
         /*  if (locationRepository.count() == 0) {
            System.out.println("Ajout des lieux...");

          Location[] locations = new Location[]{
                    new Location("Centre Ville", "12 Rue de la Paix", cityRepository.findById(12L).orElseThrow()),
                    new Location("Parc Naturel", "45 Avenue des Champs", cityRepository.findById(1L).orElseThrow()),
                    new Location("Quartier Historique", "78 Boulevard Saint-Germain", cityRepository.findById(2L).orElseThrow()),
                    new Location("Zone Industrielle", "101 Rue de Bercy", cityRepository.findById(3L).orElseThrow()),
                    new Location("Port de Commerce", "202 Quai de la Tournelle", cityRepository.findById(4L).orElseThrow()),
                    new Location("Zone Résidentielle", "303 Rue du Faubourg Saint-Antoine", cityRepository.findById(5L).orElseThrow()),
                    new Location("Secteur Technologique", "404 Rue de la Gare", cityRepository.findById(6L).orElseThrow()),
                    new Location("Campus Universitaire", "505 Avenue de l'Université", cityRepository.findById(7L).orElseThrow()),
                    new Location("Place Centrale", "606 Place des Vosges", cityRepository.findById(8L).orElseThrow()),
                    new Location("Quartier Maritime", "707 Boulevard du Port", cityRepository.findById(9L).orElseThrow()),
                    new Location("Zone Artisanale", "808 Rue des Artisans", cityRepository.findById(10L).orElseThrow()),
                    new Location("Aire de Loisirs", "909 Avenue des Loisirs", cityRepository.findById(13L).orElseThrow()),
                    new Location("Ensemble Résidentiel", "1010 Rue des Résidents", cityRepository.findById(1L).orElseThrow()),
                    new Location("District Commercial", "1111 Boulevard Commercial", cityRepository.findById(2L).orElseThrow()),
                    new Location("Espace Culturel", "1212 Rue de la Culture", cityRepository.findById(3L).orElseThrow()),
                    new Location("Réserve Naturelle", "1313 Avenue de la Nature", cityRepository.findById(4L).orElseThrow()),
                    new Location("Centre Sportif", "1414 Rue des Sports", cityRepository.findById(5L).orElseThrow()),
                    new Location("Pôle Santé", "1515 Boulevard de la Santé", cityRepository.findById(6L).orElseThrow()),
                    new Location("Village Traditionnel", "1616 Place du Village", cityRepository.findById(7L).orElseThrow()),
                    new Location("Zone Touristique", "1717 Avenue Touristique", cityRepository.findById(8L).orElseThrow())
            };
            for (Location location : locations) {
                locationRepository.save(location);
            }

        }*/
    }
}