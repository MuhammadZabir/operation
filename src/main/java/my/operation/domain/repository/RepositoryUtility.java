package my.operation.domain.repository;

import my.operation.domain.entity.Authority;
import my.operation.domain.entity.Company;
import my.operation.domain.entity.PointConfig;
import my.operation.domain.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.Optional;

public class RepositoryUtility {

    private static Logger logger = LogManager.getLogger(RepositoryUtility.class);

    private RepositoryUtility() {

    }

    public static Optional<User> findUserById(SessionFactory sessionFactory, Long userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT u FROM User u WHERE " +
                    "u.id = :arg1", User.class)
                    .setParameter("arg1", userId)
                    .setReadOnly(true)
                    .getResultList().stream().findFirst();
        }
    }

    public static Optional<User> findUserByStaffIdAndDepartmentName(SessionFactory sessionFactory, String staffId, String departmentName) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT u FROM User u JOIN u.department d WHERE " +
                    "u.staffId = :arg1 AND " +
                    "d.name = :arg2 AND " +
                    "u.activated = :arg3", User.class)
                    .setParameter("arg1", staffId)
                    .setParameter("arg2", departmentName)
                    .setParameter("arg3", true)
                    .setReadOnly(true)
                    .getResultList().stream().findFirst();
        }
    }

    public static Optional<PointConfig> findPointConfigByCompanyId(SessionFactory sessionFactory, Long companyId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT pc FROM Company c JOIN c.pointConfig pc WHERE " +
                    "c.id = :arg1", PointConfig.class)
                    .setParameter("arg1", companyId)
                    .setReadOnly(true)
                    .getResultList().stream().findFirst();
        }
    }

    public static Optional<Company> findCompanyById(SessionFactory sessionFactory, Long companyId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT c FROM Company c WHERE " +
                    "c.id = :arg1", Company.class)
                    .setParameter("arg1", companyId)
                    .setReadOnly(true)
                    .getResultList().stream().findFirst();
        }
    }

    public static Optional<Authority> findAuthorityByName(SessionFactory sessionFactory, String name) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("SELECT a FROM Authority a WHERE " +
                    "a.name = :arg1", Authority.class)
                    .setParameter("arg1", name)
                    .setReadOnly(true)
                    .getResultList().stream().findFirst();
        }
    }

    public static User persistUser(SessionFactory sessionFactory, User user) {
        User newUser = null;
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            newUser = (User) session.merge(user);
            transaction.commit();
        }
        return newUser;
    }
}
