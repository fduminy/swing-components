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

import fr.duminy.components.swing.SwingComponentMessages;
import org.ez18n.runtime.BundleFactory;
import org.ez18n.runtime.Desktop;
import org.formbuilder.Form;
import org.formbuilder.FormBuilder;

import javax.swing.*;
import java.awt.*;

import static fr.duminy.components.swing.form.TypeMappers.addTypeMappers;
import static javax.swing.JOptionPane.OK_CANCEL_OPTION;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;

/**
 * A simple wrapper class displaying a dialog with a form.
 */
public class JFormPane<B> extends JPanel {
    public static <B> B showFormDialog(Component parentComponent, FormBuilder<B> formBuilder, B bean, String title, Mode mode) {
        addTypeMappers(formBuilder);

        JFormPane<B> formPane = new JFormPane<B>(formBuilder, bean);

        Object[] options = {mode.getText(), getBundle().cancelText()};
        int result = JOptionPane.showOptionDialog(parentComponent,
                formPane,
                title,
                OK_CANCEL_OPTION,
                QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        return (result == JOptionPane.OK_OPTION) ? formPane.getValue() : null;
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

    public static SwingComponentMessages getBundle() {
        return BundleFactory.get(SwingComponentMessages.class, Desktop.class);
    }

    public static enum Mode {
        CREATE {
            @Override
            public String getText() {
                return getBundle().createText();
            }

            public String getTooltip() {
                return getBundle().addItemTooltip();
            }
        },
        UPDATE {
            @Override
            public String getText() {
                return getBundle().updateText();
            }

            public String getTooltip() {
                return getBundle().updateTooltip();
            }
        };

        abstract public String getText();

        abstract public String getTooltip();
    }
}
