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
package fr.duminy.components.swing.listpanel;

import fr.duminy.components.swing.i18n.AbstractI18nAction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;

abstract public class AbstractItemActionTest<T extends Action> {
    public static Locale DEFAULT_LOCALE = Locale.ENGLISH;

    public static void setDefaultLocale() {
        Locale.setDefault(Locale.ENGLISH);
    }

    private final String shortDescription;
    private final int acceleratorKey;

    private ListActions listActions;
    private T action;

    protected AbstractItemActionTest(String shortDescription, int acceleratorKey) {
        this.shortDescription = shortDescription;
        this.acceleratorKey = acceleratorKey;
    }

    @Before
    public final void setUp() throws Exception {
        setDefaultLocale();
        listActions = mock(ListActions.class);
        action = createAction(listActions);
    }

    abstract protected T createAction(ListActions listActions);

    @Test
    public final void testExtendsAbstractI18nAction() throws Exception {
        assertTrue("action extends AbstractI18nAction", AbstractI18nAction.class.isAssignableFrom(action.getClass()));
    }

    @Test
    public final void testActionPerformed() throws Exception {
        action.actionPerformed(mock(ActionEvent.class));

        callAction(Mockito.verify(listActions, only()));
        Mockito.verifyNoMoreInteractions(listActions);
    }

    abstract protected void callAction(ListActions actions);

    @Test
    public final void testGetShortDescription() throws Exception {
        testGetProperty(Action.SHORT_DESCRIPTION, shortDescription);
    }

    @Test
    public final void testGetAcceleratorKey() throws Exception {
        testGetProperty(Action.ACCELERATOR_KEY, acceleratorKey);
    }

    @Test
    public final void testGetLargeIconKey() throws Exception {
        testGetProperty(Action.LARGE_ICON_KEY, ImageIcon.class);
    }

    private void testGetProperty(String actionProperty, Object expectedValue) {
        Object actual = action.getValue(actionProperty);

        if (expectedValue instanceof Class) {
            assertNotNull(actual);
            actual = actual.getClass();
        }

        assertEquals(expectedValue, actual);
    }
}
