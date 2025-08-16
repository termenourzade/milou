package milou.repo;

import milou.model.Email;
import milou.model.EmailRecipient;
import milou.model.EmailRecipientId;
import milou.model.User;
import milou.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class EmailRepository {
    public Email findByCode(String code) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Email e join fetch e.sender where e.code = :c", Email.class)
                    .setParameter("c", code)
                    .uniqueResult();
        }
    }

    public Email save(Email email) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            s.persist(email);
            tx.commit();
            return email;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void addRecipient(Email email, User user) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            Email managedEmail = s.get(Email.class, email.getId());
            User managedUser = s.get(User.class, user.getId());
            EmailRecipient er = new EmailRecipient(managedEmail, managedUser);
            s.persist(er);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public List<Object[]> listAllForUser(User user) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "select e.sender.email, e.subject, e.code, e.sentAt " +
                    "from EmailRecipient er join er.email e " +
                    "where er.user.id = :uid order by e.sentAt desc", Object[].class)
                .setParameter("uid", user.getId())
                .list();
        }
    }

    public List<Object[]> listUnreadForUser(User user) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "select e.sender.email, e.subject, e.code, e.sentAt " +
                    "from EmailRecipient er join er.email e " +
                    "where er.user.id = :uid and er.read = false order by e.sentAt desc",
                    Object[].class)
                .setParameter("uid", user.getId())
                .list();
        }
    }

    public List<Object[]> listSentByUser(User user) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "select e.id, e.subject, e.code, e.sentAt " +
                    "from Email e where e.sender.id = :uid order by e.sentAt desc", Object[].class)
                .setParameter("uid", user.getId())
                .list();
        }
    }

    public List<String> recipientsForEmail(Integer emailId) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery(
                    "select er.user.email from EmailRecipient er where er.email.id = :eid order by er.user.email",
                    String.class)
                .setParameter("eid", emailId)
                .list();
        }
    }

    public void markRead(Integer emailId, Integer userId) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            EmailRecipient er = s.get(EmailRecipient.class, new EmailRecipientId(emailId, userId));
            if (er != null && !er.isRead()) {
                er.markRead();
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
