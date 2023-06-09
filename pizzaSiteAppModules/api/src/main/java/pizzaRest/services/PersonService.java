package pizzaRest.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pizzaRest.models.Person;
import pizzaRest.models.Pizza;
import pizzaRest.repositiries.PersonRepository;
import pizzaRest.repositiries.PizzaRepository;
import pizzaRest.util.NotFoundException;



import java.util.List;
import java.util.Optional;

/**
 * @author Sergii Bugaienko
 */

@Service
@Transactional(readOnly = true)
public class PersonService {
    static final Logger logger = LoggerFactory.getLogger(PersonService.class);
    private final PersonRepository personRepository;
    private final PizzaRepository pizzaRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PersonService(PersonRepository personRepository, PizzaRepository pizzaRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.pizzaRepository = pizzaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Person> findUserByUsername(String username) {
        return personRepository.findByUsername(username);
    }

    public Optional<Person> findUserByEmail(String email) {
        return personRepository.findByEmail(email);
    }

    @Transactional
    public Person register(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        enrichPerson(person);
        logger.info("Add new Person, personId={}", person.getUsername());
        return personRepository.save(person);
    }

    private void enrichPerson(Person person) {
        person.setRole("ROLE_USER");
//        person.setCreatedAt(LocalDateTime.now());
//        person.setUpdatedAt(LocalDateTime.now());
//        person.setCreatedWho("ADMIN");
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    @Transactional
    public void addPizzaToFav(Person person, Pizza pizza) {
//        List<Pizza> favorites2 = person.getFavorites();
        List<Pizza> favorites = pizzaRepository.findByPersons(person);

        if (!favorites.contains(pizza)) {
            favorites.add(pizza);
        }
        person.setFavorites(favorites);
        personRepository.save(person);
        logger.info("Add Fav pizza{} to Person, personId={}",pizza.getId(), person.getId());
    }

    @Transactional
    public void removePizzaFromFav(Person person, Pizza pizza) {
        List<Pizza> favorites = pizzaRepository.findByPersons(person);
        favorites.remove(pizza);
        person.setFavorites(favorites);

        personRepository.save(person);
        logger.info("Del pizza{} from Person id={} Fav", pizza.getId(), person.getId());
    }

    @Transactional
    public void save(Person person){
        personRepository.save(person);
    }

    public Person findOne(int personId) {
        return personRepository.findById(personId).orElseThrow(NotFoundException::new);
    }
}
