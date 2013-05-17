package GameServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import utils.Constants;
import utils.Conversions;
import utils.misc;

public class GameServerOutputThread extends Thread {
	private OutputStream os;
	private GameMonitor gm;
	private long frameID = 0;

	public GameServerOutputThread(Socket s, GameMonitor gm) {
		try {
			this.os = s.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.gm = gm;

	}

	@Override
	public void run() {
		try {
			// os.write(Conversions.intToByteArray(gm.getNbrPlayers()));
			while (true) {
				byte[] movements = gm.getOutGoingCommand(frameID++);
				byte[] command = Conversions
						.intToByteArray(Constants.SENDMOVEMENT);
				// System.out.println("Writing movement from GameServer" +
				// misc.printByte(movements));
				os.write(command);
				os.write(movements);
				os.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean sendInit(long seed, int nbrOfPlayers, int i) {
		byte[] seedPart = Conversions.longToByteArray(seed);
		byte[] nbrPlayersPart = Conversions.intToByteArray(nbrOfPlayers);
		byte[] indexPart = Conversions.intToByteArray(i);
		int fullLength = seedPart.length + nbrPlayersPart.length
				+ indexPart.length;
		byte[] initMessage = new byte[fullLength];
		ArrayList<byte[]> byteArrays = new ArrayList<byte[]>();
		byteArrays.add(seedPart);
		byteArrays.add(indexPart);
		byteArrays.add(nbrPlayersPart);
			int index = 0;
			for(byte[] array: byteArrays){
				for(int j = 0; j < array.length; j++){
					initMessage[j+index] = array[j];
				}
				index += array.length;
			}
		try {
			os.write(Constants.INITGAME);
			os.write(initMessage);
			os.flush();
			return true;
		} catch (IOException e) {
			System.out.println("Could not write initmessage");
			e.printStackTrace();
			return false;
		}
	}

}
