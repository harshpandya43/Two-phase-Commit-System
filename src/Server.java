//Name: Harsh Pandya
//UTA ID: 1001552519



import java.awt.FlowLayout;
import java.awt.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


//Creating the server for the 2 phase commit application
public class Server {

//Declaring various swing objects to create GUI for the server 
	static JFrame serverWindow = new JFrame("Server");
	static JTextArea messageArea = new JTextArea(22,40);
	static BufferedReader in;
	static PrintWriter out;
	static JLabel nameLabel = new JLabel("Server");
	
	//Array list to store usernames and printwriters
	static ArrayList<PrintWriter> printWriters = new ArrayList<PrintWriter>();
	static ArrayList<String> userNames = new ArrayList<String>();
	
	//constructor for the server
	public Server() {
		serverWindow.setLayout(new FlowLayout());
		serverWindow.add(new JScrollPane(messageArea));//adding scrollpane
		messageArea.setEditable(false);//making the textarea editable
		serverWindow.setSize(475,500);//setting the size of the window
		serverWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//default operation on clicking CLOSE button
		serverWindow.setVisible(true);//making the window visible
		serverWindow.add(nameLabel);//adding the label on the window
		
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Server server = new Server();// creating new server object
		messageArea.append("Waiting for clients...."+"\n");
		ServerSocket ss = new ServerSocket(9999);//Creating new socket connection to the server with port 9999
		
		
		//This loop will continue look for incoming client requests
		while(true) {
			Socket soc = ss.accept();//Accepts the incoming client request
			messageArea.append("Client connected"+"\n");//Appends the message on the textarea
			ConversationHandler handler = new ConversationHandler(soc);//creating a new thread for the client
			handler.start();//starting the thread
		}
		
	}

}


