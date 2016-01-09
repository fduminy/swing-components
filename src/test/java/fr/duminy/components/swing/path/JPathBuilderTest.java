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

import java.nio.file.Path;

/**
 * Tests for class {@link fr.duminy.components.swing.path.JPathBuilder}.
 */
public class JPathBuilderTest extends AbstractPathTest {
    @Override
    protected final JPath createJPath() {
        return new JPathBuilder().build();
    }

    @Override
    protected final JPath createJPath(JPath.SelectionMode selectionMode) {
        return new JPathBuilder().select(selectionMode).build();
    }

    @Override
    protected final JPath createJPath(int columns) {
        return new JPathBuilder().columns(columns).build();
    }

    @Override
    protected final JPath createJPathAndSetEnabled(boolean enabled) {
        return new JPathBuilder().enable(enabled).build();
    }

    @Override
    protected final JPath createJPathAndSetFileHidingEnabled(boolean enableFileHiding) {
        return new JPathBuilder().fileHidingEnabled(enableFileHiding).build();
    }

    @Override
    protected final JPath createJPath(JPath.SelectionMode selectionMode, Path initialPath, boolean enabled) {
        JPathBuilder builder = new JPathBuilder().select(selectionMode);
        if (!enabled) {
            builder.initialPath(initialPath);
        }
        return builder.enable(enabled).build();
    }
}
