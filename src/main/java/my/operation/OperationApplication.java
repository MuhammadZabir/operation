package my.operation;

import my.operation.domain.service.ApplicationService;
import my.operation.domain.service.ElasticsearchService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class OperationApplication {

    private static final String PERSISTENCE_UNIT_NAME = "OperationPersistenceUnit";
    private static Logger logger = LogManager.getLogger(OperationApplication.class);
    private ApplicationContext applicationContext = new ApplicationContext();

    private OperationApplication() throws IOException {
        applicationContext.initProperties(Thread.currentThread().getContextClassLoader().getResourceAsStream("configuration.properties"));
        applicationContext.registerApplicationService(ElasticsearchService.class, new ElasticsearchService(applicationContext.getProperties()));

    }

    private void init() {
        applicationContext.getAllApplicationServices().forEach(ApplicationService::init);
    }

    public void shutdown() {
        applicationContext.getAllApplicationServices().forEach(ApplicationService::shutdown);
    }

    public static void main(String[] args) {
        try {
            OperationApplication operationApplication = new OperationApplication();
            operationApplication.init();
        } catch (Exception e) {
            logger.error("Error", e);
            System.exit(2);
        }
    }
}
