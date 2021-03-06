/**
 * Swing-Components is a library of swing components.
 *
 * Copyright (C) 2013-2016 Fabien DUMINY (fabien [dot] duminy [at] webmails [dot] com)
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
import fr.duminy.components.swing.list.MutableListModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.util.concurrent.CancellationException;

/**
 * This class is an implementation of {@link ListComponent} that wraps a {@link JList} component.
 *
 * @param <B> The class of items in the list.
 */
class JListComponentWrapper<B> implements ListComponent<B, JList<B>> {
    private static Logger LOG = LoggerFactory.getLogger(JListComponentWrapper.class);

    private final JList<B> list;
    private final MutableListModel<B> model;
    private final ItemManager<B> itemManager;

    /**
     * @param list        The list component to wrap.
     * @param itemManager This manager of items.
     */
    JListComponentWrapper(JList<B> list, ItemManager<B> itemManager) {
        this.list = list;
        model = (MutableListModel<B>) list.getModel();
        this.itemManager = itemManager;
    }

    @Override
    public JList<B> getComponent() {
        return list;
    }

    @Override
    public void addItem() {
        ListenableFuture<B> futureItem = itemManager.createItem();

        Futures.addCallback(futureItem, new FutureCallback<B>() {
            @Override
            public void onSuccess(B result) {
                model.add(result);
            }

            @Override
            public void onFailure(Throwable t) {
                if (t instanceof CancellationException) {
                    LOG.info("the user has cancelled the addition of an item");
                } else {
                    //TODO give a user feedback
                    LOG.error("Can't add an item", t);
                }
            }
        });
    }

    @Override
    public void updateItem(final int i) {
        if (isValidIndex(i, true, true)) {
            final B oldItem = model.getElementAt(i);
            final ListenableFuture<B> futureNewItem = itemManager.updateItem(oldItem);

            Futures.addCallback(futureNewItem, new FutureCallback<B>() {
                @Override
                public void onSuccess(B newItem) {
                    if (oldItem == newItem) {
                        //TODO also give a user feedback
                        throw new IllegalStateException("The element returned by " + itemManager.getClass().getName() +
                                ".updateItem(oldItem) must not be the same instance as oldItem");
                    }

                    model.set(i, newItem);
                }

                @Override
                public void onFailure(Throwable t) {
                    if (t instanceof CancellationException) {
                        LOG.info("the user has cancelled the update of an item");
                    } else {
                        //TODO give a user feedback
                        LOG.error("Can't update the item {} : {}", oldItem, t);
                    }
                }
            });
        }
    }

    @Override
    public void removeItem(int i) {
        if (isValidIndex(i, true, true)) {
            model.remove(i);
        }
    }

    @Override
    public void moveUpItem(int i) {
        if (isValidIndex(i, false, true)) {
            B item = model.remove(i);
            model.add(i - 1, item);
        }
    }

    @Override
    public void moveDownItem(int i) {
        if (isValidIndex(i, true, false)) {
            B item = model.remove(i);
            model.add(i + 1, item);
        }
    }

    private boolean isValidIndex(int i, boolean firstIsValid, boolean lastIsValid) {
        int min = firstIsValid ? 0 : 1;
        int max = lastIsValid ? model.getSize() : model.getSize() - 1;
        return (i >= min) && (i < max);
    }

    @Override
    public int getSize() {
        return model.getSize();
    }

    @Override
    public void addSelectionListener(ListSelectionListener listener) {
        list.addListSelectionListener(listener);
    }

    @Override
    public int[] getSelectedIndices() {
        return list.getSelectedIndices();
    }

    @Override
    public void setSelectedIndices(int... indices) {
        list.setSelectedIndices(indices);
    }

    @Override
    public B getItem(int i) {
        return model.getElementAt(i);
    }
}
