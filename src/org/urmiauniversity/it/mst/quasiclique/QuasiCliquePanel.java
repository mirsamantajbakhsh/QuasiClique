/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.urmiauniversity.it.mst.quasiclique;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Mir Saman Tajbakhsh
 */
public class QuasiCliquePanel extends JPanel {

    JTextField kvalue;

    @SuppressWarnings("unchecked")
    public QuasiCliquePanel() {
        //this.setLayout(null);
        org.jdesktop.swingx.JXHeader jXHeader1 = new org.jdesktop.swingx.JXHeader();

        jXHeader1.setDescription("Enter the value for γ (density factor, γ is in (0,1] where 1 stands for complete cliques)."); // NOI18N
        jXHeader1.setTitle("Quasi Clique Community Detector");

        JLabel label = new JLabel("Enter value of γ here:");
        label.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 13));
        this.add(label);
        kvalue = new JTextField();
        this.add(kvalue);
        Insets insets = this.getInsets();

        Dimension size = label.getPreferredSize();
        label.setBounds(20 + insets.left, 30 + insets.top, size.width, size.height);

        Dimension size1 = kvalue.getPreferredSize();
        kvalue.setBounds(20 + insets.left, 130 + insets.top, size1.width + 20, size1.height);

        javax.swing.GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jXHeader1, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(label)
                        .addContainerGap(354, Short.MAX_VALUE))
                .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(kvalue)
                        .addContainerGap(382, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(jXHeader1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(label)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(kvalue)
                        .addContainerGap(187, Short.MAX_VALUE))
        );
    }

    public double getK() {
        double i = 0.0d;
        try {
            i = Double.valueOf(kvalue.getText());
        } catch (Exception ex) {
            return 0;
        }
        return i;
    }

    public void setK(double k) {
        this.kvalue.setText(String.valueOf(k));
    }
}
