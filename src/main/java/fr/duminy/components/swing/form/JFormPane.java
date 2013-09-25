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

import org.formbuilder.Form;
import org.formbuilder.FormBuilder;

import javax.swing.*;
import java.awt.*;

import static fr.duminy.components.swing.form.TypeMappers.addTypeMappers;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.showInputDialog;

/**
 * A simple wrapper class displaying a dialog with a form.
 */
public class JFormPane<B> extends JPanel {
    public static <B> B showFormDialog(Component parentComponent, FormBuilder<B> formBuilder, B bean, String title) {
        addTypeMappers(formBuilder);
        JFormPane<B> pane = new JFormPane<B>(formBuilder, bean);
        boolean cancelled = (showInputDialog(parentComponent, pane, title, QUESTION_MESSAGE) == null);
        return cancelled ? null : pane.getValue();
    }

    private final Form<B> form;

    private JFormPane(FormBuilder<B> formBuilder, B bean) {
        form = formBuilder.buildForm();
        form.setValue(bean);
        setLayout(new BorderLayout());
        add(form.asComponent(), BorderLayout.CENTER);
    }

    public final B getValue() {
        return form.getValue();
    }
}
