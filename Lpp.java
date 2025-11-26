import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;


class Point2{
    int x,y,z;
    Point2(int x, int y){
        this.x=x;
        this.y=y;
    }
}

class Line{
    int x,y,c;
    Line(int x, int y, int c){
        this.x=x;
        this.y=y;
        this.c=c;
    }
}

class DrawPanel extends JPanel{
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        g2.setStroke(new BasicStroke(2));

        int w = getWidth();
        int h = getHeight();

        int ox = 60;
        int oy = h-60;

        g2.drawLine( ox, oy,w-30,oy);
        g2.drawLine(ox, oy,ox,30);

        if(corners.isEmpty()){
            return;
        }

        int maxX=0,maxY=0;

        for(Point2 p : corners){
            if(p.x>maxX){
                maxX=p.x;
            }
            if(p.y>maxY){
                maxY=p.y;
            }
        }
        double scale= Math.min((w-100)/(double)maxX,(h-100)/(double)maxY);
        corners.sort(Comparator.comparingDouble(p->Math.atan2(p.y,p.x)));

        int n = corners.size();
        int [] xs = new int [n];
        int [] ys = new int [n];

        for(int i=0;i<n;i++){
            xs[i]=ox + (int) (corners.get(i).x * scale);
            ys[i]=oy - (int)(corners.get(i).y * scale);
        }

        g2.setColor(new Color(0,200,0,90));
        g2.fillPolygon(xs,ys,n);

        g2.setColor(Color.GREEN.darker);
        g2.drawPolygon(xs,ys,n);

        g2.setColor(Color.BLUE);
        for(Point2 p:corners){
            int px= ox+(int)(p.x * scale);
            int py= oy-(int)(p.y * scale);
            g2.fillOval(px-4, py-4, 8, 8);
        }

        if(optimal != null){
            int px = ox + (int)(optimal.x * scale);
            int py = oy - (int)(optimal.y * scale);

            g2.setColor(Color.RED);
            g2.fillOval(px - 7, py - 7, 14, 14);
            g2.drawString("OPT (" + optimal.x + "," + optimal.y + ")", px + 10, py - 10);
        }
    }
}

public class Lpp extends JFrame{

    JTextField x1,y1,c1;    //textfield declaration
    JTextField x2,y2,c2;    //textfield declaration    
    JTextField x0,y0;         //textfield declaration

    DrawPanel panel;    //panel declaration    
    JTextArea result;   //textarea declaration

    ArrayList<Point2> corners=new ArrayList<>();    //arraylist for storing feasible points
    Point2 optimal=null;
    Line l1,l2;
    int Cx,Cy;
    int bestz=Integer.MIN_VALUE;
    Lpp(){
        setTitle("LPP using Graphical Method");
        setSize(600,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildInputPanel(),BorderLayout.WEST);

        JPanel right=new JPanel(new BorderLayout());

        panel = new DrawPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Graph"));
        right.add(panel,BorderLayout.CENTER);

        result = new JTextArea(6,30);
        result.setEditable(false);
        JScrollPane scroll = new JScrollPane(result);
        scroll.setBorder(BorderFactory.createTitledBorder("Resutl Table"));
        right.add(scroll,BorderLayout.SOUTH);

        add(right,BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);


    }
     class DrawPanel extends JPanel {
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawString("Graph will appear here", 50, 50);
        }
    }
    JPanel buildInputPanel(){
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(260,600));
        p.setLayout(new GridLayout(13,1,5,5));
        p.add(new JLabel("Max Z=x+y"));
        x0=new JTextField("x");
        y0=new JTextField("y");
        p.add(x0);
        p.add(y0);

        x1=new JTextField("x1=");
        y1= new JTextField("y1=");
        c1= new JTextField("c1=");
        p.add(x1);
        p.add(y1);
        p.add(c1);

        x2= new JTextField("x2=");
        y2= new JTextField("y2=");
        c2=new JTextField("c2=");

        p.add(x2);
        p.add(y2);
        p.add(c2);

        JButton solve=new JButton("Solve");
        solve.addActionListener(e->solvelpp());
        p.add(solve);

        return p;
    }
    void solvelpp(){
        result.setText("0");
        corners.clear();
        optimal=null;
        bestz=Integer.MIN_VALUE;

        try{
            Cx=Integer.parseInt(x0.getText().trim());
            Cy=Integer.parseInt(y0.getText().trim());

            int vx1=Integer.parseInt(x1.getText().trim());
            int vy1 = Integer.parseInt(y1.getText().trim());
            int vc1= Integer.parseInt(c1.getText().trim());
            l1= new Line(vx1, vy1, vc1);

            int vx2=Integer.parseInt(x2.getText().trim());
            int vy2 = Integer.parseInt(y2.getText().trim());
            int vc2= Integer.parseInt(c2.getText().trim());
            l2= new Line(vx1, vy2, vc2);

        }catch(Exception e){
            result.setText("Invalid Input");
            return;

        }

        Line  lx  = new Line(1,0,0);
        Line ly = new Line(0,1,0);

        Line[] lines={l1,l2,lx,ly};

        ArrayList<Point2> pts= new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            for (int j = i + 1; j < lines.length; j++) {
                Point2 p = intersect(lines[i], lines[j]);
                if (p != null && p.x >= 0 && p.y >= 0)
                    pts.add(p);
            }
        }

        for(Point2 p :pts){
            if(isFeasible(p)){
                addUniqueCorner(p);
            }
        }
        if(corners.isEmpty()){
            result.setText("No Feasilble Region");
            panel.repaint();
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("x \t y \t z \t");
        sb.append("---------------\n");

        for(Point2 p : corners){
            p.z=Cx * p.x + Cy * p.y;
            sb.append(p.x +"\t"+ p.y+"\t"+p.z+"\n");
            if(p.z > bestz){
                bestz=p.x;
                optimal=p;
            }
        }
        sb.append("\nOptimal Point\n");
        sb.append("x="+optimal.x+"\n");
        sb.append("y="+optimal.y+"\n");
        sb.append("Max Z="+optimal.z);

        result.setText(sb.toString());
        panel.repaint();

    }

     public static void main(String[] args) {
        new Lpp();
    }

}