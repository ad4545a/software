package com.jewellery.erp.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class AppConfig {

    private static SessionFactory sessionFactory;
    private static final String DB_URL = ConfigLoader.get("db.url");
    private static final String DB_USER = ConfigLoader.get("db.user");
    private static final String DB_PASS = ConfigLoader.get("db.password");

    public static void initialize() {
        System.out.println("Initializing Application...");
        setupDatabase();
    }

    private static void setupDatabase() {
        try {
            // 1. Initialize Schema & Default Data via JDBC Helper
            com.jewellery.erp.db.DatabaseInitializer.initialize();

            // 2. Setup Hibernate
            Configuration configuration = new Configuration();
            configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
            configuration.setProperty("hibernate.connection.url", DB_URL);
            configuration.setProperty("hibernate.connection.username", DB_USER);
            configuration.setProperty("hibernate.connection.password", DB_PASS);
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            configuration.setProperty("hibernate.show_sql", "true");
            configuration.setProperty("hibernate.hbm2ddl.auto", "validate"); // Validate schema created by Initializer
            configuration.setProperty("hibernate.current_session_context_class", "thread");

            // Add Annotated Classes
            configuration.addAnnotatedClass(com.jewellery.erp.model.User.class);
            configuration.addAnnotatedClass(com.jewellery.erp.model.LoginAudit.class);

            sessionFactory = configuration.buildSessionFactory();
            System.out.println("Database Connection Established & Hibernate Configured.");

        } catch (Exception e) {
            System.err.println("Database Initialization Failed: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database Initialization Failed", e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
