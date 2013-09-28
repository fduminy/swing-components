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
import org.junit.Test;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static org.formbuilder.FormBuilder.map;
import static org.formbuilder.mapping.form.FormFactories.REPLICATING;

/**
 * Tests for class {@link JFormPane}.
 */
public class JFormPaneTest extends AbstractFormTest {
    @Override
    protected void initContentPane() {
        addButton(OpenInDialog.INSTANCE, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FormBuilder<Bean> builder = map(Bean.class).formsOf(REPLICATING);
                bean = JFormPane.showFormDialog(window.component(), builder, bean, title);
            }
        });
    }

    @Test
    public final void testShowFormDialog_init_nullBean() throws Exception {
        showFormAndCheck_init_nullBean(OpenInDialog.INSTANCE);
    }

    @Test
    public final void testShowFormDialog_init_notNullBean() throws Exception {
        showFormAndCheck_init_notNullBean(OpenInDialog.INSTANCE);
    }

    @Test
    public final void testShowFormDialog_okButton() throws Exception {
        showFormAndCheck_okButton(OpenInDialog.INSTANCE);
    }

    @Test
    public final void testShowFormDialog_cancelButton() throws Exception {
        showFormAndCheck_cancelButton(OpenInDialog.INSTANCE);
    }
}
