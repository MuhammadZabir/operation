package my.operation;

import my.operation.domain.service.ApplicationService;
import my.operation.domain.service.ElasticsearchService;
import my.operation.workflow.KafkaJobReceiver;
import my.operation.workflow.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OperationApplication {

    private static final String PERSISTENCE_UNIT_NAME = "OperationPersistenceUnit";
    private static Logger logger = LogManager.getLogger(OperationApplication.class);
    private ApplicationContext applicationContext = new ApplicationContext();
    private ExecutorService executorService;

    private OperationApplication() throws IOException {
        applicationContext.initProperties(Thread.currentThread().getContextClassLoader().getResourceAsStream("configuration.properties"));

        applicationContext.registerService(KafkaJobReceiver.class, new KafkaJobReceiver(applicationContext));
        applicationContext.registerApplicationService(ElasticsearchService.class, new ElasticsearchService(applicationContext.getProperties()));

        executorService = Executors.newFixedThreadPool(applicationContext.getAllServices().size());
    }

    private void init() {
        applicationContext.getAllApplicationServices().forEach(ApplicationService::init);
        applicationContext.getAllServices().forEach(service -> executorService.submit(service));
    }

    public void shutdown() {
        applicationContext.getAllApplicationServices().forEach(ApplicationService::shutdown);
        applicationContext.getAllServices().forEach(Service::shutdown);

        executorService.shutdown();
        try {
            executorService.awaitTermination(5000L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("Error", e);
            Thread.currentThread().interrupt();
        }
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
