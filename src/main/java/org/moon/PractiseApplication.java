package org.moon;

import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.paradoxical.dropwizard.swagger.AppSwaggerConfiguration;
import io.paradoxical.dropwizard.swagger.bundles.SwaggerUIBundle;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.moon.core.Person;
import org.moon.db.PersonDAO;
import org.moon.resources.FileUploadResource;
import org.moon.resources.PeopleResource;
import org.moon.resources.PersonResource;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
import java.util.Map;

public class PractiseApplication extends Application<PractiseConfiguration> {

    private final HibernateBundle<PractiseConfiguration> hibernateBundle = new HibernateBundle<PractiseConfiguration>(Person.class) {
        @Override
        public DataSourceFactory getDataSourceFactory(PractiseConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    public static void main(final String[] args) throws Exception {
        new PractiseApplication().run(args);
    }

    @Override
    public String getName() {
        return "practise";
    }

    @Override
    public void initialize(final Bootstrap<PractiseConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);
        bootstrap.addBundle(new MultiPartBundle());
        bootstrap.addBundle(new AssetsBundle());

        bootstrap.addBundle(new MigrationsBundle<PractiseConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(PractiseConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });

        bootstrap.addBundle(new ViewBundle<PractiseConfiguration>() {
            @Override
            public Map<String, Map<String, String>> getViewConfiguration(PractiseConfiguration configuration) {
                return configuration.getViewRendererConfiguration();
            }
        });

        // enable swagger for application port
        bootstrap.addBundle(
                new SwaggerUIBundle(env -> {
                    return new AppSwaggerConfiguration(env) {
                        {
                            setTitle("My API");
                            setDescription("My API");

                            // The package name to look for swagger resources under
                            setResourcePackage(FileUploadResource.class.getPackage().getName());

                            setLicense("Apache 2.0");
                            setLicenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html");

                            setContact("admin@paradoxical.io");

                            setVersion("1.0");
                        }
                    };
                }));
    }

    @Override
    public void run(final PractiseConfiguration configuration,
                    final Environment environment) {
        final PersonDAO dao = new PersonDAO(hibernateBundle.getSessionFactory());
        FileUploadResource uploadResource = new FileUploadResource();

        environment.jersey().register(uploadResource);
        environment.jersey().register(new PeopleResource(dao));
        environment.jersey().register(new PersonResource(dao));

        // Enable CORS headers
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }

}
