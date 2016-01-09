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

import org.formbuilder.TypeMapper;

import javax.swing.*;

import static org.formbuilder.mapping.form.FormFactories.REPLICATING;

/**
 * Default implementation of {@link FormBuilder}.
 *
 * @param <B> The type of beans managed by this builder.
 */
public class DefaultFormBuilder<B> implements FormBuilder<B> {
    private static final TypeMapper<?, ?>[] TYPE_MAPPERS = new TypeMapper<?, ?>[]{
            FileTypeMapper.INSTANCE,
            PathTypeMapper.INSTANCE
    };

    private final Class<B> beanClass;

    public DefaultFormBuilder(Class<B> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public final Form<B> buildForm() {
        return new DefaultForm(beanClass);
    }

    @Override
    public final Class<B> getBeanClass() {
        return beanClass;
    }

    protected void configureBuilder(org.formbuilder.FormBuilder<B> builder) {
    }

    private class DefaultForm implements Form<B> {
        private final org.formbuilder.Form<B> form;

        private DefaultForm(Class<B> beanClass) {
            org.formbuilder.FormBuilder<B> builder = org.formbuilder.FormBuilder.map(beanClass).formsOf(REPLICATING);
            builder.use(TYPE_MAPPERS);
            configureBuilder(builder);
            form = builder.buildForm();
        }

        @Override
        public final void setValue(B value) {
            form.setValue(value);
        }

        @Override
        public final B getValue() {
            return form.getValue();
        }

        @Override
        public final JComponent asComponent() {
            return form.asComponent();
        }
    }
}
