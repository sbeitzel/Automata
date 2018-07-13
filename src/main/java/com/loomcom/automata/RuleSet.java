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
 * Define a set of rules which transform one 2D array into
 * another.
 *
 * @author Seth Morabito
 * @version $Id: RuleSet.java,v 1.8 2003/10/13 04:00:24 sethm Exp $
 */
public class RuleSet {
    final int[] mBornOn;
    final int[] mSurviveOn;
    String mName;
    String mShortName;

    /**
     * Create a new ruleset with a nickname.
     *
     * @param name      The displayable name of this rule, i.e. "Life"
     * @param born      Array of neighbors required for an empty cell to
     *          come to life.
     * @param survive  Array of neighbors required for a living cell to
     *             survive.
     */
    public RuleSet(String name, int[] born, int[] survive) {
        mBornOn = born;
        mSurviveOn = survive;
        mShortName = name;

        makeDisplayableName(name);
    }

    /**
     * Create a new un-named ruleset.
     *
     * @param born      Array of neighbors required for an empty cell to
     *                          come to life.
     * @param survive   Array of neighbors required for a living cell to
     *                                  survive.
     */
    public RuleSet(int[] born, int[] survive) {
        this(null, born, survive);
    }

    /**
     * Transform from one generation of cells to the next.
     */
    public void transform(boolean[][] from, boolean[][] to) {
        if (from == null || to == null) { return; }

        int cols = Utilities.getWidth(to);
        int rows = Utilities.getHeight(to);

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {

                int count = Utilities.getNeighborCount(from, i, j);
                boolean val = false;

                if (!from[i][j]) {
                    // "Born" rules
                    for (int k = 0; k < mBornOn.length; k++) {
                        val |= (count == mBornOn[k]);
                    }
                } else {
                    // "Survive" rules
                    for (int k = 0; k < mSurviveOn.length; k++) {
                        val |= (count == mSurviveOn[k]);
                    }
                }

                to[i][j] = val;
            }
        }
    }

    /**
     * Get the name of this ruleset.
     *
     * @return The displayable name of this ruleset.
     */
    public String getName() {
        return mName;
    }

    /**
     * Get the short name of this ruleset.
     *
     * @return The displayable name of this ruleset.
     */
    public String getShortName() {
        return mShortName;
    }

    /**
     * Munge the parameter string into a fancy displayable name.
     */
    void makeDisplayableName(String s) {
        StringBuffer buf = new StringBuffer();

        if (s != null) {
            buf.append(s);
            buf.append(" (");
        }

        buf.append("B");
        for (int i = 0; i < mBornOn.length; i++) {
            buf.append(Integer.toString(mBornOn[i]));
        }
        buf.append("/S");
        for (int i = 0; i < mSurviveOn.length; i++) {
            buf.append(Integer.toString(mSurviveOn[i]));
        }
        if (s != null) {
            buf.append(")");
        }

        mName = buf.toString();
    }

}
