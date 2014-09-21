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
import org.apache.commons.lang3.builder.Builder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.nio.file.Paths;

/**
 * An implementation of {@link org.formbuilder.TypeMapper} for a {@link java.lang.String} representing a {@link java.nio.file.Path}.
 */
@ThreadSafe
final public class StringPathTypeMapper extends AbstractFileTypeMapper<String> {
    public static final StringPathTypeMapper INSTANCE = new StringPathTypeMapper();

    private StringPathTypeMapper() {
        super(String.class);
    }

    public StringPathTypeMapper(Builder<JPath> jPathBuilder) {
        super(String.class, jPathBuilder);
    }

    @Nullable
    @Override
    public final String getValue(@Nonnull JPath jPath) {
        return (jPath.getPath() == null) ? null : jPath.getPath().toString();
    }

    @Override
    public final void setValue(@Nonnull JPath jPath, @Nullable String value) {
        jPath.setPath((value == null) ? null : Paths.get(value));
    }
}
