package Project1;
import java.applet.*;
import java.awt.*;
import java.awt.Color;
public class GraphicsFirstTry extends Applet{

	public void paint(Graphics g)
	{
		setSize(500,500);
		int sq=40;
		for(int i=0;i<9;i++)
		{
			for(int j=0;j<9;j++)
			{
				//Color gr=new Color(70,70,70);
				//g.setColor(gr);
				g.drawRect(70+sq*i, 100+sq*j, sq, sq);
				//g.drawRoundRect(5, 5, 30, 30, 6, 6);
				
				//Color bl=new Color(255,255,255);
				//g.drawRect(20+sq*i, 20+sq*j, sq*3, sq*3);

			}
		}
	}
}
