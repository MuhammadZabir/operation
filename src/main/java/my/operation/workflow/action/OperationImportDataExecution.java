package my.operation.workflow.action;

import my.operation.domain.entity.Company;
import my.operation.domain.entity.data.DataStorage;
import my.operation.domain.repository.RepositoryUtility;
import my.operation.workflow.Storage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.SessionFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class OperationImportDataExecution extends OperationAction {
    private static Logger logger = LogManager.getLogger(OperationImportDataExecution.class);

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
        Iterator<Row> iterator = sheet.iterator();

        List<DataStorage> dataStorages = new ArrayList<>();
        while (iterator.hasNext()) {
            DataStorage dataStorage = new DataStorage();
            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();

            while (cellIterator.hasNext()) {
                if (currentRow.getRowNum() == 0) {
                    continue;
                }
                Cell currentCell = cellIterator.next();
                switch (currentCell.getColumnIndex()) {
                    case 0:
                        dataStorage.setName(currentCell.getStringCellValue());
                        break;
                    case 1:
                        dataStorage.setIssueName(currentCell.getStringCellValue());
                        break;
                    case 2:
                        dataStorage.setDescription(currentCell.getStringCellValue());
                        break;
                    case 3:
                        dataStorage.setDifficulty(currentCell.getStringCellValue());
                        break;
                    case 4:
                        dataStorage.setStatus(currentCell.getStringCellValue());
                        break;
                    case 5:
                        dataStorage.setStartDate(currentCell.getStringCellValue());
                        break;
                    case 6:
                        dataStorage.setEndDate(currentCell.getStringCellValue());
                        break;
                    case 7:
                        dataStorage.setActualEndDate(currentCell.getStringCellValue());
                        break;
                    default:
                        break;
                }
            }
            dataStorages.add(dataStorage);
        }
    }


    private void persistingIssues(List<DataStorage> dataStorages) {
        dataStorages.forEach(dataStorage -> );
    }
}
