package com.loomcom.automata;
/*
 * Copyright 7/10/18 by Stephen Beitzel
 */

import java.net.URL;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Controller for the setup dialog.
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class Setup {

    @FXML public Label _createLabel;
    @FXML public Label _columnLabel;
    @FXML public Label _rowLabel;
    @FXML public Label _sizeLabel;
    @FXML public Label _inPixelsLabel;
    @FXML public Button _okButton;
    @FXML public Button _cancelButton;
    @FXML public TextField _columnField;
    @FXML public TextField _rowField;
    @FXML public TextField _sizeField;

    private Stage _stage;

    public static void display(Stage parent) {
        try {
            Stage stage = new Stage();
            stage.initOwner(parent);
            URL layout = Thread.currentThread().getContextClassLoader()
                               .getResource("Setup.fxml");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(layout);
            Parent root = loader.load();
            Setup setup = loader.getController();
            setup._stage = stage;

            stage.setTitle(UIStrings.getString(UIStrings.WINDOW_SETUP_TITLE));
            stage.setScene(new javafx.scene.Scene(root, 190, 370));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.show();
            stage.centerOnScreen();
        } catch (Exception e) {
            // nothing to do, really. We could log an error, but there's nothing a user can do to fix it.
        }
    }

    @FXML
    private void initialize() {
        // set label text to the localized strings
        _createLabel.setText(UIStrings.getString(UIStrings.LABEL_CREATE));
        _columnLabel.setText(UIStrings.getString(UIStrings.LABEL_COLUMNS));
        _rowLabel.setText(UIStrings.getString(UIStrings.LABEL_ROWS));
        _sizeLabel.setText(UIStrings.getString(UIStrings.LABEL_CELLSIZE));
        _inPixelsLabel.setText(UIStrings.getString(UIStrings.LABEL_INPIXELS));

        // set control text
        _okButton.setText(UIStrings.getString(UIStrings.BUTTON_OK));
        _cancelButton.setText(UIStrings.getString(UIStrings.BUTTON_CANCEL));
    }

    @FXML
    @SuppressWarnings("unused")
    public void onOk(ActionEvent evt) {
        // validate that we've got reasonable numbers
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        int rows;
        int columns;
        int cellSize;
        try {
            rows = Integer.parseInt(_rowField.getText());
            columns = Integer.parseInt(_columnField.getText());
            cellSize = Integer.parseInt(_sizeField.getText());
            if (rows < 1 || columns < 1 || cellSize < 1) {
                // zero or negative dimension. Not displayable.
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(UIStrings.getString(UIStrings.ERROR_TEXT_DIMENSIONS_SMALL));
                alert.showAndWait();
            } else {
                if (cellSize * columns > primaryScreenBounds.getWidth() ||
                        cellSize * rows > primaryScreenBounds.getHeight()) {
                    // dimension(s) too large to fit on screen
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText(UIStrings.getString(UIStrings.ERROR_TEXT_DIMENSIONS_LARGE));
                    alert.showAndWait();
                } else {
                    // create a new sim window
                    SimWindow.display((Stage) _stage.getOwner(), columns, rows, cellSize);
                    // hide the setup window
                    _stage.close();
                }
            }
        } catch (Exception e) {
            // can't convert one or more of the dimensions into an integer
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(UIStrings.getString(UIStrings.ERROR_NUMBERS_ONLY));
            alert.showAndWait();
        }
    }

    @FXML
    @SuppressWarnings("unused")
    public void onCancel(ActionEvent evt) {
        _stage.close();
        Platform.exit();
    }
}
