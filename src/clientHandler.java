/**
@author Reabaln
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class clientHandler implements Runnable {
	
    Socket ClientSocket; 
    DataInputStream InServer; 
    DataOutputStream OutServer; 
    BufferedReader dStream;
    FileInputStream fStream;
	private Scanner controlScanner;
	private PrintWriter controlWriter;



	clientHandler(Socket s, DataInputStream dis, DataOutputStream dos ) {
		ClientSocket = s;
		InServer = dis;
		OutServer = dos; 
		try {
        	controlScanner = new Scanner(ClientSocket.getInputStream());
			controlWriter = new PrintWriter(ClientSocket.getOutputStream(),true);
        } catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("Problem getting input and/or outputsreams for data and/or control sockets:" + e);
		}
	}// End Constructor

	@Override
	public void run() {
	
		String cmd = controlScanner.next();

			System.out.println("Recieved command: " + cmd);
			switch (cmd) {
			
			case "GET":
				do_get();
				break;
			case "PUT": 
				do_put() ;
				break;
			case "dir":
				do_dir();
				break;
			case "mkdir":
				do_mkdir();
				break;
			case "rmdir":
				do_rmdir();
				break;
			case "rm":
				do_rm();
				break;
			case "shutdown":
				do_shutdown();
				break;
				
			default:
				System.out.println("Invalid socket control message received");
				controlWriter.println("INVALID");
				break;	}//switch  close	

			//}//while close

			

	
		try {
			System.out.println("Session ended from " + ClientSocket.getInetAddress() +" port " + ClientSocket.getPort());
			ClientSocket.close();
		} catch (IOException e) {
			System.out.println("Problem closing control and/or data socket " + e);
		}		
	
	}// End Run()

	



	//Remove file 
	private void do_rm() {
		System.out.println("rm SERVER WAS INVOKED");	
		String filePath = controlScanner.next().trim();		
		try {
			File fileName = new File(filePath);
				if(fileName.delete())OutServer.writeBytes("Successfully deleted the file: " + filePath );
				else
					OutServer.writeBytes("Error deleting the file - file doesn't exist ");	
		}  catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(System.out);
		}		
	}

	//Make new Directory, Delete if empty 
	private void do_mkdir() {
		System.out.println("mkdir SERVER WAS INVOKED");	
		String directoryPath = controlScanner.next().trim();
		try {
			File dir = new File(directoryPath);
			//Checking if the file exist, if true check whether empty or not 
			if (dir.exists()) { 
				
                 if (dir.list().length>0) {
     				OutServer.writeBytes("Directory Path Already Exist");
				}
				else {
					if (dir.delete()) OutServer.writeBytes("Directory Path was empty and has been deleted");		
					else
					OutServer.writeBytes("Error deleting existing empty file");					
				} 
							
				}// end outer  if 
			
			//Create new Directory
			else 
			{
				dir.mkdir();
				OutServer.writeBytes("Directory has been created successfully");						
			}
		
	}// end try
		 catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(System.out);
			}
		} // end mkdir
	
	//Remove Directory 
	private void do_rmdir() {
		System.out.println("rmdir SERVER WAS INVOKED");	
		String directoryPath = controlScanner.next().trim();		
		try {
			File dir = new File(directoryPath);
			if (!dir.isDirectory()) {
				OutServer.writeBytes("Invalid Directory");	return;}		
			else {
				
			if(dir.list().length>0) {

			File[] filesList = dir.listFiles();
			//Deleting Directory Content
			for(File file : filesList){
				System.out.println("Deleting "+file.getName());
				file.delete();
			}}
			if (dir.delete()) OutServer.writeBytes("Successfully deleted the Directory: " + directoryPath );
			else OutServer.writeBytes("Error deleting the directory: " + directoryPath );
			}//else end
		}  catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}


	//List Directory Content
	private void do_dir() {
		System.out.println("dir SERVER WAS INVOKED");	
		String directoryPath = controlScanner.next().trim();
		try {
		File dir = new File(directoryPath);
		if (dir.exists()) {
		File[] filesList = dir.listFiles();
		int i = 0;
		for (File file : filesList) {
		    if (file.isFile()) {
					OutServer.writeBytes(++i +":" +file.getName()+"\n");
					} 
		    }
		}//if exist end
		else {
			OutServer.writeBytes("Directory Path Doesn't Exist");

		}
			

		}// end try block
				 catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }//end dir()

	

	//Receive from Client 
	private void do_put() {
		BufferedInputStream bis;
		try {
		byte [] mybytearray  = new byte [150000];
		bis= new BufferedInputStream(ClientSocket.getInputStream());
		bis.read(mybytearray, 0,mybytearray.length);
		System.out.println("A new file has been uploaded to file server");
		OutServer.close();
		InServer.close();
		
		} catch (IOException e) {
				e.printStackTrace();
				}
	}

	//Send to Client
	private void do_get() {
		
		System.out.println("GET SERVER WAS INVOKED");
		
		String fname = controlScanner.next().trim();
		String filed= controlScanner.next().trim();
		System.out.println("Searching file name= " + fname);
		
		try {
		

		File inFile = new File(fname);
		
		if (inFile.exists()) {			
			System.out.println("File Found ....");
			System.out.println("\nThe selected file is being sent to path= " + filed);

			byte [] mybytearray  = new byte [150000];
			fStream = new FileInputStream(fname);
			FileOutputStream fos = new FileOutputStream(filed);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			int received = fStream.read(mybytearray,0,mybytearray.length);
			int len= received ;
			while (received>= 0) {
				Integer downloaded =  ((received/len)*100);
				OutServer.writeBytes("Downloading File.... " + downloaded.toString() + "%\n");
				received = fStream.read(mybytearray,len, (mybytearray.length - len));
				len += received;


			}
			bos.write(mybytearray, 0, len);
			bos.flush();
			OutServer.writeBytes("File Downloaded. \n\n");
			fos.close();
			OutServer.close();
			InServer.close();
		}
		else {
			System.out.println("File " + fname + " does not exist.");
			OutServer.writeBytes("File does not exist.");


		}
		}
		catch (IOException e) {
			System.out.println("Error receiving file." + e);

		}

	}
	
	
	private void do_shutdown() {
		System.out.println("SHUTDOWN SERVER WAS INVOKED");	
		try {
			OutServer.writeBytes("File Server Terminated Successfully... ");
			OutServer.close();
			ClientSocket.close();
			System.out.println("Server Terminated by Client");	
			System.exit(1);
			
		} catch (IOException e) {
			e.printStackTrace();
		}	

	}
	
	
	
}//	Class close