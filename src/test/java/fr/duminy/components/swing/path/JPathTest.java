/**
 * Swing-Components is a library of swing components.
 *
 * Copyright (C) 2013-2016 Fabien DUMINY (fabien [dot] duminy [at] webmails [dot] com)
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

import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import java.nio.file.Path;

import static fr.duminy.components.swing.path.JPath.SelectionMode;

/**
 * Tests for class {@link JPath}.
 */
@RunWith(Theories.class)
public class JPathTest extends AbstractPathTest {
    @Override
    protected final JPath createJPath() {
        return new JPath();
    }

    @Override
    protected final JPath createJPath(SelectionMode selectionMode) {
        JPath field = new JPath();
        field.setSelectionMode(selectionMode);
        return field;
    }

    @Override
    protected final JPath createJPath(int columns) {
        JPath field = new JPath();
        field.setColumns(columns);
        return field;
    }

    @Override
    protected final JPath createJPathAndSetEnabled(boolean enabled) {
        JPath field = new JPath();
        field.setEnabled(enabled);
        return field;
    }

    @Override
    protected final JPath createJPathAndSetFileHidingEnabled(boolean enableFileHiding) {
        JPath field = new JPath();
        field.setFileHidingEnabled(enableFileHiding);
        return field;
    }

    @Override
    protected final JPath createJPath(SelectionMode selectionMode, Path initialPath, boolean enabled) {
        final JPath result = new JPath();
        result.setSelectionMode(selectionMode);
        if (!enabled) {
            result.setPath(initialPath);
        }
        result.setEnabled(enabled);
        return result;
    }
}
