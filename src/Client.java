import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;



/*
 * This class create a new client which is a seperate thread,
 * different from the ui thread.
 * If you have better ways to do this stuff, do it and send a pull request
 */
public class Client extends Thread{
	BufferedReader bReader=null;
	BufferedWriter bWriter = null; 
	String newString="",oldString="",recievedString="";
	boolean running=true;
	ServerSocket  serverSocket = null;
    Socket socket= null;
    boolean wait=false;
	public Client() throws IOException{
//		socket = s;
	}
	
	@Override
	public void run(){
	   try {
		   //Running a server on port 2800
			serverSocket = new ServerSocket(2800);
			System.out.println("Waiting For Client");
			socket = serverSocket.accept();
			System.out.println("Connected to "+ socket.getInetAddress().toString());
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			System.out.println("Socket Closed");
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		try {
			bReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bWriter = new BufferedWriter(new PrintWriter(socket.getOutputStream()));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

//		Below code sets up the system clipboard for us
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		oldString="";newString="";
		try{
//			Get the new string from the clip board
			newString = (String) clipboard.getData(DataFlavor.stringFlavor);
		}
		catch(Exception e){
			
		}
		oldString = newString;
		Reciever r1 = new  Reciever(this);
		r1.start();
		while(running){
//			Compares the content of the clip board after every half second
//			.If content is different, send it to the mobile phone
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			synchronized (newString) {
				try{
					newString = (String) clipboard.getData(DataFlavor.stringFlavor);
				}
				catch(Exception e){
					
				}
				if(!oldString.equals(newString)){
					System.out.println("boom: "+" "+newString);
					if(bWriter!=null){
						try {
							bWriter.write(newString+"\n");
							bWriter.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					
				}
				oldString=newString;	
			}
		}
	}
	
//	called by the receiver class, see below
	public void  setString(String data){
		synchronized (newString) {
			newString=oldString=data;	
			System.out.println("In set String "+ newString);
			StringSelection stringSelection = new StringSelection(data);
			Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			clpbrd.setContents(stringSelection, null);
		}
	}
	
	public void stopRunning(){
    	try {
    		if(socket!=null)
    			socket.close();
    		serverSocket.close();
    		if(bReader!=null && bWriter!=null){
    			bReader.close();
    			bWriter.close();
    		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//			e.printStackTrace();
			System.out.println("System Stopped");
		}
    	running=false;
	}
	
//	Separate THread that receives the data from the android app and sets the 
//	system clip board
	class Reciever extends Thread{
		Client client;
		public Reciever(Client client) {
			this.client = client;
		}
		@Override
		public void run(){
			System.out.println("Waiting for input fron client");
			while(running){
				try {
					String data = bReader.readLine();
						if(data==null)
							return;
						client.setString(data);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("Done");
		}
	}
}
