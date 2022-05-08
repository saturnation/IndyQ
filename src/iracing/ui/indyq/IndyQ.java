package iracing.ui.indyq;

import jirsdk.Util;
import jirsdk.data.Run;
import iracing.ui.indyq.IndyQLogic.TimeDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import static java.awt.Color.WHITE;
import static jirsdk.Util.lap_time;
import static jirsdk.Util.lap_time_short;
import static jirsdk.data.defines.TrackLocation.description;

public class IndyQ extends JComponent {

    IndyQReadings indyQReadings = new IndyQReadings();
    IndyQLogic logic = new IndyQLogic();

    Timer t = new Timer(50, e -> update());

    Font f;
    FontMetrics fm;
    private final Dimension d = new Dimension(400, 250);
    Run run;

    public IndyQ() {
        f = new Font("Courier New", Font.BOLD, 16);
        run = new Run();
    }

    public void start() {
        t.start();
    }

    void update() {
        indyQReadings.update(run);
        logic.update(indyQReadings);
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return d;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setFont(f);
        if (fm == null) {
            fm = g.getFontMetrics(f);
            setSize(d);
        }
        if (indyQReadings.running) {
            message(g, "Lap: " + indyQReadings.lap, 20);
            display_time(g, "1", 40, logic.timeDisplay(1), 0);
            display_time(g, "2", 60, logic.timeDisplay(2), 1);
            display_time(g, "3", 80, logic.timeDisplay(3), 2);
            display_time(g, "4", 100, logic.timeDisplay(4), 3);
            mph(g, Util.mph(logic.avg_mph), 120);
            message(g, "Avg  : " + lap_time(logic.avg_lap_time), 120);
            message(g, description(indyQReadings.track_surface), 140);
            message(g, logic.description(), 160);
            message(g, "" + indyQReadings.percent_lap, 180);
            message(g, "State : " + logic.state(), 200);
            message(g, "iRacing Running...", 220);
        } else {
            if (indyQReadings.ready) {
                message(g, "iRacing ready but not running...", 20);
            } else {
                message(g, "iRacing not ready...", 20);
            }
        }

    }

    private void display_time(Graphics g, String lap_name, int y, TimeDisplay td, int i) {
        switch (td) {
            case NO_TIME -> message(g, "Lap " + lap_name + ": 0.0", y);
            case ON_LAP -> message(g, "Lap " + lap_name + ": " + lap_time_short(indyQReadings.lap_time), y);
            case LAP_COMPLETE -> message(g, "Lap " + lap_name + ": " + lap_time(logic.laps[i]), y);
        }
        if (logic.mph[i] > 0.0f) {
            mph(g, Util.mph(logic.mph[i]), y);
        }
    }

    public void message(Graphics g, String m, int y) {
        g.setColor(WHITE);
        g.drawString(m, 20, y);
    }

    public void mph(Graphics g, String speed, int y) {
        g.setColor(WHITE);
        g.drawString(speed, 165, y);
    }

    static int x, y;

    public static void main(String[] args) throws InterruptedException {

        final JFrame w = new JFrame();
        w.setUndecorated(true);
        w.setOpacity(0.7f);
        w.setFocusable(true);
        final IndyQ indyQ = new IndyQ();
        w.getContentPane().add(indyQ);
        w.setBounds(760, 650, 0, 0);
        w.pack();
        w.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
            }
        });
        w.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                x = e.getX();
                y = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        w.setAlwaysOnTop(true);
        w.setVisible(true);
        indyQ.start();
    }
}
