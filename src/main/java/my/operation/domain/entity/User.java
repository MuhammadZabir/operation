package my.operation.domain.entity;

import java.util.Set;

public class User extends AbstractAuditingEntity {

    private String username;

    private String password;

    private String name;

    private String staff_id;

    private Role role;

    private Department department;

    private Set<Issue> issues;

    private Set<Feedback> feedbacks;

    private Set<CommentIssue> commentIssues;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStaff_id() {
        return staff_id;
    }

    public void setStaff_id(String staff_id) {
        this.staff_id = staff_id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Set<Issue> getIssues() {
        return issues;
    }

    public void setIssues(Set<Issue> issues) {
        this.issues = issues;
    }

    public Set<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(Set<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public Set<CommentIssue> getCommentIssues() {
        return commentIssues;
    }

    public void setCommentIssues(Set<CommentIssue> commentIssues) {
        this.commentIssues = commentIssues;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", staff_id='" + staff_id + '\'' +
                ", role=" + role +
                ", department=" + department +
                ", issues=" + issues +
                ", feedbacks=" + feedbacks +
                ", commentIssues=" + commentIssues +
                '}';
    }
}
