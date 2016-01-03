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

import org.assertj.swing.driver.ComponentDriver;
import org.assertj.swing.exception.ComponentLookupException;
import org.assertj.swing.fixture.AbstractContainerFixture;
import org.junit.Assert;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestUtilities {
    public static String dumpComponents(org.assertj.swing.core.Robot robot) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        robot.printer().printComponents(new PrintStream(os));
        return "\nComponent hierarchy:\n" + os.toString();
    }

    public static <S, C extends Container, D extends ComponentDriver> void assertThatButtonIsPresent(AbstractContainerFixture<S, C, D> container, String buttonName) {
        if (!buttonIsPresent(container, buttonName)) {
            Assert.fail("The button '" + buttonName + "' was expected in container " + container.target());
        }
    }

    public static <S, C extends Container, D extends ComponentDriver> void assertThatButtonIsAbsent(AbstractContainerFixture<S, C, D> container, String buttonName) {
        if (buttonIsPresent(container, buttonName)) {
            Assert.fail("The button '" + buttonName + "' was not expected in container " + container.target());
        }
    }

    private static <S, C extends Container, D extends ComponentDriver> boolean buttonIsPresent(AbstractContainerFixture<S, C, D> container, String buttonName) {
        boolean present;

        try {
            container.button(buttonName);
            present = true;
        } catch (ComponentLookupException cle) {
            present = false;
        }

        return present;
    }
}
