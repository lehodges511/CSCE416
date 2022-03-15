/*
 * Implementation of a two-way message server in java
 * By Srihari Nelakuditi for CSCE 416
 * modified by Lauren Hodges
 */

// Package for I/O related stuff
import java.io.*;

// Package for socket related stuff
import java.net.*;

/*
 * This class does all the server's job
 * It receives the connection from client
 * and prints messages sent from the client
 */
public class TwoWayMesgServer {
	/*
	 * The server program starts from here
	 */
	public static void main(String args[]) {
		// Server needs the port number to listen on
		if (args.length != 1) {
			System.out.println("usage: java TwoWayMesgServer <port>");
			System.exit(1);
		}
// Get the port on which server should listen */
		int serverPort = Integer.parseInt(args[0]);

		// Be prepared to catch socket related exceptions
		try {
			// Create a server socket with the given port
			ServerSocket serverSock = new ServerSocket(serverPort);
			System.out.println("Waiting for a client ...");

			// Wait to receive a connection request
			Socket clientSock = serverSock.accept();
			System.out.println("Connected to client");

			// No other clients, close the server socket
			serverSock.close();

			// Prepare to read from client
			BufferedReader fromClientReader = new BufferedReader(
					new InputStreamReader(clientSock.getInputStream()));

			//write to client
			PrintWriter toClient = new PrintWriter(clientSock.getOutputStream(), true);

			//read from keyboard
			BufferedReader fromUserReader = new BufferedReader(new InputStreamReader(System.in));
			while (true) {
				// Read a message from the client
				String message = fromClientReader.readLine();

				// If we get null, it means client sent EOF
				if (message == null) {
					System.out.println("Client closed connection");
					break;
				}

				// Display the message
				System.out.println("Client: " + message);
				String msg = fromUserReader.readLine();
				toClient.println(msg);
			}
			clientSock.close();
		}
		catch(Exception e) {
			// Print the exception message
			System.out.println(e);
		}
	}
}
