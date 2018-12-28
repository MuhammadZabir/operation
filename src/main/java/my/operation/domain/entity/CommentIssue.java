package my.operation.domain.entity;

public class CommentIssue extends AbstractAuditingEntity {

    private String comment;

    private User user;

    private Issue issue;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    @Override
    public String toString() {
        return "CommentIssue{" +
                "comment='" + comment + '\'' +
                ", user=" + user +
                ", issue=" + issue +
                '}';
    }
}
