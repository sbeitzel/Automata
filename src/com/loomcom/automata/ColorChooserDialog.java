/*
 * Automata, a Cellular Automata explorer.
 *
 * Copyright (c) 2003, Seth J. Morabito <sethm@loomcom.com> All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See  the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.loomcom.automata;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Color chooser dialog.  This dialog allows the user to set colors for the
 * cells, background, and outlines.
 *
 * @author Seth Morabito
 * @version $Id: ColorChooserDialog.java,v 1.6 2003/08/23 21:09:15 sethm Exp $
 */
public class ColorChooserDialog extends JDialog {
    private CellPanel mCellPanel;
    private JColorChooser mColorChooser;
    private JComboBox mElementBox;

    // The cell panel we actually update.
    private CellPanel mPreviewCells;

    private Color mCellColor;
    private Color mBackgroundColor;
    private Color mOutlineColor;

    boolean mSetCellColor;
    boolean mSetBackgroundColor;
    boolean mSetOutlineColor;

    private static final String CELLS_STRING = "Cells";
    private static final String BG_STRING = "Background";
    private static final String OUTLINE_STRING = "Outline";

    /**
     * Construct a color chooser dialog.
     *
     * @param p    The cell panel to set colors for.
     */
    public ColorChooserDialog(CellPanel p) {
        mCellPanel = p;

        JPanel bannerPanel = new JPanel();
        bannerPanel.setLayout(new FlowLayout());
        bannerPanel.setBorder(BorderFactory.
            createTitledBorder("Set color for"));

        mElementBox = new JComboBox();
        mElementBox.addItem(CELLS_STRING);
        mElementBox.addItem(BG_STRING);
        mElementBox.addItem(OUTLINE_STRING);

        mElementBox.addItemListener(new SelectElementListener());

        bannerPanel.add(mElementBox);

        mColorChooser = new JColorChooser();

        // Set the preview panel.
        // Cheat!  Since we already have this component handy, make a
        // small 3x3 one with 20px cells as our preview panel.
        JPanel previewPanel = new JPanel();

        previewPanel.setLayout(new FlowLayout());

        CellModel model = new CellModel(3,3);
        mPreviewCells = new CellPanel(model, 20);
        model.flipCell(1, 1);

        previewPanel.add(mPreviewCells);

        // Some VMs seem to require an explicit size setting here in
        // order for the preview panel component to be displayed
        // properly.
        previewPanel.setSize(mPreviewCells.getSize());
        previewPanel.setBorder(BorderFactory.createEmptyBorder(0,0,1,0));

        mColorChooser.setPreviewPanel(previewPanel);

        mColorChooser.getSelectionModel().addChangeListener(
            new PreviewListener());

        // Dialog OK/Cancel buttons
        JPanel buttonBar = new JPanel();
        buttonBar.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(new SetColorListener());

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                setVisible(false);
            }
        });

        buttonBar.add(okButton);
        buttonBar.add(cancelButton);

        getContentPane().add(bannerPanel, BorderLayout.NORTH);
        getContentPane().add(mColorChooser, BorderLayout.CENTER);
        getContentPane().add(buttonBar, BorderLayout.SOUTH);

        pack();
        setResizable(false);
    }

    /**
     * Target a new cell panel.
     */
    public void setCellPanel(CellPanel p) {
        mCellPanel = p;
        p.setForeground(mCellColor);
        p.setBackground(mBackgroundColor);
        p.setOutline(mOutlineColor);
    }

    /**
     * Apply the new colors to the CellPanel we're updating, then hide.
     */
    final private class SetColorListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            if (mSetCellColor) {
                mCellPanel.setForeground(mCellColor);
            }
            if (mSetBackgroundColor) {
                mCellPanel.setBackground(mBackgroundColor);
            }
            if (mSetOutlineColor) {
                mCellPanel.setOutline(mOutlineColor);
            }
            mCellPanel.repaint();
            // Hide, but stick around.
            setVisible(false);
        }
    }

    /**
     * When the user selects a new element to change the color for,
     * update the color chooser's model to reflect the color that's
     * already there.
     */
    final private class SelectElementListener implements ItemListener {
        public void itemStateChanged(ItemEvent evt) {
            if (mElementBox.getSelectedItem().equals(CELLS_STRING)) {
                mColorChooser.setColor(mPreviewCells.getForeground());
            } else if (mElementBox.getSelectedItem().equals(BG_STRING)) {
                mColorChooser.setColor(mPreviewCells.getBackground());
            } else if (mElementBox.getSelectedItem().equals(OUTLINE_STRING)) {
                mColorChooser.setColor(mPreviewCells.getOutline());
            }

            mPreviewCells.repaint();
        }
    }

    /**
     * When the user selects a new color, update the preview panel
     * and instance colors appropriately.
     */
    final private class PreviewListener implements ChangeListener {
        public void stateChanged(ChangeEvent evt) {
            if (mElementBox.getSelectedItem().equals(CELLS_STRING)) {
                mCellColor = mColorChooser.getColor();
                mSetCellColor = true;
                mPreviewCells.setForeground(mCellColor);
            } else if (mElementBox.getSelectedItem().equals(BG_STRING)) {
                mBackgroundColor = mColorChooser.getColor();
                mSetBackgroundColor = true;
                mPreviewCells.setBackground(mBackgroundColor);
            } else if (mElementBox.getSelectedItem().equals(OUTLINE_STRING)) {
                mOutlineColor = mColorChooser.getColor();
                mSetOutlineColor = true;
                mPreviewCells.setOutline(mOutlineColor);
            }

            mPreviewCells.repaint();
        }
    }
}
