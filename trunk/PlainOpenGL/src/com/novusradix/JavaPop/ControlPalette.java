/*
 * ControlPalette.java
 *
 * Created on November 13, 2008, 7:49 PM
 */

package com.novusradix.JavaPop;

/**
 *
 * @author  mom
 */
public class ControlPalette extends javax.swing.JPanel {

    /** Creates new form ControlPalette */
    public ControlPalette() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ToolGroup = new javax.swing.ButtonGroup();
        UpDownButton = new javax.swing.JToggleButton();
        LightningButton = new javax.swing.JToggleButton();

        setMaximumSize(new java.awt.Dimension(128, 64));
        setMinimumSize(new java.awt.Dimension(128, 64));
        setPreferredSize(new java.awt.Dimension(128, 64));
        setLayout(new java.awt.GridLayout());

        ToolGroup.add(UpDownButton);
        UpDownButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/novusradix/JavaPop/icons/UpDown.png"))); // NOI18N
        UpDownButton.setSelected(true);
        UpDownButton.setToolTipText("Raise and lower land");
        UpDownButton.setPreferredSize(new java.awt.Dimension(64, 64));
        add(UpDownButton);

        ToolGroup.add(LightningButton);
        LightningButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/novusradix/JavaPop/icons/Lightning.png"))); // NOI18N
        LightningButton.setToolTipText("Lightning");
        LightningButton.setPreferredSize(new java.awt.Dimension(64, 64));
        add(LightningButton);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton LightningButton;
    private javax.swing.ButtonGroup ToolGroup;
    private javax.swing.JToggleButton UpDownButton;
    // End of variables declaration//GEN-END:variables

}
