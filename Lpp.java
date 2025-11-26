import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

class Point2 {
    int x, y, z;
    Point2(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class Line {
    int a, b, c;
    Line(int a, int b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
}

public class Lpp extends JFrame {

    JTextField x1, y1, c1;
    JTextField x2, y2, c2;
    JTextField x0, y0;

    DrawPanel panel;
    JTextArea result;

    ArrayList<Point2> corners = new ArrayList<>();
    Point2 optimal = null;
    Line l1, l2;
    int Cx, Cy;
    int bestz = Integer.MIN_VALUE;

    Lpp() {
        setTitle("LPP using Graphical Method");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildInputPanel(), BorderLayout.WEST);

        JPanel right = new JPanel(new BorderLayout());

        panel = new DrawPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Graph"));
        right.add(panel, BorderLayout.CENTER);

        result = new JTextArea(6, 30);
        result.setEditable(false);
        JScrollPane scroll = new JScrollPane(result);
        scroll.setBorder(BorderFactory.createTitledBorder("Result Table"));
        right.add(scroll, BorderLayout.SOUTH);

        add(right, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

   class DrawPanel extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int W = getWidth();
        int H = getHeight();

        int Ox = 60;      // X-origin pixel
        int Oy = H - 60;  // Y-origin pixel

        // Background
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, W, H);

        // Draw grid
        g2.setColor(new Color(200, 200, 200));
        for (int i = Ox; i < W; i += 40) g2.drawLine(i, 20, i, Oy);
        for (int j = Oy; j > 20; j -= 40) g2.drawLine(Ox, j, W - 20, j);

        // Draw axes
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(Ox, Oy, W - 20, Oy); // X-axis
        g2.drawLine(Ox, Oy, Ox, 20);     // Y-axis
        g2.drawString("X", W - 30, Oy + 15);
        g2.drawString("Y", Ox - 20, 30);

        if (corners.isEmpty())
            return;

        // Determine scale
        int maxX = 0, maxY = 0;
        for (Point2 p : corners) {
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }

        double scale = Math.min((W - 100) / (double) maxX, (H - 100) / (double) maxY);

        // Draw tick marks
        g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
        for (int i = 1; i <= maxX; i++) {
            int x = Ox + (int) (i * scale);
            g2.drawLine(x, Oy - 3, x, Oy + 3);
            g2.drawString(String.valueOf(i), x - 3, Oy + 15);
        }
        for (int i = 1; i <= maxY; i++) {
            int y = Oy - (int) (i * scale);
            g2.drawLine(Ox - 3, y, Ox + 3, y);
            g2.drawString(String.valueOf(i), Ox - 20, y + 3);
        }

        // Sort corners around centroid to avoid twisted polygon
    double cx = 0, cy = 0;
    for (Point2 p : corners) {
        cx += p.x;
        cy += p.y;
        }
        cx /= corners.size();
        cy /= corners.size();

        final double CCX = cx;
        final double CCY = cy;

        corners.sort(Comparator.comparingDouble(p -> Math.atan2(p.y - CCY, p.x - CCX)));

        // Draw feasible region
        int n = corners.size();
        int[] xs = new int[n];
        int[] ys = new int[n];

        for (int i = 0; i < n; i++) {
            xs[i] = Ox + (int) (corners.get(i).x * scale);
            ys[i] = Oy - (int) (corners.get(i).y * scale);
        }

        g2.setColor(new Color(0, 200, 0, 80));
        g2.fillPolygon(xs, ys, n);
        g2.setColor(Color.GREEN.darker());
        g2.drawPolygon(xs, ys, n);

        // Draw corner points
        g2.setColor(Color.BLUE);
        for (Point2 p : corners) {
            int px = Ox + (int) (p.x * scale);
            int py = Oy - (int) (p.y * scale);
            g2.fillOval(px - 4, py - 4, 8, 8);
            g2.drawString("(" + p.x + "," + p.y + ")", px + 6, py - 6);
        }

        // Draw optimal point
        if (optimal != null) {
            int px = Ox + (int) (optimal.x * scale);
            int py = Oy - (int) (optimal.y * scale);
            g2.setColor(Color.RED);
            g2.fillOval(px - 7, py - 7, 14, 14);
            g2.drawString("OPT (" + optimal.x + ", " + optimal.y + ")", px + 10, py - 10);
        }
    }
}


    JPanel buildInputPanel() {
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(260, 600));
        p.setLayout(new GridLayout(15, 1, 5, 5));

        p.add(new JLabel("Maximize Z = Cx X + Cy Y"));
        x0 = new JTextField("x");
        y0 = new JTextField("y");
        p.add(x0);
        p.add(y0);

        x1 = new JTextField("x1");
        y1 = new JTextField("y1");
        c1 = new JTextField("c1");
        p.add(x1);
        p.add(y1);
        p.add(c1);

        x2 = new JTextField("x2");
        y2 = new JTextField("y2");
        c2 = new JTextField("c2");
        p.add(x2);
        p.add(y2);
        p.add(c2);

        JButton solve = new JButton("Solve");
        solve.addActionListener(e -> solvelpp());
        p.add(solve);

        return p;
    }

    void solvelpp() {
        result.setText("");
        corners.clear();
        optimal = null;
        bestz = Integer.MIN_VALUE;

        try {
            Cx = Integer.parseInt(x0.getText().trim());
            Cy = Integer.parseInt(y0.getText().trim());

            int a1 = Integer.parseInt(x1.getText().trim());
            int b1 = Integer.parseInt(y1.getText().trim());
            int c1v = Integer.parseInt(c1.getText().trim());
            l1 = new Line(a1, b1, c1v);

            int a2 = Integer.parseInt(x2.getText().trim());
            int b2 = Integer.parseInt(y2.getText().trim());
            int c2v = Integer.parseInt(c2.getText().trim());
            l2 = new Line(a2, b2, c2v);

        } catch (Exception e) {
            result.setText("Invalid Input");
            return;
        }

        Line lx = new Line(1, 0, 0);
        Line ly = new Line(0, 1, 0);

        Line[] lines = {l1, l2, lx, ly};

        ArrayList<Point2> pts = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            for (int j = i + 1; j < lines.length; j++) {
                Point2 p = intersect(lines[i], lines[j]);
                if (p != null && p.x >= 0 && p.y >= 0)
                    pts.add(p);
            }
        }

        for (Point2 p : pts) {
            if (isFeasible(p))
                addUniqueCorner(p);
        }

        if (corners.isEmpty()) {
            result.setText("No Feasible Region");
            panel.repaint();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("x\ty\tZ\n");
        sb.append("------------------------------\n");

        for (Point2 p : corners) {
            p.z = Cx * p.x + Cy * p.y;
            sb.append(p.x + "\t" + p.y + "\t" + p.z + "\n");

            if (p.z > bestz) {
                bestz = p.z;
                optimal = p;
            }
        }

        sb.append("\nOptimal Point:\n");
        sb.append("x = " + optimal.x + "\n");
        sb.append("y = " + optimal.y + "\n");
        sb.append("Max Z = " + optimal.z);

        result.setText(sb.toString());
        panel.repaint();
    }

    Point2 intersect(Line L1, Line L2) {
        int det = L1.a * L2.b - L2.a * L1.b;
        if (det == 0)
            return null;

        int x = (L1.c * L2.b - L2.c * L1.b) / det;
        int y = (L1.a * L2.c - L2.a * L1.c) / det;

        return new Point2(x, y);
    }

    boolean isFeasible(Point2 p) {
        return (l1.a * p.x + l1.b * p.y <= l1.c &&
                l2.a * p.x + l2.b * p.y <= l2.c &&
                p.x >= 0 && p.y >= 0);
    }

    void addUniqueCorner(Point2 p) {
        for (Point2 q : corners) {
            if (q.x == p.x && q.y == p.y)
                return;
        }
        corners.add(p);
    }

    public static void main(String[] args) {
        new Lpp();
    }
}
