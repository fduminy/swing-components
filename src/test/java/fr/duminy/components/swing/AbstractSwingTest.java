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

import com.google.common.base.Supplier;
import org.assertj.swing.core.Robot;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiQuery;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.slf4j.LoggerFactory;

import javax.swing.*;


abstract public class AbstractSwingTest extends AssertJSwingJUnitTestCase {

    public FrameFixture window;
    private JFrame frame;

    @Override
    protected void onSetUp() {
        createFrame();
        window = new FrameFixture(robot(), frame);
        window.show();

        initRobotSettings();
    }

    public Robot getRobot() {
        return robot();
    }

    private void initRobotSettings() {
        String valueStr = System.getProperty("delayBetweenEvents");
        try {
            int value = Integer.valueOf(valueStr);
            robot().settings().delayBetweenEvents(value);
        } catch (NumberFormatException nfe) {
            // ignore
        }
        LoggerFactory.getLogger(getClass()).info("delayBetweenEvents=" + robot().settings().delayBetweenEvents());
    }

    protected JFrame getFrame() {
        return frame;
    }

    @Override
    protected void onTearDown() {
        window.cleanUp();
    }

    public <T extends JComponent> T buildAndShowWindow(final Supplier<T> factory)
            throws Exception {

        return GuiActionRunner.execute(new GuiQuery<T>() {
            protected T executeInEDT() {
                T object = factory.get();
                frame.setContentPane(object);
                frame.pack();
                frame.invalidate();
                return object;
            }
        });
    }

    protected JFrame createFrame() {
        frame = GuiActionRunner.execute(new GuiQuery<JFrame>() {
            protected JFrame executeInEDT() {
                return new JFrame();
            }
        });
        return frame;
    }
}
