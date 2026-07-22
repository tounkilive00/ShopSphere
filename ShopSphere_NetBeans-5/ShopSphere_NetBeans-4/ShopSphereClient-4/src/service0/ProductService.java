package service0;
import java.rmi.Remote;
import java.rmi.RemoteException;
import model.Product;
import model.Product.Category;

public interface ProductService extends Remote {
    Product createProductRecord(Product p) throws RemoteException;
    Product updateProductRecord(Product p) throws RemoteException;
    Product deleteProductRecord(Product p) throws RemoteException;
    Product findProductRecordById(int id) throws RemoteException;
    java.util.List<Product> findAllProductRecords() throws RemoteException;
    java.util.List<Product> findAvailableProductRecords() throws RemoteException;
    java.util.List<Product> findProductRecordsByCategory(Category cat) throws RemoteException;
    java.util.List<Product> findProductRecordsByFarmer(int farmerId) throws RemoteException;
    java.util.List<Product> findProductRecordsBySeller(int sellerId) throws RemoteException;
    java.util.List<Product> searchProductRecordsByName(String keyword) throws RemoteException;
    java.util.List<Product> findByPriceRange(double min, double max) throws RemoteException;
}
