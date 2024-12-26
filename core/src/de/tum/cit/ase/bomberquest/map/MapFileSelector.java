package de.tum.cit.ase.bomberquest.map;
import javax.swing.*;
import java.io.File;

public class MapFileSelector {
    public static String selectMapFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a Map File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Properties Files", "properties"));

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        } else {
            System.err.println("No file selected. Exiting...");
            System.exit(1); // Exit the game if no file is selected
            return null;
        }
    }
}
