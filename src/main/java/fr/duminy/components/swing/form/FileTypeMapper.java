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
package fr.duminy.components.swing.form;

import fr.duminy.components.swing.path.JPath;
import org.apache.commons.lang3.builder.Builder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.File;

/**
 * An implementation of {@link org.formbuilder.TypeMapper} for {@link java.io.File}.
 */
@ThreadSafe
final public class FileTypeMapper extends AbstractFileTypeMapper<File> {
    public static final FileTypeMapper INSTANCE = new FileTypeMapper();

    private FileTypeMapper() {
        super(File.class);
    }

    public FileTypeMapper(Builder<JPath> jPathBuilder) {
        super(File.class, jPathBuilder);
    }

    @Nullable
    @Override
    public final File getValue(@Nonnull JPath jPath) {
        return (jPath.getPath() == null) ? null : jPath.getPath().toFile();
    }

    @Override
    public final void setValue(@Nonnull JPath jPath, @Nullable File value) {
        jPath.setPath((value == null) ? null : value.toPath());
    }
}
