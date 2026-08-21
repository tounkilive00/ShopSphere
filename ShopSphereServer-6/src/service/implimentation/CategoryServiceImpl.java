/*
 * ShopSphere - CategoryServiceImpl
 * Meme structure qu'AgriConnect service/implimentation/*.java
 * extends UnicastRemoteObject implements service0.CategoryService
 */
package service.implimentation;

import dao.CategoryDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Category;
import service0.CategoryService;

/**
 * Implementation RMI du service categorie.
 * Meme structure qu'AgriConnect *ServiceImpl — extends UnicastRemoteObject.
 * @author ShopSphere
 */
public class CategoryServiceImpl extends UnicastRemoteObject implements CategoryService {

    private final CategoryDao categoryDao = new CategoryDao();

    public CategoryServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public Category createCategoryRecord(Category categoryObj) throws RemoteException {
        return categoryDao.createCategory(categoryObj);
    }

    @Override
    public Category updateCategoryRecord(Category categoryObj) throws RemoteException {
        return categoryDao.updateCategory(categoryObj);
    }

    @Override
    public Category deleteCategoryRecord(Category categoryObj) throws RemoteException {
        return categoryDao.deleteCategory(categoryObj);
    }

    @Override
    public Category findCategoryRecordById(int id) throws RemoteException {
        return categoryDao.findCategoryById(id);
    }

    @Override
    public Category findCategoryRecordBySlug(String slug) throws RemoteException {
        return categoryDao.findBySlug(slug);
    }

    @Override
    public List<Category> findAllCategoryRecords() throws RemoteException {
        return categoryDao.findAll();
    }

    @Override
    public List<Category> findRootCategoryRecords() throws RemoteException {
        return categoryDao.findRootCategories();
    }

    @Override
    public List<Category> findCategoryRecordsByParent(int parentId) throws RemoteException {
        return categoryDao.findByParent(parentId);
    }
}
