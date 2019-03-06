//Name: Harsh Pandya
//UTA ID: 1001552519

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Logger;


//Handler class that is responsible for creating new Participant Threads
class ConversationHandler extends Thread{
	Socket socket;
	BufferedReader in;
	PrintWriter out;
	String name;
	int clientCount=1;
	
	//Constructor to enable Socket connection to this thread
	public ConversationHandler(Socket socket)throws IOException {
		// TODO Auto-generated constructor stub
		this.socket=socket;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		int clientCount=0;//counter to check the name conflicts
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(),true);
			
			/*******************/
			while(true) {
				if(clientCount>0) { //checks if the name already exists or not
					out.println("NAMEALREADYEXISTS");
				}else {// if not then it will send the message NAMEREQUIRED so that user can enter the name
					out.println("NAMEREQUIRED");
				}
				
				name = in.readLine();// reads the name entered by the participant
				if(name == null) {//if the name is null then the user is asked to enter the name again
					return;
				}
				//this checks if the username is alredy present. If not then it will add it to the userNames array list
				if(!Server.userNames.contains(name)) {
					Server.userNames.add(name);
					//the following code generates a log file for every new client created
					try {
						FileHandler handler = new FileHandler("C:\\Users\\pandy\\Desktop\\"+name+".log", true);// creating the new log file with username.log
						Logger logger = Logger.getLogger("Log");
						logger.addHandler(handler);
						logger.info("INIT");//sets the state of the participant as INIT initially
						System.out.println("log created");
					} catch (SecurityException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//LOGS END
					break;
				}
				clientCount++;
			}
			out.println("NAMEACCEPTED"+name);//if the name is accepted then send it over the output stream
			/************************************/
						
			Server.printWriters.add(out);
			 //For sending message to call clients
			while(true) {			
				
				String message = in.readLine();//Reads the message over the input stream
				//System.out.println(message);
				//If any participant sends abort then it helps coordinator to initialize GLOBAL ABORT
				if(message.equalsIgnoreCase("abort")) {
					//out.println("GLOBAL_ABORT");
					for(PrintWriter writer: Server.printWriters) {
						writer.println(name+":"+"GLOBAL_ABORT");//prints GLOBAL ABORT on every participant screen
						Server.messageArea.append(name+":"+"GLOBAL_ABORT");//Sends which user initialized GLOBAL ABORT
					}
					break;
				}
				//If the commit is received then send the COMMIT_INITIATED message to the coordinotor and 
				//informs client about the Commit operation
				if(message.equalsIgnoreCase("COMMIT")) {
					for(PrintWriter writer: Server.printWriters) {
						writer.println(name+":"+"COMMIT_INITIATED");
						//Participant.messageArea.append("COMMIT INITIATED");
						
					}
					break;
				}
				if(message == null) {
					return;
				}
				
				for(PrintWriter writer: Server.printWriters) {
					writer.println(name+":"+message);
					Server.messageArea.append(message+"\n");
				}
			}
			
		}catch(Exception e) {
			
		}
	}
}