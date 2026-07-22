/*
 * ShopSphere - OrderHistory
 * Historique des commandes et depenses du client
 * Meme structure qu'AgriConnect view/OrderUser.java
 */
package view;

import java.awt.*;
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

    private final User currentUser;
    private JTable     ordersTable;
    private JLabel     totalLabel;

    public OrderHistory(User user) {
        this.currentUser = user;
        initComponents();
        buildUI();
        loadOrders();
    }

    private void buildUI() {
        setTitle("ShopSphere — Historique de mes commandes");
        setSize(820, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Theme.NEUTRAL);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.PRIMARY);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(new EmptyBorder(0, 20, 0, 20));
        JLabel titre = new JLabel("Historique de mes commandes");
        titre.setFont(Theme.FONT_SUBTITLE);
        titre.setForeground(Theme.WHITE);
        header.add(titre, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Tableau
        String[] cols = {"#", "Date", "Statut", "Articles", "Total (EUR)"};
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
                    String s = v.toString();
                    if (s.contains("TERMINEE") || s.contains("LIVREE"))
                        setForeground(Theme.SUCCESS);
                    else if (s.contains("ANNULEE"))
                        setForeground(Theme.ERROR);
                    else if (s.contains("EXPEDIEE"))
                        setForeground(new Color(0x8B, 0x5C, 0xF6));
                    else
                        setForeground(Theme.ACCENT);
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
                return RMIClient.getOrderService().findOrderRecordsByBuyer(currentUser.getId());
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
                    totalLabel.setText(String.format("Total depense : %.2f EUR | %d commande(s)",
                            totalDepense, orders.size()));
                } catch (Exception ex) {
                    totalLabel.setText("Erreur chargement : " + ex.getMessage());
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
