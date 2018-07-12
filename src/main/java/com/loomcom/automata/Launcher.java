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

import java.util.Locale;

import com.aquafx_project.AquaFx;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Application setup and launch dialog.
 *
 * @author Seth Morabito
 * @version $Id: Launcher.java,v 1.7 2003/10/13 04:00:24 sethm Exp $
 */
public class Launcher extends Application {
    // Static reference to ourself
    private static Launcher mLauncher;

    private Stage _primary;

    @Override
    public void start(Stage primaryStage) throws Exception {
        UIStrings.init(Locale.getDefault());
        mLauncher = this;
        _primary = primaryStage;
        AquaFx.style(); // this should be conditionally executed; only call on MacOS
        // display the setup dialog
        Setup.display(primaryStage);
    }

    @Override
    public void stop() {
    }

    public static Stage getPrimaryStage() {
        return mLauncher._primary;
    }
}
