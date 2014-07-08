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
package fr.duminy.components.swing.form;

import fr.duminy.components.swing.FixtureUtilities;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JPanelFixture;

/**
 * A fixture to help testing a {@link fr.duminy.components.swing.form.JFormPane}.
 */
public class JFormPaneFixture extends JPanelFixture {
    public JFormPaneFixture(Robot robot, String panelName) {
        super(robot, FixtureUtilities.find(robot, JFormPane.class, panelName));
    }

    public JFormPaneFixture(Robot robot, Class<?> beanClass) {
        super(robot, find(robot, beanClass));
    }

    public JButtonFixture okButton() {
        return button(JFormPane.OK_BUTTON_NAME);
    }

    public JButtonFixture cancelButton() {
        return button(JFormPane.CANCEL_BUTTON_NAME);
    }

    private static JFormPane find(org.fest.swing.core.Robot robot, Class<?> beanClass) {
        String name = JFormPane.getDefaultPanelName(beanClass);
        return FixtureUtilities.find(robot, JFormPane.class, name);
    }
}
