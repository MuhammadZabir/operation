package my.operation.workflow;

import my.operation.ApplicationContext;
import my.operation.domain.kafka.OperationMessage;
import my.operation.domain.service.ElasticsearchService;
import my.operation.workflow.action.OperationDepartmentExecution;
import my.operation.workflow.action.OperationImportDataExecution;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaJobReceiver implements Service {

    private Logger logger = LogManager.getLogger(this.getClass());
    private AtomicBoolean shutdown = new AtomicBoolean(false);
    private ApplicationContext applicationContext;

    public KafkaJobReceiver(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void shutdown() {
        shutdown.set(true);
    }

    @Override
    public void run() {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", applicationContext.getProperties().getProperty("kafka.bootstrap.server"));
        properties.setProperty("key.deserializer", StringDeserializer.class.getName());
        properties.setProperty("value.deserializer", OperationMessage.OperationMessageDeserializer.class.getName());
        properties.setProperty("group.id", applicationContext.getProperties().getProperty("operation.kafka.adhoc.group"));

        try (KafkaConsumer<String, OperationMessage> kafkaConsumer = new KafkaConsumer<>(properties)) {
            kafkaConsumer.subscribe(Collections.singletonList(applicationContext.getProperties().getProperty("operation.kafka.adhoc.topic")));
            System.out.println("Kafka receiver is running");

            while (!shutdown.get()) {
                ConsumerRecords<String, OperationMessage> records = kafkaConsumer.poll(5000L);
                for (ConsumerRecord<String, OperationMessage> record : records) {
                    OperationMessage message = record.value();

                    Storage storage = new Storage();
                    storage.setApplicableCompanyId(message.getId());
                    storage.setFilename(message.getFilename());
                    storage.setElasticsearchService(applicationContext.getApplicationService(ElasticsearchService.class));

                    Thread thread = new Thread(() -> execute(storage));
                    thread.start();
                }
            }
        }
    }

    private void execute(Storage storage) {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        try (SessionFactory sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory()) {
            OperationImportDataExecution operationImportDataExecution = new OperationImportDataExecution();
            operationImportDataExecution.execute(sessionFactory, storage);
            OperationDepartmentExecution operationDepartmentExecution = new OperationDepartmentExecution();
            operationDepartmentExecution.execute(sessionFactory, storage);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("There is an error,", e);
        }
    }
}
