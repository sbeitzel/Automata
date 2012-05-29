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
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

/**
 * The visual representation of a two dimensional field of cells.  Cells which
 * are alive are represented by a colored rectangle on a background field.
 *
 * @author Seth Morabito
 * @version $Id: CellPanel.java,v 1.10 2003/10/03 23:41:03 sethm Exp $
 */
public class CellPanel extends JComponent implements Observer {
    private int mCols;
    private int mRows;
    private int mCellSize;

    private Color mOutlineColor; // Outline color

    private CellModel mCellModel;

    private boolean mShowCellOutlines = true; // Show outlines by default
    private boolean mShowAging;

    /**
     * Construct a new CellPanel.
     *
     * @param model     The cell model for this panel.
     * @param cellSize  The size of the cells, in pixels.
     */
    public CellPanel(CellModel model, int cellSize) {
        setDoubleBuffered(true);
        mCellModel = model;
        model.addObserver(this);
        mCols = model.getCols();
        mRows = model.getRows();
        mCellSize = cellSize;

        // Default colors
        setForeground(Color.black);
        setBackground(Color.white);
        setOutline(Color.lightGray);

        setSize(mCellSize * mCols, mCellSize * mRows);
        setPreferredSize(new Dimension(mCellSize * mCols, mCellSize * mRows));
    }

    /**
     * Set the cell outline color.
     *
     * @param c The color to use when drawing the cell outlines.
     */
    public void setOutline(Color c) {
        mOutlineColor = c;
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
     * Overridden to remove the default update() behavior,
     * which clears the component on each paint call.
     */
    public void update(Graphics g) {
        return; // do nothing
    }

    /**
     * Implementation of the Observer interface.
     */
    public void update(Observable t, Object o) {
        repaint();
    }

    /**
     * Produce a lighter version of this color.  Not to be confused
     * with the Color.brighen() method!  This method will return a
     * color with a higher alpha value based on the percent parameter.
     */
    private Color lightenColor(Color c, int age) {
        int red = c.getRed();
        int green = c.getGreen();
        int blue = c.getBlue();
        int alpha = c.getAlpha();

        double p = 1.0 - (0.05 * age);

        if (p > 0.20) {
            alpha = (int)(p * alpha);
        } else {
            alpha = (int)(0.20 * alpha);
        }

        return new Color(red, green, blue, alpha);
    }

    /**
     * Draw the cell field.
     */
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        int width = getSize().width;
        int height = getSize().height;

        // Clear the panel.
        g2d.setPaint(getBackground());
        g2d.fill(new Rectangle(0, 0, width, height));

        g2d.setPaint(getForeground());
        for (int i = 0; i < mCols; i++) {
            for (int j = 0; j < mRows; j++) {
                if (mCellModel.getCell(i, j)) {
                    // If cell aging is enabled, draw aged cells
                    int age = 0;
                    if (mShowAging) {
                        age = mCellModel.getCellAge(i, j);
                        g2d.setPaint(lightenColor(getForeground(), age));
                    }
                    g2d.fillRect(i * mCellSize,
                                 j * mCellSize,
                                 mCellSize,
                                 mCellSize);
                    if (mShowAging) {
                        g2d.setPaint(getForeground());
                    }
                }
            }
        }

        // Show outlines if desired
        if (mShowCellOutlines) {
            drawCellOutlines(g2d);
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
    }

    /**
     * Draw a grid showing the outlines of the cells.
     */
    private void drawCellOutlines(Graphics2D g2d) {
        g2d.setPaint(mOutlineColor);
        g2d.setStroke(new BasicStroke(1.0f));

        int width = getSize().width;
        int height = getSize().height;

        // Draw rectangle outlining the entire component
        g2d.draw(new Rectangle(0, 0, width - 1, height - 1));

        // Draw the vertical lines
        for (int i = 0; i < mCols; i++) {
            g2d.drawLine(i * mCellSize, 0, i * mCellSize, height);
        }

        // Draw the horizontal lines
        for (int j = 0; j < mRows; j++) {
            g2d.drawLine(0, j * mCellSize, width, j * mCellSize);
        }
    }
}
