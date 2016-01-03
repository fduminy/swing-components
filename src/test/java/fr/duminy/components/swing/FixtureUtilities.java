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
package fr.duminy.components.swing;

import org.assertj.swing.core.TypeMatcher;
import org.assertj.swing.exception.ComponentLookupException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static fr.duminy.components.swing.TestUtilities.dumpComponents;

/**
 * Utility methods for fixtures.
 */
public class FixtureUtilities {
    public static <T extends JComponent> T find(org.assertj.swing.core.Robot robot, Class<T> componentClass, String componentName) {
        java.util.List<T> components = new ArrayList<>();
        for (Component window : robot.finder().findAll(new TypeMatcher(Window.class))) {
            for (Component component : robot.finder().findAll((Window) window, new TypeMatcher(componentClass))) {
                if ((component.getName() != null) && component.getName().equals(componentName) && !components.contains(component)) {
                    components.add(componentClass.cast(component));
                }
            }
        }

        if (components.isEmpty()) {
            fail(robot, componentClass, componentName, "Unable to find a", "", components);
        } else if (components.size() > 1) {
            StringBuilder middleMessage = new StringBuilder();
            for (T f : components) {
                middleMessage.append('\t').append(f).append('\n');
            }
            fail(robot, componentClass, componentName, "There are duplicates", middleMessage.toString(), components);
        }

        return components.get(0);
    }

    private static <T extends JComponent> void fail(org.assertj.swing.core.Robot robot, Class<T> componentClass,
                                                    String componentName, String beginMessage, String middleMessage,
                                                    java.util.List<T> panels) {
        throw new ComponentLookupException(beginMessage + " " + componentClass.getSimpleName() + " with name '" + componentName + "'\n" + middleMessage +
                dumpComponents(robot), panels);
    }
}
