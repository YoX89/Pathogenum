package client;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

import utils.Conversions;
import utils.GameAddress;
/**
 * an inputthread responsible for getting input to the client from the server.
 * @author BigFarmor, Mardrey
 *
 */
public class InputThread extends Thread {
    Socket sock;
    DatagramSocket udpSocket;
    InputStream is;
    LinkedList<String> chatBuffer = new LinkedList<String>();
    ArrayList<GameAddress> gameList = new ArrayList<GameAddress>();
    boolean ok = true;
    public InputThread(Socket sock, DatagramSocket udpSocket){
        this.sock = sock;
        this.udpSocket = udpSocket;
        try {
            is = sock.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run(){
        while(ok){
            byte[] command = new byte[4];
            try {
                is.read(command);
                int intCommand = Conversions.ByteArrayToInt(command);
                switch(intCommand){
                case ClientConnectionHandler.SENDMESSAGE:
                    int check = is.read(command);       
                    if(checkInput(check))
                        return;
                    intCommand = Conversions.ByteArrayToInt(command);
                    byte[] input = new byte[intCommand];
                    check = is.read(input);   
                    if(checkInput(check))
                        return;
                    String text = new String(input);
                    chatBuffer.offer(text);
                    break;
                case ClientConnectionHandler.GAMELISTING:
                	System.out.println("gets game");
                	gameList = new ArrayList<GameAddress>();
                	int check2 = is.read(command);
                	 if(checkInput(check2))
                         return;
                	 int nbrGames = Conversions.ByteArrayToInt(command);
                	 for(int i = 0; i < nbrGames; i++){
                		 check2 = is.read(command);
                		 if(checkInput(check2))
                             return;
                		 int nameLength = Conversions.ByteArrayToInt(command);
                		 byte[] nameArray = new byte[nameLength];
                		 check2 = is.read(nameArray);
                		 if(checkInput(check2))
                             return;
                		 String name = new String(nameArray);
                		 check2 = is.read(command);
                		 if(checkInput(check2))
                             return;
                		 int hostLength = Conversions.ByteArrayToInt(command);
                		 byte[] hostArray = new byte[hostLength];
                		 check2 = is.read(hostArray);
                		 if(checkInput(check2))
                             return;
                		 String host = new String(hostArray);
                		 check2 = is.read(command);
                		 if(checkInput(check2))
                             return;
                		 int port = Conversions.ByteArrayToInt(command);
                		 gameList.add(new GameAddress(name,host,port));
                		 break;
                	 }
                }
               
            } catch (IOException e) {
                return;
            }
        }
    }
   /**
    * check to see if the input could be read, otherwise close socket
    * @param check
    * @return
    */
    private boolean checkInput(int check) {
        if(check == -1){
            try {
            	if(sock.isClosed())
            		return true;
                sock.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * returns the available chatmessages.
     * @return
     */
    public ArrayList<String> getChatMessages() {

        ArrayList<String> list = new ArrayList<String>();
        int size = chatBuffer.size();
        for(int i = 0; i < size; i++){
            list.add(chatBuffer.pop());
        }
        return list;
    }
	public ArrayList<GameAddress> getGames() {
		return gameList;
	}
   
}