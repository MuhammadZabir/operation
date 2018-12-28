package my.operation.domain.repository;

import my.operation.domain.entity.Company;
import my.operation.domain.entity.PointConfig;
import my.operation.domain.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Optional;

public class RepositoryUtility {

    private static Logger logger = LogManager.getLogger(RepositoryUtility.class);

    private RepositoryUtility() {

    }

    public static Optional<User> findUserById(SessionFactory sessionFactory, Long userId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.createQuery("SELECT u FROM User u WHERE " +
                    "u.id = :arg1", User.class)
                    .setParameter("arg1", userId)
                    .setReadOnly(true)
                    .getSingleResult());
        }
    }

    public static Optional<User> findUserByNameAndDepartmentName(SessionFactory sessionFactory, String userName, String departmentName) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.createQuery("SELECT u FROM User u JOIN Department d ON u.department.id = d.id WHERE " +
                    "u.name = :arg1 AND " +
                    "d.department.name = :arg2", User.class)
                    .setParameter("arg1", userName)
                    .setParameter("arg2", departmentName)
                    .setReadOnly(true)
                    .getSingleResult());
        }
    }

    public static Optional<PointConfig> findPointConfigByCompanyId(SessionFactory sessionFactory, Long companyId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.createQuery("SELECT pc FROM PointConfig pc WHERE " +
                    "pc.company.id = :arg1", PointConfig.class)
                    .setParameter("arg1", companyId)
                    .setReadOnly(true)
                    .getSingleResult());
        }
    }

    public static Optional<Company> findCompanyById(SessionFactory sessionFactory, Long companyId) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.createQuery("SELECT c FROM Company c WHERE " +
                    "c.id = :arg1", Company.class)
                    .setParameter("arg1", companyId)
                    .setReadOnly(true)
                    .getSingleResult());
        }
    }
}
