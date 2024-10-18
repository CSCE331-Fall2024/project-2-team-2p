
import javax.swing.*;
import java.awt.*;
import java.net.ConnectException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;


public class CashierEditorUI extends javax.swing.JFrame {
    private int type = -1;
    private int meal = -1;
    private int side = -1;
    private ArrayList<String> Entrees = new ArrayList<>();
    private ArrayList<String> Sides = new ArrayList<>();
    private DBConnection connect = new DBConnection(false);
    private ArrayList<JLabel> entreeLabels = new ArrayList<>();
    private double totalPrice = 0.0;
   /**
    * Creates new form CashierEditorUI
    */
   public CashierEditorUI(String username, int pin) {
        initComponents();
        getContentPane().setBackground(new java.awt.Color(231, 81, 82));
        connect.verifyCredentials(username, pin);
        ArrayList<HashMap<String, Object>> menuItems = new ArrayList<>();
        connect.populateMenuItems(menuItems);
        populateJPanel3(menuItems);
        populateJPanel4(menuItems);
   }


    // Method to populate the menuItems ArrayList from the database
    // Method to dynamically add buttons to jPanel3 based on menuItems
    public void populateJPanel3(ArrayList<HashMap<String, Object>> menuItems) {
        jPanel3.removeAll(); // Clear existing components
        // Determine the number of items to display (excluding the last 4)
        int itemCount = Math.max(0, menuItems.size()); // Ensure it doesn't go negative
    
        // Set a layout manager for jPanel3 (using GridLayout)
        int rows = (int) Math.ceil(itemCount / 2.0); // 2 columns now
        jPanel3.setLayout(new GridLayout(rows, 2, 10, 10)); // 2 columns, with 10px gaps
        
        // Iterate over the menu items, but exclude the last 4 items
        for (int i = 0; i < itemCount; i++) {
            HashMap<String, Object> item = menuItems.get(i);
            String name = (String) item.get("Name");
            double price = (Double) item.get("Additional Cost");
            int entreebool = (int) item.get("Entree");
            
            if(entreebool == 1){
            // Create a new button for each menu item
                JButton menuItemButton = new JButton(name + " - $" + price);
            
                // Add an ActionListener to handle button clicks (optional)
                menuItemButton.addActionListener(evt -> {
                    int entre = 0;
                    switch (type){
                        case 0:
                            entre = 1;
                            break;
                        case 1:
                            entre = 2;
                            break;
                        case 2:
                            entre = 3;
                            break;
                    }
                    Entrees.add(name);
                    System.out.println("Selected item: " + name);
                    totalPrice += price;
                    jLabel6.setText("Total: $" + totalPrice);
                    jLabel13.setText("Total: $" + totalPrice);


                    if(Entrees.size() == entre){
                        jTabbedPane1.setSelectedIndex(2);
                    }
                    System.out.println(Entrees.size() + " " + entre);
                    int iC = Entrees.size();
                    switch (iC) {
                        case 1:
                            jLabel8.setText(name);
                            jLabel15.setText(name);
                            break;
                        case 2:
                            jLabel9.setText(name);
                            jLabel16.setText(name);
                            break;
                        case 3:
                            jLabel10.setText(name);
                            jLabel17.setText(name);
                            break;
                    }
                });
        
            // Add the button to jPanel3
            jPanel3.add(menuItemButton);
            }
        }
        JButton backButton = new JButton("Back");
        backButton.addActionListener(evt -> {
            Entrees.clear();
            type = -1;
            jTabbedPane1.setSelectedIndex(0); // Assuming the first tab is the previous screen
        });
        
        // Add the back button to the last row in jPanel3
        jPanel3.add(backButton);

        // Revalidate and repaint to update the panel with new components
        jPanel3.revalidate();
        jPanel3.repaint();
        
    }

    public void populateJPanel4(ArrayList<HashMap<String, Object>> menuItems) {
        jPanel4.removeAll(); // Clear existing components
    
        // Set BoxLayout for jPanel4 (Y_AXIS for vertical stacking)
        jPanel4.setLayout(new BoxLayout(jPanel4, BoxLayout.Y_AXIS));
    
        // Add buttons for each menu item
        for (int i = 0; i < menuItems.size(); i++) {
            HashMap<String, Object> item = menuItems.get(i);
            String name = (String) item.get("Name");
            double price = (Double) item.get("Additional Cost");
            int entreebool = (int) item.get("Entree");
    
            if (entreebool == 0) {
                // Create a new button for each menu item
                JButton menuItemButton = new JButton(name + " - $" + price);
    
                // Set preferred size for the button to increase height
                menuItemButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
                menuItemButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    
                // Optionally add an ActionListener here
                menuItemButton.addActionListener(evt -> {
                    Sides.add(name);
                    jTabbedPane1.setSelectedIndex(3);
                    jLabel18.setText(name);
                    jLabel11.setText(name);
                });
    
                // Add the button to jPanel4
                jPanel4.add(menuItemButton);
                jPanel4.add(Box.createRigidArea(new Dimension(0, 10))); // Add space between buttons
            }
        }
    
        // Back button setup
        JButton backButton2 = new JButton("Back");
        backButton2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        backButton2.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton2.addActionListener(evt -> {
            jTabbedPane1.setSelectedIndex(1);
            Entrees.clear();
            Sides.clear();
        });
    
        // Add the back button to jPanel4
        jPanel4.add(backButton2);
    
        // Revalidate and repaint to update the panel with new components
        jPanel4.revalidate();
        jPanel4.repaint();
    }

   /**
    * This method is called from within the constructor to initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is always
    * regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
   private void initComponents() {

       jTextField1 = new javax.swing.JLabel();
       jPanel1 = new javax.swing.JPanel();
       jTextField2 = new javax.swing.JTextField();
       jTextField3 = new javax.swing.JTextField();
       jTextField4 = new javax.swing.JTextField();
       jTextField5 = new javax.swing.JTextField();
       jTabbedPane1 = new javax.swing.JTabbedPane();
       jPanel2 = new javax.swing.JPanel();
       jLabel1 = new javax.swing.JLabel();
       jLabel5 = new javax.swing.JLabel();
       jLabel6 = new javax.swing.JLabel();
       jLabel7 = new javax.swing.JLabel();
       jLabel8 = new javax.swing.JLabel();
       jLabel9 = new javax.swing.JLabel();
       jLabel10 = new javax.swing.JLabel();
       jLabel11 = new javax.swing.JLabel();
       jButton8 = new javax.swing.JButton();
       jButton9 = new javax.swing.JButton();
       jButton7 = new javax.swing.JButton();
       jPanel3 = new javax.swing.JPanel();
       jLabel2 = new javax.swing.JLabel();
       jButton2 = new javax.swing.JButton();
       jButton17 = new javax.swing.JButton();
       jButton18 = new javax.swing.JButton();
       jButton19 = new javax.swing.JButton();
       jButton20 = new javax.swing.JButton();
       jButton21 = new javax.swing.JButton();
       jButton22 = new javax.swing.JButton();
       jButton23 = new javax.swing.JButton();
       jButton24 = new javax.swing.JButton();
       jButton26 = new javax.swing.JButton();
       jButton27 = new javax.swing.JButton();
       jButton28 = new javax.swing.JButton();
       jButton29 = new javax.swing.JButton();
       jPanel4 = new javax.swing.JPanel();
       jLabel3 = new javax.swing.JLabel();
       jButton3 = new javax.swing.JButton();
       jButton11 = new javax.swing.JButton();
       jButton12 = new javax.swing.JButton();
       jButton13 = new javax.swing.JButton();
       jButton14 = new javax.swing.JButton();
       jPanel5 = new javax.swing.JPanel();
       jLabel4 = new javax.swing.JLabel();
       jButton4 = new javax.swing.JButton();
       jPanel6 = new javax.swing.JPanel();
       jTextField6 = new javax.swing.JTextField();
       jTextField7 = new javax.swing.JTextField();
       jTextField8 = new javax.swing.JTextField();
       jTextField9 = new javax.swing.JTextField();
       jButton1 = new javax.swing.JButton();
       jButton5 = new javax.swing.JButton();
       jPanel7 = new javax.swing.JPanel();
       jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();

       setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
       setBackground(new java.awt.Color(231, 81, 82));

       jTextField1.setBackground(new java.awt.Color(231, 81, 82));
       jTextField1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
       jTextField1.setText("Meals");

        jPanel1.setLayout(new BoxLayout(jPanel1, BoxLayout.Y_AXIS));
        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Cart");
        jPanel1.add(jLabel5);

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Total: $0.00");
        jPanel1.add(jLabel6);


        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel5)
                .addGap(32, 32, 32)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addContainerGap())
        );

       jTabbedPane1.setBackground(new java.awt.Color(204, 204, 204));

       jPanel2.setBackground(new java.awt.Color(204, 204, 204));

       jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
       jLabel1.setText("Type of Meal");

       jButton8.setBackground(new java.awt.Color(231, 81, 82));
       jButton8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
       jButton8.setForeground(new java.awt.Color(255, 255, 255));
       jButton8.setText("Bowl");
       jButton8.addActionListener(new java.awt.event.ActionListener() {
           public void actionPerformed(java.awt.event.ActionEvent evt) {
               jButton8ActionPerformed(evt);
           }
       });

       jButton9.setBackground(new java.awt.Color(231, 81, 82));
       jButton9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
       jButton9.setForeground(new java.awt.Color(255, 255, 255));
       jButton9.setText("Plate");
       jButton9.addActionListener(new java.awt.event.ActionListener() {
           public void actionPerformed(java.awt.event.ActionEvent evt) {
               jButton9ActionPerformed(evt);
           }
       });

       jButton7.setBackground(new java.awt.Color(231, 81, 82));
       jButton7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
       jButton7.setForeground(new java.awt.Color(255, 255, 255));
       jButton7.setText("Bigger Plate");
       jButton7.addActionListener(new java.awt.event.ActionListener() {
           public void actionPerformed(java.awt.event.ActionEvent evt) {
               jButton7ActionPerformed(evt);
           }
       });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);

        // Horizontal Group
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Add JLabel to horizontal group
                    .addComponent(jLabel1) // This was missing from the horizontal group
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(14, 14, 14)) // Add a gap to the right
        );

        // Vertical Group
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1) // Make sure JLabel is in vertical group too
                .addGap(111, 111, 111)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(153, Short.MAX_VALUE))
        );

       jTabbedPane1.addTab("tab1", jPanel2);

       jPanel3.setBackground(new java.awt.Color(204, 204, 204));

       jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
       jLabel2.setText("Entree");

       jButton2.setText("Back");
       jButton2.addActionListener(new java.awt.event.ActionListener() {
           public void actionPerformed(java.awt.event.ActionEvent evt) {
               jButton2ActionPerformed(evt);
           }
       });


       javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel2)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(400, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel2)
                    .addGap(18, 18, 18)
                    .addComponent(jButton2)
                    .addContainerGap(250, Short.MAX_VALUE))
        );

       jTabbedPane1.addTab("tab2", jPanel3);

       jPanel4.setBackground(new java.awt.Color(204, 204, 204));

       jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
       jLabel3.setText("Sides");

       jButton3.setText("Back");
       jButton3.addActionListener(new java.awt.event.ActionListener() {
           public void actionPerformed(java.awt.event.ActionEvent evt) {
               jButton3ActionPerformed(evt);
               System.out.println("Debug");
           }
       });


       javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(400, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel3)
                    .addGap(18, 18, 18)
                    .addComponent(jButton3)
                    .addContainerGap(250, Short.MAX_VALUE))
        );

       jTabbedPane1.addTab("tab3", jPanel4);

       jPanel5.setBackground(new java.awt.Color(204, 204, 204));

       jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
       jLabel4.setText("Final Order");

       jButton4.setText("Back");
       jButton4.addActionListener(new java.awt.event.ActionListener() {
           public void actionPerformed(java.awt.event.ActionEvent evt) {
               jButton4ActionPerformed(evt);
           }
       });

       jPanel6.setBackground(new java.awt.Color(231, 81, 82));

       jTextField6.setText("Cart");
       jTextField6.addActionListener(new java.awt.event.ActionListener() {
           public void actionPerformed(java.awt.event.ActionEvent evt) {
               jTextField6ActionPerformed(evt);
           }
       });

       jTextField7.setText("1x Plate");

       jTextField8.setText("Total");

       jTextField9.setText("$11.99");

       javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
       jPanel6.setLayout(jPanel6Layout);
       jPanel6Layout.setHorizontalGroup(
           jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
           .addGroup(jPanel6Layout.createSequentialGroup()
               .addGap(6, 6, 6)
               .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                   .addComponent(jTextField7)
                   .addGroup(jPanel6Layout.createSequentialGroup()
                       .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                       .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                       .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
               .addContainerGap())
           .addComponent(jTextField6, javax.swing.GroupLayout.Alignment.TRAILING)
       );
       jPanel6Layout.setVerticalGroup(
           jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
           .addGroup(jPanel6Layout.createSequentialGroup()
               .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
               .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 132, Short.MAX_VALUE)
               .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                   .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                   .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
               .addContainerGap())
       );

       jButton1.setBackground(new java.awt.Color(231, 81, 82));
       jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
       jButton1.setForeground(new java.awt.Color(255, 255, 255));
       jButton1.setText("Add to order");
       jButton1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton1ActionPerformed(evt);
        }
    });

       jPanel7.setBackground(new java.awt.Color(231, 81, 82));

        jLabel12.setBackground(new java.awt.Color(255, 255, 255));
        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Cart");

        jLabel13.setBackground(new java.awt.Color(255, 255, 255));
        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Total: $11.99");

        jLabel14.setBackground(new java.awt.Color(255, 255, 255));
        jLabel14.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Plate");

        jLabel15.setBackground(new java.awt.Color(204, 204, 204));
        jLabel15.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel15.setText(" ");

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel16.setText(" ");

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel17.setText(" ");

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel18.setText(" ");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addGap(32, 32, 32)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(116, 116, 116))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(252, 252, 252))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

       jTabbedPane1.addTab("tab4", jPanel5);

       jButton5.setBackground(new java.awt.Color(231, 81, 82));
       jButton5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
       jButton5.setForeground(new java.awt.Color(255, 255, 255));
       jButton5.setText("Log Out");
       jButton5.addActionListener(new java.awt.event.ActionListener() {
           public void actionPerformed(java.awt.event.ActionEvent evt) {
               jButton5ActionPerformed(evt);
           }
       });

       ImageIcon icon = new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("image 9.png")).getImage().getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH));

        // 2. Create a JLabel for the image
        JLabel imageLabel = new JLabel();
        imageLabel.setIcon(icon);

        // 3. Set the layout to include the new image below the log out button
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 750, javax.swing.GroupLayout.PREFERRED_SIZE) // Increased width by 150 pixels
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            // Add the image label below the button
                            .addComponent(imageLabel))))  // <-- Add this line to position the image below the button
                .addContainerGap(50, Short.MAX_VALUE))
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)) // Same height
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        // Add space between the button and image
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                        .addComponent(imageLabel)))  // <-- Add this line to position the image below the button
                .addGap(66, 66, 66))
        );
        // Set the initial size of the JFrame (starting screen)
        setPreferredSize(new Dimension(1150, 700)); // Adjusted the JFrame size to accommodate the wider TabbedPane
        setSize(1150, 700); // Apply the preferred size
        setLocationRelativeTo(null); // Center the window on the screen

        pack();
   }// </editor-fold>       
   
   public void updateJPanel1() {
        jPanel1.removeAll(); // Clear existing components

        JLabel cartLabel = new JLabel("Cart");
        cartLabel.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jPanel1.add(cartLabel);

        // Add each selected entree as a JLabel
        for (String entree : Entrees) {
            JLabel entreeLabel = new JLabel(entree);
            entreeLabel.setFont(new java.awt.Font("Segoe UI", 1, 12));
            jPanel1.add(entreeLabel);
        }

        // Update total
        jLabel6.setText("Total: $" + String.format("%.2f", totalPrice));
        jPanel1.add(jLabel6); // Add total label

        // Revalidate and repaint to update the panel
        jPanel1.revalidate();
        jPanel1.repaint();
    }

    // Method to add an entree and update the panel
    public void addEntree(String name, double price) {
        Entrees.add(name);
        totalPrice += price; // Update total price
        updateJPanel1(); // Update the display
    }

   private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {                                            
       // TODO add your handling code here:
   }                                           

   private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {                                            
       // TODO add your handling code here:
   }                                           

   private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
       type = -1;
       jTabbedPane1.setSelectedIndex(0);
       Entrees.clear();
   }                                        

   private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {                                         
       jTabbedPane1.setSelectedIndex(1);
       Entrees.clear();
       Sides.clear();
   }                                        

   private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {                                         
       side = -1;
       jTabbedPane1.setSelectedIndex(2);
       Sides.clear();
   }                                        

   private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {                                         
       type = 0;
       jTabbedPane1.setSelectedIndex(1);
       totalPrice = 8.99;
       jLabel14.setText("Bowl");
       jLabel7.setText("Bowl");
       jLabel6.setText("Total: $" + totalPrice);
       jLabel13.setText("Total: $" + totalPrice);
   }                                        

   private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {                                          
       // TODO add your handling code here:
   }
   private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        String[] ents = new String[Entrees.size()];
        ents = Entrees.toArray(ents);
        String[] sd = new String[Sides.size()];
        sd = Sides.toArray(sd);                                     
        Order order = connect.createOrder(type, ents);
        connect.placeOrder(order, ents, sd);
        Entrees.clear();
        Sides.clear();
        type = -1;
        jTabbedPane1.setSelectedIndex(0);
        totalPrice = 0;

   }                                                

   private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {                                          
       // TODO add your handling code here:
   }                                         

   private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {                                          
       // TODO add your handling code here:
   }                                         

   private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {                                          
       // TODO add your handling code here:
   }                                         

   private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {                                          
       // TODO add your handling code here:
   }                                         

   private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {                                          
       // TODO add your handling code here:
   }                                         

   private void jButton23ActionPerformed(java.awt.event.ActionEvent evt) {                                          
       // TODO add your handling code here:
   }                                         

   private void jButton24ActionPerformed(java.awt.event.ActionEvent evt) {                                          
       // TODO add your handling code here:
   }                                         

   private void jButton26ActionPerformed(java.awt.event.ActionEvent evt) {                                          
       // TODO add your handling code here:
   }                                         

   private void jButton27ActionPerformed(java.awt.event.ActionEvent evt) {                                          
       // TODO add your handling code here:
   }                                         

   private void jButton28ActionPerformed(java.awt.event.ActionEvent evt) {                                          
       // TODO add your handling code here:
   }                                         

   private void jButton29ActionPerformed(java.awt.event.ActionEvent evt) {                                          
       // TODO add your handling code here:
   }                                         

   private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {                                          
       // TODO add your handling code here:
   }                                         

   private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {                                          
       // TODO add your handling code here:
   }                                         

   private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {                                          
       // TODO add your handling code here:
   }                                         

   private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {                                          
       // TODO add your handling code here:
   }                                         

   private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {                                            
       // TODO add your handling code here:
   }                                           

   private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {                                         
       // TODO add your handling code here:
   }                                        

   private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {                                         
       type = 1;
       jTabbedPane1.setSelectedIndex(1);
       totalPrice = 9.99;
       jLabel14.setText("Plate");
       jLabel7.setText("Plate");
       jLabel6.setText("Total: $" + totalPrice);
       jLabel13.setText("Total: $" + totalPrice);
   }                                        

   private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {                                         
       type = 2;
       jTabbedPane1.setSelectedIndex(1);
       totalPrice = 11.99;
       jLabel14.setText("Bigger Plate");
       jLabel7.setText("Bigger Plate");
       jLabel6.setText("Total: $" + totalPrice);
       jLabel13.setText("Total: $" + totalPrice);
   }                                        

   /**
    * @param args the command line arguments
    */
   public static void main(String args[]) {
       /* Set the Nimbus look and feel */
       //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
       /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
        * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
        */

       DBConnection connect = new DBConnection(false);
       connect.verifyCredentials("Smiles", 3333);
       try {
           for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
               if ("Nimbus".equals(info.getName())) {
                   javax.swing.UIManager.setLookAndFeel(info.getClassName());
                   break;
               }
           }
       } catch (ClassNotFoundException ex) {
           java.util.logging.Logger.getLogger(CashierEditorUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
       } catch (InstantiationException ex) {
           java.util.logging.Logger.getLogger(CashierEditorUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
       } catch (IllegalAccessException ex) {
           java.util.logging.Logger.getLogger(CashierEditorUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
       } catch (javax.swing.UnsupportedLookAndFeelException ex) {
           java.util.logging.Logger.getLogger(CashierEditorUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
       }
       //</editor-fold>
       /* Create and display the form */
       java.awt.EventQueue.invokeLater(new Runnable() {
           public void run() {
               new CashierEditorUI("Smiles", 3333).setVisible(true);
           }
       });
   }

   // Variables declaration - do not modify                     
   private javax.swing.JButton jButton1;
   private javax.swing.JButton jButton11;
   private javax.swing.JButton jButton12;
   private javax.swing.JButton jButton13;
   private javax.swing.JButton jButton14;
   private javax.swing.JButton jButton17;
   private javax.swing.JButton jButton18;
   private javax.swing.JButton jButton19;
   private javax.swing.JButton jButton2;
   private javax.swing.JButton jButton20;
   private javax.swing.JButton jButton21;
   private javax.swing.JButton jButton22;
   private javax.swing.JButton jButton23;
   private javax.swing.JButton jButton24;
   private javax.swing.JButton jButton26;
   private javax.swing.JButton jButton27;
   private javax.swing.JButton jButton28;
   private javax.swing.JButton jButton29;
   private javax.swing.JButton jButton3;
   private javax.swing.JButton jButton4;
   private javax.swing.JButton jButton5;
   private javax.swing.JButton jButton7;
   private javax.swing.JButton jButton8;
   private javax.swing.JButton jButton9;
   private javax.swing.JLabel jLabel1;
   private javax.swing.JLabel jLabel10;
   private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JLabel jLabel3;
   private javax.swing.JLabel jLabel4;
   private javax.swing.JLabel jLabel5;
   private javax.swing.JLabel jLabel6;
   private javax.swing.JLabel jLabel7;
   private javax.swing.JLabel jLabel8;
   private javax.swing.JLabel jLabel9;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JPanel jPanel2;
   private javax.swing.JPanel jPanel3;
   private javax.swing.JPanel jPanel4;
   private javax.swing.JPanel jPanel5;
   private javax.swing.JPanel jPanel6;
   private javax.swing.JPanel jPanel7;
   private javax.swing.JTabbedPane jTabbedPane1;
   private javax.swing.JLabel jTextField1;
   private javax.swing.JTextField jTextField2;
   private javax.swing.JTextField jTextField3;
   private javax.swing.JTextField jTextField4;
   private javax.swing.JTextField jTextField5;
   private javax.swing.JTextField jTextField6;
   private javax.swing.JTextField jTextField7;
   private javax.swing.JTextField jTextField8;
   private javax.swing.JTextField jTextField9;
   // End of variables declaration                   
}
