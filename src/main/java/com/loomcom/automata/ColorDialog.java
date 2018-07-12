package com.loomcom.automata;
/*
 * Copyright 7/12/18 by Stephen Beitzel
 */

import java.net.URL;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * FX version of the color chooser dialog
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class ColorDialog {

    @FXML public Label _foregroundLabel;
    @FXML public ColorPicker _foreground;
    @FXML public Label _backgroundLabel;
    @FXML public ColorPicker _background;
    @FXML public Label _outlineLabel;
    @FXML public ColorPicker _outlinePicker;
    @FXML public Label _previewLabel;
    @FXML public CellPanel _preview;

    public static void display(Color foreground, Color background, Color outline,
                               Consumer<Color> fgConsumer, Consumer<Color> bgConsumer, Consumer<Color> olConsumer) {
        try {
            URL layout = Thread.currentThread().getContextClassLoader()
                               .getResource("ColorDialog.fxml");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(layout);
            DialogPane content = loader.load();
            ColorDialog cd = loader.getController();
            cd._preview.setForeground(foreground);
            cd._preview.setBackground(background);
            cd._preview.setOutline(outline);
            cd._preview.getModel().flipCell(1, 1);

            ButtonType ok = new ButtonType(UIStrings.getString(UIStrings.BUTTON_OK), ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType(UIStrings.getString(UIStrings.BUTTON_CANCEL), ButtonBar.ButtonData.CANCEL_CLOSE);
            content.getButtonTypes().addAll(ok, cancel);

            Dialog<ButtonType> theDialog = new Dialog<>();
            theDialog.setTitle(UIStrings.getString(UIStrings.DIALOG_COLOR_TITLE));
            theDialog.setDialogPane(content);
            Optional<ButtonType> response = theDialog.showAndWait();
            if (response.isPresent() && response.get().getButtonData() == ButtonType.OK.getButtonData()) {
                fgConsumer.accept(cd._foreground.getValue());
                bgConsumer.accept(cd._background.getValue());
                olConsumer.accept(cd._outlinePicker.getValue());
            }
        } catch (Exception e) {
            // SBTODO add logging
        }
    }

    @FXML
    private void initialize() {
        _foregroundLabel.setText(UIStrings.getString(UIStrings.LABEL_FOREGROUND));
        _backgroundLabel.setText(UIStrings.getString(UIStrings.LABEL_BACKGROUND));
        _outlineLabel.setText(UIStrings.getString(UIStrings.LABEL_OUTLINE));
        _preview.setModel(new CellModel(3, 3), 20);
    }

    @FXML
    @SuppressWarnings("unused")
    public void onForeground(ActionEvent evt) {
        _preview.setForeground(_foreground.getValue());
    }

    @FXML
    @SuppressWarnings("unused")
    public void onBackground(ActionEvent evt) {
        _preview.setBackground(_background.getValue());
    }

    @FXML
    @SuppressWarnings("unused")
    public void onOutline(ActionEvent evt) {
        _preview.setOutline(_outlinePicker.getValue());
    }
}
