/*
 * ShopSphere - RMIClient
 * Meme structure qu'AgriConnect — connexion RMI centralisee
 * Fournit les stubs des services depuis le registre RMI serveur
 */
package view;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import service0.NotificationService;
import service0.OrderService;
import service0.ProductService;
import service0.UserService;

/**
 * Utilitaire de connexion RMI — identique a AgriConnect.
 * Lookup des services depuis le registre RMI du serveur (port 4999).
 * @author ShopSphere
 */
public class RMIClient {

    public static final String  HOST = "localhost";
    public static final int     PORT = 4999; // meme port qu'AgriConnect

    private static Registry registry;

    private static Registry getRegistry() throws Exception {
        if (registry == null) {
            registry = LocateRegistry.getRegistry(HOST, PORT);
        }
        return registry;
    }

    public static UserService getUserService() throws Exception {
        return (UserService) getRegistry().lookup("UserService");
    }

    public static ProductService getProductService() throws Exception {
        return (ProductService) getRegistry().lookup("ProductService");
    }

    public static OrderService getOrderService() throws Exception {
        return (OrderService) getRegistry().lookup("OrderService");
    }

    public static NotificationService getNotificationService() throws Exception {
        return (NotificationService) getRegistry().lookup("NotificationService");
    }
}
