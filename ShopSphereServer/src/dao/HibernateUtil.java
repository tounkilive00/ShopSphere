/*
 * ShopSphere - HibernateUtil
 * Copie exacte d'AgriConnect dao/HibernateUtil.java
 * Fournit le singleton SessionFactory depuis hibernate.cfg.xml
 */
package dao;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Utilitaire Hibernate — identique a AgriConnect.
 * @author ShopSphere
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        try {
            Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
            cfg.addAttributeConverter(LocalDateTimeConverter.class);
            sessionFactory = cfg.buildSessionFactory();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
