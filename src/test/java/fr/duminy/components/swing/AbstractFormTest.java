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
import fr.duminy.components.swing.form.JFormPaneTest;
import fr.duminy.components.swing.listpanel.AbstractItemActionTest;
import fr.duminy.components.swing.listpanel.SimpleItemManager;
import fr.duminy.components.swing.listpanel.SimpleItemManagerTest;
import org.fest.swing.core.Robot;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.ContainerFixture;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.experimental.theories.DataPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.util.Locale;

import static fr.duminy.components.swing.DesktopSwingComponentMessages_fr.CANCEL_TEXT_KEY;
import static fr.duminy.components.swing.DesktopSwingComponentMessages_fr.getExpectedMessage;
import static fr.duminy.components.swing.form.JFormPane.Mode.UPDATE;
import static javax.swing.SwingUtilities.getAncestorOfClass;
import static org.fest.assertions.Assertions.assertThat;
import static org.fest.swing.core.matcher.JButtonMatcher.withText;

/**
 * And abstract test for class related to a form.
 */
public abstract class AbstractFormTest extends AbstractSwingTest {
    @DataPoint
    public static final ContainerType OPEN_IN_DIALOG = OpenInDialog.INSTANCE;
    @DataPoint
    public static final ContainerType OPEN_IN_PANEL = SimpleItemManagerTest.OpenInPanel.INSTANCE;

    @DataPoint
    public static final Locale FRENCH = Locale.FRENCH;
    @DataPoint
    public static final Locale ENGLISH = AbstractItemActionTest.DEFAULT_LOCALE;

    @DataPoint
    public static final NameType DEFAULT_NAME = NameType.DEFAULT;
    @DataPoint
    public static final NameType CUSTOM_NAME = NameType.CUSTOM;

    protected static final String PARENT_PANEL_NAME = "parentPanel";

    public static enum NameType {
        DEFAULT {
            @Override
            public final String getName() {
                return Bean.class.getSimpleName();
            }
        },
        CUSTOM {
            @Override
            public final String getName() {
                return "formPanel";
            }
        };

        abstract public String getName();
    }

    private static final Logger LOG = LoggerFactory.getLogger(AbstractFormTest.class);

    private static final String TITLE = "title";
    private static final String NAME = "Steve";
    private static final String NEW_NAME = "Georges";

    public static final Bean ERROR_BEAN = new Bean("ErrorBean");
    protected Action buttonAction;
    protected JPanel formContainer;

    private JPanel panel;
    private JPanel buttonsPanel;
    private Bean bean;
    protected String title;

    @Override
    public final void onSetUp() {
        super.onSetUp();

        setUpForm();
    }

    protected final void setUpForm() {
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

    protected final void initContentPane() {
        AbstractAction action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.debug("actionPerformed: buttonAction={}", buttonAction);
                buttonAction.actionPerformed(e);
            }
        };
        addButton(OpenInDialog.INSTANCE, action);
        addButton(SimpleItemManagerTest.OpenInPanel.INSTANCE, action);
        formContainer = addPanel(PARENT_PANEL_NAME);
    }

    protected static final void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public final Bean getBean() {
        return bean;
    }

    public final void setBean(Bean bean) {
        this.bean = bean;
        LOG.debug("setBean({})", bean);
    }

    protected abstract class FormTest {
        protected final Logger LOG = LoggerFactory.getLogger(getLoggerClass());

        private Class getLoggerClass() {
            Class result = getClass();
            if (result.getName().contains("$")) {
                result = result.getSuperclass();
            }
            return result;
        }

        protected final NameType nameType;
        protected ContainerFixture f;

        protected FormTest(NameType nameType) {
            this.nameType = nameType;
        }

        public final void run(final ContainerType type, final Locale locale) {
            new AbstractLocaleTest() {
                @Override
                void doRun() {
                    boolean error = true;

                    try {
                        LOG.info("*** run.BEGIN ({}, {})", type.getType(), locale);
                        LOG.info("*** run: before call to init()");
                        init();

                        sleep();
                        LOG.info("*** run: before call to openForm()");
                        openForm(type);

                        sleep();
                        LOG.info("*** run: before call to checkStaticProperties()");
                        f = type.checkStaticProperties(robot(), nameType, title);

                        sleep();
                        LOG.info("*** run: before call to checkInitialFormState()");
                        checkInitialFormState();

                        sleep();
                        LOG.info("*** run: before call to fillForm()");
                        fillForm(type);

                        LOG.info("*** run: before call to checkFinalFormState()");
                        checkFinalFormState();
                        error = false;
                    } finally {
                        LOG.info("*** run.{} ({}, {})", new Object[]{error ? "ERROR" : "END", type.getType(), locale});
                    }
                }
            }.run(locale);
        }

        protected void init() {
        }

        protected final void openForm(ContainerType type) {
            window.button(type.getButtonName()).click();
        }

        protected void checkInitialFormState() {
            window.panel(nameType.getName()); // checks panel's name
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
        public InitNullBeanFormTest(NameType nameType) {
            super(nameType);
        }

        @Override
        protected void init() {
            init(null, TITLE);
        }

        @Override
        protected void checkInitialFormState() {
            super.checkInitialFormState();
            f.textBox("name").requireText("");
            f.panel("file").textBox("pathField").requireText("");
            f.panel("path").textBox("pathField").requireText("");
        }
    }

    public class InitNotNullBeanFormTest extends FormTest {
        public InitNotNullBeanFormTest(NameType nameType) {
            super(nameType);
        }

        @Override
        protected void init() {
            init(new Bean(NAME), TITLE);
        }

        @Override
        protected void checkInitialFormState() {
            super.checkInitialFormState();
            f.textBox("name").requireText(getBean().getName());
        }
    }

    private abstract class AbstractButtonFormTest extends FormTest {
        private final JFormPane.Mode mode;
        private final boolean checkRemovedFromContainer;

        protected Bean oldBean;

        private AbstractButtonFormTest(JFormPane.Mode mode, boolean checkRemovedFromContainer, NameType nameType) {
            super(nameType);
            this.mode = mode;
            this.checkRemovedFromContainer = checkRemovedFromContainer;
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
            super.checkInitialFormState();
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

            if (checkRemovedFromContainer) {
                assertThat(formContainer.getComponentCount()).isZero();
            }
        }

        protected JFormPane.Mode getMode() {
            return mode;
        }
    }

    public class OkButtonFormTest extends AbstractButtonFormTest {
        public OkButtonFormTest(JFormPane.Mode mode, boolean checkRemovedFromContainer, NameType nameType) {
            super(mode, checkRemovedFromContainer, nameType);
        }

        @Override
        protected void fillForm(ContainerType type) {
            super.fillForm(type);
            type.clickOkButton(LOG, robot(), nameType.getName(), getMode());
        }

        @Override
        protected void checkFinalFormState() {
            super.checkFinalFormState();
            assertThat(getBean()).as("bean").isNotNull();
            assertThat(getBean().getName()).as("bean name").isEqualTo(NEW_NAME);
        }
    }

    public class CancelButtonFormTest extends AbstractButtonFormTest {
        public CancelButtonFormTest(JFormPane.Mode mode, boolean checkRemovedFromContainer, NameType nameType) {
            super(mode, checkRemovedFromContainer, nameType);
        }

        @Override
        protected void fillForm(ContainerType type) {
            super.fillForm(type);
            type.clickCancelButton(LOG, robot(), nameType.getName());
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
                return new JPanel(new BorderLayout());
            }
        });
        p.setName(name);
        panel.add(p, BorderLayout.CENTER);

        resetContentPane();
        return p;
    }

    protected final void resetContentPane() {
        if (formContainer != null) {
            GuiActionRunner.execute(new GuiQuery<Object>() {
                protected Object executeInEDT() {
                    formContainer.add(new JLabel("No form"), BorderLayout.CENTER);
                    return null;
                }
            });
        }
    }

    public static abstract class ContainerType {
        private final String buttonName;
        private final boolean checkTooltip;

        protected ContainerType(String buttonName, boolean checkTooltip) {
            this.buttonName = buttonName;
            this.checkTooltip = checkTooltip;
        }

        public final JPanelFixture getFormPaneFixture(Robot robot, String panelName) {
            return JFormPaneTest.formPane(robot, panelName);
        }

        abstract public ContainerFixture checkStaticProperties(Robot robot, NameType nameType, String title);

        public final String getButtonName() {
            return buttonName;
        }

        public final <T extends Container> void clickOkButton(Logger log, Robot robot, String panelName, JFormPane.Mode mode) {
            JButtonFixture f = getOkButtonFixture(robot, panelName, mode);
            if (checkTooltip) {
                f.requireToolTip(mode.getTooltip());
            }
            f.click();
            log.info("OK button clicked : {}", f.component());
        }

        abstract protected <T extends Container> JButtonFixture getOkButtonFixture(Robot robot, String panelName, JFormPane.Mode mode);

        public final <T extends Container> void clickCancelButton(final Logger log, Robot robot, String panelName) {
            JButtonFixture f = getCancelButtonFixture(robot, panelName);
            if (checkTooltip) {
                f.requireToolTip(getExpectedMessage(CANCEL_TEXT_KEY));
            }
            f.click();
            log.info("Cancel button clicked");
        }

        abstract protected <T extends Container> JButtonFixture getCancelButtonFixture(Robot robot, String panelName);

        abstract public SimpleItemManager.ContainerType getType();
    }

    public static final class OpenInDialog extends ContainerType {
        public static final OpenInDialog INSTANCE = new OpenInDialog();

        private OpenInDialog() {
            super("openInDialog", false);
        }

        @Override
        public JPanelFixture checkStaticProperties(Robot robot, NameType nameType, String title) {
            JPanel formPane = getFormPaneFixture(robot, nameType.getName()).component();
            assertThat(formPane).isInstanceOf(JFormPane.class);

            Window parentWindow = SwingUtilities.getWindowAncestor(formPane);

            assertThat(parentWindow).isInstanceOf(Dialog.class);
            assertThat(((Dialog) parentWindow).getTitle()).isEqualTo(title);

            return new JPanelFixture(robot, formPane);
        }

        @Override
        protected <T extends Container> JButtonFixture getOkButtonFixture(Robot robot, String panelName, JFormPane.Mode mode) {
            return getOptionPaneFixture(robot, panelName).button(withText(getExpectedMessage(mode)));
        }

        @Override
        protected <T extends Container> JButtonFixture getCancelButtonFixture(Robot robot, String panelName) {
            return getOptionPaneFixture(robot, panelName).button(withText(getExpectedMessage(CANCEL_TEXT_KEY)));
        }

        private DialogFixture getOptionPaneFixture(Robot robot, String panelName) {
            JDialog p = (JDialog) getAncestorOfClass(JDialog.class, getFormPaneFixture(robot, panelName).component());
            return new DialogFixture(robot, p);
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
