package boundary;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class MainFrame extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
    static MainFrame frame;
    private JMenu mnHome, mnWine, mnWineType, mnProducers, mnOccasion, mnFoodPairings, mnEmployees;
    private JMenu mnCustomers, mnStorageLocation, mnReportsData, mnRegularOrder, mnUrgentOrder;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                frame = new MainFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MainFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 753, 521);
        setTitle("Management System");

        // ✅ Set white background for content pane
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        // ✅ Create Menu Bar
        createMenuBar();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);

        // ✅ Home Menu
        mnHome = new JMenu("Home");
        mnHome.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                new MainMenu().setVisible(true);
                dispose();
            }
            public void menuDeselected(MenuEvent e) {}
            public void menuCanceled(MenuEvent e) {}
        });
        menuBar.add(mnHome);

        // ✅ Wine Management
        mnWine = new JMenu("Wine");
        mnWine.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                new WineManagment().setVisible(true);
                dispose();
            }
            public void menuDeselected(MenuEvent e) {}
            public void menuCanceled(MenuEvent e) {}
        });
        menuBar.add(mnWine);

        // ✅ Wine Type Management
        mnWineType = new JMenu("Wine Type");
        mnWineType.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                new WineTypeManagement().setVisible(true);
                dispose();
            }
            public void menuDeselected(MenuEvent e) {}
            public void menuCanceled(MenuEvent e) {}
        });
        menuBar.add(mnWineType);

        // ✅ Producers Management
        mnProducers = new JMenu("Producers");
        mnProducers.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                new FrameProducer().setVisible(true);
                dispose();
            }
            public void menuDeselected(MenuEvent e) {}
            public void menuCanceled(MenuEvent e) {}
        });
        menuBar.add(mnProducers);

        // ✅ Occasion Management
        mnOccasion = new JMenu("Occasion");
        mnOccasion.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                new OccasionManagment().setVisible(true);
                dispose();
            }
            public void menuDeselected(MenuEvent e) {}
            public void menuCanceled(MenuEvent e) {}
        });
        menuBar.add(mnOccasion);

        // ✅ Food Pairings Management
        mnFoodPairings = new JMenu("Food Pairings");
        mnFoodPairings.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                new FoodPairingManaegment().setVisible(true);
                dispose();
            }
            public void menuDeselected(MenuEvent e) {}
            public void menuCanceled(MenuEvent e) {}
        });
        menuBar.add(mnFoodPairings);

        // ✅ Employees Management
        mnEmployees = new JMenu("Employees");
        mnEmployees.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                new EmployeeManagement().setVisible(true);
                dispose();
            }
            public void menuDeselected(MenuEvent e) {}
            public void menuCanceled(MenuEvent e) {}
        });
        menuBar.add(mnEmployees);

        // ✅ Customers Management
        mnCustomers = new JMenu("Customers");
        mnCustomers.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                new CustomerManagement().setVisible(true);
                dispose();
            }
            public void menuDeselected(MenuEvent e) {}
            public void menuCanceled(MenuEvent e) {}
        });
        menuBar.add(mnCustomers);

        // ✅ Storage Location Management
        mnStorageLocation = new JMenu("Storage Location");
        mnStorageLocation.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                new StorageLocationManagement().setVisible(true);
                dispose();
            }
            public void menuDeselected(MenuEvent e) {}
            public void menuCanceled(MenuEvent e) {}
        });
        menuBar.add(mnStorageLocation);

        // ✅ Reports & Data Management
        mnReportsData = new JMenu("Reports and Data");
        mnReportsData.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                new ReportsAndData().setVisible(true);
                dispose();
            }
            public void menuDeselected(MenuEvent e) {}
            public void menuCanceled(MenuEvent e) {}
        });
        menuBar.add(mnReportsData);

        // ✅ Regular Order Management
        mnRegularOrder = new JMenu("Regular Order");
        mnRegularOrder.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                new RegularOrderManagement().setVisible(true);
                dispose();
            }
            public void menuDeselected(MenuEvent e) {}
            public void menuCanceled(MenuEvent e) {}
        });
        menuBar.add(mnRegularOrder);

        // ✅ Urgent Order Management
        mnUrgentOrder = new JMenu("Urgent Order");
        mnUrgentOrder.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                new UrgentOrderManagement().setVisible(true);
                dispose();
            }
            public void menuDeselected(MenuEvent e) {}
            public void menuCanceled(MenuEvent e) {}
        });
        menuBar.add(mnUrgentOrder);

        setJMenuBar(menuBar);
    }

    public void updateMenuSelectionHome() {
        mnHome.setOpaque(true);
        mnHome.setBackground(SystemColor.activeCaption);
    }

    public void updateMenuSelectionWine() {
        mnWine.setOpaque(true);
        mnWine.setBackground(SystemColor.activeCaption);
    }

    public void updateMenuSelectionWineType() {
        mnWineType.setOpaque(true);
        mnWineType.setBackground(SystemColor.activeCaption);
    }

    public void updateMenuSelectionProducers() {
        mnProducers.setOpaque(true);
        mnProducers.setBackground(SystemColor.activeCaption);
    }

    public void updateMenuSelectionOccasion() {
        mnOccasion.setOpaque(true);
        mnOccasion.setBackground(SystemColor.activeCaption);
    }

    public void updateMenuSelectionFoodPairings() {
        mnFoodPairings.setOpaque(true);
        mnFoodPairings.setBackground(SystemColor.activeCaption);
    }

    public void updateMenuSelectionEmployees() {
        mnEmployees.setOpaque(true);
        mnEmployees.setBackground(SystemColor.activeCaption);
    }

    public void updateMenuSelectionCustomers() {
        mnCustomers.setOpaque(true);
        mnCustomers.setBackground(SystemColor.activeCaption);
    }

    public void updateMenuSelectionStorageLocation() {
        mnStorageLocation.setOpaque(true);
        mnStorageLocation.setBackground(SystemColor.activeCaption);
    }

    public void updateMenuSelectionReportsData() {
        mnReportsData.setOpaque(true);
        mnReportsData.setBackground(SystemColor.activeCaption);
    }

    public void updateMenuSelectionRegularOrder() {
        mnRegularOrder.setOpaque(true);
        mnRegularOrder.setBackground(SystemColor.activeCaption);
    }

    public void updateMenuSelectionUrgentOrder() {
        mnUrgentOrder.setOpaque(true);
        mnUrgentOrder.setBackground(SystemColor.activeCaption);
    }
}
