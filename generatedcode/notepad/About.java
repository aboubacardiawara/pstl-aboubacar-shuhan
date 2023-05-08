public class About extends JPanel {
    private static final long serialVersionUID = 1;

    public About() {
        JLabel label1 = new JLabel(new ImageIcon(this.getClass().getResource("images/java.gif")));
        this.add(label1);
        JLabel label2 = new JLabel("<html><li>JAVA� Notepad</li><li><p>Ver# 2.0</li>"
                + "<li><p>Coded by: Salah Al-Thubaiti</li><li><p>KFUPM, CS</li><li>"
                + "<p>CopyRight� 2001-2002</li></html>");
        this.add(label2);
    }
}