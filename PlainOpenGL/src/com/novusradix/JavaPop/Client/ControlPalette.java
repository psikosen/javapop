/*
 * ControlPalette.java
 *
 * Created on November 13, 2008, 7:49 PM
 */
package com.novusradix.JavaPop.Client;

import com.novusradix.JavaPop.Client.Tools.BaseTool;
import com.novusradix.JavaPop.Client.Tools.Tool.ToolType;

/**
 *
 * @author  mom
 */
public class ControlPalette extends javax.swing.JPanel {

    /** Creates new form ControlPalette */
    public ControlPalette() {
        initComponents();
        BaseTool.InitControlPalette(this);
    }

    public void setTool(ToolType t) {
        switch (t) {
            case RaiseLower:
                this.UpDownButton.setSelected(true);
                break;
            default:
                throw new UnsupportedOperationException("setTool only implemented for RaiseLower");
        }
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
        setLayout(new java.awt.GridLayout(1, 0));

        ToolGroup.add(UpDownButton);
        UpDownButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/novusradix/JavaPop/icons/UpDown.png"))); // NOI18N
        UpDownButton.setSelected(true);
        UpDownButton.setToolTipText("Raise and lower land");
        UpDownButton.setPreferredSize(new java.awt.Dimension(64, 64));
        UpDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpDownButtonActionPerformed(evt);
            }
        });
        add(UpDownButton);

        ToolGroup.add(LightningButton);
        LightningButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/novusradix/JavaPop/icons/Lightning.png"))); // NOI18N
        LightningButton.setToolTipText("Lightning");
        LightningButton.setPreferredSize(new java.awt.Dimension(64, 64));
        LightningButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LightningButtonActionPerformed(evt);
            }
        });
        add(LightningButton);
    }// </editor-fold>//GEN-END:initComponents

private void UpDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpDownButtonActionPerformed
    BaseTool.setTool(ToolType.RaiseLower, false);
}//GEN-LAST:event_UpDownButtonActionPerformed

private void LightningButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LightningButtonActionPerformed
    BaseTool.setTool(ToolType.Lightning, false);
}//GEN-LAST:event_LightningButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton LightningButton;
    private javax.swing.ButtonGroup ToolGroup;
    private javax.swing.JToggleButton UpDownButton;
    // End of variables declaration//GEN-END:variables
}