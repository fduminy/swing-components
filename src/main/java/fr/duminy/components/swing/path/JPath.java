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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Swing component allowing to choose a {@link java.nio.file.Path} and display its value.
 */
public class JPath extends JPanel {
    private static final Logger LOG = LoggerFactory.getLogger(JPath.class);

    static final String PATH_FIELD_NAME = "pathField";
    static final String CHOOSE_BUTTON_NAME = "chooseButton";

    private JTextField pathField;
    private JButton chooseButton;
    private final SelectionMode selectionMode;
    private Path path;

    public JPath(SelectionMode selectionMode) {
        super(new BorderLayout());

        this.selectionMode = (selectionMode == null) ? SelectionMode.FILES_ONLY : selectionMode;

        buildComponent();

        pathField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePathFromText();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePathFromText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePathFromText();
            }
        });

        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayFileChooser();
            }
        });

        setColumns(20);
    }

    private void buildComponent() {
        pathField = new JTextField(new LockablePlainDocument(), null, 0);
        pathField.setName(PATH_FIELD_NAME);

        add(pathField, BorderLayout.CENTER);

        chooseButton = new JButton("...");
        chooseButton.setName(CHOOSE_BUTTON_NAME);
        add(chooseButton, BorderLayout.EAST);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        chooseButton.setEnabled(enabled);
        pathField.setEnabled(enabled);
        pathField.setEditable(enabled);
        ((LockablePlainDocument) pathField.getDocument()).setWritable(enabled);
    }

    public void setColumns(int columns) {
        pathField.setColumns(columns);
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        setPath(path, true);
    }

    SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public static void displayFileChooser(final JPath parent, final Path initialPath, final SelectionMode selectionMode) {
        final JFileChooser jfc = new JFileChooser();

        jfc.setFileSelectionMode(selectionMode.fileChooserMode);

        if (initialPath != null) {
            File f = initialPath.toAbsolutePath().toFile();
            jfc.setCurrentDirectory(f.getParentFile());
            jfc.setSelectedFile(f);
        } else {
            jfc.setSelectedFile(null);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int jfcResult = jfc.showOpenDialog(parent);
                if (jfcResult == JFileChooser.APPROVE_OPTION) {
                    Path selectedPath = jfc.getSelectedFile().toPath().toAbsolutePath();
                    parent.setPath(selectedPath, true);
                }
            }
        });
    }

    private void displayFileChooser() {
        displayFileChooser(this, getPath(), selectionMode);
    }

    private void updatePathFromText() {
        String text = pathField.getText();
        Path path = Paths.get(text);
        setPath(path, false);
    }

    private static void throwDisabledFieldException() {
        throw new IllegalStateException("component is disabled");
    }

    private void setPath(Path path, boolean updateText) {
        if (!isEnabled()) {
            throwDisabledFieldException();
        }

        this.path = path;

        if (updateText) {
            if (path == null) {
                pathField.setText("");
            } else {
                pathField.setText(path.toString());
            }
        }
    }

    public static enum SelectionMode {
        /**
         * @see {@link JFileChooser#FILES_ONLY}.
         */
        FILES_ONLY(JFileChooser.FILES_ONLY, true, false),

        /**
         * @see {@link JFileChooser#DIRECTORIES_ONLY}.
         */
        DIRECTORIES_ONLY(JFileChooser.DIRECTORIES_ONLY, false, true),

        /**
         * @see {@link JFileChooser#FILES_AND_DIRECTORIES}.
         */
        FILES_AND_DIRECTORIES(JFileChooser.FILES_AND_DIRECTORIES, true, true);

        private final int fileChooserMode;
        private final boolean allowsFile;
        private final boolean allowsDirectory;

        private SelectionMode(int fileChooserMode, boolean allowsFile, boolean allowsDirectory) {
            this.fileChooserMode = fileChooserMode;
            this.allowsFile = allowsFile;
            this.allowsDirectory = allowsDirectory;
        }

        public final boolean allowsFile() {
            return allowsFile;
        }

        public final boolean allowsDirectory() {
            return allowsDirectory;
        }
    }

    private static class LockablePlainDocument extends PlainDocument {
        private boolean writable = true;

        @Override
        public void insertString(int offs, String str, AttributeSet a)
                throws BadLocationException {
            if (writable) {
                super.insertString(offs, str, a);
            } else {
                throwDisabledFieldException();
            }
        }

        @Override
        public void remove(int offs, int len) throws BadLocationException {
            if (writable) {
                super.remove(offs, len);
            } else {
                throwDisabledFieldException();
            }
        }

        public void setWritable(boolean writable) {
            this.writable = writable;
        }
    }
}
