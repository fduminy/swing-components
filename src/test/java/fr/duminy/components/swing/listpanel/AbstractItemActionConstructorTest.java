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

import fr.duminy.components.swing.SwingComponentMessages;
import org.junit.Rule;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

/**
 * Tests for constructors of {@link fr.duminy.components.swing.listpanel.AbstractItemAction} class.
 */
@RunWith(Theories.class)
public class AbstractItemActionConstructorTest {
    @DataPoint
    public static final String NULL_ICON_RESOURCE = null;
    @DataPoint
    public static final String EMPTY_ICON_RESOURCE = "";
    @DataPoint
    public static final String BLANK_ICON_RESOURCE = " ";
    @DataPoint
    public static final String WRONG_ICON_RESOURCE = "aWrongValue";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @SuppressWarnings("unchecked")
    @Theory
    public final void testConstructor_iconResource_notFound(String iconResource) throws Exception {
        if (iconResource == null) {
            thrown.expect(NullPointerException.class);
            thrown.expectMessage("Icon resource is null");
        } else {
            thrown.expect(IllegalArgumentException.class);
            thrown.expectMessage("Icon resource not found : " + "'" + iconResource + "'");
        }
        new MockAbstractItemAction(Mockito.mock(ListActions.class), 0, iconResource, SwingComponentMessages.class);
    }

    private static class MockAbstractItemAction<T, M> extends AbstractItemAction<T, M> {
        MockAbstractItemAction(ListActions<T> listener, int acceleratorKey, String iconResource, Class<M> messagesClass) {
            super(listener, acceleratorKey, iconResource, messagesClass);
        }

        @Override
        protected void doAction(ListActions listener) {
        }

        @Override
        protected String getShortDescription(Object bundle) {
            return "";
        }

        @Override
        public void updateState(int[] selectedItems, int listSize) {
        }
    }
}
