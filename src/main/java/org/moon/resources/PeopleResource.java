package org.moon.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.moon.core.Person;
import org.moon.db.PersonDAO;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "dropwizard practise")
public class PeopleResource {

    private final PersonDAO peopleDAO;

    public PeopleResource(PersonDAO peopleDAO) {
        this.peopleDAO = peopleDAO;
    }

    @POST
    @UnitOfWork
    @ApiOperation(
            value = "create person",
            response = Person.class
    )
    public Person createPerson(Person person) {
        return peopleDAO.create(person);
    }

    @GET
    @UnitOfWork
    @ApiOperation(
            value = "get all person",
            response = List.class
    )
    public List<Person> listPeople() {
        return peopleDAO.findAll();
    }

}

