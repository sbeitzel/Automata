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


import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.web.WebView;

/**
 * Information about the application.  A standard "About" box, to stroke ego.
 *
 * @author Seth Morabito
 * @version $Id: AboutDialog.java,v 1.11 2003/10/04 00:37:59 sethm Exp $
 */
public class AboutDialog {

    @FXML public WebView _webView;

    public static void display() {
        try {
            URL layout = Thread.currentThread().getContextClassLoader()
                               .getResource("AboutDialog.fxml");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(layout);
            DialogPane content = loader.load();

            ButtonType ok = new ButtonType(UIStrings.getString(UIStrings.BUTTON_OK), ButtonBar.ButtonData.OK_DONE);
            content.getButtonTypes().add(ok);

            Dialog<ButtonType> theDialog = new Dialog<>();
            theDialog.setTitle(UIStrings.getString(UIStrings.DIALOG_ABOUT_TITLE));
            theDialog.setDialogPane(content);
            theDialog.showAndWait();
        } catch (Exception e) {
            // SBTODO add logging
        }
    }

    @FXML
    private void initialize() {
        try {
            URL aboutURL = Thread.currentThread().getContextClassLoader().getResource("AboutPage.html");
            assert aboutURL != null;
            _webView.getEngine().load(aboutURL.toString());
        } catch (Exception e) {
            // SBTODO add logging
        }
    }
}
