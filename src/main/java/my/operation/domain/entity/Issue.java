package my.operation.domain.entity;

import java.time.ZonedDateTime;
import java.util.Set;

public class Issue extends AbstractAuditingEntity {

    private String name;

    private String type;

    private String category;

    private ZonedDateTime durationStart;

    private ZonedDateTime expectedDurationEnd;

    private ZonedDateTime durationEnd;

    private String description;

    private String status;

    private Set<CommentIssue> commentIssues;

    private Set<OverdueIssue> overdueIssues;

    private String difficulty;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ZonedDateTime getDurationStart() {
        return durationStart;
    }

    public void setDurationStart(ZonedDateTime durationStart) {
        this.durationStart = durationStart;
    }

    public ZonedDateTime getExpectedDurationEnd() {
        return expectedDurationEnd;
    }

    public void setExpectedDurationEnd(ZonedDateTime expectedDurationEnd) {
        this.expectedDurationEnd = expectedDurationEnd;
    }

    public ZonedDateTime getDurationEnd() {
        return durationEnd;
    }

    public void setDurationEnd(ZonedDateTime durationEnd) {
        this.durationEnd = durationEnd;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<CommentIssue> getCommentIssues() {
        return commentIssues;
    }

    public void setCommentIssues(Set<CommentIssue> commentIssues) {
        this.commentIssues = commentIssues;
    }

    public Set<OverdueIssue> getOverdueIssues() {
        return overdueIssues;
    }

    public void setOverdueIssues(Set<OverdueIssue> overdueIssues) {
        this.overdueIssues = overdueIssues;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", category='" + category + '\'' +
                ", durationStart=" + durationStart +
                ", expectedDurationEnd=" + expectedDurationEnd +
                ", durationEnd=" + durationEnd +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", commentIssues=" + commentIssues +
                ", overdueIssues=" + overdueIssues +
                ", difficulty=" + difficulty +
                '}';
    }
}
