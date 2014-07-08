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
import org.formbuilder.TypeMapper;
import org.formbuilder.mapping.change.ChangeHandler;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

import static fr.duminy.components.swing.path.JPath.SelectionMode.FILES_AND_DIRECTORIES;

/**
 * Base implementation of {@link org.formbuilder.TypeMapper} for following property types :
 * <ul>
 * <li>{@link java.io.File}</li>
 * <li>{@link java.nio.file.Path}</li>
 * </ul>.
 */
@ThreadSafe
abstract class AbstractFileTypeMapper<T> implements TypeMapper<JPath, T> {
    private final Class<T> valueType;

    AbstractFileTypeMapper(Class<T> valueType) {
        this.valueType = valueType;
    }

    @Override
    public final void handleChanges(@Nonnull JPath jPath, @Nonnull ChangeHandler changeHandler) {
        //TODO implement this.
    }

    @Nonnull
    @Override
    public final JPath createEditorComponent() {
        return new JPath(FILES_AND_DIRECTORIES);
    }

    @Nonnull
    @Override
    public final Class<T> getValueClass() {
        return valueType;
    }
}
