public class Center {
    Notepad n;

    Fonts f;

    public Center(Notepad n) {
        this.n = n;
    }

    public Center(Fonts f) {
        this.f = f;
    }

    public void nCenter() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        n.setLocation((screenSize.width - n.getWidth()) / 2, (screenSize.height - n.getHeight()) / 2);
    }

    public void fCenter() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        f.setLocation((screenSize.width - f.getWidth()) / 2, (screenSize.height - f.getHeight()) / 2);
    }
}