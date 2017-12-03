package org.moon.service;

import io.dropwizard.hibernate.UnitOfWork;
import org.moon.core.Person;
import org.moon.db.PersonDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeopleService {
    private static final Logger logger = LoggerFactory.getLogger(PeopleService.class);
    private PersonDAO personDAO;

    public PeopleService(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @UnitOfWork
    public void createPerson(Person person) {
        logger.info("start to create person : " + person.getFullName());
        personDAO.create(person);
        try {
            logger.info("sleep 10 seconds");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("end to create person : " + person.getFullName());
    }
}
