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
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JPanelFixture;

import javax.swing.*;

import static fr.duminy.components.swing.listpanel.EditingFeature.*;
import static fr.duminy.components.swing.listpanel.ManualOrderFeature.DOWN_BUTTON_NAME;
import static fr.duminy.components.swing.listpanel.ManualOrderFeature.UP_BUTTON_NAME;

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

    public JButtonFixture addButton() {
        return new JButtonFixture(robot, findByName(ADD_BUTTON_NAME, JButton.class));
    }

    public JButtonFixture removeButton() {
        return new JButtonFixture(robot, findByName(REMOVE_BUTTON_NAME, JButton.class));
    }

    public JButtonFixture upButton() {
        return new JButtonFixture(robot, findByName(UP_BUTTON_NAME, JButton.class));
    }

    public JButtonFixture downButton() {
        return new JButtonFixture(robot, findByName(DOWN_BUTTON_NAME, JButton.class));
    }

    public JButtonFixture updateButton() {
        return new JButtonFixture(robot, findByName(UPDATE_BUTTON_NAME, JButton.class));
    }

    public JButtonFixture userButton(String buttonName) {
        return new JButtonFixture(robot, findByName(buttonName, JButton.class));
    }

    //TODO add a method for list fixture (JList or JTable ...)
}
