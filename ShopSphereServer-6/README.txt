ShopSphere - Serveur RMI
========================
Inspire de AgriConnectServer26748 (meme structure exacte)

STRUCTURE DES PACKAGES (identique a AgriConnect) :
  src/model/           - Entites (User, Product, Order, OrderItem, OtpRecord, Notification, Category)
  src/dao/             - DAOs Hibernate (HibernateUtil, UserDao, ProductDao, OrderDao...)
  src/service0/        - Interfaces RMI (UserService, ProductService, OrderService, NotificationService)
  src/service/implimentation/ - Implementations RMI (UserServiceImpl, ProductServiceImpl...)
  src/controller/      - Serveur principal (Server.java - demarre le registre RMI port 4999)

DEMARRAGE DANS NETBEANS :
  1. File > Open Project > selectionner ShopSphereServer/
  2. Toutes les librairies necessaires sont deja dans lib/ :
       - Hibernate 4.3.x (hibernate-core, hibernate-entitymanager, hibernate-c3p0,
         hibernate-ehcache, antlr, c3p0, dom4j, ehcache-core, javassist,
         jboss-logging, jboss-transaction-api, mchange-commons, slf4j-api, slf4j-simple)
       - Persistence JPA2.1 (hibernate-jpa-2.1-api)
       - postgresql-42.7.10.jar
       - JDK 1.8 (Default)
     Aucune autre dependance n'est requise — le hashage des mots de passe
     (PasswordUtil) est implemente en PBKDF2 100% JDK, sans jbcrypt.
  3. Configurer src/hibernate.cfg.xml (PostgreSQL — url/utilisateur/mot de passe)
  4. Creer la base : CREATE DATABASE shopsphere_db;
  5. Clic droit > Run (ou F6)

COMPTES DE TEST (inseres automatiquement) :
  Admin   : admin@shopsphere.com  / Admin@1234
  Vendeur : marie@shopsphere.com  / Seller@1234
  Client  : jean@example.com      / Client@1234

DIFFERENCES AVEC AGRICONNECT :
  - OTP dans OtpRecord (hash PBKDF2) au lieu de user.otpCode (String en clair)
  - PasswordUtil (PBKDF2-HMAC-SHA256, JDK natif) pour les mots de passe
    (etait .equals() en clair)
  - Roles CLIENT/SELLER/ADMIN (etait FARMER/BUYER/ADMIN)
  - Langue principale : Francais (fr)
  - Statuts commande enrichis (8 vs 5)
