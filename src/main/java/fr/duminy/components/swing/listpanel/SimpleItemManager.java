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

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import fr.duminy.components.swing.form.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

import static fr.duminy.components.swing.form.JFormPane.Mode;
import static fr.duminy.components.swing.form.JFormPane.Mode.CREATE;
import static fr.duminy.components.swing.form.JFormPane.Mode.UPDATE;

/**
 * An implementation of {@link ItemManager} interface that use {@link FormBuilder} to build a form to create/modify a bean.
 */
public class SimpleItemManager<T> implements ItemManager<T> {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleItemManager.class);

    static interface FormDisplayer {
        <T> ListenableFuture<T> displayForm(SimpleItemManager<T> manager, T item, Mode mode);

        Container checkParentComponent(Container parentComponent);
    }

    public static enum ContainerType implements FormDisplayer {
        DIALOG {
            @Override
            public <T> ListenableFuture<T> displayForm(SimpleItemManager<T> manager, T item, Mode mode) {
                LOG.debug("displayForm(container=DIALOG, item={})", item);
                SettableFuture<T> futureItem = SettableFuture.create();

                T newItem;
                if (manager.panelName == null) {
                    newItem = JFormPane.showFormDialog(manager.parentComponent, manager.formBuilder, item, manager.title, mode);
                } else {
                    newItem = JFormPane.showFormDialog(manager.parentComponent, manager.formBuilder, item, manager.title, mode, manager.panelName);
                }

                if (newItem == null) {
                    cancel(futureItem);
                } else {
                    futureItem.set(newItem);
                }
                return futureItem;
            }

            public Container checkParentComponent(Container parentComponent) {
                return parentComponent;
            }
        },
        PANEL {
            @Override
            public <T> ListenableFuture<T> displayForm(final SimpleItemManager<T> manager, T item, Mode mode) {
                final SettableFuture<T> futureItem = SettableFuture.create();

                final JFormPane<T> formPane = new JFormPane<T>(manager.formBuilder, manager.title, mode);
                formPane.setValue(item);
                formPane.setName(JFormPane.getActualPanelName(manager.formBuilder, manager.panelName));

                formPane.addTo(checkParentComponent(manager.parentComponent));
                formPane.addFormListener(new FormListener<T>() {
                    @Override
                    public void formValidated(Form<T> form) {
                        formPane.removeFrom(manager.parentComponent);
                        futureItem.set(form.getValue());
                    }

                    @Override
                    public void formCancelled(Form<T> form) {
                        formPane.removeFrom(manager.parentComponent);
                        cancel(futureItem);
                    }
                });

                return futureItem;
            }

            public JComponent checkParentComponent(Container parentComponent) {
                if (!(parentComponent instanceof JComponent)) {
                    throw new IllegalArgumentException("parentComponent must be an instance of JComponent");
                }
                return (JComponent) parentComponent;
            }
        };

        private static <T> void cancel(SettableFuture<T> futureItem) {
            futureItem.cancel(true); //TODO should we use mayInterruptIfRunning=false ? 
        }

    }

    private final Class<T> itemClass;
    private final FormBuilder<T> formBuilder;
    private final Container parentComponent;
    private final String title;
    private final FormDisplayer type;
    private String panelName;

    public SimpleItemManager(Class<T> itemClass, Container parentComponent, String title, ContainerType type) {
        this(itemClass, new DefaultFormBuilder<>(itemClass), parentComponent, title, (FormDisplayer) type);
    }

    public SimpleItemManager(Class<T> itemClass, FormBuilder<T> formBuilder, Container parentComponent, String title, ContainerType type) {
        this(itemClass, formBuilder, parentComponent, title, (FormDisplayer) type);
    }

    /**
     * Constructor used for testing purposes.
     *
     * @param itemClass
     * @param formBuilder
     * @param parentComponent
     * @param title
     * @param type
     */
    SimpleItemManager(Class<T> itemClass, FormBuilder<T> formBuilder, Container parentComponent, String title, FormDisplayer type) {
        this.itemClass = itemClass;
        this.formBuilder = formBuilder;
        this.parentComponent = type.checkParentComponent(parentComponent);
        this.title = title;
        this.type = type;
    }

    @Override
    public final ListenableFuture<T> createItem() {
        T item;

        try {
            item = itemClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return Futures.immediateFailedFuture(e);
        }

        initItem(item);
        return type.displayForm(this, item, CREATE);
    }

    protected void initItem(T item) {
    }

    @Override
    public final ListenableFuture<T> updateItem(T item) {
        return type.displayForm(this, item, UPDATE);
    }

    public final void setPanelName(String panelName) {
        this.panelName = panelName;
    }
}
