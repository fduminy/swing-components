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
package fr.duminy.components.swing;

import com.google.common.base.Supplier;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.junit.testcase.FestSwingJUnitTestCase;

import javax.swing.*;


abstract public class AbstractSwingTest extends FestSwingJUnitTestCase {

    protected FrameFixture window;
    private JFrame frame;

//	@BeforeClass
//	public static void setUpOnce() {
//		FailOnThreadViolationRepaintManager.install();
//	}

    @Override
    protected void onSetUp() {
        frame = GuiActionRunner.execute(new GuiQuery<JFrame>() {
            protected JFrame executeInEDT() {
                return new JFrame();
            }
        });
//		frame = new JFrame();
        window = new FrameFixture(robot(), frame);
        window.show();
    }

    protected JFrame getFrame() {
        return frame;
    }

    @Override
    protected void onTearDown() {
        window.cleanUp();
    }

    protected <T extends JComponent> T buildAndShowWindow(final Supplier<T> factory)
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
}
