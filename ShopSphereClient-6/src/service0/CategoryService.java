/*
 * ShopSphere - CategoryService (interface RMI)
 * Meme structure qu'AgriConnect service0/*.java
 * Corrige : model.Category n'avait ni DAO ni service — entite orpheline.
 */
package service0;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Category;

/**
 * Interface RMI du service categorie.
 * Meme convention que les autres services ShopSphere — chaque methode throws RemoteException.
 * @author ShopSphere
 */
public interface CategoryService extends Remote {

    // CRUD de base (meme convention qu'AgriConnect)
    Category createCategoryRecord(Category categoryObj)     throws RemoteException;
    Category updateCategoryRecord(Category categoryObj)     throws RemoteException;
    Category deleteCategoryRecord(Category categoryObj)     throws RemoteException;
    Category findCategoryRecordById(int id)                 throws RemoteException;
    Category findCategoryRecordBySlug(String slug)          throws RemoteException;
    List<Category> findAllCategoryRecords()                 throws RemoteException;

    // Hierarchie parent/enfant
    List<Category> findRootCategoryRecords()                throws RemoteException;
    List<Category> findCategoryRecordsByParent(int parentId) throws RemoteException;
}
