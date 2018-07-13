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

/**
 * The data model representing the cells.  This model represents the
 * current state of all the "cells" (boolean bits) in the two-dimensional
 * world.
 *
 * @author Seth Morabito
 * @version $Id: CellModel.java,v 1.6 2003/07/09 23:32:49 sethm Exp $
 */
public class CellModel extends java.util.Observable {
    private boolean[][] mCells;         // The actual data.
    private boolean[][] mTempCells;     // Temp array used when transforming

    private int[][] mCellAges;  // Array of cell ages.

    private int mCols;  // Width of the cell array
    private int mRows;  // Height of the cell array
    private RuleSet mRuleSet;   // Rule set to use when transforming

    private int mGeneration;    // Current "generation"

    /**
     * Create a new two dimensional cell array with width <tt>x</tt>
     * and height <tt>y</tt>.
     *
     * @param x The number of cell columns
     * @param y The number of cell rows
     */
    public CellModel(int x, int y) {
        this(x, y, null);
    }

    /**
     * Create a new two dimensional cell array with width <tt>x</tt>
     * and height <tt>y</tt>, and set the initial rule set.
     *
     * @param x The number of cell columns
     * @param y The number of cell rows
     * @param rs        The RuleSet to use at creation time
     */
    public CellModel(int x, int y, RuleSet rs) {
        mCells = new boolean[x][y];
        mTempCells = new boolean[x][y];
        mCellAges = new int[x][y];
        mCols = x;
        mRows = y;
        mRuleSet = rs;
    }

    /**
     * Return the boolean value of the cell at <tt>(x,y)</tt>
     *
     * @param x The cell <tt>x</tt> coordinate
     * @param y The cell <tt>y</tt> coordinate
     * @return  True if the cell is alive, false if not
     */
    public boolean getCell(int x, int y) {
        if (x < 0 || y < 0 || x > (mCols - 1) || y > (mRows - 1))
            return false;

        return mCells[x][y];
    }

    /**
     * Return the age, in generations, of the cell at <tt>(x,y)</tt>
     */
    public int getCellAge(int x, int y) {
        if (x < 0 || y < 0 || x > (mCols - 1) || y > (mRows - 1))
            return 0;

        return mCellAges[x][y];
    }

    /**
     * Toggle the cell at <tt>(x,y)</tt>.
     *
     * @param x The cell <tt>x</tt> coordinate.
     * @param y The cell <tt>y</tt> coordinate.
     */
    public void flipCell(int x, int y) {
        if (x < 0 || y < 0 || x > (mCols - 1) || y > (mRows - 1))
            return;

        mCells[x][y] = !mCells[x][y];
        setChanged();
        notifyObservers();
    }

    /**
     * Set the cell at <tt>(x,y)</tt> with the supplied boolean value.
     *
     * @param x The cell <tt>x</tt> coordinate.
     * @param y The cell <tt>y</tt> coordinate.
     * @param b The value to set.
     */
    public void setCell(int x, int y, boolean b) {
        if (x < 0 || y < 0 || x > (mCols - 1) || y > (mRows - 1))
            return;

        mCells[x][y] = b;
        setChanged();
        notifyObservers();
    }

    /**
     * Get the number of columns in this cell field.
     *
     * @return  The number of columns.
     */
    public int getCols() {
        return mCols;
    }

    /**
     * Get the number of rows in this cell field.
     *
     * @return  The number of rows.
     */
    public int getRows() {
        return mRows;
    }

    /**
     * Reset the field, clearing the data and setting the generation
     * count back to 0.
     */
    public void reset() {
        for (int i = 0; i < mCols; i++) {
            for (int j = 0; j < mRows; j++) {
                mCells[i][j] = false;
                mCellAges[i][j] = 0;
            }
        }
        mGeneration = 0;
        setChanged();
        notifyObservers();
    }

    /**
     * Return the current cell generation.
     *
     * @return  Cell generation.
     */
    public int getGeneration() {
        return mGeneration;
    }

    /**
     * Transform to the next generation of cells.
     */
    public void transform() {
        if (mRuleSet == null) { return; }
        mRuleSet.transform(mCells, mTempCells);

        for (int i = 0; i < mCols; i++) {
            for (int j = 0; j < mRows; j++) {
                if (mCells[i][j] & mTempCells[i][j])
                    mCellAges[i][j]++;
                else
                    mCellAges[i][j] = 0;
            }
        }

        // Swap the arrays, speedily
        boolean[][] b = mCells;
        mCells = mTempCells;
        mTempCells = b;
        b = null;
        mGeneration++;

        setChanged();
        notifyObservers();
    }


    /**
     * Set the RuleSet which will be used when transforming from one
     * generation to the next.
     *
     * @param rs        The RuleSet to use when transforming
     */
    public void setRuleSet(RuleSet rs) {
        mRuleSet = rs;
    }

    /**
     * Return the current rule set used for transforms.
     *
     * @return  The current rule set.
     */
    public RuleSet getRuleSet() {
        return mRuleSet;
    }

    /**
     * Place a shape into the cell field, centered on point <tt>(x,y)</tt>
     *
     * @param pattern   A 2D array of booleans representing the pattern bits.
     */
    public void drawShape(int x, int y, boolean[][] pattern) {
        int width = Utilities.getWidth(pattern);
        int height = Utilities.getHeight(pattern);


        // On the cell panel, the shape will begin at origin
        // ((x - width / 2), (y - height / 2))
        int originX = (x - width / 2);
        int originY = (y - height / 2);

        // Set the appropriate bits in the mCells array
        int patternX = 0;
        outer:
        for (int i = originX; i < (originX + width) ; i++, patternX++) {
            int patternY = 0;
            for (int j = originY; j < (originY + height); j++, patternY++) {
                if (i < 0 || i > mCols - 1)
                    continue;
                if (j < 0 || j > mRows - 1)
                    continue outer;
                if (pattern[patternX][patternY])
                    mCells[i][j] = true;
            }
        }
        setChanged();
        notifyObservers();
    }
}
