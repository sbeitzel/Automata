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
import java.util.*;
import javax.swing.*;

import javafx.scene.shape.Rectangle;

/**
 * The main Cellular Automata application window.
 *
 * @author Seth Morabito
 * @version  $Id: AutomataFrame.java,v 1.14 2003/10/13 04:00:23 sethm Exp $
 */
public class AutomataFrame extends Rectangle implements Observer {

    // Constants
    private static final String FRAME_TITLE = "Automata";

    // Pseudo-Constants defined during initialization
    private String DEFAULT_SPEED;
    private JRadioButtonMenuItem DEFAULT_RULESET;
    private JRadioButtonMenuItem DEFAULT_SHAPE;

    // The size of our cells, in pixels
    int mCellSize;

    // Thread which will update the Cell Panel
    private UpdateThread mUpdateThread;

    // The shape we want to draw, or null for no shape.
    private boolean[][] mDrawGlider;

    // Cell panel mouse and mousemotion listener
    private CellSelectListener mCellSelectListener;

    // The cell data model
    private CellModel mCellModel;

    // Components
    private CellPanel mCellPanel;
    private ColorChooserDialog mColorChooser;
    private AboutDialog mAboutDialog;
    private JLabel mGenerationCountLabel;
    private JButton mStepButton;
    private JToggleButton mStartStopButton;
    private JComboBox mSpeedSelector;

    // The menu bar and associated menu items
    private JMenuBar mMenuBar;

    private JMenu mFileMenu;
    private JMenuItem mAboutMenuItem;
    private JMenuItem mNewAutomataMenuItem;
    private JMenuItem mQuitMenuItem;

    private JMenu mDrawMenu;
    private JCheckBoxMenuItem mShowOutlinesMenuItem;
    private JMenuItem mEditColorsMenuItem;
    private JCheckBoxMenuItem mShowAgingMenuItem;
    private JMenuItem mResetMenuItem;
    private JMenu mGliderSubMenu;
    private ButtonGroup mGliderButtonGroup;

    private JMenu mRulesMenu;
    private ButtonGroup mRulesButtonGroup;

    // Containers associated with the menu items
    private TreeMap mGliderMap;
    private TreeMap mRulesMap;

    // UI layout containers
    private JPanel mCellPanelContainer;
    private JPanel mButtonContainer;
    private JPanel mUIButtons;
    private JPanel mStatusContainer;

    /**
     * Construct a new frame to hold our automata simulation.
     * Just a pass-through to setupUI() at the moment.
     */
    public AutomataFrame() {
        setupUI();
    }

    /**
     * Add a new cell panel to the main app window.
     */
    public void addCells(int cols, int rows, int cellSize) {
        // remove any existing model
        removeCells();

        mCellSize = cellSize;

        mCellModel = new CellModel(cols, rows);
        mCellModel.addObserver(this);

        mCellPanel = new CellPanel(mCellModel, cellSize);

        mCellSelectListener = new CellSelectListener();

        mCellPanel.addMouseListener(mCellSelectListener);
        mCellPanel.addMouseMotionListener(mCellSelectListener);

        mCellPanelContainer.add(mCellPanel);

        mUpdateThread = new UpdateThread(mCellModel);

        // set initial conditions
        setDefaults();

        // Register with the color chooser
        if (mColorChooser != null) {
            mColorChooser.setCellPanel(mCellPanel);
        }

        // Start the thread which updates the cell model.
        mUpdateThread.start();
    }

    /**
     * Remove the current cell panel from the main app window.
     */
    public void removeCells() {
        mCellSize = 0;

        if (mUpdateThread != null) {
            mUpdateThread.doStop();
        }

        if (mCellPanelContainer != null) {
            mCellPanelContainer.remove(mCellPanel);
        }

        if (mCellPanel != null) {
            mCellPanel.removeMouseListener(mCellSelectListener);
            mCellPanel.removeMouseMotionListener(mCellSelectListener);
        }

        mCellModel = null;
        mCellSelectListener = null;
        mCellPanel = null;
    }

    /**
     * Each time a new cell panel is added, reset the UI
     * widgets to default values.
     */
    private void setDefaults() {
        // Make sure the Start/Stop button is reset
        stopCellUpdates();

        if (mCellSize > 3) {
            mShowOutlinesMenuItem.setSelected(true);
            mCellPanel.showCellOutlines(true);
        } else {
            mShowOutlinesMenuItem.setSelected(false);
            mShowOutlinesMenuItem.setEnabled(false);
            mCellPanel.showCellOutlines(false);
        }

        mSpeedSelector.setSelectedItem(DEFAULT_SPEED);

        // Programatically select the default ruleset
        DEFAULT_RULESET.setSelected(true);
        DEFAULT_RULESET.doClick();

        // Programatically select the default ruleset
        DEFAULT_SHAPE.setSelected(true);
        DEFAULT_SHAPE.doClick();

        mGenerationCountLabel.setText("0");

        mShowAgingMenuItem.setSelected(true);
        mCellPanel.setCellAging(true);
    }


    /**
     * Implementation of the Observer interface.
     */
    public void update(Observable t, Object o) {
        mGenerationCountLabel.setText(
            Integer.toString(mCellModel.getGeneration()));
    }

    /**
     * Attempt to cleanly shut down the application in a polite way.
     */
    private void shutdown() {
        if (mUpdateThread != null) {
            try {
                // Offer a short window to politely join with
                // the thread before shutting down, good manners
                mUpdateThread.doStop();
                mUpdateThread.join(500);
            } catch (InterruptedException ex) {
                ;
            }
        }
        System.exit(0);
    }

    /**
     * Lay out the UI.
     */
    private void setupUI() {
        setTitle(FRAME_TITLE);

        // Components
        JComboBox speedComboBox;
        JCheckBox cellOutlineSelector;
        JLabel cellTypeLabel;
        JLabel speedLabel;

        // The main menu bar.
        mMenuBar = new JMenuBar();

        int menuKeyMask =
            java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        /*
         *  The "File" Menu
         */
        mFileMenu = new JMenu("File");
        mFileMenu.setMnemonic(KeyEvent.VK_F);

        mAboutMenuItem = new JMenuItem("About");
        mAboutMenuItem.setMnemonic(KeyEvent.VK_A);
        mAboutMenuItem.addActionListener(new AboutListener());

        mNewAutomataMenuItem = new JMenuItem("New Automata...");
        mNewAutomataMenuItem.setMnemonic(KeyEvent.VK_N);
        mNewAutomataMenuItem.addActionListener(new NewAutomataListener());

        mQuitMenuItem = new JMenuItem("Quit", KeyEvent.VK_Q);
        mQuitMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                                                            KeyEvent.VK_Q, menuKeyMask));

        mQuitMenuItem.addActionListener(new QuitListener());

        // Assemble the menu
        mFileMenu.add(mAboutMenuItem);
        mFileMenu.add(mNewAutomataMenuItem);
        mFileMenu.add(mQuitMenuItem);

        /*
         *  The "Draw" Menu
         */
        mDrawMenu = new JMenu("Draw");
        mDrawMenu.setMnemonic(KeyEvent.VK_D);

        // Show or hide cell outlines
        mShowOutlinesMenuItem = new JCheckBoxMenuItem("Show Cell Outlines");
        mShowOutlinesMenuItem.setMnemonic(KeyEvent.VK_O);
        mShowOutlinesMenuItem.addItemListener(new ShowOutlinesListener());

        // Enable or disable the showing of cell "age"
        mShowAgingMenuItem = new JCheckBoxMenuItem("Show Cell Aging");
        mShowAgingMenuItem.setMnemonic(KeyEvent.VK_A);
        mShowAgingMenuItem.addItemListener(new ShowAgingListener());

        // Set custom colors...
        mEditColorsMenuItem = new JMenuItem("Edit Colors...");
        mEditColorsMenuItem.setMnemonic(KeyEvent.VK_C);
        mEditColorsMenuItem.addActionListener(new EditColorsListener());

        // Clear the panel and reset...
        mResetMenuItem = new JMenuItem("Clear Display");
        mResetMenuItem.setMnemonic(KeyEvent.VK_L);
        mResetMenuItem.addActionListener(new ResetPanelListener());

        mGliderSubMenu = new JMenu("Draw Glider");

        // A set of shapes we know how to draw...
        mGliderButtonGroup = new ButtonGroup();

        mGliderMap = setupGliders();

        final String noGlider = "None";

        ShapeMenuListener shapeMenuListener = new ShapeMenuListener(noGlider);

        JRadioButtonMenuItem noGliderItem = new JRadioButtonMenuItem(noGlider);

        DEFAULT_SHAPE = noGliderItem;

        noGliderItem.addActionListener(shapeMenuListener);
        mGliderButtonGroup.add(noGliderItem);
        mGliderSubMenu.add(noGliderItem);

        for (Iterator i = mGliderMap.keySet().iterator(); i.hasNext(); ) {
            String s = (String) i.next();

            JRadioButtonMenuItem item = new JRadioButtonMenuItem(s);
            item.addActionListener(shapeMenuListener);
            mGliderButtonGroup.add(item);
            mGliderSubMenu.add(item);
        }

        // Populate the "Draw" menu
        mDrawMenu.add(mShowOutlinesMenuItem);
        mDrawMenu.add(mShowAgingMenuItem);
        mDrawMenu.addSeparator();
        mDrawMenu.add(mEditColorsMenuItem);
        mDrawMenu.addSeparator();
        mDrawMenu.add(mResetMenuItem);
        mDrawMenu.addSeparator();
        mDrawMenu.add(mGliderSubMenu);

        /*
         *  The "Rules" Menu
         */
        mRulesMap = setupRuleSets();

        mRulesMenu = new JMenu("Rules");
        mRulesButtonGroup = new ButtonGroup();

        // Handle menu events from the Rules menu
        RulesMenuListener rulesMenuListener = new RulesMenuListener();

        // Populate the "Rules" menu
        boolean first = true;
        for (Iterator i = mRulesMap.keySet().iterator(); i.hasNext();) {
            String s = (String) i.next();

            JRadioButtonMenuItem item = new JRadioButtonMenuItem(s);
            item.addActionListener(rulesMenuListener);

            // We should automatically choose a rule set to start with.
            // It should be either the first rule set on the menu, or "Life",
            // if it is found.
            if (first || "Life".equals(((RuleSet)mRulesMap.get(s)).getShortName())) {
                DEFAULT_RULESET = item;
                first = false;
            }

            mRulesButtonGroup.add(item);
            mRulesMenu.add(item);
        }

        // Assembling the menu bar
        mMenuBar.add(mFileMenu);
        mMenuBar.add(mDrawMenu);
        mMenuBar.add(mRulesMenu);

        this.setJMenuBar(mMenuBar);

        // The "Start/Pause" button
        mStartStopButton = new JToggleButton("Start", false);
        mStartStopButton.addItemListener(new StartButtonListener());

        // Combo Box that determines the generation update speed
        mSpeedSelector = new JComboBox();
        mSpeedSelector.addItem("1");
        mSpeedSelector.addItem("2");
        mSpeedSelector.addItem("5");
        mSpeedSelector.addItem("10");
        mSpeedSelector.addItem("20");

        // Set the default speed to be selected when creating a new
        // automata cell panel.
        DEFAULT_SPEED = "5";

        mSpeedSelector.addActionListener(new SpeedSelectorListener());

        // "Step" button
        mStepButton = new JButton("Step");
        mStepButton.addActionListener(new StepButtonListener());

        // Container for the Cell Panel
        mCellPanelContainer = new JPanel();
        mCellPanelContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
        // mCellPanelContainer.add(mCellPanel);

        // Container for UI buttons and status labels
        mButtonContainer = new JPanel();

        mUIButtons = new JPanel();

        GridBagLayout gridBag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        mUIButtons.setLayout(gridBag);
        c.fill = GridBagConstraints.HORIZONTAL;

        speedLabel = new JLabel("Generations per second");
        c.gridx = 0;
        c.gridy = 0;
        gridBag.setConstraints(speedLabel, c);
        mUIButtons.add(speedLabel);

        c.gridx = 0;
        c.gridy = 1;
        gridBag.setConstraints(mSpeedSelector, c);
        mUIButtons.add(mSpeedSelector);

        c.gridx = 1;
        c.gridy = 1;
        gridBag.setConstraints(mStartStopButton, c);
        mUIButtons.add(mStartStopButton);

        c.gridx = 2;
        c.gridy = 1;
        gridBag.setConstraints(mStepButton, c);
        mUIButtons.add(mStepButton);

        // Status label
        mStatusContainer = new JPanel();

        mGenerationCountLabel = new JLabel("0", JLabel.CENTER);
        mGenerationCountLabel.setForeground(Color.red);

        mStatusContainer.setLayout(new BorderLayout());
        mStatusContainer.add(new JLabel("Generations"), BorderLayout.NORTH);
        mStatusContainer.add(mGenerationCountLabel, BorderLayout.SOUTH);

        mButtonContainer.add(mUIButtons, BorderLayout.WEST);
        mButtonContainer.add(mStatusContainer, BorderLayout.EAST);

        getContentPane().add(mCellPanelContainer, BorderLayout.CENTER);
        getContentPane().add(mButtonContainer, BorderLayout.SOUTH);
    }

    /**
     * Pause refreshing the cell generations.
     */
    private void stopCellUpdates() {
        if (mUpdateThread != null) {
            mStepButton.setEnabled(true);
            mStepButton.doClick();
        }
    }

    /**
     * Update one cell generation, then stop.
     */
    private void stepCellUpdates() {
        if (mCellPanel != null && mUpdateThread.isPaused()) {
            mCellModel.transform();
        }
    }

    /**
     * A list of built-in shapes we can draw.  Most of these are
     * gliders in the Life rule set, as well as some others.
     *
     * @return      A map of pattern names and arrays
     */
    private TreeMap setupGliders() {

        TreeMap gliderMap = new TreeMap();

        // "The Glider"
        gliderMap.put("The Glider", new boolean[][] {
                          {true, false, false},   // Row 1
                          {true, false, true},    // Row 2
                          {true, true, false}             // Row 3
                      });

        // A "Fountain" glider
        gliderMap.put("Fountain", new boolean[][] {
                          {true, true, true, false, false, false},
                          {true, false, false, false, true, true},
                          {false, true, true, true, true, true},
                          {false, false, false, false, false, false},
                          {false, true, true, true, true, true},
                          {true, false, false, false, true, true},
                          {true, true, true, false, false, false}
                      });

        // "The Coe Ship" glider
        gliderMap.put("The Coe Ship", new boolean[][] {
                          {false, true, true, false, false, false, false, false, false, false},
                          {true, true, false, true, true, false, false, false, false, false},
                          {false, true, true, true, true, false, false, false, false, false},
                          {false, false, true, true, false, false, false, false, false, false},
                          {false, false, false, true, false, false, false, false, false, false},
                          {false, true, false, false, false, true, false, false, false, false},
                          {true, false, false, false, false, false, true, false, true, true},
                          {true, false, false, false, false, false, true, true, false, false},
                          {true, true, true, true, true, true, false, false, false, false},
                      });

        // "Lightweight Spaceship" glider
        gliderMap.put("Lightweight Spaceship", new boolean[][] {
                          {false, true, true, true},
                          {true, false, false, true},
                          {false, false, false, true},
                          {false, false, false, true},
                          {true, false, true, false}
                      });

        // More, as time permits...
        return gliderMap;
    }

    /**
     * Populate the list of rule sets.
     *
     * NOTE:  To add new rule sets to the application, list 'em here!
     * Future versions will allow creation of new rule sets on the fly at
     * through a nifty dialog.
     */
    private TreeMap setupRuleSets() {

        LinkedList<RuleSet> rules = new LinkedList<>();
        TreeMap<String, RuleSet> rulesMap = new TreeMap<>();

        // Start with the classic.
        rules.add(new RuleSet("Life", new int[]{3}, new int[]{2,3}));

        rules.add(new RuleSet("Amoeba", new int[]{3,5,7}, new int[]{1,3,5,8}));
        rules.add(new RuleSet("Assimilation", new int[]{3,4,5},
                              new int[]{4,5,6,7}));
        rules.add(new RuleSet("Bacteria", new int[]{3,4}, new int[]{4,5,6}));
        rules.add(new RuleSet("Blinkers", new int[]{3,4,5}, new int[]{2}));
        rules.add(new RuleSet("Blossom", new int[]{2,3}, new int[]{2,3}));
        rules.add(new RuleSet("Bugs", new int[]{3,5,6,7},
                              new int[]{1,5,6,7,8}));
        rules.add(new RuleSet("Coagulations", new int[]{3,7,8},
                              new int[]{2,3,5,6,7,8}));
        rules.add(new RuleSet("Coral", new int[]{3},
                              new int[]{4,5,6,7,8}));
        rules.add(new RuleSet("Day & Night", new int[]{3,6,7,8},
                              new int[]{3,4,6,7,8}));
        rules.add(new RuleSet("Diamoeba", new int[]{3,5,6,7,8},
                              new int[]{5,6,7,8}));
        rules.add(new RuleSet("Gnarl", new int[]{1}, new int[]{1}));
        rules.add(new RuleSet("H-trees", new int[]{1},
                              new int[]{0,1,2,3,4,5,6,7,8}));
        rules.add(new RuleSet("HighLife", new int[]{3,6}, new int[]{2,3}));
        rules.add(new RuleSet("Holstein", new int[]{3,5,6,7,8},
                              new int[]{4,6,7,8}));
        rules.add(new RuleSet("Iceballs", new int[]{2,5,6,7,8},
                              new int[]{5,6,7,8}));
        rules.add(new RuleSet("Land Rush", new int[]{3,6},
                              new int[]{2,3,4,5,7,8}));
        rules.add(new RuleSet("Life Without Death", new int[]{3},
                              new int[]{0,1,2,3,4,5,6,7,8}));
        rules.add(new RuleSet("LongLife", new int[]{3,4,5}, new int[]{5}));
        rules.add(new RuleSet("Majority", new int[]{4,5,6,7,8},
                              new int[]{5,6,7,8}));
        rules.add(new RuleSet("Maze", new int[]{3}, new int[]{1,2,3,4,5}));
        rules.add(new RuleSet("Move", new int[]{3,6,8}, new int[]{2,4,5}));
        rules.add(new RuleSet("Pseudo Life", new int[]{3,5,7},
                              new int[]{2,3,8}));
        rules.add(new RuleSet("Replicator", new int[]{1,3,5,7},
                              new int[]{1,3,5,7}));
        rules.add(new RuleSet("Seeds", new int[]{2},
                              new int[]{}));
        rules.add(new RuleSet("Serviettes", new int[]{2,3,4},
                              new int[]{}));

        // Rulesets without nicknames
        rules.add(new RuleSet(new int[]{2,4,8}, new int[]{2,4,8}));
        rules.add(new RuleSet(new int[]{3,4,5}, new int[]{2,4,5}));
        rules.add(new RuleSet(new int[]{3,4}, new int[]{2,5}));
        rules.add(new RuleSet(new int[]{3,5}, new int[]{2,4}));

        // "Probabilistic" rulesets
        rules.add(new ChanceRuleSet("Randomized Life", new int[]{3},
                                    new int[]{2,3},
                                    99.0, 99.0));

        for (RuleSet r : rules) {
            rulesMap.put(r.getName(), r);
        }

        return rulesMap;
    }

    /*
     * Event Listener Implementations
     */
    private final class StartButtonListener implements ItemListener {
        public void itemStateChanged(ItemEvent evt) {
            JToggleButton b = (JToggleButton) evt.getSource();
            if (mUpdateThread != null) {
                if (evt.getStateChange() == ItemEvent.SELECTED) {
                    mStartStopButton.setText("Pause");
                    mStepButton.setEnabled(false);
                    mUpdateThread.go();
                } else {
                    mStartStopButton.setText("Start");
                    mStepButton.setEnabled(true);
                    mUpdateThread.pause();
                }
            }
        }
    }

    private final class StepButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            stepCellUpdates();
        }
    }

    private final class SpeedSelectorListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            JComboBox source = (JComboBox) evt.getSource();
            String s = (String) source.getSelectedItem();
            int gps = Integer.valueOf(s).intValue();
            if (gps > 0 && gps < 1000) {
                mUpdateThread.setSleepInterval(1000 / gps);
            }
        }
    }

    private final class RulesMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            String s = evt.getActionCommand();
            RuleSet rs = (RuleSet) mRulesMap.get(s);
            mCellModel.setRuleSet(rs);
        }
    }

    private final class ShapeMenuListener implements ActionListener {
        private final String noGlider;
        private ShapeMenuListener(String noGlider) {
            super();
            this.noGlider = noGlider;
        }
        public void actionPerformed(ActionEvent evt) {
            String s = evt.getActionCommand();
            // Our special case is a single cell.  If we're just
            // drawing single cells, there's no need to set any
            // glider shape.
            if (noGlider.equals(s)) {
                mDrawGlider = null;
                return;
            }

            mDrawGlider = (boolean[][]) mGliderMap.get(s);
        }
    }

    private final class ResetPanelListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            stopCellUpdates();
            mCellModel.reset();
        }
    }

    private final class EditColorsListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            if (mColorChooser == null) {
                mColorChooser = new ColorChooserDialog(mCellPanel);
                Utilities.centerWindow(mColorChooser);
            }
            mColorChooser.setVisible(true);
        }
    }

    private final class ShowAgingListener implements ItemListener {
        public void itemStateChanged(ItemEvent evt) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                mCellPanel.setCellAging(true);
            } else {
                mCellPanel.setCellAging(false);
            }
            mCellPanel.repaint();
        }
    }

    private final class ShowOutlinesListener implements ItemListener {
        public void itemStateChanged(ItemEvent evt) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                mCellPanel.showCellOutlines(true);
            } else {
                mCellPanel.showCellOutlines(false);
            }
            mCellPanel.repaint();
        }
    }

    private final class QuitListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            shutdown();
        }
    }

    private final class NewAutomataListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            // Hide ourselves...
            setVisible(false);

            // Remove our cells.
            removeCells();

            // Show the launcher.
            Launcher launcher = Launcher.getLauncher();
            launcher.setVisible(true);
        }
    }

    private final class AboutListener implements ActionListener {
        public void actionPerformed(ActionEvent evt) {
            if (mAboutDialog == null) {
                mAboutDialog = new AboutDialog();
                mAboutDialog.pack();
            }
            Utilities.centerWindow(mAboutDialog);
            mAboutDialog.setVisible(true);
        }
    }

    /**
     * Listen for mouse events in the Cell Panel.
     */
    private final class CellSelectListener
        implements MouseListener, MouseMotionListener {
        int mLastCellX = 0;
        int mLastCellY = 0;

        // MouseListener implementations

        /*
         *Toggle the clicked-on cell
         */
        public void mouseClicked(MouseEvent evt) {
            CellPanel source = (CellPanel)evt.getSource();
            int x = evt.getX();
            int y = evt.getY();
            int cellX = x / mCellSize;
            int cellY = y / mCellSize;

            if (mDrawGlider != null) {
                mCellModel.drawShape(cellX, cellY, mDrawGlider);
            } else {
                mCellModel.flipCell(cellX, cellY);
            }
        }
        public void mouseEntered(MouseEvent evt) {
        }
        public void mouseExited(MouseEvent evt) {
        }
        public void mousePressed(MouseEvent evt) {
        }
        public void mouseReleased(MouseEvent evt) {
        }

        // MouseMotionListener implementations

        /*
         *  Allow primitive pattern drawing while dragging the mouse.
         */
        public void mouseDragged(MouseEvent evt) {
            int x = evt.getX();
            int y = evt.getY();
            int cellX = x / mCellSize;
            int cellY = y / mCellSize;
            boolean drawOrErase =
                !((evt.getModifiers()
                   & (InputEvent.SHIFT_DOWN_MASK | InputEvent.SHIFT_MASK))
                  != 0);
            if (cellX != mLastCellX || cellY != mLastCellY) {
                mCellModel.setCell(cellX, cellY, drawOrErase);

                mLastCellX = cellX;
                mLastCellY = cellY;
            }
        }
        public void mouseMoved(MouseEvent evt) {
        }
    }
}
