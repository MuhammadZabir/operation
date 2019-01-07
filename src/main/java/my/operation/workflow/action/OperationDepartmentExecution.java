package my.operation.workflow.action;

import my.operation.domain.entity.Company;
import my.operation.domain.entity.Department;
import my.operation.domain.entity.User;
import my.operation.domain.repository.RepositoryUtility;
import my.operation.workflow.Storage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.SessionFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OperationDepartmentExecution extends OperationAction {

    private static Logger logger = LogManager.getLogger(OperationDepartmentExecution.class);

    @Override
    public void execute(SessionFactory sessionFactory, Storage storage) throws Exception {
        OperationUserExecution operationUserExecution = new OperationUserExecution();
        Optional<Company> optionalCompany = RepositoryUtility.findCompanyById(sessionFactory, storage.getApplicableCompanyId());

        if (!optionalCompany.isPresent()) {
            logger.error("Could not find the company with the ID [%s]", storage.getApplicableCompanyId());
            return;
        }

        Company applicableCompany = optionalCompany.get();

        storage.setDepartmentPercentage(new HashMap<>());
        Set<Department> departments = applicableCompany.getDepartments();
        departments.forEach(department -> {
            storage.getDepartmentPercentage().put(department.getName(), new HashMap<>());
            Map<String, BigDecimal> userPercentages = storage.getDepartmentPercentage().get(department.getName());
            Set<User> users = department.getUser().stream().filter(User::isActivated).collect(Collectors.toSet());
            users.forEach(user -> userPercentages.put(user.getStaffId(), BigDecimal.ZERO));
        });

        for (Map.Entry<String, Map<String, BigDecimal>> entry : storage.getDepartmentPercentage().entrySet()) {
            storage.setDepartmentInProgress(entry.getKey());
            operationUserExecution.execute(sessionFactory, storage);
        }

        generateExcel(storage, applicableCompany);
    }


    private void generateExcel(Storage storage, Company company) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet departmentSheet = workbook.createSheet("Department");

        int rowNum = 0;
        int colNum = 0;
        Row departmentHeaderRow = departmentSheet.createRow(rowNum++);
        Cell departmentNameHeader = departmentHeaderRow.createCell(colNum++);
        departmentNameHeader.setCellValue("Name");
        Cell departmentPercentageHeader = departmentHeaderRow.createCell(colNum);
        departmentPercentageHeader.setCellValue("Overall Score (%)");
        for (Map.Entry<String, Map<String, BigDecimal>> entry : storage.getDepartmentPercentage().entrySet()) {
            Row row = departmentSheet.createRow(rowNum++);
            colNum = 0;
            BigDecimal totalValue = BigDecimal.ZERO;
            BigDecimal optimalValue = BigDecimal.ZERO;
            for (BigDecimal value : entry.getValue().values()) {
                totalValue = totalValue.add(value);
                optimalValue = optimalValue.add(new BigDecimal(100));
            }
            BigDecimal totalPercentage = totalValue.divide(optimalValue, 2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP);
            Cell name = row.createCell(colNum++);
            name.setCellValue(entry.getKey());
            Cell percentage = row.createCell(colNum);
            percentage.setCellValue(totalPercentage.doubleValue());
        }

        XSSFSheet employeeSheet = workbook.createSheet("Employee");

        rowNum = 0;
        colNum = 0;
        Row employeeHeaderRow = employeeSheet.createRow(rowNum++);
        Cell employeeStaffIdHeader = employeeHeaderRow.createCell(colNum++);
        employeeStaffIdHeader.setCellValue("Staff Id");
        Cell employeePercentageHeader = employeeHeaderRow.createCell(colNum);
        employeePercentageHeader.setCellValue("Overall Score (%)");
        for (Map<String, BigDecimal> department : storage.getDepartmentPercentage().values()) {
            for (Map.Entry<String, BigDecimal> entry : department.entrySet()) {
                Row row = employeeSheet.createRow(rowNum++);
                colNum = 0;
                Cell staffId = row.createCell(colNum++);
                staffId.setCellValue(entry.getKey());
                Cell percentage = row.createCell(colNum);
                percentage.setCellValue(entry.getValue().doubleValue());
            }
        }

        if (Files.exists(Paths.get(System.getProperty("user.dir"), company.getName() + ".xlsx"))) {
            Files.delete(Paths.get(System.getProperty("user.dir"), company.getName() + ".xlsx"));
        }

        FileOutputStream outputStream = new FileOutputStream(System.getProperty("user.dir") +
                "/" + company.getName() + ".xlsx");

        workbook.write(outputStream);
        workbook.close();
    }
}
