import java.util.*;
import java.io.*;
import java.net.*;

/**
 * Creates a server that allows
 * someone to connect and interact
 * with the array sorting GUI.
 *
 * @author Russell Coleman
 * @version 1.0.0
 */
public class ArrayServer implements Runnable {
	private static final int PORT = 25671;
	private ArrayInterface interf;
	private Runnable connectionCallback;
	private static final int HANDSHAKE = 5309352; //magic number, chosen at random

	/**
	 * Constructs the server with an interface
	 * with which to communicate
	 *
	 * @param interf The interface
	 */
	public ArrayServer(ArrayInterface interf, Runnable connectionCallback) {
		this.interf = interf;
		this.connectionCallback = connectionCallback;
	}

	/**
	 * Does its thing. Builds the server
	 * and communicates with the client
	 * until it's finished.
	 */
	@Override
	public void run() {
		log("Opening port " + PORT);
		try (
			ServerSocket serverSocket = new ServerSocket(PORT);
			Socket socket = serverSocket.accept();
			DataInputStream inStream = new DataInputStream(socket.getInputStream());
			DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
		) {
			log("Connected to client.");
			int firstPacket = inStream.readInt();
			if(firstPacket != HANDSHAKE) {
				log("Client failed handshake.");
				return;
			}
			log("Client successfully handshook!");
			if(connectionCallback != null) connectionCallback.run();

			outerLoop:
			while(true) {
				log("Waiting for array...");
				//input array from client
				int arraySize = inStream.readInt();
				if(arraySize <= 0) {
					log("Invalid array size: " + arraySize);
					return;
				}
				int[] arr = new int[arraySize];
				for(int i = 0; i < arraySize; i++) {
					arr[i] = inStream.readInt();
					if(arr[i] < 0) {
						log("All array values must be >= 0.");
						return;
					}
				}

				//initialize array with gui
				log("Received array.");
				interf.init(arr);
				log("Initialized array with GUI.");

				while(true) {
					//read in commands from client
					int command = inStream.readInt();
					switch(command) {
						case 0:
							//exit and try another array
							continue outerLoop;
						case 1:
							//set an array value
							int index = inStream.readInt();
							int newVal = inStream.readInt();
							interf.set(index, newVal);
							break;
						default:
							log("Invalid command: " + command);
							return;
					}
				}
			}
		} catch(IOException ioe) {
			log("IOException!");
			ioe.printStackTrace();
		}
	}

	/**
	 * Prints a message from the server
	 * to the console.
	 *
	 * @param str The string to print
	 */
	private void log(String str) {
		System.out.println("[Server] " + str);
	}
}
