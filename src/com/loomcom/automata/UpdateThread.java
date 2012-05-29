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
 * Thread which updates the cell model.
 *
 * @author Seth Morabito
 * @version $Id: UpdateThread.java,v 1.5 2003/10/04 01:23:14 sethm Exp $
 */
public class UpdateThread extends Thread {
    CellModel mCellModel;
    int mSleepInterval;
    boolean mStop = false;
    boolean mPause = true;

    public UpdateThread(CellModel m) {
        mCellModel = m;
    }

        /**
         * Set the delay between each cell data update.
         *
         * @param i The delay, in milliseconds.
         */
    public void setSleepInterval(int i) {
        mSleepInterval = i;
    }

    /**
     * Transform the cell model, then wait for a specified time and loop.
     */
    public void run() {
        while (!mStop) {
            while (mPause) {
                try {
                    synchronized(this) {
                        wait();
                        mPause = false;
                    }
                } catch (InterruptedException ex) {
                    System.out.println(ex);
                }
            }

            // Update the cell generation one step.
            mCellModel.transform();

            try {
                Thread.sleep(mSleepInterval);
            } catch (InterruptedException ex) {
                System.out.println(ex);
            }
        }
    }

    /**
     * Start updating the cell data if the thread is currently paused.
     */
    public synchronized void go() {
        this.notifyAll();
    }

    /**
     * Pause the thread.  The thread can be started again using
     * the <tt>go()</tt> method.
     *
     */
    public void pause() {
        mPause = true;
    }

    /**
     * Stop the thread.  The thread cannot be started again once
     * it has been stopped.
     */
    public void doStop() {
        mStop = true;
    }

    /**
     * Returns true of the thread is paused (not updating the model).
     *
     * @return  true if the thread is not currently updating
     *                  the cell data (i.e., in a wait state)
     */
    public boolean isPaused() {
        return mPause;
    }
}
