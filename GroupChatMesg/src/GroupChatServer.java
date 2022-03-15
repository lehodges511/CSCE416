/*
 * Psuedocode for implementation of a group chat server in java
 * By Srihari Nelakuditi for CSCE 416
 * Modified by Lauren Hodges for CSCE 416 Program 2
 */

// Package for I/O related stuff
import java.io.*;

// Package for socket related stuff
import java.net.*;

// Package for list related stuff
import java.util.*;

/*
 * This class does all the group chat server's job
 *
 * It consists of parent thread (code inside main method) which accepts
 * new client connections and then spawns a thread per connection
 *
 * Each child thread (code inside run method) reads messages
 * from its socket and relays the message to the all active connections
 *
 * Since a thread is being created with this class object,
 * this class declaration includes "implements Runnable"
 */
public class GroupChatServer implements Runnable
{
    // Each instance has a separate socket
    private Socket clientSock;

    // The class keeps track of active clients
    private static List<PrintWriter> clientList;

    // Constructor sets the socket for the child thread to process
    public GroupChatServer(Socket sock)
    {
        clientSock = sock;
    }

    // Add the given client to the active clients list
    // Since all threads share this, we use "synchronized" to make it atomic
    public static synchronized boolean addClient(PrintWriter toClientWriter)
    {
        return(clientList.add(toClientWriter));
    }

    // Remove the given client from the active clients list
    // Since all threads share this, we use "synchronized" to make it atomic
    public static synchronized boolean removeClient(PrintWriter toClientWriter)
    {
        return(clientList.remove(toClientWriter));
    }

    // Relay the given message to all the active clients
    // Since all threads share this, we use "synchronized" to make it atomic
    public static synchronized void relayMessage(
            PrintWriter fromClientWriter, String mesg)
    {
        // Iterate through the client list and
        // relay message to each client (but not the sender)
    }

    // The child thread starts here
    public void run()
    {
        // Read from the client and relay to other clients
        try {
            // Prepare to read from socket
            BufferedReader sockReader = new BufferedReader(new InputStreamReader((clientSock.getInputStream())));

            // Get the client name
            String cName = sockReader.readLine();

            // Prepare to write to socket with auto flush on
            PrintWriter sockWriter = new PrintWriter(clientSock.getOutputStream(), true);

            // Add this client to the active client list
            addClient(sockWriter);

            // Keep doing till client sends EOF
            while (true) {
                // Read a line from the client
                String mesg = sockReader.readLine();

                // If we get null, it means client quit, break out of loop
                if(mesg == null){
                    System.out.println("Client exited.");
                    break;
                }

                // Else, relay the line to all active clients
                relayMessage(sockWriter, cName+" :\t"+mesg);
            }

            // Done with the client, remove it from client list
            removeClient(sockWriter);
            sockWriter.close();
        }
        catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    /*
     * The group chat server program starts from here.
     * This main thread accepts new clients and spawns a thread for each client
     * Each child thread does the stuff under the run() method
     */
    public static void main(String args[])
    {
        // Server needs a port to listen on
        if (args.length != 1) {
            System.out.println("usage: java GroupChatServer <server port>");
            System.exit(1);
        }

        // Be prepared to catch socket related exceptions
        Socket cSock = null;
        try {
            // Create a server socket with the given port
            ServerSocket sSock = new ServerSocket(Integer.parseInt(args[0]));

            // Keep accepting/serving new clients
            while (true) {
                // Wait to accept another client
                System.out.println("Waiting for a client...");
                cSock = sSock.accept();
                System.out.println("Connected to a client.");

                // Spawn a thread to read/relay messages from this client
                Thread child = new Thread(new GroupChatServer(cSock));
                child.start();
            }
        }
        catch(Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }
}