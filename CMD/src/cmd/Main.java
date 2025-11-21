package cmd;

import javax.swing.*;

/**
 *
 * @author marye
 */
public class Main {
    public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> {
            new GUICmd(); 
        });
         
         
    }
}
