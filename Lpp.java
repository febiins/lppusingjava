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

    }

     public static void main(String[] args) {
        new Lpp();
    }

}