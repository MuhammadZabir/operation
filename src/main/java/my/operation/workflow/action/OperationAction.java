package my.operation.workflow.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import my.operation.workflow.Storage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.util.Map;

public abstract class OperationAction {

    Logger logger = LogManager.getLogger(this.getClass());
    ObjectMapper objectMapper;

    OperationAction() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public abstract void execute(SessionFactory sessionFactory, Storage storage) throws Exception;
}
