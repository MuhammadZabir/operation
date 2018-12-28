package my.operation.domain.entity;

public class Achievement extends AbstractAuditingEntity {

    private String status;

    private User user;

    private Department department;

    private Criteria criteria;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public String toString() {
        return "Achievement{" +
                "status='" + status + '\'' +
                ", user=" + user +
                ", department=" + department +
                ", criteria=" + criteria +
                '}';
    }
}
