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
import org.apache.commons.lang3.mutable.MutableObject;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.formbuilder.FormBuilder;
import org.junit.Test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;

import static org.fest.assertions.Assertions.assertThat;
import static org.formbuilder.FormBuilder.map;
import static org.formbuilder.mapping.form.FormFactories.REPLICATING;

/**
 * Tests for class {@link JFormPane}.
 */
public class JFormPaneTest extends AbstractSwingTest {
    private static final String NAME = "Steve";
    private static final String NEW_NAME = "Georges";

    private final MutableObject<Bean> bean = new MutableObject<>();
    private final MutableObject<String> title = new MutableObject<>();

    @Override
    public void onSetUp() {
        super.onSetUp();

        JButton b = GuiActionRunner.execute(new GuiQuery<JButton>() {
            protected JButton executeInEDT() {
                return new JButton(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final FormBuilder<Bean> builder = map(Bean.class).formsOf(REPLICATING);
                        Bean b = bean.getValue();
                        String t = title.getValue();
                        b = JFormPane.showFormDialog(window.component(), builder, b, t);
                        bean.setValue(b);
                    }
                });
            }
        });
        b.setName("button");

        try {
            buildAndShowWindow(Suppliers.ofInstance(b));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testShowFormDialog_init_nullBean() throws Exception {
        testShowFormDialog_init(null);

        window.optionPane().textBox("name").requireText("");
        window.optionPane().panel("file").textBox("pathField").requireText("");
        window.optionPane().panel("path").textBox("pathField").requireText("");
    }

    @Test
    public void testShowFormDialog_init_notNullBean() throws Exception {
        testShowFormDialog_init(new Bean("Steve"));

        window.optionPane().textBox("name").requireText(bean.getValue().getName());
    }

    private void testShowFormDialog_init(Bean b) throws Exception {
        initAndClick(b, "title");

        window.optionPane().requireQuestionMessage().requireTitle(title.getValue());
    }

    @Test
    public void testShowFormDialog_okButton() throws Exception {
        Bean bean = testShowFormDialog();

        window.optionPane().okButton().click();

        assertThat(bean.getName()).isEqualTo(NAME);
        assertThat(this.bean.getValue()).isNotNull();
        assertThat(this.bean.getValue().getName()).isEqualTo(NEW_NAME);
    }

    @Test
    public void testShowFormDialog_cancelButton() throws Exception {
        Bean bean = testShowFormDialog();

        window.optionPane().cancelButton().click();

        assertThat(this.bean.getValue()).isNull();
        assertThat(bean.getName()).isEqualTo(NAME);
    }

    private Bean testShowFormDialog() throws Exception {
        Bean bean = new Bean(NAME);
        initAndClick(bean, "title");

        JOptionPaneFixture f = window.optionPane();
        f.requireQuestionMessage().requireTitle(title.getValue());
        f.textBox("name").requireText(NAME);
        f.textBox("name").setText(NEW_NAME);

        return bean;
    }

    private void initAndClick(Bean b, String title) {
        this.bean.setValue(b);
        this.title.setValue(title);

        window.button("button").click();
    }

    public static class Bean {
        private String name;
        private File file;
        private Path path;

        public Bean() {
            this(null);
        }

        public Bean(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public Path getPath() {
            return path;
        }

        public void setPath(Path path) {
            this.path = path;
        }
    }
}
