package milou.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {
    private static SessionFactory sessionFactory;

    private HibernateUtil() {}

    public static void setUpSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        }
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) setUpSessionFactory();
        return sessionFactory;
    }

    public static void closeSessionFactory() {
        if (sessionFactory != null)
            sessionFactory.close();
    }
}
