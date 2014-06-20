/**
 * Swing-Components is a library of swing components.
 *
 * Copyright (C) 2013-2013 Fabien DUMINY (fabien [dot] duminy [at] webmails [dot] com)
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

import org.fest.swing.core.Robot;
import org.fest.swing.core.TypeMatcher;
import org.fest.swing.exception.ComponentLookupException;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JPanelFixture;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * A fixture to help testing a {@link fr.duminy.components.swing.form.JFormPane}.
 */
public class JFormPaneFixture extends JPanelFixture {
    public static String dumpComponents(org.fest.swing.core.Robot robot) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        robot.printer().printComponents(new PrintStream(os));
        return "\nComponent hierarchy:\n" + os.toString();
    }

    public JFormPaneFixture(Robot robot, String panelName) {
        super(robot, find(robot, panelName));
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
        return find(robot, name);
    }

    private static JFormPane find(org.fest.swing.core.Robot robot, String panelName) {
        java.util.List<JFormPane> panels = new ArrayList<>();
        for (Component window : robot.finder().findAll(new TypeMatcher(Window.class))) {
            for (Component formPane : robot.finder().findAll((Window) window, new TypeMatcher(JFormPane.class))) {
                if (formPane.getName().equals(panelName) && !panels.contains(formPane)) {
                    panels.add((JFormPane) formPane);
                }
            }
        }

        if (panels.isEmpty()) {
            fail(robot, panelName, "Unable to find a", "", panels);
        } else if (panels.size() > 1) {
            StringBuilder middleMessage = new StringBuilder();
            for (JFormPane f : panels) {
                middleMessage.append('\t').append(f).append('\n');
            }
            fail(robot, panelName, "There are duplicates", middleMessage.toString(), panels);
        }

        return panels.get(0);
    }

    private static void fail(org.fest.swing.core.Robot robot, String panelName, String beginMessage, String middleMessage,
                             java.util.List<JFormPane> panels) {
        throw new ComponentLookupException(beginMessage + " JFormPane with name '" + panelName + "'\n" + middleMessage +
                dumpComponents(robot), panels);
    }
}
