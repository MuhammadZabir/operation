package my.operation.workflow.action;

import my.operation.domain.entity.Company;
import my.operation.domain.entity.Department;
import my.operation.domain.entity.User;
import my.operation.domain.repository.RepositoryUtility;
import my.operation.workflow.Storage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class OperationDepartmentExecution extends OperationAction {

    private static Logger logger = LogManager.getLogger(OperationDepartmentExecution.class);

    @Override
    public void execute(SessionFactory sessionFactory, Storage storage) throws Exception {
        OperationUserExecution operationUserExecution = new OperationUserExecution();
        Optional<Company> optionalCompany = RepositoryUtility.findCompanyById(sessionFactory, storage.getApplicableCompanyId());

        if (!optionalCompany.isPresent()) {
            logger.error("Could not find the company with the ID [%s]", storage.getApplicableCompanyId());
            return;
        }

        Company applicableCompany = optionalCompany.get();

        storage.setDepartmentPercentage(new HashMap<>());
        Set<Department> departments = applicableCompany.getDepartments();
        departments.forEach(department -> {
            storage.getDepartmentPercentage().put(department.getName(), new HashMap<>());
            Map<String, BigDecimal> userPercentages = storage.getDepartmentPercentage().get(department.getName());
            Set<User> users = department.getUsers();
            users.forEach(user -> userPercentages.put(user.getName(), BigDecimal.ZERO));
        });

        for (Map.Entry<String, Map<String, BigDecimal>> entry : storage.getDepartmentPercentage().entrySet()) {
            storage.setDepartmentInProgress(entry.getKey());
            operationUserExecution.execute(sessionFactory, storage);
        }
    }
}
