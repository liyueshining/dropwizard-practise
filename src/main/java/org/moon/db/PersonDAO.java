package org.moon.db;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.moon.core.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class PersonDAO extends AbstractDAO<Person> {
    private static final Logger logger = LoggerFactory.getLogger(PersonDAO.class);

    public PersonDAO(SessionFactory factory) {
        super(factory);
    }

    public Optional<Person> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    public Person create(Person person) {
        logger.info("persist object with dao");
        return persist(person);
    }

    public List<Person> findAll() {
        return list(namedQuery("org.moon.core.Person.findAll"));
    }

    public Person findByName(String name) {
        logger.info("name is " + name);

        Query query = namedQuery("org.moon.core.Person.findByName");
        query.setParameter("name", name);
        logger.info(query.toString());

        return uniqueResult(query);
    }

    public void delete(Person person) {
        this.currentSession().delete(person);
    }
}
