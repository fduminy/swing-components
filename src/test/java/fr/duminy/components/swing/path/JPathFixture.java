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
package fr.duminy.components.swing.path;

import fr.duminy.components.swing.FixtureUtilities;
import org.assertj.swing.core.Robot;
import org.assertj.swing.fixture.JPanelFixture;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * Fixture to help testing a {@link fr.duminy.components.swing.path.JPath}.
 */
public class JPathFixture extends JPanelFixture {
    public JPathFixture(Robot robot, String componentName) {
        this(robot, FixtureUtilities.find(robot, JPath.class, componentName));
    }

    public JPathFixture(Robot robot, JPath component) {
        super(robot, component);
    }

    public JPathFixture requireSelectionMode(JPath.SelectionMode selectionMode) {
        if (!path().getSelectionMode().equals(selectionMode)) {
            fail("JPath component named '" + target().getName() + "' must have selection mode " + selectionMode);
        }
        return this;
    }

    public JPathFixture requireFileHidingEnabled(boolean fileHidingEnabled) {
        if (path().isFileHidingEnabled() != fileHidingEnabled) {
            fail("JPath component named '" + target().getName() + "' must have fileHidingEnabled=" + fileHidingEnabled);
        }
        return this;
    }

    public JPathFixture selectPath(Path path) {
        path().setPath(path);
        return this;
    }

    public JPathFixture requireSelectedPath(Path path) {
        assertThat(path().getPath()).as("selectedPath").isEqualTo(path);
        return this;
    }

    private JPath path() {
        return (JPath) target();
    }
}
