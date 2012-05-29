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
import java.awt.event.*;
import javax.swing.*;

/**
 * Information about the application.  A standard "About" box, to stroke ego.
 *
 * @author Seth Morabito
 * @version $Id: AboutDialog.java,v 1.11 2003/10/04 00:37:59 sethm Exp $
 */
public class AboutDialog extends JFrame {

    private static String dateString = "October 3, 2003";
    private static String versionString = "1.2.4";

    /**
     * Construct an About Box frame.
     */
    public AboutDialog() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createEmptyBorder(18,18,18,18));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        Font largeFont = new Font("SansSerif", Font.BOLD, 18);
        Font midFont = new Font("SansSerif", Font.BOLD, 14);
        Font textFont = new Font("SansSerif", Font.PLAIN, 14);
        Font smallFont = new Font("SansSerif", Font.PLAIN, 10);

        JLabel nameLabel = new JLabel("Cellular Automata Explorer");
        nameLabel.setFont(largeFont);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel authorLabel = new JLabel("Seth Morabito");
        authorLabel.setFont(midFont);
        authorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel companyLabel = new JLabel("Loom Communications");
        companyLabel.setFont(textFont);
        companyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel urlLabel = new JLabel("http://www.loomcom.com/");
        urlLabel.setFont(textFont);
        urlLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel versionLabel = new JLabel("Version " + versionString);
        versionLabel.setFont(smallFont);
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel dateLabel = new JLabel(dateString);
        dateLabel.setFont(smallFont);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextArea license =
            new JTextArea("Distributed under the terms of the GNU\n" +
                          "Public License.  For details, see the file\n" +
                          "LICENSE.TXT, or http://www.gnu.org/licenses/gpl.html");
        license.setFont(smallFont);
        license.setEditable(false);
        license.setBackground(getBackground());
        license.setAlignmentX(Component.LEFT_ALIGNMENT);


        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0,10)));
        infoPanel.add(authorLabel);
        infoPanel.add(companyLabel);
        infoPanel.add(urlLabel);
        infoPanel.add(versionLabel);
        infoPanel.add(dateLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0,10)));
        infoPanel.add(license);

        JButton okButton = new JButton("OK");
        okButton.setSelected(true);

        okButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    dispose();
                }
            });

        JPanel buttonBar = new JPanel();
        buttonBar.setLayout(new FlowLayout(FlowLayout.CENTER));

        buttonBar.add(okButton);

        getContentPane().add(infoPanel, BorderLayout.CENTER);
        getContentPane().add(buttonBar, BorderLayout.SOUTH);

        setResizable(false);
    }
}
