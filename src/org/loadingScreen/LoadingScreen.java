package org.loadingScreen;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class LoadingScreen extends JFrame {

    private static final long serialVersionUID = -2368849047541331782L;

    private final ColorCalculator coCa;

    private final JPanel content;
    private final JPanel[] sqr;
    private static final int radius = 50;
    private static final int sqrsize = 15;

    private boolean mouseResetted;

    public LoadingScreen() {
        super();

        this.setBackground(new Color(0, 0, 0));
        setUndecorated(true);
        // The restriction error can be suppressed by once removing and then
        // again adding the JRE System Library to the build path
        // See:
        // http://stackoverflow.com/questions/860187/access-restriction-on-class-due-to-restriction-on-required-library-rt-jar
        // AWTUtilities.setWindowOpaque(this, false);
        // setOpacity(1);
        coCa = new ColorCalculator(this, 50);

        content = new JPanel();
        content.setLayout(null);
        content.setBackground(new Color(123, 123, 123, 0));
        content.setVisible(true);

        sqr = new JPanel[10];

        content.setPreferredSize(new Dimension((radius + sqrsize) * 2, (radius + sqrsize) * 2));

        for (int i = 0; i < sqr.length; i++) {

            final int x = radius + sqrsize + (int) (radius * Math.sin(2 * i * Math.PI / sqr.length));
            final int y = radius + sqrsize + (int) (radius * Math.cos(2 * i * Math.PI / sqr.length));

            sqr[i] = new JPanel();
            sqr[i].setBackground(Color.white);
            sqr[i].setSize(sqrsize, sqrsize);
            sqr[i].setLocation(x, y);
            sqr[i].setVisible(true);
            content.add(sqr[i]);
        }
        this.add(content);

        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);

        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(final MouseEvent arg0) {
                mouseResetted = true;
            }

            @Override
            public void mousePressed(final MouseEvent arg0) {
            }

            @Override
            public void mouseExited(final MouseEvent arg0) {
            }

            @Override
            public void mouseEntered(final MouseEvent arg0) {
            }

            @Override
            public void mouseClicked(final MouseEvent arg0) {
            }
        });

        mouseResetted = true;
        final MouseMotionListener mml = new MouseMotionListener() {

            private int lastX;
            private int lastY;

            @Override
            public void mouseMoved(final MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseDragged(final MouseEvent arg0) {
                if (!mouseResetted) {
                    final int deltaX = arg0.getXOnScreen() - lastX;
                    final int deltaY = arg0.getYOnScreen() - lastY;

                    if (isVisible()) {
                        setLocation(getLocationOnScreen().x + deltaX, getLocationOnScreen().y + deltaY);
                    }
                }
                mouseResetted = false;

                lastX = arg0.getXOnScreen();
                lastY = arg0.getYOnScreen();

            }
        };

        this.addMouseMotionListener(mml);
    }

    public void start() {
        this.setVisible(true);
        new Thread(coCa).start();
    }

    public void stop() {
        coCa.stop();
        mouseResetted = true;
        this.setVisible(false);
    }

    public void setColor(final float r, final float g, final float b) {
        for (int i = (sqr.length - 1); i > 0; i--) {
            sqr[i].setBackground(sqr[i - 1].getBackground());
        }
        sqr[0].setBackground(new Color(r, g, b));
    }

    public void setColor(final int r, final int g, final int b) {
        for (int i = sqr.length - 1; i > 0; i--) {
            sqr[i].setBackground(sqr[i - 1].getBackground());
        }
        sqr[0].setBackground(new Color(r, g, b));
    }
}
