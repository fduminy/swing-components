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
import fr.duminy.components.swing.path.JPathBuilder;
import org.apache.commons.lang3.builder.Builder;
import org.formbuilder.TypeMapper;
import org.formbuilder.mapping.change.ChangeHandler;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

/**
 * Base implementation of {@link org.formbuilder.TypeMapper} for following property types :
 * <ul>
 * <li>{@link java.io.File}</li>
 * <li>{@link java.nio.file.Path}</li>
 * <li>{@link java.lang.String}</li>
 * </ul>.
 *
 * @param <B> The type of property associated with the {@link fr.duminy.components.swing.path.JPath} component.
 */
@ThreadSafe
abstract class AbstractFileTypeMapper<B> implements TypeMapper<JPath, B> {
    private static final Builder<JPath> DEFAULT_BUILDER = new JPathBuilder();

    private final Class<B> valueType;
    private final Builder<JPath> jPathBuilder;

    AbstractFileTypeMapper(Class<B> valueType) {
        this(valueType, DEFAULT_BUILDER);
    }

    AbstractFileTypeMapper(Class<B> valueType, Builder<JPath> jPathBuilder) {
        this.valueType = valueType;
        this.jPathBuilder = jPathBuilder;
    }

    @Override
    public final void handleChanges(@Nonnull JPath jPath, @Nonnull ChangeHandler changeHandler) {
        //TODO implement this.
    }

    @Nonnull
    @Override
    public final JPath createEditorComponent() {
        return jPathBuilder.build();
    }

    @Nonnull
    @Override
    public final Class<B> getValueClass() {
        return valueType;
    }
}
