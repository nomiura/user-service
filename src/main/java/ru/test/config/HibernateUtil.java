package ru.test.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        buildSessionFactory("hibernate.cfg.xml");
    }

    public static void buildSessionFactory(String configFile) {
        try {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure(configFile)
                    .build();

            Metadata metadata = new MetadataSources(registry)
                    .buildMetadata();

            sessionFactory = metadata.buildSessionFactory();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при создании SessionFactory: " + e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void setSessionFactory(SessionFactory session) {
        sessionFactory = session;
    }
}