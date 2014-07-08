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

import com.google.common.base.Supplier;
import fr.duminy.components.swing.AbstractFormTest;
import fr.duminy.components.swing.AbstractSwingTest;
import org.fest.swing.exception.ComponentLookupException;
import org.fest.swing.fixture.JButtonFixture;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Test for class {@link fr.duminy.components.swing.form.JFormPaneFixture}.
 */
@RunWith(Theories.class)
public class JFormPaneFixtureTest extends AbstractFormTest {
    private static final String PANEL_NAME = Bean.class.getSimpleName();
    private static final String BUTTON_NAME = "openInDialog";

    @DataPoint
    public static final Action<JFormPane<Bean>> OPEN_IN_PANEL = new Action<>(new Supplier<JFormPane<Bean>>() {
        @Override
        public JFormPane<Bean> get() {
            final FormBuilder<Bean> builder = new DefaultFormBuilder<>(Bean.class);
            JFormPane<Bean> form = new JFormPane<>(builder, "title", JFormPane.Mode.CREATE);
            form.setName(PANEL_NAME);
            return form;
        }
    });

    @DataPoint
    public static final Action<JButton> OPEN_IN_DIALOG = new Action<JButton>(new CustomSupplier<JButton>() {
        @Override
        public JButton get() {
            JButton result = new JButton("Open Dialog");
            result.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    final FormBuilder<Bean> builder = new DefaultFormBuilder<>(Bean.class);
                    JFormPane.showFormDialog(parentComponent, builder, null, "title", JFormPane.Mode.CREATE);
                }
            });
            result.setName(BUTTON_NAME);
            return result;
        }
    }) {
        @Override
        public JComponent openForm(Component parentComponent, AbstractSwingTest test) throws Exception {
            ((CustomSupplier) this.supplier).parentComponent = parentComponent;
            super.openForm(parentComponent, test);
            test.window.button(BUTTON_NAME).click();
            return (JComponent) test.window.robot.finder().findByName(PANEL_NAME);
        }
    };

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Theory
    public void testConstructor_panelName(Action<JComponent> action) throws Exception {
        action.openForm(null, this);

        JFormPaneFixture fixture = new JFormPaneFixture(robot(), PANEL_NAME);

        assertThat(fixture).isNotNull();
    }

    @Theory
    public void testConstructor_beanClass(Action<JComponent> action) throws Exception {
        action.openForm(null, this);

        JFormPaneFixture fixture = new JFormPaneFixture(robot(), Bean.class);

        assertThat(fixture).isNotNull();
    }

    @Test
    public void testConstructor_panelName_componentNotFound() throws Exception {
        Supplier<JFormPane<Bean>> wrongName = new Supplier<JFormPane<Bean>>() {
            @Override
            public JFormPane<Bean> get() {
                JFormPane<Bean> form = OPEN_IN_PANEL.supplier.get();
                form.setName("WrongName");
                return form;
            }
        };
        buildAndShowWindow(wrongName);
        thrown.expect(ComponentLookupException.class);

        new JFormPaneFixture(robot(), PANEL_NAME);
    }

    @Test
    public void testConstructor_beanClass_componentNotFound() throws Exception {
        Supplier<JLabel> wrongComponentClass = new Supplier<JLabel>() {
            @Override
            public JLabel get() {
                JLabel l = new JLabel("");
                l.setName(PANEL_NAME);
                return l;
            }
        };
        buildAndShowWindow(wrongComponentClass);
        thrown.expect(ComponentLookupException.class);

        new JFormPaneFixture(robot(), Bean.class);
    }

    @Theory
    public void testComponent_beanClass(Action<JComponent> action) throws Exception {
        testComponent(action, true);
    }

    @Theory
    public void testComponent_panelName(Action<JComponent> action) throws Exception {
        testComponent(action, false);
    }

    private void testComponent(Action<JComponent> action, boolean useBeanClass) throws Exception {
        JComponent expectedForm = action.openForm(null, this);
        JFormPaneFixture fixture = useBeanClass ? new JFormPaneFixture(robot(), Bean.class) : new JFormPaneFixture(robot(), PANEL_NAME);

        JPanel form = fixture.component();

        assertThat(form).isInstanceOf(JFormPane.class);
        assertThat(form).isEqualTo(expectedForm);
    }

    @Theory
    @SuppressWarnings("unchecked")
    public void testOkButton(Action<JComponent> action) throws Exception {
        action.openForm(null, this);
        JFormPaneFixture fixture = new JFormPaneFixture(robot(), PANEL_NAME);
        JFormPane<Bean> formPane = (JFormPane<Bean>) fixture.component();
        FormListener<Bean> listener = Mockito.mock(FormListener.class);
        formPane.addFormListener(listener);

        JButtonFixture buttonFixture = fixture.okButton();
        assertThat(buttonFixture).isNotNull();
        buttonFixture.click();

        verify(listener).formValidated(any(Form.class));
        verifyNoMoreInteractions(listener);
    }

    @Theory
    @SuppressWarnings("unchecked")
    public void testCancelButton(Action<JComponent> action) throws Exception {
        action.openForm(null, this);
        JFormPaneFixture fixture = new JFormPaneFixture(robot(), PANEL_NAME);
        JFormPane<Bean> formPane = (JFormPane<Bean>) fixture.component();
        FormListener<Bean> listener = Mockito.mock(FormListener.class);
        formPane.addFormListener(listener);

        JButtonFixture buttonFixture = fixture.cancelButton();
        assertThat(buttonFixture).isNotNull();
        buttonFixture.click();

        verify(listener).formCancelled(any(Form.class));
        verifyNoMoreInteractions(listener);
    }

    public static class Action<T extends JComponent> {
        protected final Supplier<T> supplier;

        private Action(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @SuppressWarnings("unchecked")
        public JComponent openForm(Component parentComponent, AbstractSwingTest test) throws Exception {
            return test.buildAndShowWindow(supplier);
        }
    }

    private static abstract class CustomSupplier<T> implements Supplier<T> {
        protected Component parentComponent;
    }
}
