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


import fr.duminy.components.swing.Bundle;

import javax.swing.*;

/**
 * Abstract implementation of {@link I18nAction}.
 *
 * @param <M> The class of message bundle.
 */
public abstract class AbstractI18nAction<M> extends AbstractAction implements I18nAction {
    private final Class<M> messagesClass;

    public AbstractI18nAction(Class<M> messagesClass) {
        this.messagesClass = messagesClass;
    }

    @Override
    public final void updateMessages() {
        putValue(Action.NAME, getName(getBundle()));
        putValue(Action.SHORT_DESCRIPTION, getShortDescription(getBundle()));
    }

    protected final M getBundle() {
        return getBundle(messagesClass);
    }

    M getBundle(Class<M> messagesClass) {
        return Bundle.getBundle(messagesClass);
    }

    abstract protected String getShortDescription(M bundle);

    protected String getName(M bundle) {
        return null;
    }
}
