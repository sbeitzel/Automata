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

import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * The visual representation of a two dimensional field of cells.  Cells which
 * are alive are represented by a colored rectangle on a background field.
 *
 * @author Seth Morabito
 * @version $Id: CellPanel.java,v 1.10 2003/10/03 23:41:03 sethm Exp $
 */
public class CellPanel extends Canvas implements Observer {
    private int mCols;
    private int mRows;
    private int mCellSize;

    private Color mOutlineColor; // Outline color
    private Color mBackground;
    private Color mForeground;

    private CellModel mCellModel;

    private boolean mShowCellOutlines = true; // Show outlines by default
    private boolean mShowAging = false;

    public CellPanel() {
        super();
    }

    /**
     * Initialize the cell field with a model. This method will cause the CellPanel
     * to resize itself.
     *
     * @param model the model to use
     * @param cellSize the size of a single cell
     */
    public void setModel(CellModel model, int cellSize) {
        if (mCellModel != null) {
            mCellModel.deleteObserver(this);
        }
        mCellModel = model;
        model.addObserver(this);
        mCols = model.getCols();
        mRows = model.getRows();
        mCellSize = cellSize;

        // Default colors
        mBackground = Color.WHITE;
        mForeground = Color.BLACK;
        setOutline(Color.LIGHTGRAY);

        setWidth(mCellSize * mCols);
        setHeight(mCellSize * mRows);
    }

    public CellModel getModel() {
        return mCellModel;
    }

    public int getCellSize() {
        return mCellSize;
    }

    public Color getBackground() {
        return mBackground;
    }

    public void setBackground(Color c) {
        mBackground = c;
        schedulePaint();
    }

    public Color getForeground() {
        return mForeground;
    }

    public void setForeground(Color c) {
        mForeground = c;
        schedulePaint();
    }

    /**
     * Set the cell outline color.
     *
     * @param c The color to use when drawing the cell outlines.
     */
    public void setOutline(Color c) {
        mOutlineColor = c;
        schedulePaint();
    }

    /**
     * Tell the panel whether to draw cell outlines or not.
     *
     * @param b True to show outlines, false otherwise.
     */
    public void showCellOutlines(boolean b) {
        this.mShowCellOutlines = b;
    }

    /**
     * Implementation of the Observer interface.
     */
    @Override
    public void update(Observable t, Object o) {
        schedulePaint();
    }

    public void schedulePaint() {
        // cause the cell field to be redrawn.
        if (Platform.isFxApplicationThread()) {
            paint();
        } else {
            Platform.runLater(this::paint);
        }
    }

    /**
     * Produce a lighter version of this color.  Not to be confused
     * with the Color.brighen() method!  This method will return a
     * color with a higher alpha value based on the percent parameter.
     */
    private Color lightenColor(Color c, int age) {
        double red = c.getRed();
        double green = c.getGreen();
        double blue = c.getBlue();
        double alpha = c.getOpacity();

        double p = 1.0 - (0.05 * age);

        if (p > 0.20) {
            alpha = (p * alpha);
        } else {
            alpha = (0.20 * alpha);
        }

        return new Color(red, green, blue, alpha);
    }

    /**
     * Draw the cell field. This should only be called on the FX thread
     */
    private void paint() {
        if (Platform.isFxApplicationThread()) {
            GraphicsContext g2d = getGraphicsContext2D();

            double width = getWidth();
            double height = getHeight();

            // Clear the panel.
            g2d.setFill(getBackground());
            g2d.fillRect(0, 0, width, height);

            g2d.setFill(getForeground());
            for (int i = 0; i < mCols; i++) {
                for (int j = 0; j < mRows; j++) {
                    if (mCellModel.getCell(i, j)) {
                        // If cell aging is enabled, draw aged cells
                        int age = 0;
                        if (mShowAging) {
                            age = mCellModel.getCellAge(i, j);
                            g2d.setFill(lightenColor(getForeground(), age));
                        }
                        g2d.fillRect(i * mCellSize,
                                     j * mCellSize,
                                     mCellSize,
                                     mCellSize);
                        if (mShowAging) {
                            g2d.setFill(getForeground());
                        }
                    }
                }
            }

            // Show outlines if desired
            if (mShowCellOutlines) {
                drawCellOutlines(g2d);
            }
        }
    }

    /**
     * Return the color used to draw the cell outlines.
     *
     * @return  The color used to draw the cell outlines.
     */
    public Color getOutline() {
        return mOutlineColor;
    }

    /**
     * Enable or disable the display of cell "aging"
     */
    public void setCellAging(boolean b) {
        mShowAging = b;
        schedulePaint();
    }

    /**
     * Draw a grid showing the outlines of the cells.
     */
    private void drawCellOutlines(GraphicsContext g2d) {
        g2d.setStroke(mOutlineColor);
        g2d.setLineWidth(1);

        double width = getWidth();
        double height = getHeight();

        // Draw rectangle outlining the entire component
        g2d.strokeRect(0, 0, width - 1, height - 1);

        // Draw the vertical lines
        for (int i = 0; i < mCols; i++) {
            g2d.strokeLine(i * mCellSize, 0, i * mCellSize, height);
        }

        // Draw the horizontal lines
        for (int j = 0; j < mRows; j++) {
            g2d.strokeLine(0, j * mCellSize, width, j * mCellSize);
        }
    }
}
