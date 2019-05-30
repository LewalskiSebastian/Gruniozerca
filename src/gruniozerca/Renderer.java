package gruniozerca;

import javax.swing.*;
import java.awt.*;

public class Renderer extends JPanel
{
    private static final long serialVersionUID = 1;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Gruniozerca.gruniozerca.repaint(g);
    }
}
