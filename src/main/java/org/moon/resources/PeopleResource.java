package org.moon.resources;

import com.google.common.collect.Maps;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.jaxrs.PATCH;
import org.moon.core.Person;
import org.moon.db.PersonDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @POST
    @Path("/test")
    @UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "create person"
    )
    public void createPersons(List<Person> persons) {
        for (Person person : persons) {
            logger.info("start to create person : " + person.getFullName());
            peopleDAO.create(person);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @PATCH
    @UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "create person by patch",
            response = Map.class
    )
    public void createPersonByPatch(@Suspended final AsyncResponse asyncResponse, final List<Person> persons) {
        asyncResponse.register((CompletionCallback) throwable -> {
            if (throwable == null) {
                for (Person person : persons) {
                    logger.info("start to create person : " + person.getFullName());
                    peopleDAO.create(person);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                logger.info("CompletionCallback-onComplete: ERROR" + throwable.getMessage());
            }
        });

        UUID uuid = UUID.randomUUID();
        Map task = Maps.newHashMap();
        task.put("taskId", uuid);
        asyncResponse.resume(task);
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

    @DELETE
    @UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "get all person"
    )
    public void deleteAllPeople() {
        for (Person person : peopleDAO.findAll()) {
            peopleDAO.delete(person);
        }
    }
}

