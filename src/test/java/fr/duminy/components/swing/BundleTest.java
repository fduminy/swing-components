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
package fr.duminy.components.swing;

import fr.duminy.components.swing.listpanel.AbstractItemActionTest;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Locale;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test for class {@link Bundle}.
 */
@RunWith(Theories.class)
public class BundleTest {
    @DataPoint
    public static final Locale FRENCH = Locale.FRENCH;
    @DataPoint
    public static final Locale ENGLISH = AbstractItemActionTest.DEFAULT_LOCALE;

    @Theory
    public void testGetBundle(Locale locale) throws Exception {
        runTest(true, locale);
    }

    @Theory
    public void testGetBundleForClass(Locale locale) throws Exception {
        runTest(false, locale);
    }

    private <T> void runTest(final boolean callDefault, Locale locale) {
        new AbstractLocaleTest() {
            @Override
            void doRun() {
                SwingComponentMessages bundle = callDefault ? Bundle.getBundle() : Bundle.getBundle(SwingComponentMessages.class);

                assertThat(bundle).isNotNull();
                assertThat(bundle.createText()).isEqualTo(DesktopSwingComponentMessages_fr.getExpectedMessage(DesktopSwingComponentMessages_fr.CREATE_TEXT_KEY)); // test an arbitrary method
            }
        }.run(locale);
    }
}
