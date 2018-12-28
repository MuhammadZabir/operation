package my.operation.domain.entity;

import java.time.ZonedDateTime;

public class OverdueIssue extends AbstractAuditingEntity {

    private ZonedDateTime durationStart;

    private ZonedDateTime durationEnd;

    private Issue issue;

    public ZonedDateTime getDurationStart() {
        return durationStart;
    }

    public void setDurationStart(ZonedDateTime durationStart) {
        this.durationStart = durationStart;
    }

    public ZonedDateTime getDurationEnd() {
        return durationEnd;
    }

    public void setDurationEnd(ZonedDateTime durationEnd) {
        this.durationEnd = durationEnd;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    @Override
    public String toString() {
        return "OverdueIssue{" +
                "durationStart=" + durationStart +
                ", durationEnd=" + durationEnd +
                ", issue=" + issue +
                '}';
    }
}
