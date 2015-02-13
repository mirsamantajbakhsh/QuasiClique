/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.urmiauniversity.it.mst.quasiclique;

import javax.swing.JPanel;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mir Saman Tajbakhsh
 */
@ServiceProvider(service = StatisticsUI.class)
public class QuasiCliqueUI implements StatisticsUI {

    private QuasiCliquePanel panel;
    private QuasiClique myCliqueDetector;

    @Override
    public JPanel getSettingsPanel() {
        panel = new QuasiCliquePanel();
        return panel;
    }

    @Override
    public void setup(Statistics ststcs) {
        this.myCliqueDetector = (QuasiClique) ststcs;
        if (panel != null) {
            panel.setK(myCliqueDetector.getK());
        }
    }

    @Override
    public void unsetup() {
        if (panel != null) {
            myCliqueDetector.setK(panel.getK());
        }
        panel = null;
    }

    @Override
    public Class<? extends Statistics> getStatisticsClass() {
        return QuasiClique.class;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Quasi Clique Community Detector";
    }

    @Override
    public String getShortDescription() {
        return "A simple community detector based on quasi cliques of specific gamma";
    }

    @Override
    public String getCategory() {
        return CATEGORY_NETWORK_OVERVIEW;
    }

    @Override
    public int getPosition() {
        return 800;
    }

}
