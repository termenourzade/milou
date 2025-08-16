package milou.repo;

import milou.model.User;
import milou.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserRepository {
    public User findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User u where u.email = :e", User.class)
                    .setParameter("e", email)
                    .uniqueResult();
        }
    }

    public User save(User user) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            return user;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
