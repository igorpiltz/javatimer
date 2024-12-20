package mytimer;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

import com.jeta.forms.components.panel.FormPanel;


public class MyTimerFrame extends JFrame {
	
	JButton startButton, stopButton, resetButton;
	private Date startTime, pauseTime;
	private TimerThread timer;
	private JLabel timerLabel;
	
	public MyTimerFrame() {
		
		this.setDefaultCloseOperation ( JFrame. DO_NOTHING_ON_CLOSE );
		WindowListener windowListener = new WindowAdapter() {
			// anonymous WindowAdapter class
			public void windowClosing (WindowEvent w) {
				setVisible(false);
				dispose();
				System.exit(0);
			}
		};// end anonymous class
		this.addWindowListener( windowListener );

		
		FormPanel panel = new FormPanel("MyTimer.jfrm");
		getContentPane().add( panel );
		
		
		startButton = (JButton)panel.getButton("startButton");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startButtonHandler();
			}
		});
		
		stopButton = (JButton)panel.getButton("stopButton");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				stopButtonHandler();
			}
		});
		
		resetButton = (JButton)panel.getButton("resetButton");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				resetButtonHandler();
			}
		});
		
		timerLabel = (JLabel)panel.getComponentByName("timerLabel");
		
		
		
	}
	
	
	protected void resetButtonHandler() {
		if (timer == null) {
			startTime = null;
			pauseTime = null;
			setTimerLabel(0);
		}
			
	}


	protected void stopButtonHandler() {
		
		if (timer != null) {
			pauseTime = new Date();
			timer.stopTimer();
			timer.interrupt();
			timer = null;
			
			setTimerLabel((pauseTime.getTime() - startTime.getTime())/1000);
		}
		
	}


	protected void startButtonHandler() {
		if (timer != null)
			return;
		
		if ((pauseTime != null) && (startTime != null)) {
			Date now = new Date();
			startTime = new Date(now.getTime() - (pauseTime.getTime()-startTime.getTime()));
			
			pauseTime = null;
			timer = new TimerThread();
			timer.start();
			
		} else {
		
			startTime = new Date();
			pauseTime = null;
			timer = new TimerThread();
			timer.start();
		}
	}

	private class TimerThread extends Thread {
		
		private boolean keepTiming = true;
				
		public void run() {
			
			do {
				
				Date now = new Date();
				long secondDifference =  (now.getTime() - startTime.getTime())/1000;
				setTimerLabel(secondDifference);
				
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} while (keepTiming);
			
		}
		
		public void stopTimer() {
			
			keepTiming = false;
		}
		
		
		
	}
	
	private void setTimerLabel(long seconds) {
		
		
		long hours = seconds/(60*60);
		if (hours > 0) 
			seconds -= hours*60*60;
		
		long minutes = seconds/(60);
		if (minutes > 0) 
			seconds -= minutes*60;
		
		
		timerLabel.setText(
				leadingZero(hours) + ":"
				+ leadingZero(minutes) + ":" 
				+ leadingZero(seconds));
	}
	

	public static void main(String args[]) throws FileNotFoundException, IOException {
		try {
		      UIManager.setLookAndFeel(new com.jgoodies.looks.plastic.PlasticXPLookAndFeel());
		   } catch (Exception e) {}


		

		
		MyTimerFrame main = new MyTimerFrame();
		
		// set the size and location of this frame       
		// ComponentSizeHandler.attach(main, "MainGUI.siz", new Dimension(600, 200), true);       
		main.setLocation( 200, 100 );
		main.pack();
		main.setVisible( true );
	}


	public String leadingZero(long time) {
		if (time < 10) 
			return "0" + time;
		else return "" + time;
	}

}
