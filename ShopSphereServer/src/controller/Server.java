/*
 * ShopSphere - Server (point d'entree RMI)
 * Meme structure qu'AgriConnect controller/Server.java
 * Demarre le registre RMI et lie tous les services
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
import model.Category;
import model.Notification;
import model.Notification.TypeNotif;
import model.Product;
import model.Product.ProductStatus;
import model.User;
import model.User.AccountStatus;
import model.User.Role;
import service.implimentation.BCryptUtil;
import service.implimentation.NotificationServiceImpl;
import service.implimentation.OrderServiceImpl;
import service.implimentation.ProductServiceImpl;
import service.implimentation.UserServiceImpl;

/**
 * Point d'entree du serveur ShopSphere.
 * Identique a AgriConnect controller/Server.java :
 *   - Meme port RMI (4999)
 *   - Meme pattern : registry.rebind(nom, implementation)
 *   - Meme initialisation des donnees de test
 * @author ShopSphere
 */
public class Server {

    // Port RMI — meme que AgriConnect (4999)
    public static final int RMI_PORT = 4999;

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║         ShopSphere Server - Demarrage            ║");
        System.out.println("╠══════════════════════════════════════════════════╣");
        System.out.println("║  Port RMI : " + RMI_PORT + "                                  ║");
        System.out.println("║  Base     : shopsphere_db (PostgreSQL)           ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        try {
            // ── Creer le registre RMI — meme qu'AgriConnect ──────────────
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            System.out.println("[SERVER] Registre RMI cree sur le port " + RMI_PORT);

            // ── Instancier les implementations — meme pattern qu'AgriConnect ──
            UserServiceImpl         userService   = new UserServiceImpl();
            ProductServiceImpl      productService = new ProductServiceImpl();
            OrderServiceImpl        orderService  = new OrderServiceImpl();
            NotificationServiceImpl notifService  = new NotificationServiceImpl();

            // ── Lier les services au registre — meme qu'AgriConnect ──────
            registry.rebind("UserService",         userService);
            registry.rebind("ProductService",      productService);
            registry.rebind("OrderService",        orderService);
            registry.rebind("NotificationService", notifService);

            System.out.println("[SERVER] Services lies au registre RMI :");
            System.out.println("  -> UserService");
            System.out.println("  -> ProductService");
            System.out.println("  -> OrderService");
            System.out.println("  -> NotificationService");

            // ── Initialiser les donnees de test ───────────────────────────
            initTestData();

            // ── Nettoyage OTP expires (toutes les 15 min) ─────────────────
            Timer otpTimer = new Timer(true);
            otpTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    new OtpRecordDao().deleteExpired();
                    System.out.println("[SERVER] Nettoyage OTP expires effectue.");
                }
            }, 0, 15 * 60 * 1000L);

            System.out.println("[SERVER] ShopSphere pret — en attente de connexions clients...");

        } catch (Exception ex) {
            System.err.println("[SERVER] Erreur demarrage : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Insere les donnees de test au demarrage.
     * Meme role que DataInitializer dans AgriConnect.
     */
    private static void initTestData() {
        UserDao userDao         = new UserDao();
        ProductDao productDao   = new ProductDao();
        NotificationDao notifDao = new NotificationDao();

        // Ne rien faire si des donnees existent deja
        if (!userDao.findAll().isEmpty()) {
            System.out.println("[SERVER] Donnees existantes detectees — initialisation ignoree.");
            return;
        }

        System.out.println("[SERVER] Insertion des donnees de test...");

        // ── Admin ─────────────────────────────────────────────────────────
        User admin = new User();
        admin.setFullName("Admin ShopSphere");
        admin.setEmail("admin@shopsphere.com");
        admin.setPasswordHash(BCryptUtil.hash("Admin@1234"));
        admin.setRole(Role.ADMIN);
        admin.setStatus(AccountStatus.ACTIVE);
        admin.setEmailVerified(true);
        admin.setPreferredLanguage("fr");
        userDao.createUser(admin);

        // ── Vendeur ───────────────────────────────────────────────────────
        User seller = new User();
        seller.setFullName("Marie Dupont");
        seller.setEmail("marie@shopsphere.com");
        seller.setPasswordHash(BCryptUtil.hash("Seller@1234"));
        seller.setRole(Role.SELLER);
        seller.setStatus(AccountStatus.ACTIVE);
        seller.setEmailVerified(true);
        seller.setPhone("+33612345678");
        seller.setPhoneVerified(true);
        seller.setPreferredLanguage("fr");
        userDao.createUser(seller);

        // ── Client ────────────────────────────────────────────────────────
        User client = new User();
        client.setFullName("Jean Martin");
        client.setEmail("jean@example.com");
        client.setPasswordHash(BCryptUtil.hash("Client@1234"));
        client.setRole(Role.CLIENT);
        client.setStatus(AccountStatus.ACTIVE);
        client.setEmailVerified(true);
        client.setPreferredLanguage("fr");
        userDao.createUser(client);

        // ── Produits ──────────────────────────────────────────────────────
        Product p1 = new Product("Robe d'ete fleurie",
                Product.Category.MODE, 25, 49.99,
                "Robe legere pour l'ete, disponible en plusieurs tailles.", seller);
        p1.setSalePrice(34.99);
        p1.setBrand("MarieStyle");
        p1.setSku("MS-ROBE-001");
        p1.setStatus(ProductStatus.ACTIF);
        productDao.createProduct(p1);

        Product p2 = new Product("Ecouteurs Bluetooth Pro",
                Product.Category.ELECTRONIQUE, 50, 89.99,
                "Son haute qualite, autonomie 30h, etui de charge inclus.", seller);
        p2.setBrand("SoundPro");
        p2.setSku("SP-ECO-BT-001");
        p2.setStatus(ProductStatus.ACTIF);
        productDao.createProduct(p2);

        Product p3 = new Product("Lampe LED bureau reglable",
                Product.Category.MAISON, 15, 35.00,
                "Eclairage LED 3 modes, chargeur USB integre.", seller);
        p3.setBrand("LumiereHome");
        p3.setSku("LH-LAMP-001");
        p3.setStatus(ProductStatus.ACTIF);
        productDao.createProduct(p3);

        Product p4 = new Product("Montre connectee sport GPS",
                Product.Category.SPORT, 30, 129.99,
                "GPS integre, etanche 50m, 15 modes sport, 7 jours d'autonomie.", seller);
        p4.setSalePrice(99.99);
        p4.setBrand("FitTech");
        p4.setSku("FT-WATCH-S1");
        p4.setStatus(ProductStatus.ACTIF);
        productDao.createProduct(p4);

        // ── Notification de bienvenue ──────────────────────────────────────
        notifDao.createNotification(new Notification(client,
                "Bienvenue sur ShopSphere !",
                "Decouvrez notre catalogue et profitez de nos offres exclusives.",
                TypeNotif.ANNONCE));

        System.out.println("[SERVER] Donnees de test inserees avec succes :");
        System.out.println("  Admin   : admin@shopsphere.com / Admin@1234");
        System.out.println("  Vendeur : marie@shopsphere.com / Seller@1234");
        System.out.println("  Client  : jean@example.com    / Client@1234");
    }
}
