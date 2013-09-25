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

import com.google.common.base.Suppliers;
import fr.duminy.components.swing.AbstractSwingTest;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.formbuilder.Form;
import org.formbuilder.FormBuilder;
import org.junit.Test;

import javax.swing.*;

import static org.formbuilder.FormBuilder.map;
import static org.formbuilder.mapping.form.FormFactories.REPLICATING;

/**
 * Abstract Tests for class {@link FileTypeMapper} and {@link PathTypeMapper}.
 */
abstract public class AbstractFileTypeMapperTest<B, TM extends AbstractFileTypeMapper> extends AbstractSwingTest {
    private final Class<B> beanClass;
    private final TM typeMapper;
    private final String fieldName;

    private JPanel content;

    protected AbstractFileTypeMapperTest(Class<B> beanClass, TM typeMapper, String fieldName) {
        this.beanClass = beanClass;
        this.typeMapper = typeMapper;
        this.fieldName = fieldName;
    }

    @Override
    public void onSetUp() {
        super.onSetUp();

        content = GuiActionRunner.execute(new GuiQuery<JPanel>() {
            protected JPanel executeInEDT() {
                return new JPanel();
            }
        });

        try {
            buildAndShowWindow(Suppliers.ofInstance(content));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public final void testBuild() throws Exception {
        final FormBuilder<B> builder = map(beanClass).formsOf(REPLICATING);
        String fileName = "aFile";
        final B b = createBean(fileName);

        GuiActionRunner.execute(new GuiQuery<Object>() {
            protected Object executeInEDT() {
                Form<B> form = builder.use(typeMapper).buildForm();
                form.setValue(b);
                content.add(form.asComponent());
                return null;
            }
        });

        window.panel(fieldName).textBox("pathField").requireText(fileName);
    }

    abstract B createBean(String fileName);
}
