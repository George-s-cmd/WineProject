package boundary;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends MainFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MainMenu() {
        setTitle("Main Menu");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Load the background image
        ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/boundary/images/wine3.jpg"));

        // Create a custom panel for the background
        JPanel backgroundPanel = new JPanel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Sidebar Panel with White Shadow
        JPanel sidebarPanel = new JPanel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 100)); // White transparency effect
                g2d.fillRoundRect(-10, 10, getWidth() + 20, getHeight() - 20, 20, 20);
                g2d.dispose();
            }
        };

        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setOpaque(false);
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));

        // 🔹 ADD SPACING TO MOVE BUTTONS LOWER
        sidebarPanel.add(Box.createVerticalGlue());

        // Sidebar Buttons
        RoundedButton btnWineManagement = createSidebarButton("Wine Management");
        RoundedButton btnWineTypeManagement = createSidebarButton("WineType Management");
        RoundedButton btnProducerManagement = createSidebarButton("Producer Management");
        RoundedButton btnCustomerManagement = createSidebarButton("Customer Management");
        RoundedButton btnEmployeeManagement = createSidebarButton("Employee Management");
        RoundedButton btnRegularOrderManagement = createSidebarButton("Regular Order Management");
        RoundedButton btnUrgentOrderManagement = createSidebarButton("Urgent Order Management");
        RoundedButton btnFoodPairingManagement = createSidebarButton("Food Pairing Management");
        RoundedButton btnOccasionManagement = createSidebarButton("Occasion Management");
        

        // 🔹 NEW BUTTONS
        RoundedButton btnStorageLocationManagement = createSidebarButton("Storage Location Management");
        RoundedButton btnDataReportManagement = createSidebarButton("Data and Report Management");
       

        // 🔹 ADD ACTION LISTENERS
        btnWineManagement.addActionListener(e -> openFrame(new WineManagment()));
        btnWineTypeManagement.addActionListener(e -> openFrame(new WineTypeManagement()));
        btnProducerManagement.addActionListener(e -> openFrame(new FrameProducer()));
        btnCustomerManagement.addActionListener(e -> openFrame(new CustomerManagement()));
        btnEmployeeManagement.addActionListener(e -> openFrame(new EmployeeManagement()));
        btnRegularOrderManagement.addActionListener(e -> openFrame(new RegularOrderManagement()));
        btnUrgentOrderManagement.addActionListener(e -> openFrame(new UrgentOrderManagement()));
        btnFoodPairingManagement.addActionListener(e -> openFrame(new FoodPairingManaegment()));
        btnOccasionManagement.addActionListener(e -> openFrame(new OccasionManagment()));
        btnStorageLocationManagement.addActionListener(e -> openFrame(new StorageLocationManagement()));
        btnDataReportManagement.addActionListener(e->openFrame(new ReportsAndData()));
//        btnDataReportManagement.addActionListener(e -> openFrame(new ReportsAndData ()));

        // 🔹 ADD BUTTONS TO SIDEBAR
        sidebarPanel.add(btnWineManagement);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); 
        sidebarPanel.add(btnWineTypeManagement);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); 
        sidebarPanel.add(btnProducerManagement);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); 
        sidebarPanel.add(btnCustomerManagement);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); 
        sidebarPanel.add(btnEmployeeManagement);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); 
        sidebarPanel.add(btnRegularOrderManagement);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); 
        sidebarPanel.add(btnUrgentOrderManagement);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); 
        sidebarPanel.add(btnFoodPairingManagement);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); 
        sidebarPanel.add(btnOccasionManagement);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); 

        // 🔹 ADD NEW BUTTONS TO SIDEBAR
        sidebarPanel.add(btnStorageLocationManagement);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10))); 
        sidebarPanel.add(btnDataReportManagement);
        sidebarPanel.add(Box.createVerticalGlue()); 
        ;

        // Main Panel (Center Content)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

     // Create a panel to hold the title with transparency effect
        JPanel titlePanel = new JPanel() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Soft transparent white background (same effect as sidebar)
                g2d.setColor(new Color(255, 255, 255, 100)); 
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2d.dispose();
            }
        };
        titlePanel.setOpaque(false); // Ensure panel itself is not solid
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding

        // Create the Title Label
        JLabel titleLabel = new JLabel("Cheers System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 92, 92)); // Pinkish-purple color
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add label to the panel
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Add titlePanel to the main panel
        mainPanel.add(titlePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Space below title

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Add Panels to Background Panel
        backgroundPanel.add(sidebarPanel, BorderLayout.WEST);
        backgroundPanel.add(mainPanel, BorderLayout.CENTER);

        // Set Content Pane
        setContentPane(backgroundPanel);
        setLocationRelativeTo(null);
    }

    // Method to open a new frame
   

    // Sidebar Button Styling
    private RoundedButton createSidebarButton(String text) {
        RoundedButton button = new RoundedButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(255, 92, 92)); // Set background color to #FFFDA4
        button.setForeground(Color.white); // Set text color for contrast
        return button;
    }
 // Method to refresh and reopen any frame
    private void refreshFrame(JFrame frame) {
        frame.dispose(); // ✅ Close the current frame
        JFrame newFrame; // ✅ Create a new frame instance

        // Check which frame to open and create a fresh instance
        if (frame instanceof WineManagment) {
            newFrame = new WineManagment(); 
        } else if (frame instanceof FrameProducer) {
            newFrame = new FrameProducer();
        } else if (frame instanceof CustomerManagement) {
            newFrame = new CustomerManagement();
        } else if (frame instanceof EmployeeManagement) {
            newFrame = new EmployeeManagement();
        } else if (frame instanceof RegularOrderManagement) {
            newFrame = new RegularOrderManagement();
        } else if (frame instanceof UrgentOrderManagement) {
            newFrame = new UrgentOrderManagement();
        } else if (frame instanceof FoodPairingManaegment) {
            newFrame = new FoodPairingManaegment();
        } else if (frame instanceof OccasionManagment) {
            newFrame = new OccasionManagment();
        } else if (frame instanceof StorageLocationManagement) {
            newFrame = new StorageLocationManagement();
        } else if (frame instanceof ReportsAndData) {
            newFrame = new ReportsAndData();
        } else {
            newFrame = frame; // If no match, open the same frame
        }

        newFrame.setVisible(true); // ✅ Display the refreshed frame
    }
    private void openFrame(JFrame frame) {
        refreshFrame(frame); 
        // ✅ Refresh data before opening the frame
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu mainMenu = new MainMenu();
            mainMenu.setVisible(true);
        });
    }
}
