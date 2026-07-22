/*
 * ShopSphere - Server (point d'entree RMI)
 * Meme structure simplifiee qu'AgriConnect controller/Server.java
 */
package controller;

import dao.NotificationDao;
import dao.OtpRecordDao;
import dao.ProductDao;
import dao.UserDao;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;
import model.Notification;
import model.Product;
import model.User;
import service.implimentation.BCryptUtil;
import service.implimentation.NotificationServiceImpl;
import service.implimentation.OrderServiceImpl;
import service.implimentation.ProductServiceImpl;
import service.implimentation.UserServiceImpl;

public class Server {

    private static final int RMI_PORT = 4999;

    public static void main(String[] args) {
        try {
            // Instancier les services
            UserServiceImpl         userService    = new UserServiceImpl();
            ProductServiceImpl      productService = new ProductServiceImpl();
            OrderServiceImpl        orderService   = new OrderServiceImpl();
            NotificationServiceImpl notifService   = new NotificationServiceImpl();

            // Creer le registre RMI
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);

            // Lier les services
            registry.rebind("UserService",         userService);
            registry.rebind("ProductService",      productService);
            registry.rebind("OrderService",        orderService);
            registry.rebind("NotificationService", notifService);

            System.out.println("==================================================");
            System.out.println("  ShopSphere RMI Server started on port " + RMI_PORT);
            System.out.println("==================================================");

            // Initialiser les donnees de test si nécessaire
            initTestData();

            // Nettoyage OTP expires (toutes les 15 min)
            new Timer(true).scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    new OtpRecordDao().deleteExpired();
                }
            }, 0, 15 * 60 * 1000L);

        } catch (Exception ex) {
            System.err.println("Server failed to start: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void initTestData() {
        UserDao userDao = new UserDao();
        if (!userDao.findAll().isEmpty()) return;

        System.out.println("[SERVER] Insertion des donnees de test...");

        // Admin
        User admin = new User();
        admin.setFullName("Admin ShopSphere");
        admin.setEmail("admin@shopsphere.com");
        admin.setPasswordHash(BCryptUtil.hash("Admin@1234"));
        admin.setRole(User.Role.ADMIN);
        admin.setStatus(User.AccountStatus.ACTIVE);
        admin.setEmailVerified(true);
        userDao.createUser(admin);

        // Vendeur
        User seller = new User();
        seller.setFullName("Marie Dupont");
        seller.setEmail("marie@shopsphere.com");
        seller.setPasswordHash(BCryptUtil.hash("Seller@1234"));
        seller.setRole(User.Role.SELLER);
        seller.setStatus(User.AccountStatus.ACTIVE);
        seller.setEmailVerified(true);
        userDao.createUser(seller);

        // Client
        User client = new User();
        client.setFullName("Jean Martin");
        client.setEmail("jean@example.com");
        client.setPasswordHash(BCryptUtil.hash("Client@1234"));
        client.setRole(User.Role.CLIENT);
        client.setStatus(User.AccountStatus.ACTIVE);
        client.setEmailVerified(true);
        userDao.createUser(client);

        // Produits
        ProductDao productDao = new ProductDao();
        productDao.createProduct(new Product("Robe d'ete fleurie", Product.Category.MODE, 25, 49.99, "Robe legere.", seller));
        productDao.createProduct(new Product("Ecouteurs Bluetooth Pro", Product.Category.ELECTRONIQUE, 50, 89.99, "Son haute qualite.", seller));

        System.out.println("  Admin   : admin@shopsphere.com / Admin@1234");
        System.out.println("  Vendeur : marie@shopsphere.com / Seller@1234");
        System.out.println("  Client  : jean@example.com    / Client@1234");
    }
}
