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
	private long timeOffsetMilli;
	private TimerThread timer;
	private JLabel timerLabel;
	private JButton plusTenMinuteButton, plusFiveMinuteButton, plusOneMinuteButton;
	private JButton countdownButton;
	
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
		
		// Reset Button
		resetButton = (JButton)panel.getButton("resetButton");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				resetButtonHandler();
			}
		});
		
		// Plus 10 minute button
		plusTenMinuteButton = (JButton)panel.getButton("plusTenMinuteButton");
		plusTenMinuteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				plusXMinuteButtonHandler(10);
			}
		});
		
		// Plus 5 minute button
		plusFiveMinuteButton = (JButton)panel.getButton("plusFiveMinuteButton");
		plusFiveMinuteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				plusXMinuteButtonHandler(5);
			}
		});
		
		// Plus 1 minute button
		plusOneMinuteButton = (JButton)panel.getButton("plusOneMinuteButton");
		plusOneMinuteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				plusXMinuteButtonHandler(1);
			}
		});
		
		// Countdown button
		countdownButton = (JButton)panel.getButton("countdownButton");
		countdownButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				countdownButtonHandler();
			}
		});
		
		
		timerLabel = (JLabel)panel.getComponentByName("timerLabel");
		
		
		
	}
	
	
	protected void resetButtonHandler() {
		if (timer == null) {
			timeOffsetMilli = 0;
			setTimerLabel(timeOffsetMilli);
		}
			
	}


	protected void stopButtonHandler() {
		
		if (timer != null) {
			
			timeOffsetMilli = timer.getCurrentTimeOffsetMilli();
			
			timer.stopTimer();
			timer.interrupt();
						
			timer = null;
			
			setTimerLabel(timeOffsetMilli);
		}
		
	}


	protected void startButtonHandler() {
		if (timer != null)
			return;
			
		timer = new TimerThread(timeOffsetMilli, TimeDirection.UP);
		timeOffsetMilli = 0;
		timer.start();
		
		
	}
	
	protected void plusXMinuteButtonHandler(int minutes) {
		if (timer != null)
			return;
		
		timeOffsetMilli += minutes*60*1000;
		
		setTimerLabel(timeOffsetMilli);		
	}
	
	protected void countdownButtonHandler() {
		if (timer != null)
			return;
			
		timer = new TimerThread(timeOffsetMilli, TimeDirection.DOWN);
		timeOffsetMilli = 0;
		timer.start();
		
	}
	
	protected void countDownFinishedHandler() {
		timer = null;
		timeOffsetMilli = 0;
		
		setTimerLabel(timeOffsetMilli);
		
		setAlwaysOnTop(true);
		toFront();
		requestFocus();
		setAlwaysOnTop(false);
	}
	
	
	public enum TimeDirection {UP, DOWN}

	private class TimerThread extends Thread {
		
		private boolean keepTiming = true;
		
		private long timeOffsetMilli;
		
		private long currentTimeOffsetMilli;	// latest value calculated
		
		private TimeDirection direction;
		
		private Date startTime;
		
		public TimerThread(long timeOffsetMilli, TimeDirection direction) {
			this.timeOffsetMilli = timeOffsetMilli;
			this.direction = direction;
			
			startTime = new Date();
		}
		
		public void run() {
			
			do {
				
				long cto = getCurrentTimeOffsetMilli();
				System.out.print("Wake: " + cto + "\n");
				
				if (cto < 0) {
					countDownFinishedHandler();
					break;
				}
															  
				setTimerLabel(cto);
					
				try {
					long sleepTime;
					if (direction == TimeDirection.UP)
						 sleepTime = 1000 - cto%1000;
					else sleepTime = cto%1000;
					
					if (sleepTime < 1)
						sleepTime = 1000;
					
					sleep(sleepTime);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			} while (keepTiming);
			
		}
		
		public long getCurrentTimeOffsetMilli() {
			Date now = new Date();
			if (direction == TimeDirection.UP) {
				return now.getTime() - startTime.getTime() + timeOffsetMilli;
			} else {
				// TimeDirection.DOWN
				return timeOffsetMilli - (now.getTime() - startTime.getTime()); 
				
			}
		}
		
		public void stopTimer() {
			
			keepTiming = false;
		}
		
		
		
	}
	
	private void setTimerLabel(long milliSeconds) {
		
		long seconds = milliSeconds/1000;
		
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
