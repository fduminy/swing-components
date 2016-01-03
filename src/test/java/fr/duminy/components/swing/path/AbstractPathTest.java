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
package fr.duminy.components.swing.path;

import com.google.common.base.Supplier;
import fr.duminy.components.swing.AbstractSwingTest;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.edt.GuiQuery;
import org.assertj.swing.exception.WaitTimedOutError;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JFileChooserFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static fr.duminy.components.swing.path.JPath.CHOOSE_BUTTON_NAME;
import static fr.duminy.components.swing.path.JPath.PATH_FIELD_NAME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Abstract class for tests related to {@link fr.duminy.components.swing.path.JPath}.
 */
@RunWith(Theories.class)
abstract public class AbstractPathTest extends AbstractSwingTest {
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @DataPoint
    public static final JPath.SelectionMode FILES_ONLY = JPath.SelectionMode.FILES_ONLY;
    @DataPoint
    public static final JPath.SelectionMode DIRECTORIES_ONLY = JPath.SelectionMode.DIRECTORIES_ONLY;
    @DataPoint
    public static final JPath.SelectionMode FILES_AND_DIRECTORIES = JPath.SelectionMode.FILES_AND_DIRECTORIES;

    @DataPoint
    public static final int ONE_COLUMN = 1;
    @DataPoint
    public static final int TWENTY_COLUMNS = 20;

    private Path path;
    private Path pathForDisabledField;

    @Override
    public void onSetUp() {
        super.onSetUp();
        try {
            InputStream data = new ByteArrayInputStream("data".getBytes());

            path = createFile(1, data);
            pathForDisabledField = createFile(2, data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Path createFile(int fileNum, InputStream data) throws IOException {
        Path grandParent = tempFolder.newFolder("grandParent" + fileNum).toPath();
        Path parent = grandParent.resolve("parent" + fileNum);
        Files.createDirectories(parent);

        Path result = parent.resolve("childFile" + fileNum);
        Files.copy(data, result);

        return result;
    }

    abstract protected JPath createJPath();

    abstract protected JPath createJPath(JPath.SelectionMode selectionMode);

    abstract protected JPath createJPath(int columns);

    abstract protected JPath createJPathAndSetEnabled(boolean enabled);

    abstract protected JPath createJPathAndSetFileHidingEnabled(boolean enableFileHiding);

    abstract protected JPath createJPath(JPath.SelectionMode selectionMode, Path initialPath, boolean enabled);

    @Test
    public void testConstructor() throws Exception {
        JPath jpath = buildAndShowWindow(new Supplier<JPath>() {
            @Override
            public JPath get() {
                return createJPath();
            }
        });

        checkFileChooserState(true, false, JPath.SelectionMode.FILES_AND_DIRECTORIES);
        checkEnabledState(jpath, true);
    }

    @Theory
    public void testSetSelectionMode(final JPath.SelectionMode selectionMode) throws Exception {
        testSetSelectionMode(selectionMode, selectionMode);
    }

    @Test
    public void testSetSelectionMode_null() throws Exception {
        testSetSelectionMode(null, JPath.SelectionMode.FILES_AND_DIRECTORIES);
    }

    private void testSetSelectionMode(final JPath.SelectionMode selectionMode, JPath.SelectionMode expectedSelectionMode) throws Exception {
        buildAndShowWindow(new Supplier<JPath>() {
            @Override
            public JPath get() {
                return createJPath(selectionMode);
            }
        });

        checkFileChooserState(true, false, expectedSelectionMode);
    }

    @Theory
    public void testSetColumns(final int columns) throws Exception {
        buildAndShowWindow(new Supplier<JPath>() {
            @Override
            public JPath get() {
                return createJPath(columns);
            }
        });

        JTextField tf = (JTextField) window.textBox(PATH_FIELD_NAME).target();
        assertThat(tf.getColumns()).isEqualTo(columns);
    }

    @Test
    public void testSetEnabled_true() throws Exception {
        testSetEnabled(true);
    }

    @Test
    public void testSetEnabled_false() throws Exception {
        testSetEnabled(false);
    }

    public void testSetEnabled(final boolean enabled) throws Exception {
        JPath field = buildAndShowWindow(new Supplier<JPath>() {
            @Override
            public JPath get() {
                return createJPathAndSetEnabled(enabled);
            }
        });

        checkEnabledState(field, enabled);
    }

    @Test
    public void testEnableFileHiding_true() throws Exception {
        testEnableFileHiding(true);
    }

    @Test
    public void testEnableFileHiding_false() throws Exception {
        testEnableFileHiding(false);
    }

    public void testEnableFileHiding(final boolean enableFileHiding) throws Exception {
        buildAndShowWindow(new Supplier<JPath>() {
            @Override
            public JPath get() {
                return createJPathAndSetFileHidingEnabled(enableFileHiding);
            }
        });

        checkFileChooserState(enableFileHiding, false, JPath.SelectionMode.FILES_AND_DIRECTORIES);
    }

    @Theory
    public void testSetPath_disabled(JPath.SelectionMode selectionMode) throws Exception {
        testSetPath(selectionMode, false);
    }

    @Theory
    public void testSetPath_enabled(JPath.SelectionMode selectionMode) throws Exception {
        testSetPath(selectionMode, true);
    }

    private void testSetPath(final JPath.SelectionMode selectionMode, final boolean enabled) throws Exception {
        final Parameters parameters = new Parameters(enabled, selectionMode);
        final JPath field = buildAndShowWindow(new Supplier<JPath>() {
            @Override
            public JPath get() {
                return createJPath(selectionMode, parameters.getInitialPath(), enabled);
            }

        });

        Exception exception = GuiActionRunner.execute(new GuiQuery<Exception>() {
            protected Exception executeInEDT() {
                Exception result = null;
                try {
                    field.setPath(parameters.getPath());
                } catch (Exception e) {
                    result = e;
                }
                return result;
            }
        });

        checkActualState(field, parameters, exception, true, true);
    }

    @Theory
    public void testSelectPath_disabled(JPath.SelectionMode selectionMode) throws Exception {
        testSelectPath(selectionMode, false);
    }

    @Theory
    public void testSelectPath_enabled(JPath.SelectionMode selectionMode) throws Exception {
        testSelectPath(selectionMode, true);
    }


    private void testSelectPath(final JPath.SelectionMode selectionMode, final boolean enabled) throws Exception {
        final Parameters parameters = new Parameters(enabled, selectionMode);
        JPath field = buildAndShowWindow(new Supplier<JPath>() {
            @Override
            public JPath get() {
                return createJPath(selectionMode, parameters.getInitialPath(), enabled);
            }
        });

        window.textBox().requireText(parameters.getInitialText());

        final JButtonFixture jbf = window.button(CHOOSE_BUTTON_NAME);
        if (enabled) {
            jbf.requireEnabled();

            JFileChooserFixture jfc = checkFileChooserState(true, false, selectionMode);

            File fileToSelect = parameters.getPath().toFile();
            if (selectionMode.allowsDirectory()) {
                jfc.setCurrentDirectory(fileToSelect.getParentFile());
            }
            jfc.selectFile(fileToSelect);
            jfc.approve();
        } else {
            jbf.requireDisabled();
        }

        checkActualState(field, parameters, null, false, false);
    }

    @Theory
    public void testEnterPath_disabled(JPath.SelectionMode selectionMode) throws Exception {
        testEnterPath(selectionMode, false);
    }

    @Theory
    public void testEnterPath_enabled(JPath.SelectionMode selectionMode) throws Exception {
        testEnterPath(selectionMode, true);
    }

    private void testEnterPath(final JPath.SelectionMode selectionMode, final boolean enabled) throws Exception {
        final Parameters parameters = new Parameters(enabled, selectionMode);
        JPath field = buildAndShowWindow(new Supplier<JPath>() {
            @Override
            public JPath get() {
                return createJPath(selectionMode, parameters.getInitialPath(), enabled);
            }

        });

        window.textBox().requireText(parameters.getInitialText());

        boolean expectError = !enabled;
        Exception exception = executeInEDT(new GuiQuery<Void>() {
            protected Void executeInEDT() throws Exception {
                window.textBox().target().setText(parameters.getText());
                return null;
            }
        }, expectError);

        checkActualState(field, parameters, exception, false, false);
    }

    private JFileChooserFixture checkFileChooserState(boolean expectFileHidingEnabled, boolean expectError, JPath.SelectionMode expectSelectionMode) {
        final JButtonFixture jbf = window.button(CHOOSE_BUTTON_NAME);
        executeInEDT(new GuiQuery<Void>() {
            protected Void executeInEDT() {
                jbf.targetCastedTo(JButton.class).doClick();
                return null;
            }
        }, expectError);

        JFileChooserFixture jfc = window.fileChooser();

        // check file chooser state
        JFileChooser fileChooser = jfc.target();
        assertThat(fileChooser.isFileHidingEnabled()).as("fileHidingEnabled").isEqualTo(expectFileHidingEnabled);
        assertThat(JPath.SelectionMode.of(fileChooser)).as("selectionMode").isEqualTo(expectSelectionMode);
        return jfc;
    }

    private Exception executeInEDT(GuiQuery<Void> query, boolean expectError) {
        Exception result = null;
        try {
            GuiActionRunner.execute(query);
        } catch (Exception e) {
            if (expectError) {
                result = e;
            } else {
                throw e;
            }
        }
        return result;
    }

    private void checkActualState(JPath field, Parameters parameters, Exception exception, boolean expectException, boolean expectIllegalStateException) {
        final JTextComponentFixture textBoxFixture = checkEnabledState(field, parameters.enabled);

        checkExpectedException(parameters.enabled, exception, expectException, expectIllegalStateException);

        Path expectedPath = parameters.getExpectedPath();

        assertThat(field.getPath()).isEqualTo(expectedPath);

        textBoxFixture.requireText(expectedPath.toAbsolutePath().toString());
    }

    private void checkExpectedException(boolean enabled, Exception exception, boolean expectException, boolean expectIllegalStateException) {
        if (enabled) {
            if (exception != null) {
                Assert.fail("expected exception to be null but is " + exception);
            }
        } else {
            if (expectException) {
                AbstractThrowableAssert<?, ? extends Throwable> ta = assertThat(exception).as("exception thrown when disabled")
                        .isNotNull();

                if (expectIllegalStateException) {
                    ta.isExactlyInstanceOf(IllegalStateException.class).as("expected exception").hasMessage("component is disabled");
                } else {
                    ta.isExactlyInstanceOf(WaitTimedOutError.class).as("expected exception").hasMessageStartingWith("Timed out waiting for path chooser to be found using matcher");
                }
            }
        }
    }

    private JTextComponentFixture checkEnabledState(JPath field, boolean expectEnabled) {
        assertThat(field.isEnabled()).isEqualTo(expectEnabled);

        //TODO add requireEnabled(boolean), requireEditable(boolean) ... etc to JTextComponentFixture, JButtonFixture...
        final JTextComponentFixture textBoxFixture = window.textBox(PATH_FIELD_NAME);
        JTextComponent textBox = textBoxFixture.target();
        assertThat(textBox.isEnabled()).as("textBox.enabled").isEqualTo(expectEnabled);
        assertThat(textBox.isEditable()).as("textBox.editable").isEqualTo(expectEnabled);

        JButton button = window.button(CHOOSE_BUTTON_NAME).target();
        assertThat(button.isEnabled()).as("button.enabled").isEqualTo(expectEnabled);
        return textBoxFixture;
    }

    private class Parameters {
        private final boolean enabled;
        private final JPath.SelectionMode selectionMode;

        private final Path initialPath;
        private final Path pathToSelect;

        public Parameters(final boolean enabled, JPath.SelectionMode selectionMode) {
            this.enabled = enabled;
            this.selectionMode = selectionMode;

            final boolean testPathIsADirectory = selectionMode.allowsDirectory();

            pathToSelect = testPathIsADirectory ? path.getParent() : path;

            if (enabled) {
                initialPath = null;
            } else {
                initialPath = selectionMode.allowsDirectory() ? pathForDisabledField.getParent() : pathForDisabledField;
            }
        }

        public Path getExpectedPath() {
            return enabled ? getPath() : getInitialPath();
        }

        public Path getInitialPath() {
            return initialPath;
        }

        public Path getPath() {
            return pathToSelect;
        }

        public String getInitialText() {
            return getText(initialPath);
        }

        public String getText() {
            return getText(pathToSelect);
        }

        private String getText(Path file) {
            String text = "";
            if (file != null) {
                text = file.toString();
            }
            return text;
        }

        @Override
        public String toString() {
            return "[enabled=" + enabled
                    + ", selectionMode=" + selectionMode + ", pathToSelect="
                    + pathToSelect + "]";
        }
    }
}
