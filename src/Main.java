import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JogoMinesGUI jogoGUI = new JogoMinesGUI();
            jogoGUI.setVisible(true);
        });
    }
}
