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
 * Test for {@link fr.duminy.components.swing.listpanel.RemoveItemAction} class.
 */
public class RemoveItemActionTest extends AbstractItemActionTest<RemoveItemAction> {

    public RemoveItemActionTest() {
        super(SwingComponentMessages.REMOVE_ITEM_TOOLTIP, KeyEvent.VK_DELETE);
    }

    @Override
    protected RemoveItemAction createAction(ListActions listActions) {
        return new RemoveItemAction(listActions);
    }

    @Override
    protected void callAction(ListActions actions) {
        actions.removeItem();
    }
}
