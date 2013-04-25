package test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

import LobbyServer.LobbyServer;

import utils.Conversions;


public class TestLobbyServer {

	private void sendMessage(OutputStream os, String message) throws IOException{
		byte[] buff = Conversions.intToByteArray(LobbyServer.SENDMESSAGE);
		os.write(buff);
		buff = Conversions.intToByteArray(message.getBytes().length);
		os.write(buff);
		buff = message.getBytes();
		os.write(buff);
	}
	
	private String readMessage(InputStream is) throws IOException{
		byte[] buff = new byte[4];
		is.read(buff);
		int com = Conversions.ByteArrayToInt(buff);
		if(com != LobbyServer.SENDMESSAGE){
			System.out.println("Not sendmessage");
			return null;
		}
		is.read(buff);
		int mLength = Conversions.ByteArrayToInt(buff);
		buff = new byte[mLength];
		is.read(buff);
		String retmsg = new String(buff);
		return retmsg;
	}
	
	@Test
	public void test() {
		boolean succ = true;
		int port = 2085;
		LobbyServer ls = new LobbyServer(port);
		ls.start();
		String returnmsg = "";
		try {
			Socket connection = new Socket("localhost",port);
			InputStream is = connection.getInputStream();
			OutputStream os = connection.getOutputStream();
			String message = "Testphrase";
			
			sendMessage(os, message);
			String retmsg = readMessage(is);
			if(retmsg == null){
				succ = false;
				assertTrue(false);
				return;
			}
			String[] sp = retmsg.split(":");
			sp[1] = sp[1].trim();
			if(!sp[1].equals(message)){
				succ = false;
				System.out.println("Not same-> Org: " + message + ", Ret: " + retmsg);
			}
			returnmsg = retmsg;
			connection.close();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Return: " + returnmsg);
		assertTrue(succ);
		System.out.println("Done");
	}

}
