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

import fr.duminy.components.swing.path.JPath;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * An implementation of {@link org.formbuilder.TypeMapper} for a {@link java.lang.String} representing a {@link java.nio.file.Path}.
 */
@ThreadSafe
public class StringPathTypeMapper extends AbstractFileTypeMapper<String> {
    public static final StringPathTypeMapper INSTANCE = new StringPathTypeMapper();

    private StringPathTypeMapper() {
        super(String.class);
    }

    @Nullable
    @Override
    public final String getValue(@Nonnull JPath jPath) {
        return toString(jPath.getPath());
    }

    @Override
    public final void setValue(@Nonnull JPath jPath, @Nullable String value) {
        jPath.setPath((value == null) ? null : Paths.get(value));
    }

    public static String toString(Path path) {
        if (path == null) {
            return null;
        }

        StringBuilder elements = new StringBuilder();
        if (path.isAbsolute()) {
            elements.append(File.separatorChar);
        }
        for (int i = 0; i < path.getNameCount(); i++) {
            if (i > 0) {
                elements.append(File.separatorChar);
            }
            elements.append(path.getName(i));
        }
        return elements.toString();
    }
}
