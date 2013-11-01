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
import fr.duminy.components.swing.form.JFormPane;
import fr.duminy.components.swing.listpanel.AbstractItemActionTest;
import fr.duminy.components.swing.listpanel.SimpleItemManager;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.ContainerFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JOptionPaneFixture;
import org.junit.experimental.theories.DataPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Locale;

import static fr.duminy.components.swing.DesktopSwingComponentMessages_fr.CANCEL_TEXT_KEY;
import static fr.duminy.components.swing.DesktopSwingComponentMessages_fr.getExpectedMessage;
import static fr.duminy.components.swing.form.JFormPane.Mode.UPDATE;
import static org.fest.assertions.Assertions.assertThat;

/**
 * And abstract test for class related to a form.
 */
public abstract class AbstractFormTest extends AbstractSwingTest {
    @DataPoint
    public static final Locale FRENCH = Locale.FRENCH;
    @DataPoint
    public static final Locale ENGLISH = AbstractItemActionTest.DEFAULT_LOCALE;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractFormTest.class);

    private static final String TITLE = "title";
    private static final String NAME = "Steve";
    private static final String NEW_NAME = "Georges";

    public static final Bean ERROR_BEAN = new Bean("ErrorBean");

    private JPanel panel;
    private JPanel buttonsPanel;
    private Bean bean;
    protected String title;

    @Override
    public final void onSetUp() {
        super.onSetUp();

        setUpForm();
    }

    protected void setUpForm() {
        setBean(null);
        title = null;
        buttonsPanel = GuiActionRunner.execute(new GuiQuery<JPanel>() {
            protected JPanel executeInEDT() {
                return new JPanel(new FlowLayout());
            }
        });
        panel = GuiActionRunner.execute(new GuiQuery<JPanel>() {
            protected JPanel executeInEDT() {
                JPanel result = new JPanel(new BorderLayout());
                result.add(buttonsPanel, BorderLayout.NORTH);
                result.setPreferredSize(new Dimension(300, 200));
                return result;
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

    protected static final void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public Bean getBean() {
        return bean;
    }

    public void setBean(Bean bean) {
        this.bean = bean;
        LOG.debug("setBean({})", bean);
    }

    protected abstract class FormTest {
        protected ContainerFixture f;

        public final void run(ContainerType type, Locale locale) {
            Locale oldLocale = Locale.getDefault();

            try {
                Locale.setDefault(locale);
                init();

                sleep();
                openDialog(type);

                sleep();
                f = type.checkStaticProperties(window, title);

                sleep();
                checkInitialFormState();

                sleep();
                fillForm(type);

                sleep();
                checkFinalFormState();
            } finally {
                Locale.setDefault(oldLocale);
            }
        }

        protected void init() {
        }

        protected final void openDialog(ContainerType type) {
            window.button(type.getButtonName()).click();
        }

        protected void checkInitialFormState() {
        }

        protected void fillForm(ContainerType type) {
        }

        protected void checkFinalFormState() {
        }

        protected final void init(Bean b, String title) {
            AbstractFormTest.this.setBean(b);
            AbstractFormTest.this.title = title;
        }
    }

    public class InitNullBeanFormTest extends FormTest {
        @Override
        protected void init() {
            init(null, TITLE);
        }

        @Override
        protected void checkInitialFormState() {
            f.textBox("name").requireText("");
            f.panel("file").textBox("pathField").requireText("");
            f.panel("path").textBox("pathField").requireText("");
        }
    }

    public class InitNotNullBeanFormTest extends FormTest {
        @Override
        protected void init() {
            init(new Bean(NAME), TITLE);
        }

        @Override
        protected void checkInitialFormState() {
            f.textBox("name").requireText(getBean().getName());
        }
    }

    private abstract class AbstractButtonFormTest extends FormTest {
        private final JFormPane.Mode mode;

        protected Bean oldBean;

        private AbstractButtonFormTest(JFormPane.Mode mode) {
            this.mode = mode;
        }

        @Override
        protected void init() {
            if (UPDATE.equals(mode)) {
                oldBean = new Bean(NAME);
            }
            init(oldBean, TITLE);
        }

        @Override
        protected final void checkInitialFormState() {
            f.textBox("name").requireText((oldBean == null) ? "" : NAME);
        }

        @Override
        protected void fillForm(ContainerType type) {
            f.textBox("name").setText(NEW_NAME);
        }

        @Override
        protected void checkFinalFormState() {
            if (oldBean != null) {
                assertThat(oldBean.getName()).isEqualTo(NAME);
            }
        }

        protected JFormPane.Mode getMode() {
            return mode;
        }
    }

    public class OkButtonFormTest extends AbstractButtonFormTest {
        public OkButtonFormTest(JFormPane.Mode mode) {
            super(mode);
        }

        @Override
        protected void fillForm(ContainerType type) {
            super.fillForm(type);
            type.clickOkButton(window, getMode());
        }

        @Override
        protected void checkFinalFormState() {
            super.checkFinalFormState();
            assertThat(getBean()).isNotNull();
            assertThat(getBean().getName()).isEqualTo(NEW_NAME);
        }
    }

    public class CancelButtonFormTest extends AbstractButtonFormTest {
        public CancelButtonFormTest(JFormPane.Mode mode) {
            super(mode);
        }

        @Override
        protected void fillForm(ContainerType type) {
            super.fillForm(type);
            type.clickCancelButton(window);
        }

        @Override
        protected void checkFinalFormState() {
            super.checkFinalFormState();
            assertThat(getBean()).isNull();
        }
    }

    protected final void addButton(final ContainerType containerType, final Action action) {
        JButton b = GuiActionRunner.execute(new GuiQuery<JButton>() {
            protected JButton executeInEDT() {
                String buttonName = containerType.getButtonName();
                JButton b = new JButton(action);
                b.setText(buttonName);
                b.setName(buttonName);
                return b;
            }
        });

        buttonsPanel.add(b);
    }

    protected final JPanel addPanel(String name) {
        JPanel p = GuiActionRunner.execute(new GuiQuery<JPanel>() {
            protected JPanel executeInEDT() {
                JPanel result = new JPanel(new BorderLayout());
                result.add(new JLabel("No form"), BorderLayout.CENTER);
                return result;
            }
        });
        p.setName(name);
        panel.add(p, BorderLayout.CENTER);
        return p;
    }

    public static abstract class ContainerType {
        private final String buttonName;
        private final boolean checkTooltip;

        protected ContainerType(String buttonName, boolean checkTooltip) {
            this.buttonName = buttonName;
            this.checkTooltip = checkTooltip;
        }

        abstract public ContainerFixture getFormContainerFixture(FrameFixture window);

        abstract public ContainerFixture checkStaticProperties(FrameFixture window, String title);

        public final String getButtonName() {
            return buttonName;
        }

        public final void clickOkButton(FrameFixture window, JFormPane.Mode mode) {
            JButtonFixture f = getOkButtonFixture(window, mode);
            if (checkTooltip) {
                f.requireToolTip(mode.getTooltip());
            }
            f.click();
        }

        abstract protected JButtonFixture getOkButtonFixture(FrameFixture window, JFormPane.Mode mode);

        public final void clickCancelButton(FrameFixture window) {
            JButtonFixture f = getCancelButtonFixture(window);
            if (checkTooltip) {
                f.requireToolTip(getExpectedMessage(CANCEL_TEXT_KEY));
            }
            f.click();
        }

        abstract protected JButtonFixture getCancelButtonFixture(FrameFixture window);

        abstract public SimpleItemManager.ContainerType getType();
    }

    public static final class OpenInDialog extends ContainerType {
        public static final OpenInDialog INSTANCE = new OpenInDialog();

        private OpenInDialog() {
            super("openInDialog", false);
        }

        @Override
        public JOptionPaneFixture getFormContainerFixture(FrameFixture window) {
            return window.optionPane();
        }

        @Override
        public JOptionPaneFixture checkStaticProperties(FrameFixture window, String title) {
            JOptionPaneFixture result = getFormContainerFixture(window);
            result.requireQuestionMessage().requireTitle(title);
            return result;
        }

        @Override
        protected JButtonFixture getOkButtonFixture(FrameFixture window, JFormPane.Mode mode) {
            return getFormContainerFixture(window).buttonWithText(getExpectedMessage(mode));
        }

        @Override
        protected JButtonFixture getCancelButtonFixture(FrameFixture window) {
            return getFormContainerFixture(window).buttonWithText(getExpectedMessage(CANCEL_TEXT_KEY));
        }

        @Override
        public SimpleItemManager.ContainerType getType() {
            return SimpleItemManager.ContainerType.DIALOG;
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

        @Override
        public String toString() {
            return "Bean{" +
                    "name='" + name + '\'' +
                    ", file=" + file +
                    ", path=" + path +
                    '}';
        }
    }
}
