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
 * A subclass of RuleSet which adds probability to the rules.
 *
 * Why?  Because.
 *
 *
 * @author Seth Morabito
 * @version $Id: ChanceRuleSet.java,v 1.5 2003/07/09 23:32:49 sethm Exp $
 */
public class ChanceRuleSet extends RuleSet {

    private double mBp; // "Birth" probability
    private double mSp; // "Survival" probability

    /**
     *
     * @param name      The displayable name of this rule
     * @param born      Array of neighbors required for an empty cell to
     *                          come to life.
     * @param survive   Array neighbors required for a living cell to
     *                                  survive.
     * @param bp        The probability, between 0.0 and 100.0, that new cell
     *                          birth will succeed.
     * @param sp        The probability, between 0.0 and 100.0, that cell
     *                          survival will succeed.
     */
    public ChanceRuleSet(String name, int[] born, int[] survive,
                         double bp, double sp)
    {
        super(name, born, survive);
        mBp = bp;
        mSp = sp;
    }


    public ChanceRuleSet(int[] born, int[] survive,
                         double bp, double sp)
    {
        super(null, born, survive);
        mBp = bp;
        mSp = sp;
    }

    public void transform(boolean[][] from, boolean[][] to) {
        if (from == null || to == null) { return; }

        int cols = Utilities.getWidth(to);
        int rows = Utilities.getHeight(to);

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {

                // Compute a value between 0.0 and 100.0
                double chance = Math.random() * 100.0;

                int count = Utilities.getNeighborCount(from, i, j);
                boolean val = false;

                if (!from[i][j]) {
                    // "Born" rules
                    for (int k = 0; k < mBornOn.length; k++) {
                        val |= (count == mBornOn[k] && chance <= mBp);
                    }
                } else {
                    // "Survive" rules
                    for (int k = 0; k < mSurviveOn.length; k++) {
                        val |= (count == mSurviveOn[k] && chance <= mSp);
                    }
                }

                to[i][j] = val;
            }
        }
    }
}
