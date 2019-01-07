package my.operation.workflow.action;

import my.operation.domain.entity.*;
import my.operation.domain.entity.data.DataStorage;
import my.operation.domain.repository.RepositoryUtility;
import my.operation.workflow.Storage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class OperationImportDataExecution extends OperationAction {
    private static Logger logger = LogManager.getLogger(OperationImportDataExecution.class);
    private final String DEFAULT_PASSWORD = "root";
    private final String DEFAULT_LANGUAGE = "en";
    private final String DEFAULT_ROLE_USER = "ROLE_USER";

    @Override
    public void execute(SessionFactory sessionFactory, Storage storage) throws Exception {
        if (storage.getFilename() == null) {
            logger.error("The required file is missing");
            return;
        }

        Optional<Company> optionalCompany = RepositoryUtility.findCompanyById(sessionFactory, storage.getApplicableCompanyId());

        if (!optionalCompany.isPresent()) {
            logger.error("The company does not exist");
            return;
        }

        FileInputStream excelFile = new FileInputStream(new File(System.getProperty("user.dir"), storage.getFilename()));
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet sheet = workbook.getSheetAt(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Iterator<Row> iterator = sheet.iterator();

        List<DataStorage> dataStorages = new ArrayList<>();
        while (iterator.hasNext()) {
            DataStorage dataStorage = new DataStorage();
            Row currentRow = iterator.next();
            if (currentRow.getRowNum() == 0) {
                continue;
            }
            Iterator<Cell> cellIterator = currentRow.iterator();

            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                switch (currentCell.getColumnIndex()) {
                    case 0:
                        dataStorage.setName(currentCell.getStringCellValue());
                        break;
                    case 1:
                        dataStorage.setStaffId(currentCell.getCellType().equals(CellType.STRING) ?
                                currentCell.getStringCellValue() : String.valueOf(currentCell.getNumericCellValue()));
                        break;
                    case 2:
                        dataStorage.setDepartment(currentCell.getStringCellValue());
                        break;
                    case 3:
                        dataStorage.setIssueName(currentCell.getStringCellValue());
                        break;
                    case 4:
                        dataStorage.setDescription(currentCell.getStringCellValue());
                        break;
                    case 5:
                        dataStorage.setDifficulty(currentCell.getStringCellValue());
                        break;
                    case 6:
                        dataStorage.setStatus(currentCell.getStringCellValue());
                        break;
                    case 7:
                        dataStorage.setStartDate(sdf.format(currentCell.getDateCellValue()));
                        break;
                    case 8:
                        dataStorage.setEndDate(sdf.format(currentCell.getDateCellValue()));
                        break;
                    case 9:
                        dataStorage.setActualEndDate(sdf.format(currentCell.getDateCellValue()));
                        break;
                    default:
                        break;
                }
            }
            dataStorages.add(dataStorage);
        }
        persistUsersAndIssues(sessionFactory, dataStorages, optionalCompany.get(), storage);
        workbook.close();
        excelFile.close();
        Files.delete(Paths.get(System.getProperty("user.dir"), storage.getFilename()));
    }


    private void persistUsersAndIssues(SessionFactory sessionFactory, List<DataStorage> dataStorages,
                                  Company company, Storage storage) {
        Map<String, User> userByStaffId = new HashMap<>();
        for (DataStorage dataStorage : dataStorages) {
            User user = userByStaffId.computeIfAbsent(dataStorage.getStaffId(), key -> {
                Optional<User> optionalUser = RepositoryUtility.findUserByStaffIdAndDepartmentName(sessionFactory,
                        dataStorage.getStaffId(), dataStorage.getDepartment());
                if (!optionalUser.isPresent()) {
                    return registerUser(sessionFactory, dataStorage, company);
                } else {
                    return optionalUser.get();
                }
            });
            if (user == null) {
                continue;
            }

            user.getIssues().add(createIssue(dataStorage, user));
        }

        for (User user : userByStaffId.values()) {
            User newUser = RepositoryUtility.persistUser(sessionFactory, user);
            storage.getElasticsearchService().save(newUser);
        }
    }

    private User registerUser(SessionFactory sessionFactory, DataStorage dataStorage, Company company) {
        Department applicableDepartment = null;
        for (Department department : company.getDepartments()) {
            if (!department.getName().equalsIgnoreCase(dataStorage.getDepartment())) {
                continue;
            }
            applicableDepartment = department;
        }

        if (applicableDepartment == null) {
            logger.error("Skip due to could not find the department %s", dataStorage.getDepartment());
            return null;
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = new User();
        user.setLogin(dataStorage.getStaffId());
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        user.setFirstName(dataStorage.getName());
        user.setStaffId(dataStorage.getStaffId());
        user.setPassword(DEFAULT_LANGUAGE);
        user.setActivated(true);
        Set<Authority> authorities = new HashSet<>();
        RepositoryUtility.findAuthorityByName(sessionFactory, DEFAULT_ROLE_USER).ifPresent(authorities::add);
        user.setAuthorities(authorities);
        user.setIssues(new HashSet<>());
        user.setDepartment(applicableDepartment);
        user.setCreatedBy("operation");
        return user;
    }

    private Issue createIssue(DataStorage dataStorage, User user) {
        LocalDateTime startLDT = LocalDateTime.parse(dataStorage.getStartDate(), DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        LocalDateTime expectedLDT = LocalDateTime.parse(dataStorage.getEndDate(), DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        LocalDateTime endLDT = LocalDateTime.parse(dataStorage.getActualEndDate(), DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        Issue issue = new Issue();
        issue.setName(dataStorage.getIssueName());
        issue.setCategory(Issue.CATEGORY_DEFAULT);
        issue.setType(Issue.ISSUE_DEFAULT);
        issue.setDurationStart(startLDT.atZone(ZoneId.systemDefault()));
        issue.setExpectedDurationEnd(expectedLDT.atZone(ZoneId.systemDefault()));
        issue.setDurationEnd(endLDT.atZone(ZoneId.systemDefault()));
        issue.setDescription(dataStorage.getDescription());
        issue.setStatus(dataStorage.getStatus());
        issue.setDifficulty(dataStorage.getDifficulty());
        issue.setUser(user);
        issue.setCreatedBy("operation");
        return issue;
    }
}
