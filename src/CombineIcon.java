import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class CombineIcon implements Icon{
    private Icon top;
    private Icon bottom;
    
    public CombineIcon(Icon top, Icon bottom) {
        this.top = top;
        this.bottom = bottom;
    }

    public int getIconHeight() {
        return Math.max(top.getIconHeight(), bottom.getIconHeight());
    }

    public int getIconWidth() {
        return Math.max(top.getIconWidth(), bottom.getIconWidth());
    }

    public void paintIcon(Component c, Graphics g, int x, int y){
        bottom.paintIcon(c, g, x, y);
        top.paintIcon(c, g, x, y);
    }
}
