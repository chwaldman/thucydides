package net.thucydides.core.fixtureservices;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Load any implementations of the FixtureService class declared on the classpath.
 * FixtureService implementations must be declared in a file called net.thucydides.core.fixtureservices.FixtureService
 * in the META-INF/services directory somewhere on the classpath.
 */
public class ClasspathFixtureProviderService implements FixtureProviderService {

    @Override
    public List<FixtureService> getFixtureServices() {
        List<FixtureService> fixtureServices = new ArrayList<FixtureService>();

        ServiceLoader<FixtureService> fixtureServiceLoader = ServiceLoader.load(FixtureService.class);

        for (FixtureService fixtureService : fixtureServiceLoader) {
            fixtureServices.add(fixtureService);
        }

        return fixtureServices;
    }
}
