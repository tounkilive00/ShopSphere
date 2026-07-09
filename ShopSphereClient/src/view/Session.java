/*
 * ShopSphere - Session
 * Gestion de la session utilisateur connecte (meme concept qu'AgriConnect)
 * Stocke l'utilisateur courant + panier en memoire
 */
package view;

import java.util.ArrayList;
import java.util.List;
import model.OrderItem;
import model.User;

/**
 * Session utilisateur courante — singleton.
 * @author ShopSphere
 */
public class Session {

    private static User currentUser = null;
    private static List<OrderItem> cart = new ArrayList<>();

    private Session() {}

    // ── Utilisateur ───────────────────────────────────────────────────────
    public static User getCurrentUser()       { return currentUser; }
    public static void setCurrentUser(User u) { currentUser = u; }
    public static void logout()               { currentUser = null; cart.clear(); }
    public static boolean isLoggedIn()        { return currentUser != null; }
    public static boolean isAdmin()           { return currentUser != null && currentUser.isAdmin(); }
    public static boolean isSeller()          { return currentUser != null && currentUser.isSeller(); }

    // ── Panier ────────────────────────────────────────────────────────────
    public static List<OrderItem> getCart()   { return cart; }

    public static void addToCart(model.Product product, int qty) {
        // Verifier si le produit est deja dans le panier
        for (OrderItem item : cart) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + qty);
                return;
            }
        }
        OrderItem item = new OrderItem(product, qty, product.getPricePerUnit());
        cart.add(item);
    }

    public static void removeFromCart(int productId) {
        cart.removeIf(item -> item.getProduct().getId() == productId);
    }

    public static void clearCart()  { cart.clear(); }

    public static int getCartCount() { return cart.size(); }

    public static double getCartTotal() {
        return cart.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }
}
