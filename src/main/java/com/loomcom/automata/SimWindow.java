package com.loomcom.automata;
/*
 * Copyright 7/10/18 by Stephen Beitzel
 */

import java.net.URL;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
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
    @FXML public RadioMenuItem _fountainItem;
    @FXML public RadioMenuItem _spaceshipItem;
    @FXML public RadioMenuItem _coeshipItem;
    @FXML public RadioMenuItem _gliderItem;

    private Stage _stage;
    private UpdateThread _updateThread;
    private ToggleGroup _shapeGroup;
    private ToggleGroup _ruleGroup;

    public static void display(Stage parent, int columns, int rows, int cellSize) {
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
        }
    }

    @FXML
    private void initialize() {
        // establish radio groups
        _shapeGroup = new ToggleGroup();
        _shapeGroup.getToggles().addAll(_noneItem, _fountainItem, _spaceshipItem, _coeshipItem, _gliderItem);

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

        // TODO set localized strings on menus, buttons, labels
    }

    private void initCellModel(int rows, int columns, int cellSize) {
        RuleSet selectedSet = (RuleSet) _ruleGroup.getSelectedToggle().getUserData();
        CellModel freshModel = new CellModel(rows, columns, selectedSet);
        _simCanvas.setModel(freshModel, cellSize);
    }

    private void ruleSetChanged(RuleSet rs) {
        if (_simCanvas.getModel() != null) {
            _simCanvas.getModel().setRuleSet(rs);
        }
    }

    public void onDrag(MouseEvent event) {

    }

    public void onClick(MouseEvent event) {

    }

    public void onPause(ActionEvent evt) {

    }

    public void onStart(ActionEvent evt) {

    }

    public void onAbout(ActionEvent evt) {

    }

    public void onGlider(ActionEvent evt) {

    }

    public void onClear(ActionEvent evt) {

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
}
