package my.operation.domain.utility.user;

import my.operation.domain.entity.Issue;
import my.operation.domain.entity.IssueDifficulty;
import my.operation.domain.entity.StatusCategory;
import my.operation.domain.exception.MainStatusCategoryMissingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;

public class UserCalculatorUtility {

    private static Logger logger = LogManager.getLogger(UserCalculatorUtility.class);

    public static BigDecimal calculateActualPoint(Map<String, StatusCategory> statusCategoryMap,
                                           Map<String, IssueDifficulty> issueDifficultyMap,
                                           Set<Issue> applicableIssues) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Issue applicableIssue : applicableIssues) {
            StatusCategory applicableStatusCategory = statusCategoryMap.get(applicableIssue.getStatus());
            IssueDifficulty applicableIssueDifficulty = issueDifficultyMap.get(applicableIssue.getDifficulty());

            totalAmount = totalAmount.add(BigDecimal.valueOf(applicableStatusCategory.getPoint() + applicableIssueDifficulty.getValue()))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateOptimumPoint(Map<String, StatusCategory> statusCategoryMap,
                                            Map<String, IssueDifficulty> issueDifficultyMap,
                                            Set<Issue> applicableIssues) throws MainStatusCategoryMissingException {
        BigDecimal totalAmount = BigDecimal.ZERO;
        StatusCategory applicableStatusCategory = statusCategoryMap.values().stream().filter(statusCategory -> statusCategory.isMain()).findAny().orElse(null);
        if (applicableStatusCategory == null) {
            logger.error("There is no main status category set", MainStatusCategoryMissingException::new);
            throw new MainStatusCategoryMissingException();
        }

        for (Issue applicableIssue : applicableIssues) {
            IssueDifficulty applicableIssueDifficulty = issueDifficultyMap.get(applicableIssue.getDifficulty());

            totalAmount = totalAmount.add(BigDecimal.valueOf(applicableStatusCategory.getPoint() + applicableIssueDifficulty.getValue()))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return totalAmount.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculatePercentageOverall(BigDecimal actualPoint, BigDecimal optimumPoint) {
        return actualPoint.divide(optimumPoint).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
    }
}
