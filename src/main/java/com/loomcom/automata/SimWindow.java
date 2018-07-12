package com.loomcom.automata;
/*
 * Copyright 7/10/18 by Stephen Beitzel
 */

import java.net.URL;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Controller for the automata scene
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class SimWindow implements Observer {

    @FXML public MenuItem _newItem;
    @FXML public MenuItem _quitItem;
    @FXML public CheckMenuItem _showOutlinesItem;
    @FXML public CheckMenuItem _showAgingItem;
    @FXML public MenuItem _editColorsItem;
    @FXML public MenuItem _clearDisplayItem;
    @FXML public Menu _menuGlider;
    @FXML public Menu _ruleMenu;
    @FXML public MenuItem _aboutItem;
    @FXML public Label _generationsLabel;
    @FXML public Button _startButton;
    @FXML public Label _generationLabel;
    @FXML public CellPanel _simCanvas;
    @FXML public RadioMenuItem _noneItem;
    @FXML public ChoiceBox<Integer> _speedBox;
    public Button _stepButton;

    private Stage _stage;
    private UpdateThread _updateThread;
    private ToggleGroup _shapeGroup;
    private ToggleGroup _ruleGroup;

    // these members are for keeping track of drawing state during user interaction
    private boolean[][] _shapeToDraw = null;
    private int _lastCellX = -1;
    private int _lastCellY = -1;

    static void display(Stage parent, int columns, int rows, int cellSize) {
        try {
            Stage stage = new Stage();
            stage.initOwner(parent);
            URL layout = Thread.currentThread().getContextClassLoader()
                               .getResource("SimWindow.fxml");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(layout);
            Parent root = loader.load();
            SimWindow sw = loader.getController();
            sw._stage = stage;
            sw.initCellModel(rows, columns, cellSize);

            int canvasHeight = cellSize * columns;
            stage.setTitle(UIStrings.getString(UIStrings.WINDOW_SIM_TITLE));
            stage.setScene(new javafx.scene.Scene(root, Math.max(canvasHeight, 410), 90 + canvasHeight));
            stage.show();
            stage.centerOnScreen();
        } catch (Exception e) {
            // nothing to do, really. We could log an error, but there's nothing a user can do to fix it.
            // SBTODO add logging
        }
    }

    /**
     * A list of built-in shapes we can draw.  Most of these are
     * gliders in the Life rule set, as well as some others.
     *
     * @return      A map of pattern names and arrays
     */
    private static TreeMap<String, boolean[][]> setupGliders() {

        TreeMap<String, boolean[][]> gliderMap = new TreeMap<>();

        // "The Glider"
        gliderMap.put("The Glider", new boolean[][] {
                          {true, false, false},   // Row 1
                          {true, false, true},    // Row 2
                          {true, true, false}     // Row 3
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
    private static TreeMap<String, RuleSet> setupRuleSets() {

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

    @FXML
    private void initialize() {
        // setup the shape menu
        _shapeGroup = new ToggleGroup();
        _shapeGroup.getToggles().add(_noneItem);
        _noneItem.setUserData(null);
        Map<String, boolean[][]> shapeMap = setupGliders();
        _shapeGroup.selectToggle(_noneItem);
        for (Map.Entry<String, boolean[][]> entry : shapeMap.entrySet()) {
            String name = entry.getKey();
            boolean[][] shapeData = entry.getValue();
            RadioMenuItem shapeItem = new RadioMenuItem(name);
            shapeItem.setToggleGroup(_shapeGroup);
            shapeItem.setUserData(shapeData);
            _menuGlider.getItems().add(shapeItem);
            shapeItem.setOnAction(this::onGlider);
        }

        // populate the ruleset menu
        Map<String, RuleSet> ruleMap = setupRuleSets();
        _ruleGroup = new ToggleGroup();
        boolean isSet = false;
        for (Map.Entry<String, RuleSet> entry : ruleMap.entrySet()) {
            RadioMenuItem ruleItem = new RadioMenuItem(entry.getKey());
            RuleSet rs = entry.getValue();
            ruleItem.setToggleGroup(_ruleGroup);
            ruleItem.setUserData(rs);
            if (!isSet || "Life".equals(entry.getValue().getShortName())) {
                isSet = true;
                ruleItem.setSelected(true);
            }
            _ruleMenu.getItems().add(ruleItem);
            ruleItem.setOnAction((event) -> ruleSetChanged(rs));
        }

        // populate the speed choicebox
        _speedBox.setItems(FXCollections.observableArrayList(Integer.valueOf(1),
                                                             Integer.valueOf(2),
                                                             Integer.valueOf(5),
                                                             Integer.valueOf(10),
                                                             Integer.valueOf(20)));
        _speedBox.getSelectionModel().select(0);

        // SBTODO set localized strings on menus, buttons, labels
        _stepButton.setText(UIStrings.getString(UIStrings.BUTTON_STEP));
    }

    private void initCellModel(int rows, int columns, int cellSize) {
        RuleSet selectedSet = (RuleSet) _ruleGroup.getSelectedToggle().getUserData();
        CellModel freshModel = new CellModel(rows, columns, selectedSet);
        freshModel.addObserver(this);
        if (_simCanvas.getModel() != null) {
            _simCanvas.getModel().deleteObserver(this);
        }
        _simCanvas.setModel(freshModel, cellSize);
        _simCanvas.schedulePaint();
        if (_updateThread != null) {
            _updateThread.doStop();
        }
        _updateThread = new UpdateThread(freshModel);
        onSetSpeed(null);
        _updateThread.start();
    }

    private void ruleSetChanged(RuleSet rs) {
        if (_simCanvas.getModel() != null) {
            _simCanvas.getModel().setRuleSet(rs);
        }
    }

    @FXML
    public void onDrag(MouseEvent event) {
        CellModel model = _simCanvas.getModel();
        double x = event.getX();
        double y = event.getY();
        int cellX = (int) (x / _simCanvas.getCellSize());
        int cellY = (int) (y / _simCanvas.getCellSize());

        boolean drawOrErase = !event.isShiftDown(); // hold down shift to erase (set false)

        if (cellX != _lastCellX || cellY != _lastCellY) {
            model.setCell(cellX, cellY, drawOrErase);
            _lastCellX = cellX;
            _lastCellY = cellY;
        }
    }

    @FXML
    public void onClick(MouseEvent event) {
        CellModel model = _simCanvas.getModel();
        double x = event.getX();
        double y = event.getY();
        int cellX = (int) (x / _simCanvas.getCellSize());
        int cellY = (int) (y / _simCanvas.getCellSize());

        if (_shapeToDraw != null) {
            model.drawShape(cellX, cellY, _shapeToDraw);
        } else {
            model.flipCell(cellX, cellY);
        }
    }

    @SuppressWarnings("unused")
    private void onPause(ActionEvent evt) {
        if (_updateThread != null) {
            _updateThread.pause();
            _startButton.setText(UIStrings.getString(UIStrings.BUTTON_START));
            _startButton.setOnAction(this::onStart);
            _stepButton.setDisable(false);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    public void onStart(ActionEvent evt) {
        if (_updateThread != null && _updateThread.isPaused()) {
            _startButton.setText(UIStrings.getString(UIStrings.BUTTON_PAUSE));
            _startButton.setOnAction(this::onPause);
            _stepButton.setDisable(true);
            _updateThread.go();
        }
    }

    @FXML
    @SuppressWarnings("unused")
    public void onAbout(ActionEvent evt) {
        AboutDialog.display();
    }

    /**
     * Handler for shape menu items. Since this is bound from code in the {@link #initialize()} method
     * and not from FXML we don't need to make it public and we don't need to annotate it.
     *
     * @param evt the menu selection event that we're handling
     */
    @SuppressWarnings("unused")
    private void onGlider(ActionEvent evt) {
        _shapeToDraw = (boolean[][]) _shapeGroup.getSelectedToggle().getUserData();
    }

    @FXML
    public void onClear(ActionEvent evt) {
        onPause(evt);
        _simCanvas.getModel().reset();
    }

    @FXML
    @SuppressWarnings("unused")
    public void onEditColors(ActionEvent evt) {
        ColorDialog.display(_simCanvas.getForeground(), _simCanvas.getBackground(), _simCanvas.getOutline(),
                            _simCanvas::setForeground, _simCanvas::setBackground, _simCanvas::setOutline);
    }

    @FXML
    @SuppressWarnings("unused")
    public void onQuit(ActionEvent evt) {
        // shutdown any worker thread
        if (_updateThread != null) {
            try {
                // Offer a short window to politely join with
                // the thread before shutting down, good manners
                _updateThread.doStop();
                _updateThread.join(500);
            } catch (InterruptedException ex) {
                // if we had logging enabled, we would log this
            }
        }
        Platform.exit();
    }

    @FXML
    @SuppressWarnings("unused")
    public void onNew(ActionEvent evt) {
        // display a setup window
        Setup.display((Stage) _stage.getOwner());
        // and close this window
        _stage.close();
    }

    @Override
    public void update(Observable o, Object arg) {
        // the cell model has updated. time to redraw! Remember to make sure that happens on the FX thread.
        Runnable r = () -> _generationLabel.setText(Integer.toString(_simCanvas.getModel().getGeneration()));
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            Platform.runLater(r);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    public void onShowAging(ActionEvent evt) {
        _simCanvas.setCellAging(_showAgingItem.isSelected());
    }

    @FXML
    @SuppressWarnings("unused")
    public void onShowOutlines(ActionEvent evt) {
        _simCanvas.showCellOutlines(_showOutlinesItem.isSelected());
    }

    @FXML
    @SuppressWarnings("unused")
    public void onSetSpeed(ActionEvent evt) {
        int speed = _speedBox.getValue().intValue();
        if (_updateThread != null) {
            _updateThread.setSleepInterval(1000 / speed);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    public void onStep(ActionEvent evt) {
        _simCanvas.getModel().transform();
    }
}
