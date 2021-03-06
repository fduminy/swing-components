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
package fr.duminy.components.swing.form;

import fr.duminy.components.swing.path.JPath;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiQuery;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for class {@link fr.duminy.components.swing.form.PathTypeMapper}.
 */
public class PathTypeMapperTest extends AbstractFileTypeMapperTest<PathTypeMapperTest.Bean, PathTypeMapper> {
    public PathTypeMapperTest() {
        super(Bean.class, PathTypeMapper.INSTANCE, new PathTypeMapper(SUPPLIER), "path");
    }

    @Test
    public void testGetValue_nullPath() {
        testGetValue(null);
    }

    @Test
    public void testGetValue_nonNullPath() {
        testGetValue(Paths.get("aPath"));
    }

    private void testGetValue(final Path path) {
        JPath jPath = GuiActionRunner.execute(new GuiQuery<JPath>() {
            @Override
            protected JPath executeInEDT() throws Throwable {
                JPath jPath = new JPath();
                jPath.setPath(path);
                return jPath;
            }
        });

        Path actualPath = PathTypeMapper.INSTANCE.getValue(jPath);

        assertThat(actualPath).isEqualTo(path);
    }

    @Override
    Bean createBean(String fileName) {
        return new Bean((fileName == null) ? null : new File(fileName).toPath());
    }

    public static class Bean {
        private Path path;

        public Bean() {
            this(null);
        }

        public Bean(Path path) {
            this.path = path;
        }

        public Path getPath() {
            return path;
        }

        public void setPath(Path path) {
            this.path = path;
        }
    }
}
