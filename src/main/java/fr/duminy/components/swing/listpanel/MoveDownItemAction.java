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

import fr.duminy.components.swing.SwingComponentMessages;

import java.awt.event.KeyEvent;

/**
 * @param <B> The class of items in the list.
 */
@SuppressWarnings("serial")
class MoveDownItemAction<B> extends StandardItemAction<B> {
    MoveDownItemAction(ListActions<B> listener) {
        super(listener, KeyEvent.VK_DOWN, "down.png");
    }

    @Override
    protected void doAction(ListActions<B> listener) {
        listener.moveDownItem();
    }

    @Override
    protected String getShortDescription(SwingComponentMessages bundle) {
        return bundle.moveDownItemTooltip();
    }

    @Override
    public void updateState(int[] selectedItems, int listSize) {
        setEnabled(false);

        int lastLine = (listSize - 1);
        for (int selectedItem : selectedItems) {
            if (selectedItem < lastLine) {
                setEnabled(true);
                break;
            }
        }
    }
}
