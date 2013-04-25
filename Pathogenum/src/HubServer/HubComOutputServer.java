package HubServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import publicMonitors.ChatMonitor;

import utils.Conversions;
import utils.GameAddress;

/**
 * A server that handles output from hub to client
 * @author Mardrey
 *
 */
public class HubComOutputServer extends Thread{
	OutputStream os;
	Socket conn;
	ChatMonitor lm;
	GamesMonitor gm;
	int ok = 0;
	
	public HubComOutputServer(Socket s, ChatMonitor lm, GamesMonitor gm) {
		conn = s;
		try {
			os = conn.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.lm = lm;
		this.gm = gm;
		lm.registerOT(this);
	}


	public void run(){
		System.out.println("HubComOutputServer started");
		while(ok != -1){
			try {
				lm.waitForEvent(); //Blä linus vad jobbig du är...
				if(conn.isClosed()){
					ok = -1;	
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				readAndPrintMsg();
				readAndPrintGames();
			} catch (IOException e) {
				//e.printStackTrace();
				ok = -1;
			}
		}
		System.out.println("HubComOutputServer stopped");
		lm.deRegister(this);
		return;
	}
	
	private void readAndPrintGames() {
		ArrayList<GameAddress> games = gm.getGameAddresses();
		if(games != null && games.size()!=0){		
			try {
				System.out.println("writing game");
				os.write(Conversions.intToByteArray(HubServer.GAMELISTING));
				os.write(Conversions.intToByteArray(games.size()));
				for(GameAddress address : games){
					os.write(Conversions.intToByteArray(address.getGameName().length()));
					os.write(address.getGameName().getBytes());
					os.write(Conversions.intToByteArray(address.getHost().length()));
					os.write(address.getHost().getBytes());
					os.write(Conversions.intToByteArray(address.getPort()));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}


	/**
	 * Reads input from monitor and writes to clients 
	 * @throws IOException
	 */
	private void readAndPrintMsg() throws IOException{
		String msg = lm.getMessage(this);
		if (msg != null) {
			byte[] com = Conversions.intToByteArray(HubServer.SENDMESSAGE);
				os.write(com);
				com = Conversions.intToByteArray(msg.getBytes().length);
				os.write(com);
				os.write(msg.getBytes());
				System.out.println("Sending message: " + msg);
		}
	}
}
