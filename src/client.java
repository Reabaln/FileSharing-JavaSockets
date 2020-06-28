
/**
@author Reabaln
 */

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;

public class client {
	
	 InputStream dataIs;
	 OutputStream dataOs;
	 static DataOutputStream out;
	 static  BufferedReader in;
	 static Socket clientSocket;
    	 static FileInputStream fStream;
	 private static  PrintWriter ctrlWriter;

	
	
	public static void main(String[] args) throws Exception {
		String envVar = System.getenv("PA1_SERVER");
		String [] parts = envVar.split(":");
		String address = parts[0];
		int port = Integer.parseInt(parts[1]);
		if (args.length<1) System.out.println("Invalid Arguments - USAGE(command + command parameters" ); 
		else {
		System.out.println("Sending Request to IP address= " + address + " , Port Number= " + port);
		 
		try {
			 clientSocket = new Socket(address, port);
			ctrlWriter = new PrintWriter(clientSocket.getOutputStream(),true);

		
			System.out.println("Socket established to " + clientSocket.getInetAddress() + " port " + clientSocket.getPort());
			
			
			String userCommand = "";
		
				
				//get command and command string arguments
				if (args != null && args.length > 0 && !args[0].trim().isEmpty()) { //check for blank lines
					userCommand = args[0].trim();	//the command given

	
					switch (userCommand) {
					case "upload":
						System.out.println("Upload Selected - Client");
						if (args.length==3) {
						do_put(args[1].toString().trim(),args[2].toString().trim() );}
						else
							System.out.println("Invalid command, USAGE(command + path on client+ path on server )");

						break;
						
					case "download":
						System.out.println("Download Selected - Client");
						if (args.length==3) {
						do_get(args[1].toString().trim(),args[2].toString().trim() );}
						else
							System.out.println("Invalid command, USAGE(command + path on server + path on client)");
						break;
						
					case "dir":
						System.out.println("List Directory Content Selected - Client");
						if (args.length==2) {
						do_dir(args[1].toString().trim());}
						else
							System.out.println("Invalid command, USAGE(command + Directory Path )");
						break;
						
					case "mkdir":
						System.out.println("Create New Directory Selected - Client");
						if (args.length==2) {
						do_mkdir(args[1].toString().trim());}
						else
							System.out.println("Invalid command, USAGE(command + Directory Path )");
						break;
						
					case "rmdir":
						System.out.println("Remove  Directory Selected - Client");
						if (args.length==2) {
						do_rmdir(args[1].toString().trim());}
						else
							System.out.println("Invalid command, USAGE(command + Directory Path )");
						break;
						
					case "rm":
						System.out.println("Remove  File Selected - Client");
						if (args.length==2) {
						do_rm(args[1].toString().trim());}
						else
							System.out.println("Invalid command, USAGE(command + file Path )");
						break;						
					case "shutdown":
						do_shutdown();
					break;
					default:
						System.out.println("Invalid command.");
					}
				}
				
		
		}// Close try
		catch (ConnectException e) {
			System.out.println("Could not connect to server.");
		    e.printStackTrace(System.out);

		} catch (IOException e) {
			System.out.println("There was a problem: " + e);
			e.printStackTrace(System.out);
		}

		}
	}// End main()



	//List Directory Content
	private static void do_dir(String dirPath) {
		String dirPathOnServer = dirPath.trim();
		ctrlWriter.println("dir " +  dirPathOnServer);
		String files; //to be used in the while loop (printing files to client)
		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			while ((files= in.readLine())!=null)
			{
				System.out.println(files);
			}
			ctrlWriter.close();
			in.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	//Shutdown Server
	private static void do_shutdown() {
		ctrlWriter.println("shutdown " );
		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			System.out.println(in.readLine());
			in.close();
			ctrlWriter.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}



	// Remove file from server
	private static void do_rm(String fileName) {
		String fileToDelete = fileName.trim();
		ctrlWriter.println("rm " +  fileToDelete);
		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			System.out.println(in.readLine());
			in.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}



	// Remove Directory
	private static void do_rmdir(String dirPath) {
		String dirPathToDelete = dirPath.trim();
		ctrlWriter.println("rmdir " +  dirPathToDelete);
		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			System.out.println(in.readLine());
			in.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	// Create a new directory
	private static void do_mkdir(String newDirPath) {
		String dirPath = newDirPath.trim();
		ctrlWriter.println("mkdir " +  dirPath);
		try {
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			System.out.println(in.readLine());
			in.close();
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}



	//Upload files to server
	private static void do_put(String pathOnClient, String pathOnServer) {
		System.out.println("Upload CLIENT WAS INVOKED");	
		String fname = pathOnClient.trim();
		String filed= pathOnServer.trim();
		ctrlWriter.println("PUT ");
		System.out.println("Searching file name= " + fname);
		try {
		File outFile = new File(fname);
		
		if (outFile.exists()) {			
			System.out.println("File Found ....");
			System.out.println("\nFile is being sent to path= " + filed);
			byte [] mybytearray  = new byte [150000];
			fStream = new FileInputStream(fname);
			FileOutputStream fos = new FileOutputStream(filed);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			int received = fStream.read(mybytearray,0,mybytearray.length);
			int len= received ;
			while (received>= 0) {
				System.out.println("Uploading file ... " + (len/outFile.length())*100 + "%");
				received = fStream.read(mybytearray,len, (mybytearray.length - len));
				len += received; 
			}
			bos.write(mybytearray, 0, len);
			bos.flush();
			fos.close();
			clientSocket.close();
			System.out.println("File uploaded");
		}
		else {
			System.out.println("File " + fname + " does not exist.");
		}
		}
		catch (IOException e) {
			System.out.println("Error receiving file." + e);

		}

	}// End upload()


	//Download file from server
	private static void do_get(String fileName, String fileD) {
		String File_Name = fileName;
		String fileDest = fileD;
		ctrlWriter.println("GET " + File_Name +" "+ fileDest );
		
			try {
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				System.out.println(in.readLine());
				System.out.println(in.readLine());
				in.close();
				clientSocket.close();
				} catch (IOException e) {
				e.printStackTrace();
				}
			
	}// End download()




}
