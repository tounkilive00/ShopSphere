/*
 * ShopSphere - RMI Server Entry Point
 */
package controller;

import dao.HibernateUtil;
import dao.UserDao;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import model.User;
import service.implimentation.CategoryServiceImpl;
import service.implimentation.NotificationServiceImpl;
import service.implimentation.OrderServiceImpl;
import service.implimentation.PasswordUtil;
import service.implimentation.ProductServiceImpl;
import service.implimentation.UserServiceImpl;

/**
 * RMI Server – starts the registry and binds all service implementations.
 * Pre-warms Hibernate SessionFactory for instant query response time.
 * Auto-creates default admin account if not present.
 *
 * @author ShopSphere
 */
public class Server {

    private static final int RMI_PORT = 4999;

    public static void main(String[] args) {
        try {
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
            // Silence Hibernate, JBoss, SLF4J, and PostgreSQL logging noise
            System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "off");
            System.setProperty("org.jboss.logging.provider", "jdk");
            
            java.util.logging.LogManager.getLogManager().reset();
            java.util.logging.Logger globalLogger = java.util.logging.Logger.getLogger("");
            globalLogger.setLevel(java.util.logging.Level.OFF);
            for (java.util.logging.Handler handler : globalLogger.getHandlers()) {
                globalLogger.removeHandler(handler);
            }

            // 1. Warm up Hibernate SessionFactory in background for instant response
            new Thread(() -> {
                try {
                    HibernateUtil.getSessionFactory();
                    ensureAdminUser();
                } catch (Exception e) {
                    // silent
                }
            }).start();

            // 2. Create and bind service implementations
            UserServiceImpl         userService     = new UserServiceImpl();
            ProductServiceImpl      productService  = new ProductServiceImpl();
            OrderServiceImpl        orderService    = new OrderServiceImpl();
            NotificationServiceImpl notifService    = new NotificationServiceImpl();
            CategoryServiceImpl     categoryService = new CategoryServiceImpl();

            // 3. Create or locate existing RMI registry on port 4999
            Registry registry;
            try {
                registry = LocateRegistry.createRegistry(RMI_PORT);
            } catch (Exception e) {
                // If registry already exists, locate and re-use it
                registry = LocateRegistry.getRegistry(RMI_PORT);
            }

            registry.rebind("UserService",         userService);
            registry.rebind("ProductService",      productService);
            registry.rebind("OrderService",        orderService);
            registry.rebind("NotificationService", notifService);
            registry.rebind("CategoryService",     categoryService);

            System.out.println("==================================================");
            System.out.println("   ShopSphere RMI Server started on port " + RMI_PORT);
            System.out.println("==================================================");

        } catch (Exception ex) {
            System.err.println("Server failed to start: " + ex.getMessage());
        }
    }

    private static void ensureAdminUser() {
        try {
            UserDao userDao = new UserDao();
            String adminEmail = "madybaba20@gmail.com";
            String adminPassword = "Tounkarababa201";

            User existingUser = userDao.findUserByEmail(adminEmail);
            if (existingUser == null) {
                User admin = new User();
                admin.setFullName("Mady Baba Admin");
                admin.setEmail(adminEmail);
                admin.setPasswordHash(PasswordUtil.hash(adminPassword));
                admin.setRole(User.Role.ADMIN);
                admin.setStatus(User.AccountStatus.ACTIVE);
                admin.setEmailVerified(true);
                admin.setPhoneVerified(true);
                admin.setPreferredLanguage("fr");
                userDao.createUser(admin);
            } else {
                existingUser.setRole(User.Role.ADMIN);
                existingUser.setStatus(User.AccountStatus.ACTIVE);
                existingUser.setEmailVerified(true);
                existingUser.setPasswordHash(PasswordUtil.hash(adminPassword));
                userDao.updateUser(existingUser);
            }
        } catch (Exception e) {
            // silent
        }
    }
}
