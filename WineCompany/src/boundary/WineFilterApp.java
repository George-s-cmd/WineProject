package boundary;

import control.GeneralControl;

import control.FoodPairingsLogic;
import control.OccasionLogic;
import control.WineLogic;
import control.WineTypeLogic;
import entity.Wine;
import net.sf.jasperreports.engine.JRException;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.util.*;
import java.util.List;

public class WineFilterApp extends MainFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final GeneralControl generalControl = new GeneralControl();
    private JPanel selectedFoodPanel;
    private JPanel selectedOccasionPanel;
    private JPanel selectedWineTypePanel;
    private final ArrayList<String> selectedFoods = new ArrayList<>();
    private final ArrayList<String> selectedOccasions = new ArrayList<>();
    private final ArrayList<String> selectedWineTypes = new ArrayList<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WineFilterApp().createAndShowGUI());
    }

    public void createAndShowGUI() {
        JFrame frame = new JFrame("Wine Recommendation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);

        // Load the background image
        ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/boundary/images/wine.jpg"));

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
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));

        // Main panel with transparent background
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        // Labels
        JPanel foodLabelPanel = createAlignedLabelPanel("Select Food Items:", new Color(255, 92, 92));
        JPanel occasionLabelPanel = createAlignedLabelPanel("Select Occasions:", new Color(255, 92, 92));
        JPanel wineTypeLabelPanel = createAlignedLabelPanel("Select Wine Types:", new Color(255, 92, 92));

        // ComboBoxes setup
        JComboBox<String> foodComboBox = createModernComboBox(FoodPairingsLogic.getInstance().getAllFoodNames1());
        JComboBox<String> occasionComboBox = createModernComboBox(OccasionLogic.getInstance().getAllOccasionNames());
        JComboBox<String> wineTypeComboBox = createModernComboBox(new WineTypeLogic().getAllWineTypeIds());

        setupComboBoxListeners(foodComboBox, occasionComboBox, wineTypeComboBox);

        // Selection Panels
        selectedFoodPanel = createStyledPanel();
        selectedOccasionPanel = createStyledPanel();
        selectedWineTypePanel = createStyledPanel();

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        JButton generateButton = createStyledButton("Generate Report");
        JButton clearButton = createStyledButton("Clear");
        JButton showButton = createStyledButton("Close");

        generateButton.addActionListener(e -> handleGenerateReport(frame));
        clearButton.addActionListener(e -> handleClearSelections());
        showButton.addActionListener(e -> {
            ((JFrame) SwingUtilities.getWindowAncestor(showButton)).dispose();
        });

        buttonPanel.add(showButton);
        buttonPanel.add(generateButton);
        buttonPanel.add(clearButton);
      

        // Assemble the main panel
        assembleMainPanel(mainPanel, foodLabelPanel, foodComboBox, occasionLabelPanel, 
                         occasionComboBox, wineTypeLabelPanel, wineTypeComboBox, buttonPanel);

        // Add main panel to background panel
        backgroundPanel.add(mainPanel);

        frame.setContentPane(backgroundPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void setupComboBoxListeners(JComboBox<String> foodComboBox, 
                                      JComboBox<String> occasionComboBox, 
                                      JComboBox<String> wineTypeComboBox) {
        foodComboBox.addActionListener(e -> {
            if (e.getActionCommand().equals("comboBoxChanged")) {
                String selectedFood = (String) foodComboBox.getSelectedItem();
                if (selectedFood != null) {
                    addSelectionToPanel(selectedFood, selectedFoodPanel, selectedFoods);
                }
            }
        });

        occasionComboBox.addActionListener(e -> {
            if (e.getActionCommand().equals("comboBoxChanged")) {
                String selectedOccasion = (String) occasionComboBox.getSelectedItem();
                if (selectedOccasion != null) {
                    addSelectionToPanel(selectedOccasion, selectedOccasionPanel, selectedOccasions);
                }
            }
        });

        wineTypeComboBox.addActionListener(e -> {
            if (e.getActionCommand().equals("comboBoxChanged")) {
                String selectedWineType = (String) wineTypeComboBox.getSelectedItem();
                if (selectedWineType != null) {
                    addSelectionToPanel(selectedWineType, selectedWineTypePanel, selectedWineTypes);
                }
            }
        });
    }

    private void handleGenerateReport(JFrame frame) {
        if (selectedFoods.isEmpty() && selectedOccasions.isEmpty() && selectedWineTypes.isEmpty()) {
            showStyledDialog(frame, "Please select at least one filter option before generating a report.", "Warning");
            return;
        }

        try {
            // Get the filtered wines based on the selected filters
            List<Wine> wines = generalControl.getWinesByDishesOccasionsAndWineTypes(
                new ArrayList<>(selectedFoods),
                new ArrayList<>(selectedOccasions),
                new ArrayList<>(selectedWineTypes)
            );

            if (wines.isEmpty()) {
                showStyledDialog(frame, "No wines found for the selected filters. Please try again with different options.", "No Wines Found");
                return;
            }

            // Instead of generating a Jasper report, open the WineReportGUI
            new WineRecommendationReport(wines); 

        } catch (Exception ex) {
            ex.printStackTrace();
            showStyledDialog(frame, "An error occurred while fetching wines.", "Error");
        }
    }

  

    private void handleClearSelections() {
        selectedFoods.clear();
        selectedOccasions.clear();
        selectedWineTypes.clear();
        selectedFoodPanel.removeAll();
        selectedOccasionPanel.removeAll();
        selectedWineTypePanel.removeAll();
        selectedFoodPanel.revalidate();
        selectedFoodPanel.repaint();
        selectedOccasionPanel.revalidate();
        selectedOccasionPanel.repaint();
        selectedWineTypePanel.revalidate();
        selectedWineTypePanel.repaint();
    }

    private void assembleMainPanel(JPanel mainPanel, 
                                 JPanel foodLabelPanel, 
                                 JComboBox<String> foodComboBox,
                                 JPanel occasionLabelPanel, 
                                 JComboBox<String> occasionComboBox,
                                 JPanel wineTypeLabelPanel, 
                                 JComboBox<String> wineTypeComboBox,
                                 JPanel buttonPanel) {
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(foodLabelPanel);
        mainPanel.add(Box.createVerticalStrut(-75));
        mainPanel.add(foodComboBox);
        mainPanel.add(Box.createVerticalStrut(1));
        mainPanel.add(selectedFoodPanel);

        mainPanel.add(Box.createVerticalStrut(-30));
        mainPanel.add(occasionLabelPanel);
        mainPanel.add(Box.createVerticalStrut(-75));
        mainPanel.add(occasionComboBox);
        mainPanel.add(Box.createVerticalStrut(1));
        mainPanel.add(selectedOccasionPanel);

        mainPanel.add(Box.createVerticalStrut(-30));
        mainPanel.add(wineTypeLabelPanel);
        mainPanel.add(Box.createVerticalStrut(-75));
        mainPanel.add(wineTypeComboBox);
        mainPanel.add(Box.createVerticalStrut(1));
        mainPanel.add(selectedWineTypePanel);

        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(buttonPanel);
    }

    private JComboBox<String> createModernComboBox(ArrayList<String> items) {
        JComboBox<String> comboBox = new JComboBox<>(items.toArray(new String[0]));
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        comboBox.setUI(new RoundedComboBoxUI());
        comboBox.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        comboBox.setOpaque(false);
        return comboBox;
    }

    private static class RoundedComboBoxUI extends BasicComboBoxUI {
        @Override
        protected JButton createArrowButton() {
            JButton button = new JButton("▼");
            button.setContentAreaFilled(false);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder());
            button.setFont(new Font("Proxima Nova", Font.BOLD, 12));
            button.setForeground(new Color(255, 92, 92));
            return button;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = c.getWidth();
            int height = c.getHeight();
            int arc = 20;

            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(0, 0, width, height, arc, arc);

            g2d.setColor(new Color(255, 92, 92));
            g2d.drawRoundRect(0, 0, width - 1, height - 1, arc, arc);

            super.paint(g, c);
        }
    }

    private JPanel createStyledPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        return panel;
    }

    private JPanel createAlignedLabelPanel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(color);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        panel.add(label);
        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getForeground());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
                g2.dispose();
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(255, 92, 92));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(253, 114, 114));
                button.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(255, 92, 92));
                button.repaint();
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(253, 114, 114));
                button.repaint();
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(253, 114, 114));
                button.repaint();
            }
        });

        return button;
    }
    private void addSelectionToPanel(String item, JPanel panel, List<String> selectedItems) {
        if (!selectedItems.contains(item)) {
            selectedItems.add(item);
            JButton button = createRemovableButton(item, panel, selectedItems);
            panel.add(button);
            panel.revalidate();
            panel.repaint();
        }
    }

    private JButton createRemovableButton(String text, JPanel panel, List<String> selectedItems) {
        JButton button = new JButton(text + " ×") {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getForeground());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
            }
        };

        button.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(253, 114, 114));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 92, 92));
                button.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(253, 114, 114));
                button.repaint();
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 92, 92));
                button.repaint();
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 92, 92));
                button.repaint();
            }
        });

        button.addActionListener(e -> {
            selectedItems.remove(text);
            panel.remove(button);
            panel.revalidate();
            panel.repaint();
        });

        return button;
    }

    private void showStyledDialog(JFrame parent, String message, String title) {
        JDialog styledDialog = new JDialog(parent, title, true);
        styledDialog.setLayout(new BorderLayout());
        styledDialog.setSize(600, 200);
        styledDialog.setLocationRelativeTo(parent);

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageLabel.setForeground(new Color(70, 70, 70));

        JButton okButton = createStyledButton("OK");
        okButton.addActionListener(evt -> styledDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);

        styledDialog.add(messageLabel, BorderLayout.CENTER);
        styledDialog.add(buttonPanel, BorderLayout.SOUTH);

        styledDialog.setVisible(true);
    }
}