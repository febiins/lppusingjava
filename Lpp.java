import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;


class Point2{
    double x,y,z;
    Point2(double x, double y){
        this.x=x;
        this.y=y;
    }
}

class Line{
    double x,y,c;
    Line(double x, double y, double c){
        this.x=x;
        this.y=y;
        this.c=c;
    }
}

public class Lpp extends JFrame{

    JTextField x1,y1,c1;    //textfield declaration
    JTextField x2,y2,c2;    //textfield declaration    
    JTextField x,y;         //textfield declaration

    DrawPanel panel;    //panel declaration    
    JTextArea result;   //textarea declaration

    ArrayList<Point2> corners=new ArrayList<>();    //arraylist for storing feasible points
    Point2 optimal=null;
    Line l1,l2;
    double Cx,Cy;
    double bestz=Double.NEGATIVE_INFINITY;
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
        x=new JTextField("x");
        y=new JTextField("y");
        p.add(x);
        p.add(y);

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

        return p;
    }

     public static void main(String[] args) {
        new Lpp();
    }

}