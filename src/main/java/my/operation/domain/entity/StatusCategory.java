package my.operation.domain.entity;

public class StatusCategory extends AbstractAuditingEntity {

    private String name;

    private Double point;

    private boolean main;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }

    public boolean isMain() {
        return main;
    }

    public void setMain(boolean main) {
        this.main = main;
    }

    @Override
    public String toString() {
        return "StatusCategory{" +
                "name='" + name + '\'' +
                ", point=" + point +
                ", main=" + main +
                '}';
    }
}
