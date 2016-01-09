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

import static fr.duminy.components.swing.listpanel.ButtonsPanel.FeatureHandle;

/**
 * This enum represents the set of standard {@link fr.duminy.components.swing.listpanel.ListPanelFeature}s
 * supported by the {@link fr.duminy.components.swing.listpanel.ListPanel} component.
 */
public enum StandardListPanelFeature {
    MANUAL_ORDER {
        @Override
        <B> void install(FeatureHandle<B> handle) {
            new ManualOrderFeature<B>().install(handle);
        }
    },
    EDITING {
        @Override
        <B> void install(FeatureHandle<B> handle) {
            new EditingFeature<B>().install(handle);
        }
    };

    abstract <B> void install(FeatureHandle<B> handle);
}
