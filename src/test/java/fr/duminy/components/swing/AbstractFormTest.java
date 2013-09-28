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
package fr.duminy.components.swing;

import com.google.common.base.Suppliers;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.ContainerFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JOptionPaneFixture;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;

import static org.fest.assertions.Assertions.assertThat;

/**
 * And abstract test for class related to a form.
 */
public abstract class AbstractFormTest extends AbstractSwingTest {
    private static final String NAME = "Steve";
    private static final String NEW_NAME = "Georges";

    private JPanel panel;
    protected Bean bean;
    protected String title;

    @Override
    public final void onSetUp() {
        super.onSetUp();

        bean = null;
        title = null;
        panel = GuiActionRunner.execute(new GuiQuery<JPanel>() {
            protected JPanel executeInEDT() {
                return new JPanel(new FlowLayout());
            }
        });
        initContentPane();

        try {
            buildAndShowWindow(Suppliers.ofInstance(panel));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    abstract protected void initContentPane();

    protected final void showFormAndCheck_init_nullBean(ContainerType type) throws Exception {
        showFormAndCheck_init(null, type);

        ContainerFixture f = type.getFormContainerFixture(window);
        f.textBox("name").requireText("");
        f.panel("file").textBox("pathField").requireText("");
        f.panel("path").textBox("pathField").requireText("");
    }

    protected final void showFormAndCheck_init_notNullBean(ContainerType type) throws Exception {
        showFormAndCheck_init(new Bean("Steve"), type);

        ContainerFixture f = type.getFormContainerFixture(window);
        f.textBox("name").requireText(bean.getName());
    }

    private void showFormAndCheck_init(Bean b, ContainerType type) throws Exception {
        initAndClick(b, "title", type);

        type.checkStaticProperties(window, title);
    }

    protected final void showFormAndCheck_okButton(ContainerType type) throws Exception {
        Bean bean = showFormAndCheck(type);

        type.clickOkButton(window);

        assertThat(bean.getName()).isEqualTo(NAME);
        assertThat(this.bean).isNotNull();
        assertThat(this.bean.getName()).isEqualTo(NEW_NAME);
    }

    protected final void showFormAndCheck_cancelButton(ContainerType type) throws Exception {
        Bean bean = showFormAndCheck(type);

        type.clickCancelButton(window);

        assertThat(this.bean).isNull();
        assertThat(bean.getName()).isEqualTo(NAME);
    }

    private Bean showFormAndCheck(ContainerType type) throws Exception {
        Bean bean = new Bean(NAME);
        initAndClick(bean, "title", type);

        type.checkStaticProperties(window, title);
        ContainerFixture f = type.getFormContainerFixture(window);
        f.textBox("name").requireText(NAME);
        f.textBox("name").setText(NEW_NAME);

        return bean;
    }

    private void initAndClick(Bean b, String title, ContainerType type) {
        this.bean = b;
        this.title = title;

        window.button(type.getButtonName()).click();
    }

    protected final void addButton(ContainerType containerType, final Action action) {
        JButton b = GuiActionRunner.execute(new GuiQuery<JButton>() {
            protected JButton executeInEDT() {
                return new JButton(action);
            }
        });
        b.setName(containerType.getButtonName());
        panel.add(b);
    }

    public static abstract class ContainerType {
        private final String buttonName;

        protected ContainerType(String buttonName) {
            this.buttonName = buttonName;
        }

        abstract public ContainerFixture getFormContainerFixture(FrameFixture window);

        abstract public void checkStaticProperties(FrameFixture window, String title);

        public final String getButtonName() {
            return buttonName;
        }

        public abstract void clickOkButton(FrameFixture window);

        public abstract void clickCancelButton(FrameFixture window);
    }

    public static final class OpenInDialog extends ContainerType {
        public static final OpenInDialog INSTANCE = new OpenInDialog();

        private OpenInDialog() {
            super("openInDialog");
        }

        @Override
        public JOptionPaneFixture getFormContainerFixture(FrameFixture window) {
            return window.optionPane();
        }

        @Override
        public void checkStaticProperties(FrameFixture window, String title) {
            getFormContainerFixture(window).requireQuestionMessage().requireTitle(title);
        }

        @Override
        public void clickOkButton(FrameFixture window) {
            getFormContainerFixture(window).okButton().click();
        }

        @Override
        public void clickCancelButton(FrameFixture window) {
            getFormContainerFixture(window).cancelButton().click();
        }
    }

    public static final class Bean {
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
