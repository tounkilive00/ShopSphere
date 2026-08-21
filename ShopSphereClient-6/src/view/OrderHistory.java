/*
 * ShopSphere - OrderHistory
 * Historique des commandes et depenses du client
 * Meme structure qu'AgriConnect view/OrderUser.java
 */
package view;

import java.awt.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import model.Order;
import model.User;
import service0.OrderService;
import view.components.*;
import view.theme.Theme;

public class OrderHistory extends JFrame {

    private OrderService orderService;

    private final User currentUser;
    private JTable     ordersTable;
    private JLabel     totalLabel;

    public OrderHistory(User user) {
        this.currentUser = user;
        initComponents();
        connectToServer();
        buildUI();
        loadOrders();
    }

    /**
     * Connexion RMI — identique a AgriConnect view/OrderUser.java :
     * LocateRegistry.getRegistry(host, port) + reg.lookup("OrderService").
     */
    private void connectToServer() {
        try {
            Registry reg = LocateRegistry.getRegistry("127.0.0.1", 4999);
            this.orderService = (OrderService) reg.lookup("OrderService");
        } catch (Exception e) {
            System.err.println("Server connection failed: " + e.getMessage());
        }
    }

    private void buildUI() {
        setTitle("ShopSphere — Historique de mes commandes");
        setSize(820, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.NEUTRAL);
        setLayout(new BorderLayout());

        // Header — design fourni par view.components.PageHeader (reutilisable)
        PageHeader header = new PageHeader("Historique de mes commandes");
        SecondaryButton backBtn = new SecondaryButton("← Mon profil");
        backBtn.addActionListener(e -> AppNavigator.show(new UserProfile(currentUser)));
        header.addAction(backBtn);
        add(header, BorderLayout.NORTH);

        // Tableau
        String[] cols = {"#", "Date", "Statut", "Articles", "Total (FCFA)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        ordersTable = new JTable(model);
        ordersTable.setFont(Theme.FONT_BODY);
        ordersTable.setRowHeight(30);
        ordersTable.setGridColor(Theme.LIGHT_GREY);
        ordersTable.getTableHeader().setFont(Theme.FONT_HEADING);
        ordersTable.getTableHeader().setBackground(Theme.PRIMARY);
        ordersTable.getTableHeader().setForeground(Theme.WHITE);
        ordersTable.setSelectionBackground(Theme.NEUTRAL);
        ordersTable.setSelectionForeground(Theme.DARK_TEXT);

        // Colorer les statuts
        ordersTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                if (col == 2 && v != null) {
                    setForeground(StatusBadge.colorForOrderStatus(v.toString()));
                } else {
                    setForeground(Theme.DARK_TEXT);
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(ordersTable);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // Pied de page
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Theme.WHITE);
        footer.setBorder(new EmptyBorder(12, 20, 12, 20));
        totalLabel = new JLabel("Total depense : Chargement...");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(Theme.PRIMARY);
        footer.add(totalLabel, BorderLayout.WEST);
        add(footer, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void loadOrders() {
        SwingWorker<List<Order>, Void> worker = new SwingWorker<List<Order>, Void>() {
            @Override protected List<Order> doInBackground() throws Exception {
                if (orderService == null) { connectToServer(); }
                if (orderService == null) {
                    throw new Exception("Cannot connect to server.");
                }
                return orderService.findOrderRecordsByBuyer(currentUser.getId());
            }
            @Override protected void done() {
                try {
                    List<Order> orders = get();
                    DefaultTableModel model = (DefaultTableModel) ordersTable.getModel();
                    model.setRowCount(0);
                    double totalDepense = 0;
                    for (Order o : orders) {
                        int nbArticles = o.getOrderItems() != null ? o.getOrderItems().size() : 0;
                        model.addRow(new Object[]{
                            "#" + o.getId(),
                            o.getOrderDate() != null ? o.getOrderDate().toString() : "—",
                            o.getStatus().name(),
                            nbArticles + " article(s)",
                            String.format("%.2f", o.getTotalAmount())
                        });
                        if (o.getStatus() != Order.OrderStatus.ANNULEE)
                            totalDepense += o.getTotalAmount();
                    }
                    totalLabel.setText(String.format("Total depense : %.2f FCFA | %d commande(s)",
                            totalDepense, orders.size()));
                } catch (Exception ex) {
                    totalLabel.setText("Erreur chargement : " + ErrorUtil.rootMessage(ex));
                }
            }
        };
        worker.execute();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
