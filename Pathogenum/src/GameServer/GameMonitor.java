package GameServer;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.HashMap;


public class GameMonitor {
	
	private ArrayDeque<Byte>[] incComs;
	private HashMap<Long, Byte[]> outComs;
	private int players;
	
	public GameMonitor(int nbrOfPlayers){
		incComs = new ArrayDeque[nbrOfPlayers];
		outComs = new HashMap<Long, Byte[]>();
		
		for(int i = 0; i < nbrOfPlayers; ++i){
			incComs[i] = new ArrayDeque<Byte>();
		}
		this.players = nbrOfPlayers;
	}
	
	public int getNbrPlayers(){
		return players;
	}
	
	public synchronized void addIncomingCommand(byte b, int player){
		System.out.println("Adding incoming command: " + (int)b + " for player " + player);
		incComs[player-1].add(b);
	}
	
	public synchronized byte getIncomingCommand(int player){
		Byte b = incComs[player].pollFirst();
		if(b == null)
			b = new Byte((byte) 0x00);
		
//		System.out.println("Reading incoming command: " + b + " from player " + player);
		return b;
	}
	
	public synchronized void setOutgoingCommands(Byte[] b, long frame){
		outComs.put(frame, b);
	}
	
	public synchronized byte[] getOutGoingCommand(long frame){
		byte[] coms = new byte[1*players + 8];
		byte[] frameBytes = ByteBuffer.allocate(8).putLong(frame).array();
		
		for(int i = 0; i < 8; ++i){
			coms[i] = frameBytes[i];
		}
		
		Byte[] b = outComs.remove(frame);
		if(b == null){
			b = new Byte[players];
			for(int i = 0; i < players; i++){
				b[i] = 0;
			}
		}
		for(int i = 0; i < players; ++i){
			coms[8+i] = b[i];
		}
		
		return coms;
	}

}
