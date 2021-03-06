package lobbyserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import utils.Constants;
import utils.Conversions;

/**
 * A server that reads input to the lobby and writes it to the monitor
 * @author Mardrey
 *
 */
public class LobbyInputThread extends Thread{
	
	InputStream is;
	Socket conn;
	LobbyMonitor lm;
	int ok = 0;
	
	public LobbyInputThread(Socket s, LobbyMonitor lm){
		conn = s;
		try {
			is = conn.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.lm = lm;
	}
	
	public void run(){
		while(ok != -1){
			byte[] com = new byte[4];
			try {
				ok = is.read(com);
			}catch(IOException e){
				e.printStackTrace();
				ok = -1;
			}
			
			switch(com[0]){
			case Constants.SENDMESSAGE:
				try {
					fetchMessage();
				} catch (IOException e) {
					ok = -1;
				}
			break;
			case Constants.LEAVEGAME:
				ok = -1;
			break;
			case Constants.SENDGAMENAME:
				lm.shouldSendGameName();
				break;
			case Constants.SETREADY: 
				lm.setReady(conn.getInetAddress().getHostName(), true);
				break;
			}
			if (this.isInterrupted()) {
				return; //keep socket
			}
		
		}
		try {
			if(!conn.isClosed())
			conn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		lm.notifyWaiters();
		return;
	}

	/**
	 * Reads messages and puts them in the monitor
	 * @throws IOException
	 */
	private void fetchMessage() throws IOException{
		byte[] buff = new byte[4];
			ok = is.read(buff);
		int mLength = Conversions.ByteArrayToInt(buff);
		buff = new byte[mLength];
			ok = is.read(buff);
		String message = new String(buff);
		lm.putMessage(conn.getInetAddress().getHostAddress(),message);
	}
}
