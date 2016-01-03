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

import fr.duminy.components.swing.AbstractFormTest;
import fr.duminy.components.swing.listpanel.SimpleItemManagerTest;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiQuery;
import org.assertj.swing.fixture.FrameFixture;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Locale;

import static fr.duminy.components.swing.form.JFormPane.Mode;
import static fr.duminy.components.swing.form.JFormPane.Mode.CREATE;
import static fr.duminy.components.swing.form.JFormPane.Mode.UPDATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests for class {@link JFormPane}.
 */
@RunWith(Theories.class)
public class JFormPaneTest extends AbstractFormTest {
    private FormListener<Bean> listener;

    private static enum MockListenerActions {
        NO_MOCK_LISTENER(false, false),
        ADD_MOCK_LISTENER(true, false),
        ADD_AND_REMOVE_MOCK_LISTENER(true, true);

        private final boolean add;
        private final boolean remove;

        private MockListenerActions(boolean add, boolean remove) {
            this.add = add;
            this.remove = remove;
        }

        public boolean addListener() {
            return add;
        }

        public boolean removeListener() {
            return remove;
        }
    }

/*
    //TODO manage I18N for JFormPane
    @Test
    public final void testExtendsI18nAble() throws Exception {
        assertTrue("JFormPane extends I18nAble", I18nAble.class.isAssignableFrom(JFormPane.class));
    }
*/

    public static String getTitle(JFormPane formPane) {
        return formPane.getTitle();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testConstructor() {
        JFormPane<Bean> formPane = GuiActionRunner.execute(new GuiQuery<JFormPane<Bean>>() {
            protected JFormPane<Bean> executeInEDT() {
                FormBuilder<Bean> builder = new DefaultFormBuilder<>(Bean.class);
                return new JFormPane<>(builder, "", Mode.CREATE);
            }
        });

        assertThat(formPane.getName()).as("name").isEqualTo(Bean.class.getSimpleName());
    }

    @Theory
    public void testAddFormListener_formValidated(NameType nameType) throws Exception {
        runOkButtonFormTest(ENGLISH, Mode.CREATE, OPEN_IN_PANEL, MockListenerActions.ADD_MOCK_LISTENER, nameType);

        verify(listener, Mockito.times(1)).formValidated(Matchers.<Form<Bean>>any());
        verifyNoMoreInteractions(listener);
    }

    @Theory
    public void testAddFormListener_formCancelled(NameType nameType) throws Exception {
        runCancelButtonFormTest(ENGLISH, Mode.CREATE, OPEN_IN_PANEL, MockListenerActions.ADD_MOCK_LISTENER, nameType);

        verify(listener, Mockito.times(1)).formCancelled(Matchers.<Form<Bean>>any());
        verifyNoMoreInteractions(listener);
    }

    @Theory
    public void testRemoveFormListener_formValidated(NameType nameType) throws Exception {
        runOkButtonFormTest(ENGLISH, Mode.CREATE, OPEN_IN_PANEL, MockListenerActions.ADD_AND_REMOVE_MOCK_LISTENER, nameType);

        verifyNoMoreInteractions(listener);
    }

    @Theory
    public void testRemoveFormListener_formCancelled(NameType nameType) throws Exception {
        runCancelButtonFormTest(ENGLISH, Mode.CREATE, OPEN_IN_PANEL, MockListenerActions.ADD_AND_REMOVE_MOCK_LISTENER, nameType);

        verifyNoMoreInteractions(listener);
    }

    @Theory
    public final void testUpdateItem_init_nullBean(final Locale locale, ContainerType containerType, NameType nameType) throws Exception {
        runInitNullBeanFormTest(locale, UPDATE, containerType, nameType);
    }

    @Theory
    public final void testUpdateItem_init_notNullBean(final Locale locale, ContainerType containerType, NameType nameType) throws Exception {
        runInitNotNullBeanFormTest(locale, UPDATE, containerType, nameType);
    }

    @Theory
    public final void testUpdateItem_okButton(final Locale locale, ContainerType containerType, NameType nameType) throws Exception {
        runOkButtonFormTest(locale, UPDATE, containerType, MockListenerActions.NO_MOCK_LISTENER, nameType);
    }

    @Theory
    public final void testUpdateItem_cancelButton(final Locale locale, ContainerType containerType, NameType nameType) throws Exception {
        runCancelButtonFormTest(locale, UPDATE, containerType, MockListenerActions.NO_MOCK_LISTENER, nameType);
    }

    @Theory
    public final void testCreateItem_init_nullBean(final Locale locale, ContainerType containerType, NameType nameType) throws Exception {
        runInitNullBeanFormTest(locale, CREATE, containerType, nameType);
    }

    @Theory
    public final void testCreateItem_init_notNullBean(final Locale locale, ContainerType containerType, NameType nameType) throws Exception {
        runInitNotNullBeanFormTest(locale, CREATE, containerType, nameType);
    }

    @Theory
    public final void testCreateItem_okButton(final Locale locale, ContainerType containerType, NameType nameType) throws Exception {
        runOkButtonFormTest(locale, CREATE, containerType, MockListenerActions.NO_MOCK_LISTENER, nameType);
    }

    @Theory
    public final void testCreateItem_cancelButton(final Locale locale, ContainerType containerType, NameType nameType) throws Exception {
        runCancelButtonFormTest(locale, CREATE, containerType, MockListenerActions.NO_MOCK_LISTENER, nameType);
    }

    private void runInitNullBeanFormTest(Locale locale, final Mode mode, final ContainerType containerType, final NameType nameType) {
        new InitNullBeanFormTest(nameType) {
            @Override
            protected void init() {
                prepare(mode, containerType, MockListenerActions.NO_MOCK_LISTENER, nameType);
                super.init();
            }
        }.run(containerType, locale);
    }

    private void runInitNotNullBeanFormTest(Locale locale, final Mode mode, final ContainerType containerType, final NameType nameType) {
        new InitNotNullBeanFormTest(nameType) {
            @Override
            protected void init() {
                prepare(mode, containerType, MockListenerActions.NO_MOCK_LISTENER, nameType);
                super.init();
            }
        }.run(containerType, locale);
    }

    private void runOkButtonFormTest(Locale locale, final Mode mode, final ContainerType containerType, final MockListenerActions mockListenerActions, final NameType nameType) {
        new OkButtonFormTest(mode, false, nameType) {
            @Override
            protected void init() {
                prepare(mode, containerType, mockListenerActions, nameType);
                super.init();
            }
        }.run(containerType, locale);
    }

    private void runCancelButtonFormTest(Locale locale, final Mode mode, final ContainerType containerType, final MockListenerActions mockListenerActions, final NameType nameType) {
        new CancelButtonFormTest(mode, false, nameType) {
            @Override
            protected void init() {
                prepare(mode, containerType, mockListenerActions, nameType);
                super.init();
            }
        }.run(containerType, locale);
    }

    private void prepare(final Mode mode, final ContainerType containerType, final MockListenerActions mockListenerActions, final NameType nameType) {
        buttonAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FormBuilder<Bean> builder = new DefaultFormBuilder<>(Bean.class);

                if (OpenInDialog.INSTANCE == containerType) {
                    if (NameType.DEFAULT == nameType) {
                        setBean(JFormPane.showFormDialog(window.target(), builder, getBean(), title, mode));
                    } else {
                        setBean(JFormPane.showFormDialog(window.target(), builder, getBean(), title, mode, nameType.getName()));
                    }
                } else if (SimpleItemManagerTest.OpenInPanel.INSTANCE == containerType) {
                    final JFormPane<Bean> formPane = new JFormPane<>(builder, title, mode);
                    formPane.setValue(getBean());
                    formPane.setName(nameType.getName());
                    formPane.addTo(formContainer);

                    if (mockListenerActions.addListener()) {
                        //noinspection unchecked
                        listener = mock(FormListener.class);

                        formPane.addFormListener(listener);
                    }

                    if (mockListenerActions.removeListener()) {
                        formPane.removeFormListener(listener);
                    }

                    formPane.addFormListener(new FormListener<Bean>() {
                        @Override
                        public void formValidated(Form<Bean> form) {
                            clearFormContainer();
                            setBean(form.getValue());
                        }

                        @Override
                        public void formCancelled(Form<Bean> form) {
                            clearFormContainer();
                            setBean(null);
                        }

                        private void clearFormContainer() {
                            formPane.removeFrom(formContainer);
                            resetContentPane();
                        }
                    });
                } else {
                    throw new UnsupportedOperationException("Unsupported class: " + containerType.getClass().getName());
                }
            }
        };
    }

    public static void main(String[] args) throws Exception {
        JFormPaneTest t = new JFormPaneTest();
        JFrame frame = t.createFrame();
        t.window = new FrameFixture(BasicRobot.robotWithCurrentAwtHierarchy(), frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        t.prepare(UPDATE, OPEN_IN_DIALOG, MockListenerActions.NO_MOCK_LISTENER, NameType.DEFAULT);
        t.setUpForm();
        frame.setVisible(true);
    }
}
