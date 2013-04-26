package LobbyServer;

import java.util.ArrayDeque;
import java.util.HashMap;


public class GameMonitor {
	
	ArrayDeque<Byte>[] incComs;
	HashMap<Long, Byte>[] outComs;
	
	public GameMonitor(int nbrOfPlayers){
		incComs = new ArrayDeque[nbrOfPlayers];
		outComs = new HashMap[nbrOfPlayers];
		
		for(int i = 0; i < nbrOfPlayers; ++i){
			incComs[i] = new ArrayDeque<Byte>();
		}
	}
	
	
	public synchronized void addIncomingCommand(byte b, int player){
		incComs[player].add(b);
	}
	
	public synchronized byte getIncomingCommand(int player){
		Byte b = incComs[player].pollFirst();
		if(b == null)
			b = new Byte((byte) 0x00);
		
		return b;
	}
	
	public synchronized void setOutgoingCommands(Byte[] b, long frame){
		for(int i = 0; i < b.length; ++i){
			outComs[i].put(frame, b[i]);
		}
	}
	
	public synchronized Byte getOutGoingCommand(int player, long frame){
		Byte b = outComs[player].remove(frame);
		if(b == null)
			b = new Byte((byte)0x00);
		return b;
	}

}
