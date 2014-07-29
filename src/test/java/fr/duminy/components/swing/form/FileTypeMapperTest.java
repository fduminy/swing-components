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
package fr.duminy.components.swing.form;

import fr.duminy.components.swing.path.JPath;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests for class {@link fr.duminy.components.swing.form.FileTypeMapper}.
 */
public class FileTypeMapperTest extends AbstractFileTypeMapperTest<FileTypeMapperTest.Bean, FileTypeMapper> {
    public FileTypeMapperTest() {
        super(Bean.class, FileTypeMapper.INSTANCE, "file");
    }

    @Test
    public void testGetValue_nullPath() {
        testGetValue(null);
    }

    @Test
    public void testGetValue_nonNullPath() {
        testGetValue(new File("aPath"));
    }

    private void testGetValue(final File file) {
        JPath jPath = GuiActionRunner.execute(new GuiQuery<JPath>() {
            @Override
            protected JPath executeInEDT() throws Throwable {
                JPath jPath = new JPath();
                jPath.setPath((file == null) ? null : file.toPath());
                return jPath;
            }
        });

        Path actualPath = PathTypeMapper.INSTANCE.getValue(jPath);

        assertThat((actualPath == null) ? null : actualPath.toFile()).isEqualTo(file);
    }

    @Override
    Bean createBean(String fileName) {
        return new Bean((fileName == null) ? null : new File(fileName));
    }

    public static class Bean {
        private File file;

        public Bean() {
            this(null);
        }

        public Bean(File file) {
            this.file = file;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }
    }
}
