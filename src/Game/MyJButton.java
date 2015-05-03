package Game;

import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class MyJButton extends JButton {

    MyJButton(ImageIcon icon, ImageIcon hover, ImageIcon press) {
        super();
        setIcon(icon);
        setPressedIcon(press);
        setRolloverEnabled(true);
        setRolloverIcon(hover);
        setBorderPainted(false);
        setContentAreaFilled(false);

        Dimension size = new Dimension(getIcon().getIconWidth(), getIcon().getIconHeight());
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);

    }
}
