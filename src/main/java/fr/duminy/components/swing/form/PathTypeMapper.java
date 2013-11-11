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

import fr.duminy.components.swing.path.JPath;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.nio.file.Path;

/**
 * An implementation of {@link org.formbuilder.TypeMapper} for {@link java.nio.file.Path}.
 */
@ThreadSafe
final class PathTypeMapper extends AbstractFileTypeMapper<Path> {
    public static final PathTypeMapper INSTANCE = new PathTypeMapper();

    private PathTypeMapper() {
        super(Path.class);
    }

    @Nullable
    @Override
    public final Path getValue(@Nonnull JPath jPath) {
        return jPath.getPath();
    }

    @Override
    public final void setValue(@Nonnull JPath jPath, @Nullable Path value) {
        jPath.setPath(value);
    }
}
