package com.loomcom.automata;
/*
 * Copyright 7/10/18 by Stephen Beitzel
 */

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * convenience class to load the uistrings resource bundle and then provide
 * exception-proof accessors for strings
 *
 * @author Stephen Beitzel &lt;sbeitzel@pobox.com&gt;
 */
public class UIStrings {
    public static final String BUTTON_CANCEL = "button.cancel";
    public static final String BUTTON_OK = "button.ok";
    public static final String BUTTON_PAUSE = "button.pause";
    public static final String BUTTON_START = "button.start";
    public static final String BUTTON_STEP = "button.step";

    public static final String ERROR_TEXT_DIMENSIONS_SMALL = "error.text.dimensions.small";
    public static final String ERROR_TEXT_DIMENSIONS_LARGE = "error.text.dimensions.large";
    public static final String ERROR_NUMBERS_ONLY = "error.text.numbersOnly";

    public static final String LABEL_CELLSIZE = "label.cellSize";
    public static final String LABEL_COLUMNS = "label.columns";
    public static final String LABEL_CREATE="label.create";
    public static final String LABEL_INPIXELS = "label.inPixels";
    public static final String LABEL_ROWS = "label.rows";

    public static final String WINDOW_SETUP_TITLE = "window.setup.title";
    public static final String WINDOW_SIM_TITLE = "window.sim.title";

    private static ResourceBundle BUNDLE;

    public static void init(Locale uiLocale) {
        BUNDLE = ResourceBundle.getBundle("uistrings", uiLocale);
    }

    public static String getString(String key) {
        try {
            return BUNDLE.getString(key);
        } catch (Exception e) {
            // probably, it's a missing resource
            return key;
        }
    }
}
