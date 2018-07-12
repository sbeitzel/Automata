package com.loomcom.automata;
/*
 * Copyright 7/10/18 by Stephen Beitzel
 */

import java.net.URL;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

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
    @FXML public Button _pauseButton;
    @FXML public Label _generationLabel;
    @FXML public CellPanel _simCanvas;
    @FXML public RadioMenuItem _noneItem;
    @FXML public ChoiceBox<Integer> _speedBox;

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

    @FXML
    private void initialize() {
        // setup the shape menu
        _shapeGroup = new ToggleGroup();
        _shapeGroup.getToggles().add(_noneItem);
        _noneItem.setUserData(null);
        Map<String, boolean[][]> shapeMap = AutomataFrame.setupGliders();
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
        Map<String, RuleSet> ruleMap = AutomataFrame.setupRuleSets();
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
    }

    private void initCellModel(int rows, int columns, int cellSize) {
        RuleSet selectedSet = (RuleSet) _ruleGroup.getSelectedToggle().getUserData();
        CellModel freshModel = new CellModel(rows, columns, selectedSet);
        _simCanvas.setModel(freshModel, cellSize);
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

    @FXML
    @SuppressWarnings("unused")
    public void onPause(ActionEvent evt) {
        if (_updateThread != null) {
            _updateThread.pause();
            _startButton.setDisable(false);
            _pauseButton.setDisable(true);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    public void onStart(ActionEvent evt) {
        if (_updateThread != null && _updateThread.isPaused()) {
            _startButton.setDisable(true);
            _pauseButton.setDisable(false);
            _updateThread.go();
        }
    }

    @FXML
    @SuppressWarnings("unused")
    public void onAbout(ActionEvent evt) {
        AboutDialog.display((Stage) _stage.getOwner());
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

    public void onEditColors(ActionEvent evt) {

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
}
