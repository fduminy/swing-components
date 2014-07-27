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

import fr.duminy.components.swing.AbstractSwingTest;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.junit.Test;

import javax.swing.*;
import java.io.File;
import java.nio.file.Paths;

import static fr.duminy.components.swing.AbstractFormTest.Bean;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Test for {@link DefaultFormBuilder}.
 */
public class DefaultFormBuilderTest extends AbstractSwingTest {

    public static final String A_NAME = "aName";
    public static final String A_PATH = "aPath";
    public static final String A_FILE = "aFile";

    @Test
    public void testGetBeanClass() throws Exception {
        final FormBuilder<Bean> builder = new DefaultFormBuilder<>(Bean.class);

        assertThat(builder.getBeanClass()).isEqualTo(Bean.class);
    }

    @Test
    public void testBuildForm() throws Exception {
        final FormBuilder<Bean> builder = new DefaultFormBuilder<>(Bean.class);

        Form<Bean> form = GuiActionRunner.execute(new GuiQuery<Form<Bean>>() {
            protected Form<Bean> executeInEDT() {
                return builder.buildForm();
            }
        });

        assertThat(form).isNotNull();
    }

    @Test
    public void testForm_getValue_init() throws Exception {
        final FormBuilder<Bean> builder = new DefaultFormBuilder<>(Bean.class);
        Form<Bean> form = GuiActionRunner.execute(new GuiQuery<Form<Bean>>() {
            protected Form<Bean> executeInEDT() {
                return builder.buildForm();
            }
        });

        Bean b = form.getValue();

        assertThat(b).isNotNull();
        assertThat(b.getName()).isNull();
        assertThat(b.getFile()).isNull();
        assertThat(b.getPath()).isNull();
    }

    @Test
    public void testForm_asComponent_init() throws Exception {
        final FormBuilder<Bean> builder = new DefaultFormBuilder<>(Bean.class);
        Form<Bean> form = GuiActionRunner.execute(new GuiQuery<Form<Bean>>() {
            protected Form<Bean> executeInEDT() {
                return builder.buildForm();
            }
        });

        final JComponent component = form.asComponent();
        assertThat(component).isNotNull();

        GuiActionRunner.execute(new GuiQuery<Void>() {
            protected Void executeInEDT() {
                getFrame().setContentPane(component);
                return null;
            }
        });

        window.textBox("name").requireEmpty();
        window.panel("path").textBox("pathField").requireEmpty();
        window.panel("file").textBox("pathField").requireEmpty();
    }


    @Test
    public void testForm_asComponent_afterSetValue() throws Exception {
        final FormBuilder<Bean> builder = new DefaultFormBuilder<>(Bean.class);
        final Form<Bean> form = GuiActionRunner.execute(new GuiQuery<Form<Bean>>() {
            protected Form<Bean> executeInEDT() {
                return builder.buildForm();
            }
        });

        final Bean b = new Bean();
        b.setName(A_NAME);
        b.setFile(new File(A_FILE));
        b.setPath(Paths.get(A_PATH));
        GuiActionRunner.execute(new GuiQuery<Void>() {
            protected Void executeInEDT() {
                form.setValue(b);
                return null;
            }
        });

        final JComponent component = form.asComponent();
        assertThat(component).isNotNull();

        GuiActionRunner.execute(new GuiQuery<Void>() {
            protected Void executeInEDT() {
                getFrame().setContentPane(component);
                return null;
            }
        });

        window.textBox("name").requireText(A_NAME);
        window.panel("path").textBox("pathField").requireText(A_PATH);
        window.panel("file").textBox("pathField").requireText(A_FILE);
    }

    @Test
    public void testForm_getValue_withFilledForm() throws Exception {
        final FormBuilder<Bean> builder = new DefaultFormBuilder<>(Bean.class);
        final Form<Bean> form = GuiActionRunner.execute(new GuiQuery<Form<Bean>>() {
            protected Form<Bean> executeInEDT() {
                return builder.buildForm();
            }
        });
        GuiActionRunner.execute(new GuiQuery<Void>() {
            protected Void executeInEDT() {
                getFrame().setContentPane(form.asComponent());
                return null;
            }
        });

        window.textBox("name").setText(A_NAME);
        window.panel("path").textBox("pathField").setText(A_PATH);
        window.panel("file").textBox("pathField").setText(A_FILE);

        Bean b = form.getValue();

        assertThat(b).isNotNull();
        assertThat(b.getName()).isEqualTo(A_NAME);
        assertThat(b.getPath()).isEqualTo(Paths.get(A_PATH));
        assertThat(b.getFile()).isEqualTo(new File(A_FILE));
    }

}
