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

import org.apache.commons.lang3.builder.Builder;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class implements the builder pattern for building a {@link fr.duminy.components.swing.path.JPath}.
 */
public class JPathBuilder implements Builder<JPath> {
    private JPath.SelectionMode selectionMode;
    private Integer nbColumns;
    private Path initialPath;
    private Boolean fileHidingEnabled;
    private Boolean enabled;

    public JPathBuilder() {
    }

    public JPathBuilder select(JPath.SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
        return this;
    }

    public JPathBuilder columns(int nbColumns) {
        this.nbColumns = nbColumns;
        return this;
    }

    public JPathBuilder initialPath(Path initialPath) {
        this.initialPath = initialPath;
        return this;
    }

    public JPathBuilder initialPath(File initialPath) {
        this.initialPath = (initialPath == null) ? null : initialPath.toPath();
        return this;
    }

    public JPathBuilder initialPath(String initialPath) {
        this.initialPath = (initialPath == null) ? null : Paths.get(initialPath);
        return this;
    }

    public JPathBuilder fileHidingEnabled(boolean fileHidingEnabled) {
        this.fileHidingEnabled = fileHidingEnabled;
        return this;
    }

    public JPathBuilder enable(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public JPath build() {
        JPath jpath = new JPath();

        if (selectionMode != null) {
            jpath.setSelectionMode(selectionMode);
        }
        if (nbColumns != null) {
            jpath.setColumns(nbColumns);
        }
        if (initialPath != null) {
            jpath.setPath(initialPath);
        }
        if (fileHidingEnabled != null) {
            jpath.setFileHidingEnabled(fileHidingEnabled);
        }
        if (enabled != null) {
            jpath.setEnabled(enabled);
        }

        return jpath;
    }
}
