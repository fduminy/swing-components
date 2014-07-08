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
package fr.duminy.components.swing.i18n;

import fr.duminy.components.swing.listpanel.AbstractItemActionTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Locale;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test for class {@link AbstractItemActionTest}.
 */
@RunWith(Theories.class)
public class I18nManagerTest {
    @DataPoint
    public static final Locale FRENCH = Locale.FRENCH;
    @DataPoint
    public static final Locale ENGLISH = AbstractItemActionTest.DEFAULT_LOCALE;

    private I18nManager manager;

    @Before
    public void setUp() {
        manager = new I18nManager();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAdd_self() {
        manager.add(manager);
    }

    @Theory
    public void testSetLocale(Locale locale) {
        I18nAble object = mock(I18nAble.class);
        manager.add(object);

        manager.setLocale(locale);

        assertThat(Locale.getDefault()).isEqualTo(locale);
        verify(object, only()).updateMessages();
    }

    @Test
    public void testUpdateMessages() {
        I18nAble object = mock(I18nAble.class);
        manager.add(object);

        manager.updateMessages();
        verify(object, only()).updateMessages();

        reset(object);
        manager.remove(object);
        verify(object, never()).updateMessages();
    }
}
