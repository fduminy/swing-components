/**
 * Swing-Components is a library of swing components.
 *
 * Copyright (C) 2013-2014 Fabien DUMINY (fabien [dot] duminy [at] webmails [dot] com)
 *
 * Swing-Components is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * Swing-Components is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */
package fr.duminy.components.swing.listpanel;

import fr.duminy.components.swing.FixtureUtilities;
import org.apache.commons.lang3.StringUtils;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JPanelFixture;

import javax.swing.*;
import java.util.EnumSet;

import static fr.duminy.components.swing.listpanel.EditingFeature.*;
import static fr.duminy.components.swing.listpanel.ManualOrderFeature.DOWN_BUTTON_NAME;
import static fr.duminy.components.swing.listpanel.ManualOrderFeature.UP_BUTTON_NAME;
import static fr.duminy.components.swing.listpanel.StandardListPanelFeature.EDITING;
import static fr.duminy.components.swing.listpanel.StandardListPanelFeature.MANUAL_ORDER;

/**
 * A fixture to help testing a {@link fr.duminy.components.swing.listpanel.ListPanel}.
 *
 * @param <B> The class of items in the list.
 * @param <C> The class of list component (example : a JList).
 */
public class ListPanelFixture<B, C extends JComponent> extends JPanelFixture {
    @SuppressWarnings("unchecked")
    public ListPanelFixture(Robot robot, String componentName) {
        this(robot, FixtureUtilities.find(robot, ListPanel.class, componentName));
    }

    public ListPanelFixture(Robot robot, ListPanel<B, C> panel) {
        super(robot, panel);
    }

    public JButtonFixture upButton() {
        requireFeature(UP_BUTTON_NAME, MANUAL_ORDER);
        return new JButtonFixture(robot, findByName(UP_BUTTON_NAME, JButton.class));
    }

    public JButtonFixture downButton() {
        requireFeature(DOWN_BUTTON_NAME, MANUAL_ORDER);
        return new JButtonFixture(robot, findByName(DOWN_BUTTON_NAME, JButton.class));
    }

    public JButtonFixture addButton() {
        requireFeature(ADD_BUTTON_NAME, EDITING);
        return new JButtonFixture(robot, findByName(ADD_BUTTON_NAME, JButton.class));
    }

    public JButtonFixture removeButton() {
        requireFeature(REMOVE_BUTTON_NAME, EDITING);
        return new JButtonFixture(robot, findByName(REMOVE_BUTTON_NAME, JButton.class));
    }

    public JButtonFixture updateButton() {
        requireFeature(UPDATE_BUTTON_NAME, EDITING);
        return new JButtonFixture(robot, findByName(UPDATE_BUTTON_NAME, JButton.class));
    }

    public JButtonFixture userButton(String buttonName) {
        return new JButtonFixture(robot, findByName(buttonName, JButton.class));
    }

    public ListPanelFixture<B, C> requireOnlyFeatures(StandardListPanelFeature... expectedFeatures) {
        EnumSet<StandardListPanelFeature> actualFeatures = ListPanelTest.getActualFeatures(listPanel());

        if (!actualFeatures.equals(ListPanelTest.copyOf(StandardListPanelFeature.class, expectedFeatures))) {
            String message = String.format("The ListPanel '%s' must have only features {%s} but has actual features {%s}",
                    component().getName(), StringUtils.join(expectedFeatures, ','), StringUtils.join(actualFeatures, ','));
            throw new IllegalStateException(message);
        }

        return this;
    }

    private void requireFeature(String buttonName, StandardListPanelFeature requiredFeature) {
        if (!listPanel().hasFeature(requiredFeature)) {
            String message = String.format("The button '%s' requires ListPanel '%s' to have feature %s", buttonName, component().getName(), requiredFeature);
            throw new IllegalStateException(message);
        }
    }

    @SuppressWarnings("unchecked")
    private ListPanel<B, C> listPanel() {
        return (ListPanel<B, C>) component();
    }

    //TODO add a method for list fixture (JList or JTable ...)
}
