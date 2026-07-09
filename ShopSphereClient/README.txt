ShopSphere - Client Swing RMI
==============================
Inspire de AgriConnectClient26748 (meme structure exacte)

STRUCTURE DES PACKAGES (identique a AgriConnect) :
  src/model/                - Stubs serialisables (User, Product, Order, OrderItem, Notification)
  src/service0/             - Interfaces RMI identiques au serveur
  src/view/theme/           - Theme.java : palette 3 couleurs + typographie
  src/view/components/      - AppTextField, AppPasswordField, PrimaryButton, AccentButton,
                              SecondaryButton, AppComboBox, ProductCard, NavBar
  src/view/                 - JFrames : UserLogin, RegisterUser, MarketPlace, ProductDetail,
                              AddEditProduct, CartView, OtpVerification, OrderHistory,
                              SellerDashboard, AdminPanel, UserProfile
                              + RMIClient.java, Session.java

DEMARRAGE DANS NETBEANS :
  1. Demarrer ShopSphereServer AVANT le client
  2. File > Open Project > selectionner ShopSphereClient/
  3. Ajouter ShopSphereServer.jar dans lib/ (pour les interfaces RMI)
  4. Clic droit > Run (ou F6)
  5. La fenetre de connexion s'ouvre

COMPTES DE TEST :
  Admin   : admin@shopsphere.com  / Admin@1234  -> ouvre AdminPanel
  Vendeur : marie@shopsphere.com  / Seller@1234 -> ouvre MarketPlace + dashboard vendeur
  Client  : jean@example.com      / Client@1234 -> ouvre MarketPlace

ECRANS DISPONIBLES :
  UserLogin        - Connexion (BCrypt cote serveur)
  RegisterUser     - Inscription avec envoi OTP email
  OtpVerification  - Saisie code OTP 6 chiffres (10 min, 3 tentatives max)
  MarketPlace      - Catalogue produits grille style Amazon/Temu
  ProductDetail    - Fiche produit complete
  AddEditProduct   - Creer/modifier un produit (SELLER/ADMIN)
  CartView         - Panier avec total et validation commande
  OrderHistory     - Historique et depenses client
  SellerDashboard  - Tableau de bord vendeur (produits + ventes)
  AdminPanel       - Gestion utilisateurs, produits, commandes (ADMIN)
  UserProfile      - Profil utilisateur + deconnexion

COULEURS (3 maximum selon SRS) :
  #1A3C5E - Bleu Marine Profond (primaire)
  #E8A020 - Or Ambre            (accent)
  #F0F4F8 - Gris-Bleu Doux     (neutre/fond)
