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

import fr.duminy.components.swing.AbstractFormTest;
import org.formbuilder.FormBuilder;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Locale;

import static fr.duminy.components.swing.form.JFormPane.Mode;
import static fr.duminy.components.swing.form.JFormPane.Mode.CREATE;
import static fr.duminy.components.swing.form.JFormPane.Mode.UPDATE;
import static org.formbuilder.FormBuilder.map;
import static org.formbuilder.mapping.form.FormFactories.REPLICATING;

/**
 * Tests for class {@link JFormPane}.
 */
@RunWith(Theories.class)
public class JFormPaneTest extends AbstractFormTest {
    private Action buttonAction;

    @Override
    protected void initContentPane() {
        addButton(OpenInDialog.INSTANCE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonAction.actionPerformed(e);
            }
        });
    }

    @Theory
    public final void testUpdateItem_init_nullBean(final Locale locale) throws Exception {
        runInitNullBeanFormTest(locale, UPDATE);
    }

    @Theory
    public final void testUpdateItem_init_notNullBean(final Locale locale) throws Exception {
        runInitNotNullBeanFormTest(locale, UPDATE);
    }

    @Theory
    public final void testUpdateItem_okButton(final Locale locale) throws Exception {
        runOkButtonFormTest(locale, UPDATE);
    }

    @Theory
    public final void testUpdateItem_cancelButton(final Locale locale) throws Exception {
        runCancelButtonFormTest(locale, UPDATE);
    }

    @Theory
    public final void testCreateItem_init_nullBean(final Locale locale) throws Exception {
        runInitNullBeanFormTest(locale, CREATE);
    }

    @Theory
    public final void testCreateItem_init_notNullBean(final Locale locale) throws Exception {
        runInitNotNullBeanFormTest(locale, CREATE);
    }

    @Theory
    public final void testCreateItem_okButton(final Locale locale) throws Exception {
        runOkButtonFormTest(locale, CREATE);
    }

    @Theory
    public final void testCreateItem_cancelButton(final Locale locale) throws Exception {
        runCancelButtonFormTest(locale, CREATE);
    }

    private void runInitNullBeanFormTest(Locale locale, final Mode mode) {
        new InitNullBeanFormTest() {
            @Override
            protected void init() {
                prepare(mode);
                super.init();
            }
        }.run(OpenInDialog.INSTANCE, locale);
    }

    private void runInitNotNullBeanFormTest(Locale locale, final Mode mode) {
        new InitNotNullBeanFormTest() {
            @Override
            protected void init() {
                prepare(mode);
                super.init();
            }
        }.run(OpenInDialog.INSTANCE, locale);
    }

    private void runOkButtonFormTest(Locale locale, final Mode mode) {
        new OkButtonFormTest(mode) {
            @Override
            protected void init() {
                prepare(mode);
                super.init();
            }
        }.run(OpenInDialog.INSTANCE, locale);
    }

    private void runCancelButtonFormTest(Locale locale, final Mode mode) {
        new CancelButtonFormTest(mode) {
            @Override
            protected void init() {
                prepare(mode);
                super.init();
            }
        }.run(OpenInDialog.INSTANCE, locale);
    }

    private void prepare(final Mode mode) {
        buttonAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FormBuilder<Bean> builder = map(Bean.class).formsOf(REPLICATING);
                setBean(JFormPane.showFormDialog(window.component(), builder, getBean(), title, mode));
            }
        };
    }
}
