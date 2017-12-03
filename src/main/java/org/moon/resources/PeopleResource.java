package org.moon.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.jaxrs.PATCH;
import org.moon.core.Person;
import org.moon.db.PersonDAO;
import org.moon.service.PeopleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Path("/people")
@Api(value = "dropwizard practise")
public class PeopleResource {
    private static final Logger logger = LoggerFactory.getLogger(PeopleResource.class);

    private final PersonDAO peopleDAO;
    private final PeopleService peopleService;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    /*private Future<?> futureResult;*/

    public PeopleResource(PersonDAO peopleDAO, PeopleService peopleService) {
        this.peopleDAO = peopleDAO;
        this.peopleService = peopleService;
    }

    @PreDestroy
    public void onDestroy() {
        this.executor.shutdownNow();
    }

    /*@PostConstruct
    public void onCreate() {
        this.executor = Executors.newSingleThreadExecutor();
    }*/

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
    //@UnitOfWork
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "create person by patch",
            response = Map.class
    )
    public Map createPersonByPatch(final Person person) {

        UUID uuid = UUID.randomUUID();
        Map task = Maps.newHashMap();
        task.put("taskId", uuid);

        executor.submit(() -> peopleService.createPerson(person));

        return task;
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
        Instant start = Instant.now();
        for (Person person : peopleDAO.findAll()) {
            peopleDAO.delete(person);
        }

        Instant end = Instant.now();

        long duration = Duration.between(start, end).getSeconds();

        logger.info("time duration is: " + duration);
    }
}

