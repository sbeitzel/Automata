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

/**
 * A few useful static utilities.
 * 
 * @author Seth Morabito
 * @version $Id: Utilities.java,v 1.4 2003/07/09 23:20:11 sethm Exp $
 */
public class Utilities {
    
    /**
     * Center a window on screen.
     * 
     * @param w	The window to center.
     * @deprecated This isn't necessary with JavaFX
     */
    @Deprecated
    public static void centerWindow(Window w) {
	Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	
	int width = w.getWidth();
	int height = w.getHeight();
	
	w.setLocation(d.width/2 - width/2, d.height/2 - height/2);
    }
    
    /**
     * Determine the number of living neighbor cells for a point in a 2D
     * array.
     * 
     * This version unrolls the loop and implements "wrap-around",
     * eliminating edge effects.  The code is longer and messier, but because
     * of unrolling the loop, we actually see about a 2x execution time
     * reduction.
     *
     * @param cells	The array to look in.
     * @param x	The cell's x coordinate.
     * @param y	The cell's y coordinate.
     */
    public static int getNeighborCount(boolean[][] cells, int x, int y) {
	int neighborCount = 0;
	
	int cols = cells.length;
	int rows = cells[0].length;
	
	int x_right = x < cols-1 ? x+1 : 0; 
	int x_left = x > 0 ? x-1 : cols-1;
	int y_top = y > 0 ? y-1 : rows-1;
	int y_bottom = y < rows-1 ? y+1 : 0;

	if (cells[x_left][y_top]) neighborCount++;
	if (cells[x_left][y]) neighborCount++;
	if (cells[x_left][y_bottom]) neighborCount++;
	if (cells[x][y_top]) neighborCount++;
	if (cells[x][y_bottom]) neighborCount++;
	if (cells[x_right][y_top]) neighborCount++;
	if (cells[x_right][y]) neighborCount++;
	if (cells[x_right][y_bottom]) neighborCount++;
	return neighborCount;
    }
    
    /**
     * Safely return the "width" of a 2D array.
     */
    public static int getWidth(boolean[][] array) {
	return array.length;
    }
    
    /**
     * Safely return the "height" of a 2D array.
     */
    public static int getHeight(boolean[][] array) {
	if (array.length > 0) {
	    return array[0].length;
	} else {
	    return 0;
	}
    }
}
