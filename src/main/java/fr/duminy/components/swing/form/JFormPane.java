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

import fr.duminy.components.swing.SwingComponentMessages;
import fr.duminy.components.swing.i18n.AbstractI18nAction;
import fr.duminy.components.swing.i18n.I18nAction;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static fr.duminy.components.swing.Bundle.getBundle;

/**
 * A simple panel displaying a form. It could be displayed as a classical swing component or inside a dialog
 * (by calling {@link #showDialog(java.awt.Component)}.
 * Note the static method {@link #showFormDialog(java.awt.Component, FormBuilder, Object, String, fr.duminy.components.swing.form.JFormPane.Mode)}
 * for typical usage inside a dialog.
 *
 * @param <B> The class of bean used to represent this form.
 */
public class JFormPane<B> extends JPanel /*implements I18nAble*/ {
    private static final Logger LOG = LoggerFactory.getLogger(JFormPane.class);

    public static final String OK_BUTTON_NAME = "okButton";
    public static final String CANCEL_BUTTON_NAME = "cancelButton";

    private final Form<B> form;
    private final String title;
    private final Mode mode;

    /**
     * The panel of buttons to validate or cancel the form, null when displayed in a dialog.
     */
    private final JPanel buttonsPanel;

    private final EventListenerSupport<FormListener> listeners = EventListenerSupport.create(FormListener.class);

    /**
     * Display a form inside a modal dialog and returns the new bean corresponding to values filled in the form, or null
     * if the user has cancelled the operation.
     *
     * @param parentComponent
     * @param formBuilder     The builder to use for constructing the {@link Form}.
     * @param initialValue    The initial bean value of the form.
     * @param title           The title of the dialog.
     * @param mode            The mode to use for the form.
     * @param <B>             The type of bean representing values in the form.
     * @return The new value for the bean or null if the user has cancelled the operation.
     */
    public static <B> B showFormDialog(Component parentComponent, FormBuilder<B> formBuilder, B initialValue, String title, Mode mode) {
        return showFormDialog(parentComponent, formBuilder, initialValue, title, mode, null);
    }

    /**
     * Display a form inside a modal dialog and returns the new bean corresponding to values filled in the form, or null
     * if the user has cancelled the operation.
     *
     * @param parentComponent
     * @param formBuilder     The builder to use for constructing the {@link Form}.
     * @param initialValue    The initial bean value of the form.
     * @param title           The title of the dialog.
     * @param mode            The mode to use for the form.
     * @param panelName       The name of the panel.
     * @param <B>             The type of bean representing values in the form.
     * @return The new value for the bean or null if the user has cancelled the operation.
     */
    public static <B> B showFormDialog(Component parentComponent, FormBuilder<B> formBuilder, B initialValue, String title, Mode mode, String panelName) {
        JFormPane<B> formPane = new JFormPane<>(formBuilder, title, mode);
        formPane.setValue(initialValue);
        if (panelName != null) {
            formPane.setName(panelName);
        }
        return formPane.showDialog(parentComponent);
    }

    public static <B> String getActualPanelName(FormBuilder<B> formBuilder, String panelName) {
        return (panelName == null) ? getDefaultPanelName(formBuilder.getBeanClass()) : panelName;
    }

    public static <B> String getDefaultPanelName(Class<B> beanClass) {
        return beanClass.getSimpleName();
    }

    /**
     * Generic constructor for a form.
     *
     * @param formBuilder The builder to use for constructing the {@link Form}.
     * @param title       The title of the dialog.
     * @param mode        The mode to use for the form.
     */
    public JFormPane(FormBuilder<B> formBuilder, String title, Mode mode) {
        super(new BorderLayout());
        this.title = title;
        this.mode = mode;

        form = formBuilder.buildForm();
        add(form.asComponent(), BorderLayout.CENTER);

        buttonsPanel = buildButtonsPanel();
        add(buttonsPanel, BorderLayout.SOUTH);
        setBorder(BorderFactory.createTitledBorder(title));
        setName(getDefaultPanelName(formBuilder.getBeanClass()));
    }

    /**
     * @return The value of this form.
     */
    public final B getValue() {
        return form.getValue();
    }

    /**
     * @param value The new value to assign to this form.
     */
    public final void setValue(B value) {
        form.setValue(value);
    }

    /**
     * Display this form in a dialog.
     *
     * @param parentComponent The parent component of the dialog.
     * @return The final value entered by the user in the dialog, or null if it was cancelled.
     */
    public B showDialog(Component parentComponent) {
        Window parentWindow;
        if (parentComponent == null) {
            parentWindow = JOptionPane.getRootFrame();
        } else if (parentComponent instanceof Window) {
            parentWindow = (Window) parentComponent;
        } else {
            parentWindow = SwingUtilities.getWindowAncestor(parentComponent);
        }
        final JDialog dialog = new JDialog(parentWindow, title, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setContentPane(this);
        dialog.setResizable(false);
        setBorder(null);
        final MutableObject<Boolean> validated = new MutableObject<>(false);
        FormListener<B> listener = new FormListener<B>() {
            @Override
            public void formValidated(Form<B> form) {
                validated.setValue(true);
                dialog.dispose();
            }

            @Override
            public void formCancelled(Form<B> form) {
                dialog.dispose();
            }
        };

        try {
            addFormListener(listener);
            dialog.pack();
            dialog.setVisible(true);
            return validated.getValue() ? getValue() : null;
        } finally {
            removeFormListener(listener);
        }
    }

    /**
     * Add a listener of events on this form.
     *
     * @param listener
     */
    public void addFormListener(FormListener<B> listener) {
        listeners.addListener(listener);
    }

    /**
     * Remove a listener of events on this form.
     *
     * @param listener
     */
    public void removeFormListener(FormListener<B> listener) {
        listeners.removeListener(listener);
    }

    private JPanel buildButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2));
        I18nAction okAction = new AbstractI18nAction<SwingComponentMessages>(SwingComponentMessages.class) {
            @Override
            protected String getShortDescription(SwingComponentMessages bundle) {
                return mode.getTooltip();
            }

            @Override
            protected String getName(SwingComponentMessages bundle) {
                return mode.getText();
            }

            @SuppressWarnings("unchecked")
            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("OK button was clicked. Notifying {} listeners...", listeners.getListeners().length);
                listeners.fire().formValidated(form);
                LOG.info("listeners were notified for OK button");
            }
        };
        I18nAction cancelAction = new AbstractI18nAction<SwingComponentMessages>(SwingComponentMessages.class) {
            @Override
            protected String getShortDescription(SwingComponentMessages bundle) {
                return bundle.cancelText();
            }

            @Override
            protected String getName(SwingComponentMessages bundle) {
                return bundle.cancelText();
            }

            @SuppressWarnings("unchecked")
            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("Cancel button was clicked. Notifying {} listeners...", listeners.getListeners().length);
                listeners.fire().formCancelled(form);
                LOG.info("listeners were notified for Cancel button");
            }
        };

        addButton(buttonsPanel, okAction, OK_BUTTON_NAME);
        addButton(buttonsPanel, cancelAction, CANCEL_BUTTON_NAME);

        return buttonsPanel;
    }

    private void addButton(JPanel buttonsPanel, final I18nAction action, final String buttonName) {
        action.updateMessages();
        JButton button = new JButton(action);
        button.setName(buttonName);
        buttonsPanel.add(button);
    }

    /**
     * Add this form to a the given parent container. <strong>All parent components will be removed</strong> before adding this form to it.
     *
     * @param formContainer The new parent container for this form.
     */
    public void addTo(Container formContainer) {
        formContainer.removeAll();
        formContainer.add(this, BorderLayout.CENTER);
        formContainer.revalidate();
    }

    /**
     * Remove this form from the given parent container. Other components contained by the container won't be removed.
     *
     * @param formContainer The new parent container for this form.
     */
    public void removeFrom(Container formContainer) {
        formContainer.remove(this);
        formContainer.revalidate();
    }

    Mode getMode() {
        return mode;
    }

    String getTitle() {
        return title;
    }

/*
    //TODO manage I18N for JFormPane 
    @Override
    public void updateMessages() {
        if (buttonsPanel != null) {
            for (Component c : buttonsPanel.getComponents()) {
                if (c instanceof JButton) {
                    JButton b = (JButton) c; 
                    if (b.getAction() instanceof I18nAble) {
                        ((I18nAble) b.getAction()).updateMessages();
                    }
                }
            }
        }
    }
*/

    /**
     * An enum of possible modes for a form.
     */
    public static enum Mode {
        /**
         * Open a form in creation mode.
         */
        CREATE {
            @Override
            public String getText() {
                return getBundle().createText();
            }

            public String getTooltip() {
                return getBundle().addItemTooltip();
            }
        },

        /**
         * Open a form in update mode.
         */
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
