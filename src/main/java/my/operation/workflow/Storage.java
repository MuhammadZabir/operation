package my.operation.workflow;

import java.math.BigDecimal;
import java.util.Map;

public class Storage {
    private Map<String, Map<String, BigDecimal>> departmentPercentage;

    private Long applicableCompanyId;

    private String departmentInProgress;

    private String filename;

    public Map<String, Map<String, BigDecimal>> getDepartmentPercentage() {
        return departmentPercentage;
    }

    public void setDepartmentPercentage(Map<String, Map<String, BigDecimal>> departmentPercentage) {
        this.departmentPercentage = departmentPercentage;
    }

    public Long getApplicableCompanyId() {
        return applicableCompanyId;
    }

    public void setApplicableCompanyId(Long applicableCompanyId) {
        this.applicableCompanyId = applicableCompanyId;
    }

    public String getDepartmentInProgress() {
        return departmentInProgress;
    }

    public void setDepartmentInProgress(String departmentInProgress) {
        this.departmentInProgress = departmentInProgress;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "Storage{" +
                "departmentPercentage=" + departmentPercentage +
                ", applicableCompanyId=" + applicableCompanyId +
                ", departmentInProgress='" + departmentInProgress + '\'' +
                ", filename='" + filename + '\'' +
                '}';
    }
}
