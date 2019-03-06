Summary:
This is a java program implementation of a Two phase commit system for CSE 5306 Spring 2018 based on the requirements given in Lab 2.
Project Implemented by Harsh Pandya 


Two Phase Commit System
This project consists of three components:
1.	A passive server to relay commands between the systems.
2.	Three clients acting as a transaction participant.
3.	One client acting as a transaction coordinator.

Following features are covered:
1.	Participants, Coordinators and  server process functions correctly.
2.	Participants logs in by providing the username.
3.	Coordinator initiates the vote request and starts the timer to accept VOTES from the participants.
4.	Participants and Coordinator handles the Timeout operations.
5.	Coordinator Handles the GLOBAL ABORT or GLOBAL COMMIT operations.
6.	Server shows HTTP message format.


Software Requirements:
1.	Eclipse IDE.
2.	JDK 6 or above.


Instructions to Run the application:
1.	Open Eclipse IDE.
2.	Unzip the project file and import it to Eclipse IDE. 
3.	Run Server.java.
4.	Run Coordinator.java. 
5.	Run Participants.java for 3 times.
