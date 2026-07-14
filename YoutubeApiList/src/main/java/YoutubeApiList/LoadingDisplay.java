package YoutubeApiList;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class LoadingDisplay extends JFrame 
{
	private static final long serialVersionUID = 1L;

	private static String
		TITLE = "",
		ERROR_TEXT = "Error: ",
		COMPLETED_TEXT = "Completed.",
		DEFAULT_TEXT = "Loading...";
	private int 
		completeWait = 1000,
		errorWait = 3000;
	
	private static Dimension
		MIN_SIZE = new Dimension(300, 80);
	private static Point
		LOADING_LOCATION = new Point(200,200);
	private JLabel
		outputLabel;
	
	public LoadingDisplay()
	{
		buildWidgets();
	}
	
	public void setTitleText(String title)
	{
		this.setTitle(TITLE + title);
	}
	
	public void setCompleted()
	{
		outputLabel.setText(COMPLETED_TEXT);
		Runnable r = new Runnable() 
		{
			@Override
			public void run() 
			{
				try {
					Thread.sleep(completeWait);
					System.exit(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
	}
	
	public void setError(String error)
	{
		outputLabel.setText(ERROR_TEXT + error);
		outputLabel.setForeground(Color.RED);
		Runnable r = new Runnable() 
		{
			@Override
			public void run() 
			{
				try {
					Thread.sleep(errorWait);
					System.exit(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
	}
	
	private void buildWidgets()
	{
		outputLabel = new JLabel(DEFAULT_TEXT);
		this.add(outputLabel);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(MIN_SIZE);
		this.setLocation(LOADING_LOCATION);
		
		this.setVisible(true);
	}
}
