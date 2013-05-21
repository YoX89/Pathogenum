package gameserver;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import utils.Constants;
import utils.Conversions;
import utils.Misc;

public class GameServerOutputThread extends Thread {
	private OutputStream os;
	private GameMonitor gm;
	private long frameID = 0;
	Socket sock;
	private int player;

	public GameServerOutputThread(Socket s, GameMonitor gm, int playerIndex) {
		player = playerIndex;
		sock = s;
		try {
			this.os = s.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.gm = gm;
		gm.registerOThread(this);
	}

	@Override
	public void run() {
		boolean notClosed = true;
		try {
			// os.write(Conversions.intToByteArray(gm.getNbrPlayers()));
			while (notClosed) {
				System.out.println("Before GOCommand");
				byte[] movements = gm.getOutGoingCommand(frameID++, this);
				byte[] command = Conversions
						.intToByteArray(Constants.SENDMOVEMENT);
				System.out.println("Movements IN gsot: " + Misc.printByte(movements));
				// System.out.println("Writing movement from GameServer" +
				// misc.printByte(movements));
				os.write(command);
				os.write(movements);
				os.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (!sock.isClosed()) {
				try {
					sock.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			gm.deRegisterOThread(this);
			notClosed = false;
		}
		gm.sendDropped(player);
		return;
	}

	public boolean sendInit(long seed, int nbrOfPlayers, int i) {
		byte[] seedPart = Conversions.longToByteArray(seed);
		byte[] nbrPlayersPart = Conversions.intToByteArray(nbrOfPlayers);
		byte[] indexPart = Conversions.intToByteArray(i);
		// int fullLength = seedPart.length + nbrPlayersPart.length
		// + indexPart.length;
		// byte[] initMessage = new byte[fullLength];
		// ArrayList<byte[]> byteArrays = new ArrayList<byte[]>();
		// byteArrays.add(seedPart);
		// byteArrays.add(indexPart);
		// byteArrays.add(nbrPlayersPart);
		// int index = 0;
		// for(byte[] array: byteArrays){
		// for(int j = 0; j < array.length; j++){
		// initMessage[j+index] = array[j];
		// }
		// index += array.length;
		// }
		try {
			os.write(Constants.INITGAME);
			// os.write(initMessage);
			os.write(seedPart);
			os.write(indexPart);
			os.write(nbrPlayersPart);
			os.flush();
			return true;
		} catch (IOException e) {
			System.out.println("Could not write initmessage");
			e.printStackTrace();
			return false;
		}
	}

	public void sendDropped(int player) {
		if (!sock.isClosed()) {
			byte[] com = Conversions.intToByteArray(Constants.DROPPED);
			byte[] index = Conversions.intToByteArray(player);
			try {
				os.write(com);
				os.write(index);
			} catch (IOException ie) {
				ie.printStackTrace();
			}
		}
	}

}
