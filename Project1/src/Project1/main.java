package Project1;
import com.jaunt.*;
import com.jaunt.component.*;

import java.io.*;

public class main {


public static void main(String[] args) {
		
	String[] c0= new String[100];	//columns
	String[] c1= new String[100];
	String[] c2= new String[100];
	String[] c3= new String[100];
	String[] c4= new String[100];
	String[][][] fallHours = new String[5][7][2];	// room, day, open/close
	
	try{
		int i=0;
		UserAgent userAgent = new UserAgent();
        //open HTML from a String.
        userAgent.visit("http://recsports.nd.edu/facilities/rockne-memorial/");
        Element body = userAgent.doc.findFirst("<body>");
        Table table = userAgent.doc.getTable(0);
        
        Elements elements0 = table.getRow("Mondays");
        //for(Element element : elements) System.out.println(element.innerXML());
        for(Element element0 : elements0){
        	c0[i]=element0.innerXML();
        	i++;
        }
        
        //
        Elements elements1 = table.getRow("Tuesdays"); 
        for(Element element1 : elements1){
        	c1[i]=element1.innerXML();
        	i++;
        }
        
        //
        Elements elements2 = table.getRow("Wednesdays"); 
        for(Element element2 : elements2){
        	c2[i]=element2.innerXML();
        	i++;
        }
        
        for(int t=0;t<7;t++) c0[t]=c0[t+1]; //shifting out room of rockne 
        for(int t=0;t<7;t++) c1[t]=c1[t+1];
        for(int t=0;t<7;t++) c2[t]=c2[t+1];
        //for(int t=0;t<7;t++) c3[t]=c3[t+1];
        //for(int t=0;t<7;t++) c4[t]=c4[t+1];
        
        for(int j=0;j<7;j++){
        	System.out.println(c0[j]);
        	String delims="[-]";
        	//fallHours[1][j]=col1[j].split(delims);
        }
        
        for(int k=0;k<7;k++){
        	//System.out.println(fallHours[1][k][1]);
        	//System.out.println(fallHours[1][k][2]);
        }
	}
    catch(JauntException e){
        System.err.println(e);
    }
	
 
	
	
	
	/*
	Rectangle r=new Rectangle(10,10,20,40);
	r.setLocation(5,5);
	BankAccount Sue= new BankAccount(1000);
	BankAccount Bob = Sue;
	Bob.withdraw(999);
	System.out.println(Sue.getBalance());
	*/
	
	/*	MONTE CARLO PI
	int in=0;
	Ellipse2D.Double c=new Ellipse2D.Double(0, 0, 1, 1);
	for (int i=1;i<100000;i++)
	{
	double x = new Random().nextDouble();
	//System.out.println(x);
	double y = new Random().nextDouble();
	//System.out.println(y);
	Point2D p=new Point2D.Double(x, y);
	if(c.contains(p))
	{
		in++;
	}
	}
	System.out.println(in);
	*/
	/*
	final double taxRate = 0.07;
	//System.out.println(taxRate);
	int x = 50;
	int b = x;
	x+=10;
	System.out.println(x+" "+b);
	
	Graphics g=new Graphics(g);
	*/
	
	
	
}}

/*
	public void paint(Graphics g)
	{
		g.drawRect(0, 0, 20, 20);
	}



}
*/