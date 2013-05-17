package GameServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

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
				byte[] movements = gm.getOutGoingCommand(++frameID);
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

	public void sendInit(long seed, int nbrOfPlayers, int i) {
		System.out.println("Sending init from server with seed: " + seed + ", nbrOfPlayers: " + nbrOfPlayers + " and index: " + i);
		byte[] frameZeroPart = Conversions.longToByteArray(0);
		byte[] seedPart = Conversions.longToByteArray(seed);
		byte[] nbrPlayersPart = Conversions.intToByteArray(nbrOfPlayers);
		byte[] indexPart = Conversions.intToByteArray(i);
		byte[] initMessage = new byte[frameZeroPart.length + seedPart.length
				+ indexPart.length + nbrPlayersPart.length];
		System.out.println(frameZeroPart.length + " " + seedPart.length + " " + nbrPlayersPart.length + " " + indexPart.length);
		int index = 0;
		for (int j = 0; j < frameZeroPart.length; j++) {
			initMessage[index + j] = frameZeroPart[j];
		}
		index += frameZeroPart.length;
		for (int j = 0; j < seedPart.length; j++) {
			initMessage[index + j] = seedPart[j];
		}
		index += seedPart.length;
		for (int j = 0; j < nbrPlayersPart.length; j++) {
			initMessage[index + j] = nbrPlayersPart[j];
		}
		index += nbrPlayersPart.length;
		for (int j = 0; j < indexPart.length; j++) {
			initMessage[index + j] = indexPart[j];
		}
		try {
			os.write(Constants.SENDMOVEMENT);
			os.write(initMessage);
			os.flush();
		} catch (IOException e) {
			System.out.println("Could not write initmessage");
			e.printStackTrace();
		}
	}

}
