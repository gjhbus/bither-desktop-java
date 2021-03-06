package net.bither.viewsystem.base.renderer;

import net.bither.model.AddressCheck;
import net.bither.utils.ImageLoader;
import net.bither.viewsystem.base.ColorAndFontConstants;
import net.bither.viewsystem.themes.Themes;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CheckImageRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 154545L;

    JLabel primaryLabel = new JLabel();
    JPanel combinationPanel = new JPanel();


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row,
                                                   int column) {

        // Prepare the primary icon (used always), and an extra icon and containing panel for use as required.
        primaryLabel.setHorizontalAlignment(SwingConstants.CENTER);
        primaryLabel.setVerticalAlignment(SwingConstants.CENTER);
        primaryLabel.setOpaque(true);

        combinationPanel.setOpaque(true);
        combinationPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        // Prepare a double icon panel for use as required.
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        combinationPanel.add(primaryLabel, constraints);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        // pb.setIndeterminate(true);


        AddressCheck.CheckStatus checkStatus = (AddressCheck.CheckStatus) value;
        switch (checkStatus) {
            case Success:
                primaryLabel.setIcon(ImageLoader.CHECK_MARK);


                break;
            case Failed:
                primaryLabel.setIcon(ImageLoader.CHECK_FAILED);
                break;
            case Prepare:


                primaryLabel.setVisible(false);
                break;
        }


        if (isSelected) {

            primaryLabel.setBackground(table.getSelectionBackground());
            primaryLabel.setForeground(table.getSelectionForeground());

            combinationPanel.setBackground(table.getSelectionBackground());
        } else {
            primaryLabel.setForeground(table.getForeground());

            combinationPanel.setForeground(table.getForeground());
            if (row % 2 == 1) {
                primaryLabel.setBackground(Themes.currentTheme.detailPanelBackground());

                combinationPanel.setBackground(Themes.currentTheme.detailPanelBackground());
            } else {
                primaryLabel.setBackground(Themes.currentTheme.sidebarPanelBackground());

                combinationPanel.setBackground(Themes.currentTheme.sidebarPanelBackground());
                primaryLabel.setOpaque(true);

                combinationPanel.setOpaque(true);
            }
        }

        // Return either a single icon or a double icon panel.

        return primaryLabel;

    }


}
