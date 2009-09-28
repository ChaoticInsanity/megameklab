/*
 * MegaMekLab - Copyright (C) 2009
 *
 * Original author - jtighe (torren@users.sourceforge.net)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */

package megameklab.com.ui.Vehicle.views;

import java.awt.Color;
import java.awt.Font;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import megamek.common.Mounted;
import megamek.common.Tank;
import megamek.common.loaders.MtfFile;
import megameklab.com.util.DropTargetCriticalList;
import megameklab.com.util.IView;
import megameklab.com.util.RefreshListener;

public class CriticalView extends IView {

    /**
     *
     */
    private static final long serialVersionUID = -6960975031034494605L;

    private JPanel leftPanel = new JPanel();
    private JPanel rightPanel = new JPanel();
    private JPanel frontPanel = new JPanel();
    private JPanel rearPanel = new JPanel();
    private JPanel bodyPanel = new JPanel();
    private JPanel turretPanel = new JPanel();

    private JPanel topPanel = new JPanel();
    private JPanel middlePanel = new JPanel();
    private JPanel bottomPanel = new JPanel();
    private RefreshListener refresh;

    private boolean showEmpty = false;

    public CriticalView(Tank unit, boolean showEmpty, RefreshListener refresh) {
        super(unit);
        this.showEmpty = showEmpty;
        this.refresh = refresh;

        JPanel mainPanel = new JPanel();

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.X_AXIS));
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

        topPanel.add(frontPanel);
        topPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Front"));
        mainPanel.add(topPanel);

        middlePanel.add(leftPanel);
        leftPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Left Side"));
        middlePanel.add(bodyPanel);
        bodyPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Body"));
        middlePanel.add(rightPanel);
        rightPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Right Side"));
        mainPanel.add(middlePanel);

        rearPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Rear"));
        bottomPanel.add(rearPanel);
        mainPanel.add(bottomPanel);

        this.add(mainPanel);

        if (unit.getInternal(Tank.LOC_TURRET) > 0) {
            turretPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Turret"));
            this.add(turretPanel);
        }
    }

    public void updateRefresh(RefreshListener refresh) {
        this.refresh = refresh;
    }

    public void refresh() {
        leftPanel.removeAll();
        rightPanel.removeAll();
        bodyPanel.removeAll();
        frontPanel.removeAll();
        rearPanel.removeAll();
        turretPanel.removeAll();

        synchronized (unit) {
            for (int location = 0; location < unit.locations(); location++) {
                // JPanel locationPanel = new JPanel();
                Vector<String> critNames = new Vector<String>(1, 1);

                for (Mounted m : unit.getEquipment()) {
                    try {
                        if (m.getLocation() == location) {
                            StringBuffer critName = new StringBuffer(m.getName());
                            if (critName.length() > 25) {
                                critName.setLength(25);
                                critName.append("...");
                            }
                            if (m.isRearMounted()) {
                                critName.append("(R)");
                            }

                            critNames.add(critName.toString());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                if (critNames.size() == 0) {
                    critNames.add(MtfFile.EMPTY);
                }
                DropTargetCriticalList criticalSlotList = new DropTargetCriticalList(critNames, unit, refresh, showEmpty);
                criticalSlotList.setVisibleRowCount(critNames.size());
                criticalSlotList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                criticalSlotList.setFont(new Font("Arial", Font.PLAIN, 10));
                criticalSlotList.setName(Integer.toString(location));
                criticalSlotList.setBorder(BorderFactory.createEtchedBorder(Color.WHITE.brighter(), Color.BLACK.darker()));
                switch (location) {
                case Tank.LOC_FRONT:
                    frontPanel.add(criticalSlotList);
                    break;
                case Tank.LOC_LEFT:
                    leftPanel.add(criticalSlotList);
                    break;
                case Tank.LOC_RIGHT:
                    rightPanel.add(criticalSlotList);
                    break;
                case Tank.LOC_BODY:
                    bodyPanel.add(criticalSlotList);
                    break;
                case Tank.LOC_REAR:
                    rearPanel.add(criticalSlotList);
                    break;
                case Tank.LOC_TURRET:
                    turretPanel.add(criticalSlotList);
                    break;
                }
            }
            frontPanel.repaint();
            bodyPanel.repaint();
            leftPanel.repaint();
            rightPanel.repaint();
            rearPanel.repaint();
            turretPanel.repaint();

        }
    }
}