//Name: Harsh Pandya
//UTA ID: 1001552519

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


//creating a class for Coordinator. A coordinator is responsible for Sending 
// VOTE REQUEST and checking the following operations of ABORTING and COMMITING the operation.
public class Coordinator {
	
	// Creating the frame for coordinator
	static JFrame participantWindow = new JFrame("Coordinator");
	static JTextArea messageArea = new JTextArea(22,40);
	static JTextField textField = new JTextField(40);
	static JLabel blanklabel = new JLabel("       ");
	static JButton VoteButton = new JButton("REQUEST VOTE");

	static JLabel nameLabel =  new JLabel("Client");
	
	static BufferedReader in;
	static PrintWriter out;
	
	String agent="HTTPTool/1.1 \\\\n";
	Date d =  new Date();
	String date = d.toString();
	String type="text/html\r\n";
	int length = msg.length();
	String Server_host="http://localhost:9999";
	static String msg="";
	static String str="";
	static int voteCount =1;
	
	//Constructor for the coordinator
	public Coordinator() {
		participantWindow.setLayout(new FlowLayout());//setting the layout of the frame
		participantWindow.add(new JScrollPane(messageArea));//enabling scrollpane on the frame
		participantWindow.add(blanklabel);
		participantWindow.add(textField);//adding the textfield to send messages to the clients
		participantWindow.setSize(475,500);//setting the size of the frame
		participantWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//enabling default close operation
		participantWindow.setVisible(true);//making the window visible
		participantWindow.add(VoteButton);//adding the vote request button
		
		participantWindow.add(nameLabel);//adding the name label to the window
		
		textField.addActionListener(new SendListener());//adding the listener to the textfield to send messages by pressing ENTER key
		VoteButton.addActionListener(new VoteListener());//adding listener to vote button to send vote request to the participants
		//HTTP Encode string
		msg="User-Agent: "+agent+"\n"+"Date: "+date+"\n"+"Content-Type:"+type+"\n"+"Content-Length:"+length+"\n"+"Server Host: "+Server_host;
		
		
	}
	
	//method to start the coordinator
	void startCoordinator() throws UnknownHostException, IOException {
		Socket soc = new Socket("localhost", 9999);//Enabling socket connection on port 9999 and localhost
		in = new BufferedReader(new InputStreamReader(soc.getInputStream()));//enabling inputstream reader to read incoming streams on socket
		out = new PrintWriter(soc.getOutputStream(),true);//writing output stream to socket
		
		
		int count = 1;
		//reading messages and appending it to the textarea
		while(true) {			
			 str = in.readLine();
			messageArea.append(str+"\n");
			
		}
		
	}
	
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		Coordinator coordinator = new Coordinator();//Creating new coordinator
		coordinator.startCoordinator();//starting the coordinator
	}

}

//class to create a listener for sending the message to the participants
class SendListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Coordinator.out.println(Coordinator.textField.getText());//sending the text on the textfield to the output stream
		Coordinator.textField.setText("");//resetting the textfield
		
	}
	
}
//listener for the vote request button
class VoteListener extends TimerTask implements ActionListener {
	int count=0;// this counter checks if this is the first message sent by the Vote Button  
	int timerCount=20;//counter to track 20 second timer

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(count<1) {
		Coordinator.out.println(Coordinator.msg);//send the HTTP encode message
		count++;//incrementing the counter so that it doesnt send the message again after once
		}
		
		Coordinator.out.println("PRESS COMMIT OR ABORT");// Request particpants to Vote commit or Abort		
		new VoteTimer();// starts the timer of 20 secs to check check for other participants vote
		
		
		
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(timerCount>0) {
			//System.out.println("Timer:"+timerCount);
			//Checks if GLOBAL_ABORT is received then initiates the global abort and releases the connection
			if(Coordinator.str.contains("GLOBAL_ABORT")) {
				Coordinator.messageArea.append("Initiating GLOBAL ABORT.... \n");//Appends this message on coordinator window
				Coordinator.out.println("ABORT COMPLETED");//Send abort complete message to all participants
				try {
					//releases the input and output connection of the coordinator and participant
					Coordinator.in.close();
					Coordinator.out.close();
					Participant.in.close();
					Participant.out.close();
					cancel();//cancels the timer once the connection is released
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			//if the commit is received from the participants
			 if(Coordinator.str.contains("COMMIT_INITIATED")) {
				//System.out.println("COMMSSSSSSs");
				Coordinator.messageArea.append("Commit:"+Coordinator.voteCount);//counts the number of vote and appends it to the window
				Coordinator.out.println("Waiting for other clients to commit");
				Coordinator.voteCount++;//increments the number of votes
				//System.out.println(Coordinator.voteCount);
				//checks if the votecount is 3 the initialized the GLOBAL_COMMIT opearation
				if(Coordinator.voteCount>3) {
					Coordinator.out.println("GLOBAL_COMMIT");
					Coordinator.messageArea.append("Global commit performed");
					cancel();//cancels the timer
				}
			}
			
			timerCount--;//decrements the time counter
		}else {
			//System.out.println("Time up");
			
			//This enables GLOBAL ABORT if less than 3 commits are received
			Coordinator.messageArea.append("\n Time up. Initiating GLOBAL ABORT.... \n");
			try {
				//Sends the message to all participants
				Coordinator.out.println("ABORT COMPLETED");
				//releases the input and output connection.
				Coordinator.in.close();
				Coordinator.out.close();
				Participant.in.close();
				Participant.out.close();
				cancel();//cancels the timer
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.exit(0);
		}
		
	}
	
}
//Timer
 class VoteTimer {
	
	Timer timer;
	
	public VoteTimer() {
		
		timer = new Timer(); //creating new Timer object
		timer.schedule(new VoteListener(), 0,1*1000);//schedules the timer after evry 1 sec for next 20 seconds
	}
	
} 