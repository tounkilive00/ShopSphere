/*
 * ShopSphere - ProductServiceImpl
 * Meme structure qu'AgriConnect service/implimentation/ProductServiceImpl.java
 * extends UnicastRemoteObject implements service0.ProductService
 */
package service.implimentation;

import dao.ProductDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import model.Product;
import model.Product.Category;
import service0.ProductService;

/**
 * Implementation RMI du service produit.
 * Meme structure qu'AgriConnect ProductServiceImpl.
 * @author ShopSphere
 */
public class ProductServiceImpl extends UnicastRemoteObject implements ProductService {

    private final ProductDao productDao = new ProductDao();

    public ProductServiceImpl() throws RemoteException {
        super();
    }

    /** Etait createProductRecord() dans AgriConnect */
    @Override
    public Product createProductRecord(Product productObj) throws RemoteException {
        if (productObj.getSeller() == null) {
            throw new RemoteException("Un produit doit avoir un vendeur.");
        }
        return productDao.createProduct(productObj);
    }

    /** Etait updateProductRecord() dans AgriConnect */
    @Override
    public Product updateProductRecord(Product productObj) throws RemoteException {
        return productDao.updateProduct(productObj);
    }

    /** Etait deleteProductRecord() dans AgriConnect — soft delete vers ARCHIVE */
    @Override
    public Product deleteProductRecord(Product productObj) throws RemoteException {
        return productDao.deleteProduct(productObj); // Archive — ne supprime pas physiquement
    }

    /** Etait findProductRecordById() dans AgriConnect */
    @Override
    public Product findProductRecordById(int id) throws RemoteException {
        return productDao.findProductById(id);
    }

    /** Etait findAllProductRecords() dans AgriConnect */
    @Override
    public List<Product> findAllProductRecords() throws RemoteException {
        return productDao.findAll();
    }

    /** Etait findAvailableProductRecords() dans AgriConnect */
    @Override
    public List<Product> findAvailableProductRecords() throws RemoteException {
        return productDao.findActiveProducts();
    }

    /** Etait findProductRecordsByCategory() dans AgriConnect */
    @Override
    public List<Product> findProductRecordsByCategory(Category category) throws RemoteException {
        return productDao.findByCategory(category);
    }

    /** Etait findProductRecordsByFarmer() dans AgriConnect */
    @Override
    public List<Product> findProductRecordsByFarmer(int farmerId) throws RemoteException {
        return productDao.findBySeller(farmerId);
    }

    @Override
    public List<Product> findProductRecordsBySeller(int sellerId) throws RemoteException {
        return productDao.findBySeller(sellerId);
    }

    /** Etait searchProductRecordsByName() dans AgriConnect */
    @Override
    public List<Product> searchProductRecordsByName(String keyword) throws RemoteException {
        return productDao.searchByName(keyword);
    }

    @Override
    public List<Product> findByPriceRange(double min, double max) throws RemoteException {
        return productDao.findByPriceRange(min, max);
    }
}
