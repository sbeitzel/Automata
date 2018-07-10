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

/**
 * Application setup and launch dialog.
 *
 * @author Seth Morabito
 * @version $Id: Launcher.java,v 1.7 2003/10/13 04:00:24 sethm Exp $
 */
public class Launcher extends JFrame {

    // Cell panel size, and default values
    private int mCellSize = 10;    // Size of cells in pixels
    private int mRows = 64;        // Rows
    private int mCols = 64;        // Columns

    private AutomataFrame mAutomataFrame; // Main window.

    private Dimension mScreenSize;

    // Containers
    private JPanel mIntroPanel;
    private JPanel mInputPanel;
    private JPanel mButtonPanel;

    // Input
    private JTextField mColsField;
    private JTextField mRowsField;
    private JTextField mCellSizeField;

    // Labels
    private JLabel mIntroLabel;
    private JLabel mColsLabel;
    private JLabel mRowsLabel;
    private JLabel mCellSizeLabel;
    private JLabel mPixelLabel;

    // Buttons
    private JButton mOKButton;
    private JButton mCancelButton;

    // Static reference to ourself
    private static Launcher mLauncher;

    private static final String FRAME_TITLE = "Setup";

    /**
     * Construct a default application launcher.
     */
    public Launcher() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupUI();
        pack();
        setResizable(false);
        Utilities.centerWindow(this);
    }

    public static Launcher getLauncher() {
        if (mLauncher == null) {
            mLauncher = new Launcher();
        }

        return mLauncher;
    }
    /**
     * Lay out the visual look.
     */
    private void setupUI() {
        setTitle(FRAME_TITLE);

        // Set the system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            ; // Swallow this exception, because there's really no other
              // useful action to take here.
        }

        mScreenSize = Toolkit.getDefaultToolkit().getScreenSize();

        mIntroPanel = new JPanel();
        mInputPanel = new JPanel();
        mButtonPanel = new JPanel();

        mButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        Font largeFont = new Font("SansSerif", Font.BOLD, 16);
        Font smallFont = new Font("SansSerif", Font.PLAIN, 10);

        mIntroLabel = new JLabel("Create a New Cellular Automata");
        mIntroLabel.setFont(largeFont);

        mIntroPanel.add(mIntroLabel);

        mCellSizeLabel = new JLabel("Cell size");
        mPixelLabel = new JLabel("(in pixels)");
        mPixelLabel.setFont(smallFont);

        mColsLabel = new JLabel("Columns");
        mRowsLabel = new JLabel("Rows");

        mColsField = new JTextField(Integer.toString(mCols));
        mRowsField = new JTextField(Integer.toString(mRows));
        mCellSizeField = new JTextField(Integer.toString(mCellSize));

        mOKButton = new JButton("OK");
        mOKButton.addActionListener(new CreateCellPanelListener());

        mCancelButton = new JButton("Cancel");
        mCancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                System.exit(0);
            }
        });

        mColsField.setColumns(4);
        mRowsField.setColumns(4);
        mCellSizeField.setColumns(2);

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(5,5,5,5);

        mInputPanel.setLayout(gb);

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        gb.setConstraints(mColsLabel, c);
        mInputPanel.add(mColsLabel);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        gb.setConstraints(mColsField, c);
        mInputPanel.add(mColsField);

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        gb.setConstraints(mRowsLabel, c);
        mInputPanel.add(mRowsLabel);

        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.0;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        gb.setConstraints(mRowsField, c);
        mInputPanel.add(mRowsField);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        gb.setConstraints(mCellSizeLabel, c);
        mInputPanel.add(mCellSizeLabel);

        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        gb.setConstraints(mCellSizeField, c);
        mInputPanel.add(mCellSizeField);

        c.gridx = 2;
        c.gridy = 2;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        gb.setConstraints(mPixelLabel, c);
        mInputPanel.add(mPixelLabel);

        mButtonPanel.add(mOKButton);
        mButtonPanel.add(mCancelButton);

        getContentPane().add(mIntroPanel, BorderLayout.NORTH);
        getContentPane().add(mInputPanel, BorderLayout.CENTER);
        getContentPane().add(mButtonPanel, BorderLayout.SOUTH);
    }

    private int getCols() {
        try {
            return Integer.parseInt(mColsField.getText());
        } catch (NumberFormatException ex) {
            return mCols;
        }
    }

    private int getRows() {
        try {
            return Integer.parseInt(mRowsField.getText());
        } catch (NumberFormatException ex) {
            return mRows;
        }
    }

    private int getCellSize() {
        try {
            return Integer.parseInt(mCellSizeField.getText());
        } catch (NumberFormatException ex) {
            return mCellSize;
        }
    }

    /**
     * The main application entry point.
     *
     * @param args Application arguments (not used).
     */
    public static void main(String[] args) {
        Launcher launcher = Launcher.getLauncher();
        launcher.setVisible(true);
    }

    private final class CreateCellPanelListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            int cols = getCols();
            int rows = getRows();
            int cellSize = getCellSize();

            if (cellSize < 0 || cols < 0 || rows < 0) {
                JOptionPane.showMessageDialog(null,
                             "Please use positive numbers.");
                return;
            }

            if (cellSize * cols > mScreenSize.width ||
                cellSize * rows > mScreenSize.height) {
                JOptionPane.showMessageDialog(null,
                            "The values you've chosen would create\n" +
                            "a window too large to fit on the screen.\n" +
                            "Please choose smaller values.");
                return;
            }

            if (mAutomataFrame == null) {
                mAutomataFrame = new AutomataFrame();
            }

            mAutomataFrame.addCells(cols, rows, cellSize);

            mAutomataFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mAutomataFrame.pack();
            mAutomataFrame.setResizable(false);
            Utilities.centerWindow(mAutomataFrame);
            mAutomataFrame.setVisible(true);

            // hide ourselves
            setVisible(false);
        }
    }
}
