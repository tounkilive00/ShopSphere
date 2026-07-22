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
  2. Ajouter dans lib/ :
       - hibernate-core-5.x.jar (ou 6.x)
       - jakarta.persistence-api.jar
       - mysql-connector-j.jar
       - jbcrypt.jar (pour BCryptUtil)
  3. Configurer src/hibernate.cfg.xml (MySQL ou H2)
  4. Creer la base : CREATE DATABASE shopsphere_db;
  5. Clic droit > Run (ou F6)

COMPTES DE TEST (inseres automatiquement) :
  Admin   : admin@shopsphere.com  / Admin@1234
  Vendeur : marie@shopsphere.com  / Seller@1234
  Client  : jean@example.com      / Client@1234

DIFFERENCES AVEC AGRICONNECT :
  - OTP dans OtpRecord (BCrypt) au lieu de user.otpCode (String en clair)
  - BCryptUtil pour les mots de passe (etait .equals() en clair)
  - Roles CLIENT/SELLER/ADMIN (etait FARMER/BUYER/ADMIN)
  - Langue principale : Francais (fr)
  - Statuts commande enrichis (8 vs 5)
