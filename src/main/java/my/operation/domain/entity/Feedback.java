package my.operation.domain.entity;

public class Feedback extends AbstractAuditingEntity {

    private String subject;

    private String description;

    private Long rating;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", rating=" + rating +
                '}';
    }
}
