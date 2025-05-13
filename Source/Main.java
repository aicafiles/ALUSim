package Source;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            Alu aluInterface = new Alu();
            aluInterface.setVisible(true);
            aluInterface.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        });
    }
}