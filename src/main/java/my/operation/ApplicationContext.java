package my.operation;

import my.operation.domain.service.ApplicationService;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ApplicationContext {

    private final Map<Class<?>, ApplicationService> applicationServiceMap = new HashMap<>();
    private final Properties properties = new Properties();

    public <E extends ApplicationService> void registerApplicationService(Class<E> key, E applicationService) {
        applicationServiceMap.put(key, applicationService);
    }

    public <E> E getApplicationService(Class<E> key) {
        return key.cast(applicationServiceMap.get(key));
    }

    public List<ApplicationService> getAllApplicationServices() {
        return new ArrayList<>(Collections.unmodifiableCollection(applicationServiceMap.values()));
    }

    public Properties getProperties() {
        return properties;
    }

    public void initProperties(InputStream inputStream) throws IOException {
        properties.load(inputStream);
    }

    @Override
    public String toString() {
        return "ApplicationContext{" +
                "applicationServiceMap=" + applicationServiceMap +
                ", properties=" + properties +
                '}';
    }
}
