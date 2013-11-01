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
package fr.duminy.components.swing.listpanel;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import fr.duminy.components.swing.AbstractFormTest;
import fr.duminy.components.swing.form.JFormPane;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.formbuilder.FormBuilder;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.concurrent.CancellationException;

import static fr.duminy.components.swing.form.JFormPane.Mode.CREATE;
import static fr.duminy.components.swing.form.JFormPane.Mode.UPDATE;
import static org.fest.assertions.Assertions.assertThat;
import static org.formbuilder.FormBuilder.map;
import static org.formbuilder.mapping.form.FormFactories.REPLICATING;
import static org.mockito.Mockito.mock;

/**
 * Tests for class {@link SimpleItemManager}.
 */
@RunWith(Theories.class)
public class SimpleItemManagerTest extends AbstractFormTest {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleItemManager.class);

    @DataPoint
    public static final ContainerType OPEN_IN_DIALOG = OpenInDialog.INSTANCE;
    @DataPoint
    public static final ContainerType OPEN_IN_PANEL = OpenInPanel.INSTANCE;

    @DataPoint
    public static final SimpleItemManager.ContainerType PANEL = SimpleItemManager.ContainerType.PANEL;
    @DataPoint
    public static final SimpleItemManager.ContainerType DIALOG = SimpleItemManager.ContainerType.DIALOG;

    private static final String PANEL_NAME = "parentPanel";

    private ItemManager<Bean> manager;
    private Action buttonAction;
    private JPanel formContainer;

    @Override
    protected void initContentPane() {
        AbstractAction action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.debug("actionPerformed: buttonAction={}", buttonAction);
                buttonAction.actionPerformed(e);
            }
        };
        addButton(OpenInDialog.INSTANCE, action);
        addButton(OpenInPanel.INSTANCE, action);
        formContainer = addPanel(PANEL_NAME);
    }

    @Theory
    public final void testCreateItem_init(final ContainerType containerType, Locale locale) throws Exception {
        new InitNullBeanFormTest() {
            @Override
            protected void init() {
                super.init();
                manager = createItemManagerAndCreateItemAction(containerType);
            }
        }.run(containerType, locale);
    }

    @Test
    public void testCreateItemCallsInitItem() {
        final SimpleItemManager.FormDisplayer displayer = mock(SimpleItemManager.FormDisplayer.class);
        final FormBuilder<Bean> builder = map(Bean.class).formsOf(REPLICATING);
        final MutableBoolean called = new MutableBoolean(false);

        GuiActionRunner.execute(new GuiQuery<Object>() {
            protected Object executeInEDT() {
                final SimpleItemManager manager = new SimpleItemManager<Bean>(Bean.class, builder, new JLabel(""), title, displayer) {
                    @Override
                    protected void initItem(Bean item) {
                        called.setValue(true);
                    }
                };

                manager.createItem();
                return null;
            }
        });

        assertThat(called.getValue()).as("initItem called").isTrue();
    }

    @Theory
    public final void testCreateItem_okButton(final ContainerType containerType, Locale locale) throws Exception {
        new OkButtonFormTest(CREATE) {
            @Override
            protected void init() {
                super.init();
                manager = createItemManagerAndCreateItemAction(containerType);
            }
        }.run(containerType, locale);
    }

    @Theory
    public final void testCreateItem_cancelButton(final ContainerType containerType, Locale locale) throws Exception {
        new CancelButtonFormTest(CREATE) {
            @Override
            protected void init() {
                super.init();
                manager = createItemManagerAndCreateItemAction(containerType);
            }
        }.run(containerType, locale);
    }

    @Theory
    public final void testUpdateItem_init_nullBean(final ContainerType containerType, Locale locale) throws Exception {
        new InitNullBeanFormTest() {
            @Override
            protected void init() {
                super.init();
                manager = createItemManagerAndUpdateItemAction(containerType);
            }
        }.run(containerType, locale);
    }

    @Theory
    public final void testUpdateItem_init_notNullBean(final ContainerType containerType, Locale locale) throws Exception {
        new InitNotNullBeanFormTest() {
            @Override
            protected void init() {
                super.init();
                manager = createItemManagerAndUpdateItemAction(containerType);
            }
        }.run(containerType, locale);
    }

    @Theory
    public final void testUpdateItem_okButton(final ContainerType containerType, Locale locale) throws Exception {
        new OkButtonFormTest(UPDATE) {
            @Override
            protected void init() {
                super.init();
                manager = createItemManagerAndUpdateItemAction(containerType);
            }
        }.run(containerType, locale);
    }

    @Theory
    public final void testUpdateItem_cancelButton(final ContainerType containerType, Locale locale) throws Exception {
        new CancelButtonFormTest(UPDATE) {
            @Override
            protected void init() {
                super.init();
                manager = createItemManagerAndUpdateItemAction(containerType);
            }
        }.run(containerType, locale);
    }

    private ItemManager<Bean> createItemManagerAndCreateItemAction(ContainerType containerType) {
        return createItemManager(containerType, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCallback(manager.createItem(), "create");
            }

            @Override
            public String toString() {
                return "AbstractAction('create')";
            }
        });
    }

    private ItemManager<Bean> createItemManagerAndUpdateItemAction(ContainerType containerType) {
        return createItemManager(containerType, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCallback(manager.updateItem(getBean()), "update");
            }

            @Override
            public String toString() {
                return "AbstractAction('update')";
            }
        });
    }

    private void addCallback(ListenableFuture<Bean> futureBean, final String operation) {
        Futures.addCallback(futureBean, new FutureCallback<Bean>() {
            @Override
            public void onSuccess(Bean result) {
                setBean(result);
                LOG.info(operation + ": set bean to {}", result);
            }

            @Override
            public void onFailure(Throwable t) {
                if (CancellationException.class.isAssignableFrom(t.getClass())) {
                    LOG.error(operation + " operation has been cancelled => set bean to null");
                    setBean(null);
                } else {
                    LOG.error("Can't " + operation + " item => set bean to ErrorBean", t);
                    setBean(ERROR_BEAN);
                }
            }
        });
    }

    private ItemManager<Bean> createItemManager(ContainerType type, Action buttonAction) {
        FormBuilder<Bean> builder = map(Bean.class).formsOf(REPLICATING);
        Container parent;
        SimpleItemManager.ContainerType parentType;

        if (OpenInDialog.class.equals(type.getClass())) {
            parent = window.component();
            parentType = SimpleItemManager.ContainerType.DIALOG;
        } else if (OpenInPanel.class.equals(type.getClass())) {
            parent = formContainer;
            parentType = PANEL;
        } else {
            throw new IllegalArgumentException("wrong class : " + type.getClass().getName());
        }

        this.buttonAction = buttonAction;

        return new SimpleItemManager<>(Bean.class, builder, parent, title, parentType);
    }

    public static final class OpenInPanel extends ContainerType {
        public static final OpenInPanel INSTANCE = new OpenInPanel();

        private OpenInPanel() {
            super("openInPanel", true);
        }

        @Override
        public JPanelFixture getFormContainerFixture(FrameFixture window) {
            return window.panel(PANEL_NAME);
        }

        @Override
        public JPanelFixture checkStaticProperties(FrameFixture window, String title) {
            JPanelFixture result = getFormContainerFixture(window);
            JPanel panel = result.component();
            sleep();
            assertThat(panel.getBorder()).isInstanceOf(TitledBorder.class);
            assertThat(((TitledBorder) panel.getBorder()).getTitle()).isEqualTo(title);
            return result;
        }

        @Override
        protected JButtonFixture getOkButtonFixture(FrameFixture window, JFormPane.Mode mode) {
            return getFormContainerFixture(window).button(SimpleItemManager.OK_BUTTON_NAME).requireText(mode.getText());
        }

        @Override
        protected JButtonFixture getCancelButtonFixture(FrameFixture window) {
            return getFormContainerFixture(window).button(SimpleItemManager.CANCEL_BUTTON_NAME).requireText(JFormPane.getBundle().cancelText());
        }

        @Override
        public SimpleItemManager.ContainerType getType() {
            return SimpleItemManager.ContainerType.PANEL;
        }
    }

    public static void main(String[] args) throws Exception {
        SimpleItemManagerTest t = new SimpleItemManagerTest();
        JFrame frame = t.createFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        t.setUpForm();
        frame.setVisible(true);
        t.manager = t.createItemManagerAndCreateItemAction(OPEN_IN_PANEL);
    }
}
