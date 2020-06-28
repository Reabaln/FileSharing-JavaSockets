/**
@author Reabaln
 */

import java.io.*;
import java.net.*;


public class server {
	
	public static void main(String[] args) throws IOException  
    { 
		int portNumber= 0;
		if (args.length==2) {
			if (args[0].equals("start")) {
				portNumber=Integer.parseInt(args[1]);
			}
			else { System.out.println(" 1 Error starting server - USAGE: start <portNumber> ");
			System.exit(1);}
		}
		else {System.out.println(" 2 Error starting server - USAGE: start <portNumber> ");
		System.exit(1);}
		
        // server is listening on port 5056 
        ServerSocket ss = new ServerSocket(portNumber); 

          
        // running infinite loop for getting 
        // client request 
        while (true)  
        { 
			System.out.println("Server waiting for Connection...");
            Socket s = null; 
              
            try 
            { 
                // socket object to receive incoming client requests 
                s = ss.accept(); 
                System.out.println("A new client is connected : " + s); 
                
                // obtaining input and out streams 
                DataInputStream dis = new DataInputStream(s.getInputStream()); 
                DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
                  
                System.out.println("Assigning new thread for new client"); 
  
                // create a new thread object and invoking the start()method.

    			new Thread(new clientHandler(s,dis,dos)).start();     
                         
            } 
            catch (Exception e){ 
                e.printStackTrace(); 
            } 
            //ss.close();
        } 
    } 

}