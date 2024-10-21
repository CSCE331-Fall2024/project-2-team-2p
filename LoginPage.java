import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class LoginPage extends JFrame implements ActionListener {
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton;

    /***
     * @author Myles
     * Login page to go between Cashier and Manager Side
     */
    public LoginPage() {
        setTitle("Login Page");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        getContentPane().setBackground(new java.awt.Color(231, 81, 82));
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(10, 10, 80, 25);
        add(usernameLabel);
        
        usernameField = new JTextField();
        usernameField.setBounds(100, 10, 160, 25);
        add(usernameField);
        
        JLabel passwordLabel = new JLabel("Pin:");
        passwordLabel.setBounds(10, 40, 80, 25);
        add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(100, 40, 160, 25);
        add(passwordField);
        
        loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 80, 25);
        loginButton.addActionListener(this);
        add(loginButton);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        DBConnection verify = new DBConnection(false);

        try {
            int pin = Integer.parseInt(password);

            if (verify.verifyCredentials(username, pin) && verify.manager) {
                System.out.println("User validated as Manager.");
                dispose();  
                new ManagerFrame(username, pin);
            } else if (verify.verifyCredentials(username, pin)) {
                System.out.println("User validated as Cashier.");
                dispose(); 
                DBConnection connect = new DBConnection(false);
                connect.verifyCredentials(username, pin);
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
                        new CashierEditorUI(username, pin).setVisible(true);
                    }
                });
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login credentials");
            }
        }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "PIN must be an integer");
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}
