package my.operation.domain.entity;

import java.time.ZonedDateTime;
import java.util.Set;

public class PointConfig extends AbstractAuditingEntity {

    private ZonedDateTime startDate;

    private ZonedDateTime endDate;

    private Set<StatusCategory> statusCategories;

    private Set<IssueDifficulty> issueDifficulties;

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public Set<StatusCategory> getStatusCategories() {
        return statusCategories;
    }

    public void setStatusCategories(Set<StatusCategory> statusCategories) {
        this.statusCategories = statusCategories;
    }

    public Set<IssueDifficulty> getIssueDifficulties() {
        return issueDifficulties;
    }

    public void setIssueDifficulties(Set<IssueDifficulty> issueDifficulties) {
        this.issueDifficulties = issueDifficulties;
    }

    @Override
    public String toString() {
        return "PointConfig{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", statusCategories=" + statusCategories +
                ", issueDifficulties=" + issueDifficulties +
                '}';
    }
}
