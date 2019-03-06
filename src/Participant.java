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
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Participant {
	
	static JFrame clientWindow = new JFrame("Participant");
	static JTextArea messageArea = new JTextArea(22,40);
	static JTextField textField = new JTextField(40);
	static JLabel blanklabel = new JLabel("       ");
	static JButton commitButton = new JButton("Commit");
	static JButton abortButton = new JButton("Abort");
	static JLabel nameLabel =  new JLabel("Client");
	
	
	static BufferedReader in; //Bufferedreader to accpet incoming message from socket
	static PrintWriter out; //Printwriter to write message to socket
	static String str ="";
	public Participant() {
		clientWindow.setLayout(new FlowLayout());
		clientWindow.add(new JScrollPane(messageArea));
		clientWindow.add(blanklabel);
		clientWindow.add(textField);
		clientWindow.setSize(475,500);
		clientWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clientWindow.setVisible(true);
		clientWindow.add(commitButton);
		clientWindow.add(abortButton);
		clientWindow.add(nameLabel);
		
		textField.addActionListener(new Listener());
		commitButton.addActionListener(new CommitListener());
		abortButton.addActionListener(new AbortListener());
		
	}
	
	//Starting the client thread
	void startClient() throws UnknownHostException, IOException {
		Socket soc = new Socket("localhost", 9999);//creating new socket connection on the server with port 9999
		in = new BufferedReader(new InputStreamReader(soc.getInputStream()));//instantating new InputStreamreader 
		out = new PrintWriter(soc.getOutputStream(),true);//instantaing new outputstream reader to write to socket
		
		//this loops continously checks for the incoming messages
		while(true) {
			 str = in.readLine();//reads incoming line
			 if(str.equals("NAMEREQUIRED")) {//checks if the string is equal to NAMEREQUIRED
					String name = JOptionPane.showInputDialog(// if yes then it will generate new Dialogbox which will ask client to enter his name
							clientWindow,
							"Enter a unique name",
							"Name Required!",
							JOptionPane.PLAIN_MESSAGE);
					out.println(name);// sending the entered name back to the clinet
				}else if(str.equals("NAMEALREADYEXISTS")) {//checks if the name already exists
					String name = JOptionPane.showInputDialog(//if yes then it will ask user to enter name again
							clientWindow,
							"Enter another name",
							"Name Already Exists!",
							JOptionPane.WARNING_MESSAGE);
					out.println(name);
				}else if(str.startsWith("NAMEACCEPTED")) {//if the name is accepted
					textField.setEditable(true);//makes the textfield editable
					nameLabel.setText("Hello: "+str.substring(12));//sets the name label to username
					
					
				}else {
					messageArea.append(str+"\n");//appends the message of the client window
				}
			
						
			
		}
		
	}
	
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		Participant participant = new Participant();//creates new participant object
		participant.startClient();//starts new participant
	}

}

//Listener for textfield so that user can hit enter and send text to other participants
class Listener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Participant.out.println(Participant.textField.getText());//prints the entered text on the textfield 
		Participant.textField.setText("");//resets the textfield
		
	}
	
}
//Listener for commit button. This will also enable timer once the particant will click Commit Button
class CommitListener extends TimerTask implements ActionListener{
	int timerCount =20;//initializes the timer

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Participant.out.println("COMMIT");//Send COMMIT to the coordinator once it hits commit
		Participant.textField.setText("");//resets the textfield
		
		
	}
	
	//The following code checks if the participant doesn't hear GLOBAL_COMMIT from
	//the coordinator in 20 seconds it will ABORT the operation.
	@Override
	public void run() {
		if(timerCount>20) {
			if(!Participant.str.contains("GLOBAL_COMMIT")) {
				Participant.out.println("ABORT");
			}
			
		}
		
		// TODO Auto-generated method stub
		
	}
	
}


//Listener for Abort Button. This will send ABORT to coordinator once the participant hits it.
class AbortListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Participant.out.println("ABORT");//Send the string ABORT to other participants and the coordinator 
		Participant.textField.setText("");//resets the textfield
		
	}
	
}
//timer class for commit operation. This class enables the timer and assigns it to
//CommitListener. This timer will call CommitListener for next 20sec till the time
//it either receives GLOBAL_COMMIT else it will ABORT the operation.
class CommitTimer {
	
	Timer timer; //Creating new Timer object
	
	//constructor for Timer
	public CommitTimer() {
		
		timer = new Timer();//instantating new Timer object
		timer.schedule(new CommitListener(), 0,1*1000);//scheduling timer for 1 sec
	}
	
} 