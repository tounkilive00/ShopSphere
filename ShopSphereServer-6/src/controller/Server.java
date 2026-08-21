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
            System.out.println("==============================================");
            System.out.println("  Initialisation de ShopSphere Server...");
            System.out.println("==============================================");

            // 1. Warm up Hibernate SessionFactory in background for instant response
            new Thread(() -> {
                try {
                    HibernateUtil.getSessionFactory();
                    System.out.println("✔ Hibernate SessionFactory prêt.");
                    ensureAdminUser();
                } catch (Exception e) {
                    System.err.println("⚠ Hibernate pre-warm warning: " + e.getMessage());
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

            System.out.println("✔ Services RMI enregistrés avec succès.");
            System.out.println("==============================================");
            System.out.println("🚀 Serveur RMI ShopSphere démarré sur le port " + RMI_PORT);
            System.out.println("==============================================");

        } catch (Exception ex) {
            System.err.println("Server failed to start: " + ex.getMessage());
            ex.printStackTrace();
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
                System.out.println("✔ Compte Admin initialisé : " + adminEmail + " / " + adminPassword);
            } else {
                existingUser.setRole(User.Role.ADMIN);
                existingUser.setStatus(User.AccountStatus.ACTIVE);
                existingUser.setEmailVerified(true);
                existingUser.setPasswordHash(PasswordUtil.hash(adminPassword));
                userDao.updateUser(existingUser);
                System.out.println("✔ Compte Admin mis à jour : " + adminEmail);
            }
        } catch (Exception e) {
            System.err.println("⚠ Impossible d'assurer le compte Admin: " + e.getMessage());
        }
    }
}
