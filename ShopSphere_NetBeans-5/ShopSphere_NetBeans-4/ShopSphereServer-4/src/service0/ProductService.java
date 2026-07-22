/*
 * ShopSphere - ProductService (interface RMI)
 * Meme structure qu'AgriConnect service0/ProductService.java
 */
package service0;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Product;
import model.Product.Category;

/**
 * Interface RMI du service produit.
 * Identique a AgriConnect service0/ProductService.java — memes signatures.
 * @author ShopSphere
 */
public interface ProductService extends Remote {

    // CRUD de base (meme qu'AgriConnect)
    Product createProductRecord(Product productObj)       throws RemoteException;
    Product updateProductRecord(Product productObj)       throws RemoteException;
    Product deleteProductRecord(Product productObj)       throws RemoteException;
    Product findProductRecordById(int id)                 throws RemoteException;
    List<Product> findAllProductRecords()                 throws RemoteException;
    List<Product> findAvailableProductRecords()           throws RemoteException;

    // Filtres (meme qu'AgriConnect)
    List<Product> findProductRecordsByCategory(Category category) throws RemoteException;
    List<Product> findProductRecordsByFarmer(int farmerId)        throws RemoteException; // compat
    List<Product> findProductRecordsBySeller(int sellerId)        throws RemoteException; // alias

    // Recherche (etait searchByName dans AgriConnect)
    List<Product> searchProductRecordsByName(String keyword) throws RemoteException;

    // Nouveau ShopSphere
    List<Product> findByPriceRange(double min, double max) throws RemoteException;
}
