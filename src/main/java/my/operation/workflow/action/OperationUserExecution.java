package my.operation.workflow.action;

import my.operation.domain.entity.*;
import my.operation.domain.exception.PointConfigNotFoundException;
import my.operation.domain.exception.UserNotFoundException;
import my.operation.domain.repository.RepositoryUtility;
import my.operation.domain.utility.user.UserCalculatorUtility;
import my.operation.workflow.Storage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class OperationUserExecution extends OperationAction {

    private static Logger logger = LogManager.getLogger(OperationUserExecution.class);

    @Override
    public void execute(SessionFactory sessionFactory, Storage storage) throws Exception {
        Map<String, BigDecimal> userPercentagePoint = storage.getDepartmentPercentage().get(storage.getDepartmentInProgress());
        for (Map.Entry<String, BigDecimal> entry : userPercentagePoint.entrySet()) {
            String staffId = entry.getKey();

            Optional<User> optionalUser = RepositoryUtility.findUserByStaffIdAndDepartmentName(sessionFactory, staffId, storage.getDepartmentInProgress());

            if (!optionalUser.isPresent()) {
                String errMsg = String.format("The user with the staff ID of [%s] from department [%s] could not be found.",
                        staffId, storage.getDepartmentInProgress());
                logger.error(errMsg, UserNotFoundException::new);
                throw new UserNotFoundException();
            }

            User applicableUser = optionalUser.get();
            Optional<PointConfig> optionalPointConfig = RepositoryUtility
                    .findPointConfigByCompanyId(sessionFactory, applicableUser.getDepartment().getCompany().getId());

            if (!optionalPointConfig.isPresent()) {
                String errMsg = String.format("The point config with an user ID of [%s] could not be found.", applicableUser.getId());
                logger.error(errMsg, PointConfigNotFoundException::new);
                throw new PointConfigNotFoundException();
            }

            PointConfig applicablePointConfig = optionalPointConfig.get();
            Map<String, StatusCategory> mapStatusCategoryByName = applicablePointConfig.getStatusCategories().stream()
                    .collect(Collectors.toMap(StatusCategory::getName, statusCategory -> statusCategory));
            Map<String, IssueDifficulty> mapIssueDifficultyByName = applicablePointConfig.getIssueDifficulties().stream()
                    .collect(Collectors.toMap(IssueDifficulty::getName, issueDifficulty -> issueDifficulty));

            Map<String, Set<Issue>> mapIssuesByStatus = new HashMap<>();

            for (StatusCategory statusCategory : applicablePointConfig.getStatusCategories()) {
                Set<Issue> issues = applicableUser.getIssues().stream().filter(issue -> issue.getStatus().equalsIgnoreCase(statusCategory.getName()))
                        .sorted(Comparator.comparing(Issue::getDurationStart)
                                .thenComparing(Issue::getExpectedDurationEnd)
                                .thenComparing(Issue::getDurationEnd)
                                .thenComparing(Issue::getName))
                        .collect(Collectors.toSet());

                mapIssuesByStatus.put(statusCategory.getName(), issues);
            }

            BigDecimal totalActualPoint = UserCalculatorUtility.calculateActualPoint(mapStatusCategoryByName, mapIssueDifficultyByName,
                    mapIssuesByStatus.values().stream().flatMap(issues -> issues.stream()).collect(Collectors.toSet()));

            BigDecimal totalOptimisedPoint = UserCalculatorUtility.calculateOptimumPoint(mapStatusCategoryByName, mapIssueDifficultyByName,
                    mapIssuesByStatus.values().stream().flatMap(issues -> issues.stream()).collect(Collectors.toSet()));

            BigDecimal percentageOverall = UserCalculatorUtility.calculatePercentageOverall(totalActualPoint, totalOptimisedPoint);

            userPercentagePoint.put(applicableUser.getStaffId(), percentageOverall);
        }
    }
}
