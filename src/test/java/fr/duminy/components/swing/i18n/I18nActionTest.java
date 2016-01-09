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
package fr.duminy.components.swing.i18n;

import org.junit.Before;
import org.junit.Test;

import java.awt.event.ActionEvent;
import java.util.Locale;
import java.util.ResourceBundle;

import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for classe {@link fr.duminy.components.swing.i18n.AbstractI18nAction}.
 */
public class I18nActionTest {
    private I18nAction action;

    @Before
    public void setUp() {
        action = new NoAction();
    }

    @Test
    public void testUpdateMessages_fr() {
        testUpdateMessages(Locale.FRENCH, TestMessages_fr.TEXT, TestMessages_fr.TOOLTIP);
    }

    @Test
    public void testUpdateMessages_en() {
        testUpdateMessages(Locale.ENGLISH, TestMessages_en.MESSAGE, TestMessages_en.TOOLTIP);
    }

    private void testUpdateMessages(Locale locale, String expectedText, String expectedTooltip) {
        Locale.setDefault(locale);

        action.updateMessages();

        assertThat(action.getValue(SHORT_DESCRIPTION)).isEqualTo(expectedTooltip);
        assertThat(action.getValue(NAME)).isEqualTo(expectedText);
    }

    private static class NoAction extends AbstractI18nAction<TestMessages> {
        private NoAction() {
            super(TestMessages.class);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
        }

        @Override
        protected String getShortDescription(TestMessages bundle) {
            return bundle.tooltip();
        }

        @Override
        protected String getName(TestMessages bundle) {
            return bundle.text();
        }

        @Override
        TestMessages getBundle(Class<TestMessages> messagesClass) {
            return (TestMessages) ResourceBundle.getBundle(messagesClass.getName());
        }
    }
}
