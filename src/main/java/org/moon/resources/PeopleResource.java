package org.moon.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.moon.core.Person;
import org.moon.db.PersonDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/people")
@Api(value = "dropwizard practise")
public class PeopleResource {
    private static final Logger logger = LoggerFactory.getLogger(PeopleResource.class);

    private final PersonDAO peopleDAO;

    public PeopleResource(PersonDAO peopleDAO) {
        this.peopleDAO = peopleDAO;
    }

    @POST
    @UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "create person",
            response = Person.class
    )
    public Person createPerson(Person person) {
        return peopleDAO.create(person);
    }

    @GET
    @UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "get all person",
            response = List.class
    )
    public List<Person> listPeople() {
        return peopleDAO.findAll();
    }
}

