package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.Conversions;
import utils.GameAddress;

import HubServer.GamesMonitor;
import HubServer.HubComInputServer;
import HubServer.HubServer;

public class TestHubComServer {

	OutputStream os;
	InputStream is;
	GamesMonitor gm;
	int portinc = 2055;
	Socket s;
	
	@Before
	public void setUp() throws UnknownHostException, IOException{
		HubServer hs = new HubServer(portinc);
		gm = hs.getGM();
		hs.start();
		s = new Socket(InetAddress.getByName("localhost"), portinc);
		os = s.getOutputStream();
		is = s.getInputStream();
		portinc++;
	}
	
	private void addGame(String name) throws IOException{
		byte[] cm = new byte[1];
		byte com = HubComInputServer.ADD;
		cm[0] = com;
		os.write(cm[0]);
		byte[] arr = Conversions.intToByteArray(name.getBytes().length);
		os.write(arr);
		System.out.println("bytes: " + name.getBytes());
		os.write(name.getBytes());
		os.flush();
	}
	
	private ArrayList<GameAddress> listGames() throws IOException{
		byte[] cm = new byte[1];
		byte com = HubComInputServer.LIST;
		cm[0] = com;
		os.write(cm[0]);
		os.flush();
		ArrayList<GameAddress> games = new ArrayList<GameAddress>();
		byte[] nbrGames = new byte[4];
		is.read(nbrGames);
		int nbrG = Conversions.ByteArrayToInt(nbrGames);
		for(int i = 0; i < nbrG; i++){
			byte[] buff = new byte[4];
			is.read(buff);
			int hLength = Conversions.ByteArrayToInt(buff);
			byte[] host = new byte[hLength];
			is.read(host);
			is.read(buff);
			int GNLength = Conversions.ByteArrayToInt(buff);
			byte[] gameName = new byte[GNLength];
			is.read(gameName);
			is.read(buff);
			int port = Conversions.ByteArrayToInt(buff);
			GameAddress ga = new GameAddress(new String(gameName), new String(host), port);
			games.add(ga);
		}
		return games;
	}
	
	private void removeGame(String name) throws IOException{
		byte[] cm = new byte[1];
		byte com = HubComInputServer.REM;
		cm[0] = com;
		os.write(cm[0]);
		os.write(Conversions.intToByteArray(name.getBytes().length));
		os.write(name.getBytes());
		os.flush();
	}
	
	@Test
	public void test() throws UnknownHostException, IOException, InterruptedException {
		boolean res = true;
		addGame("TestGame1");
		Thread.sleep(1000);
		if(gm.getGameAddresses().size() != 1){
			System.out.println("False");
			res = res && false;
		}
		removeGame("TestGame1");
		Thread.sleep(1000);
		if(gm.getGameAddresses().size() != 0){
			System.out.println("False");
			res = res && false;
		}
		ArrayList<String> gameNames = new ArrayList<String>();
		for(int i = 0; i < 10;i++){
			String gName = i+": "+(Math.random()*100);
			gameNames.add(gName);
			addGame(gName);
		}
		
		ArrayList<GameAddress> gal = listGames();
		for(GameAddress gl: gal){
			System.out.println(gl);
			if(!(gameNames.contains(gl.getGameName()))){
				res = res && false;
			}
		}
		assertTrue(res);
		if(!res)
		System.out.println("If this test fails, dont be superalarmed, could be because of latency, further testing is warranted");
		//fail("Not yet implemented");
		}

}
