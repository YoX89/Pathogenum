package client;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

import utils.Conversions;

public class InputThread extends Thread {
	Socket sock;
	InputStream is;
	LinkedList<String> chatBuffer = new LinkedList<String>();
	public InputThread(Socket sock){
		this.sock = sock;
		try {
			is = sock.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void run(){
		while(true){
			byte[] command = new byte[4];
			try {
				is.read(command);
				int intCommand = Conversions.ByteArrayToInt(command);
				switch(intCommand){
				case ClientConnectionHandler.SENDMESSAGE:
					
					int check = is.read(command);		
					checkInput(check);
					intCommand = Conversions.ByteArrayToInt(command);
					byte[] input = new byte[intCommand];
					check = is.read(input);	
					checkInput(check);
					chatBuffer.offer(new String(input));
					break;
				}
				
			} catch (IOException e) {
		
				e.printStackTrace();
			}
		}
	}
	
	private void checkInput(int check) {
		if(check == -1){
			try {
				sock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	public ArrayList<String> getChatMessages() {

		ArrayList<String> list = new ArrayList<String>();
		int size = chatBuffer.size();
		for(int i = 0; i < size; i++){
			list.add(chatBuffer.pop());
		}
		return list;
	}
	
}
