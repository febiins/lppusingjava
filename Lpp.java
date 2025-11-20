import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.event.*;


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

    Drawpanel panel;    //panel declaration    
    JTextArea result;   //textarea declaration

    ArrayList<Point2> corners=new ArrayList<>();    //arraylist for storing feasible points
    Point2 optimal=null;
    Line l1.l2;
    double Cx,Cy;
    double bestz=double.NEGATIVE_INFINITY;
    LPP(){
        setTitle("LPP using Graphical Method");
        setSize(600,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

}