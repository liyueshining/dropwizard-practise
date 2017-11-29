package org.moon.resources;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.moon.core.Person;
import org.moon.db.PersonDAO;
import org.moon.views.PersonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/people")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = "dropwizard practise")
public class PersonResource {
    private static final Logger logger = LoggerFactory.getLogger(PersonResource.class);

    private final PersonDAO peopleDAO;

    public PersonResource(PersonDAO peopleDAO) {
        this.peopleDAO = peopleDAO;
    }

    @GET
    @Path("/{personId}")
    @UnitOfWork
    @ApiOperation(
            value = "get person by id",
            response = Person.class
    )
    public Person getPerson(@NotNull @PathParam("personId") LongParam personId) {
        return findSafely(personId.get());
    }

    @GET
    @Path("/test")
    @UnitOfWork
    @ApiOperation(
            value = "test",
            response = String.class
    )
    public String getTest(@HeaderParam("Access-Token") String token) {
        return token;
    }

    @GET
    @Path("/person")
    @UnitOfWork
    @ApiOperation(
            value = "find person by name",
            response = Person.class
    )
    public Person findPeopleByName(@QueryParam("name") String name) {
        logger.info("name is : " + name);
        return peopleDAO.findByName(name);
    }

    @GET
    @Path("/{personId}/view_freemarker")
    @UnitOfWork
    @Produces(MediaType.TEXT_HTML)
    public PersonView getPersonViewFreemarker(@PathParam("personId") LongParam personId) {
        return new PersonView(PersonView.Template.FREEMARKER, findSafely(personId.get()));
    }

    @GET
    @Path("/{personId}/view_mustache")
    @UnitOfWork
    @Produces(MediaType.TEXT_HTML)
    public PersonView getPersonViewMustache(@PathParam("personId") LongParam personId) {
        return new PersonView(PersonView.Template.MUSTACHE, findSafely(personId.get()));
    }

    private Person findSafely(long personId) {
        return peopleDAO.findById(personId).orElseThrow(() -> new NotFoundException("No such user."));
    }
}
