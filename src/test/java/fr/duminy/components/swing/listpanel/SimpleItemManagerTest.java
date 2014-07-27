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
package fr.duminy.components.swing.listpanel;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import fr.duminy.components.swing.AbstractFormTest;
import fr.duminy.components.swing.form.DefaultFormBuilder;
import fr.duminy.components.swing.form.FormBuilder;
import fr.duminy.components.swing.form.JFormPane;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.fest.swing.core.Robot;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.JButtonFixture;
import org.fest.swing.fixture.JPanelFixture;
import org.junit.Test;
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

import static fr.duminy.components.swing.Bundle.getBundle;
import static fr.duminy.components.swing.form.JFormPane.Mode.CREATE;
import static fr.duminy.components.swing.form.JFormPane.Mode.UPDATE;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for class {@link SimpleItemManager}.
 */
@RunWith(Theories.class)
public class SimpleItemManagerTest extends AbstractFormTest {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleItemManager.class);

    private ItemManager<Bean> manager;

    @Theory
    public final void testCreateItem_init(final ContainerType containerType, Locale locale, final NameType nameType) throws Exception {
        new InitNullBeanFormTest(nameType) {
            @Override
            protected void init() {
                super.init();
                manager = createItemManagerAndCreateItemAction(containerType, nameType);
            }
        }.run(containerType, locale);
    }

    @Test
    public void testCreateItemCallsInitItem() {
        final SimpleItemManager.FormDisplayer displayer = mock(SimpleItemManager.FormDisplayer.class);
        final FormBuilder<Bean> builder = new DefaultFormBuilder<>(Bean.class);
        final MutableBoolean called = new MutableBoolean(false);

        GuiActionRunner.execute(new GuiQuery<Void>() {
            protected Void executeInEDT() {
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
    public final void testCreateItem_okButton(final ContainerType containerType, Locale locale, final NameType nameType) throws Exception {
        new OkButtonFormTest(CREATE, true, nameType) {
            @Override
            protected void init() {
                super.init();
                manager = createItemManagerAndCreateItemAction(containerType, nameType);
            }
        }.run(containerType, locale);
    }

    @Theory
    public final void testCreateItem_cancelButton(final ContainerType containerType, Locale locale, final NameType nameType) throws Exception {
        new CancelButtonFormTest(CREATE, true, nameType) {
            @Override
            protected void init() {
                super.init();
                manager = createItemManagerAndCreateItemAction(containerType, nameType);
            }
        }.run(containerType, locale);
    }

    @Theory
    public final void testUpdateItem_init_nullBean(final ContainerType containerType, Locale locale, final NameType nameType) throws Exception {
        new InitNullBeanFormTest(nameType) {
            @Override
            protected void init() {
                super.init();
                manager = createItemManagerAndUpdateItemAction(containerType, nameType);
            }
        }.run(containerType, locale);
    }

    @Theory
    public final void testUpdateItem_init_notNullBean(final ContainerType containerType, Locale locale, final NameType nameType) throws Exception {
        new InitNotNullBeanFormTest(nameType) {
            @Override
            protected void init() {
                super.init();
                manager = createItemManagerAndUpdateItemAction(containerType, nameType);
            }
        }.run(containerType, locale);
    }

    @Theory
    public final void testUpdateItem_okButton(final ContainerType containerType, Locale locale, final NameType nameType) throws Exception {
        new OkButtonFormTest(UPDATE, true, nameType) {
            @Override
            protected void init() {
                super.init();
                manager = createItemManagerAndUpdateItemAction(containerType, nameType);
            }
        }.run(containerType, locale);
    }

    @Theory
    public final void testUpdateItem_cancelButton(final ContainerType containerType, Locale locale, final NameType nameType) throws Exception {
        new CancelButtonFormTest(UPDATE, true, nameType) {
            @Override
            protected void init() {
                super.init();
                manager = createItemManagerAndUpdateItemAction(containerType, nameType);
            }
        }.run(containerType, locale);
    }

    private ItemManager<Bean> createItemManagerAndCreateItemAction(ContainerType containerType, NameType nameType) {
        return createItemManager(containerType, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCallback(manager.createItem(), "create");
            }

            @Override
            public String toString() {
                return "AbstractAction('create')";
            }
        }, nameType);
    }

    private ItemManager<Bean> createItemManagerAndUpdateItemAction(ContainerType containerType, NameType nameType) {
        return createItemManager(containerType, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCallback(manager.updateItem(getBean()), "update");
            }

            @Override
            public String toString() {
                return "AbstractAction('update')";
            }
        }, nameType);
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

    private ItemManager<Bean> createItemManager(ContainerType type, Action buttonAction, NameType nameType) {
        FormBuilder<Bean> builder = new DefaultFormBuilder<>(Bean.class);
        Container parent;
        final SimpleItemManager.ContainerType parentType = type.getType();

        if (OpenInDialog.class.equals(type.getClass())) {
            parent = window.component();
        } else if (OpenInPanel.class.equals(type.getClass())) {
            parent = formContainer;
        } else {
            throw new IllegalArgumentException("wrong class : " + type.getClass().getName());
        }

        this.buttonAction = buttonAction;

        SimpleItemManager<Bean> manager = new SimpleItemManager<>(Bean.class, builder, parent, title, parentType);
        if (nameType == NameType.CUSTOM) {
            manager.setPanelName(NameType.CUSTOM.getName());
        }
        return manager;
    }

    public static final class OpenInPanel extends ContainerType {
        public static final OpenInPanel INSTANCE = new OpenInPanel();

        private OpenInPanel() {
            super("openInPanel", true);
        }

        @Override
        public JPanelFixture checkStaticProperties(Robot robot, NameType nameType, String title) {
            JPanelFixture result = formPane(robot, nameType.getName());
            JPanel formPane = result.component();
            assertThat(formPane).isInstanceOf(JFormPane.class);
            sleep();
            assertThat(formPane.getBorder()).isInstanceOf(TitledBorder.class);
            assertThat(((TitledBorder) formPane.getBorder()).getTitle()).isEqualTo(title);
            return result;
        }

        @Override
        protected <T extends Container> JButtonFixture getOkButtonFixture(Robot robot, String panelName, JFormPane.Mode mode) {
            return formPane(robot, panelName).button(JFormPane.OK_BUTTON_NAME).requireText(mode.getText());
        }

        @Override
        protected <T extends Container> JButtonFixture getCancelButtonFixture(Robot robot, String panelName) {
            return formPane(robot, panelName).button(JFormPane.CANCEL_BUTTON_NAME).requireText(getBundle().cancelText());
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
        t.manager = t.createItemManagerAndCreateItemAction(SimpleItemManagerTest.OpenInPanel.INSTANCE, NameType.DEFAULT);
    }
}
