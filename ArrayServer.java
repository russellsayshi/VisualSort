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
public class ArrayServer implements Runnable, UpdateableConnection {
	private static final int PORT = 25671;
	private ArrayInterface interf;
	private UserPreferences prefs;
	private ArrayList<ConnectionUpdateListener> updateListeners;
	private ConnectionPreferencesListener preferencesListener;
	private volatile Socket clientSocket;
	private static final int HANDSHAKE = 5309352; //magic number, chosen at random
	private static final int MARK_SPEED_FACTOR = 10;

	/**
	 * Constructs the server with an interface
	 * with which to communicate
	 *
	 * @param interf The interface
	 */
	public ArrayServer(ArrayInterface interf,
			UserPreferences prefs) {
		this.interf = interf;
		this.prefs = prefs;
		updateListeners = new ArrayList<>();
		this.preferencesListener = new PreferenceUpdateHandler();
	}

	/**
	 * Inner class that comes with every ArrayServer instance
	 * that handles changes made in the user preferences
	 * for the server.
	 *
	 * @author Russell Coleman
	 * @version 1.0.0
	 */
	protected class PreferenceUpdateHandler implements ConnectionPreferencesListener {
		public void endConnection() {
			try {
				if(!clientSocket.isClosed()) {
					clientSocket.close();
				}
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
			for(ConnectionUpdateListener updateListener : updateListeners) {
				updateListener.disconnected();
			}
		}
	}

	/**
	 * Adds a new updateListener to the
	 * collection of ConnectionUpdateListeners.
	 *
	 * @param listener The new listener
	 */
	@Override
	public void addConnectionUpdateListener(ConnectionUpdateListener listener) {
		updateListeners.add(listener);
	}
	
	/**
	 * Removes a ConnectionUpdateListener
	 * from the list of listeners.
	 * 
	 * @param listener To remove
	 * @return Successfully removed or not
	 */
	@Override
	public boolean removeConnectionUpdateListener(ConnectionUpdateListener listener) {
		return updateListeners.remove(listener);
	}

	/**
	 * Returns the PreferencesUpdateListener
	 * that allows another class to make
	 * changes to the connection preferences
	 * while the server is running.
	 *
	 * @return The perferences handler
	 */
	@Override
	public ConnectionPreferencesListener getConnectionPreferencesListener() {
		return preferencesListener;
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
		) {
			while(true) {
				handleClient(serverSocket);
			}
		} catch(IOException ioe) {
			log("IOE top level!");
			ioe.printStackTrace();
		}
	}

	/**
	 * Handles a single client
	 * and then exits.
	 *
	 * @param serverSocket To read from
	 */
	private void handleClient(ServerSocket serverSocket) {
		try (
			Socket socket = serverSocket.accept();
			DataInputStream inStream = new DataInputStream(socket.getInputStream());
			DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
		) {
			this.clientSocket = socket;
			log("Connected to client.");
			int firstPacket = inStream.readInt();
			if(firstPacket != HANDSHAKE) {
				log("Client failed handshake. Sent " + firstPacket + " instead of " + HANDSHAKE + ".");
				return;
			}
			log("Client successfully handshook!");
			for(ConnectionUpdateListener updateListener : updateListeners) {
				updateListener.connected(socket.getRemoteSocketAddress());
			}

			outerLoop:
			while(true) {
				//tell the client about our speed settings
				int delayAmount = (int)prefs.getDelayAmount();
				outStream.writeInt(delayAmount); //delay
				outStream.writeInt(delayAmount/MARK_SPEED_FACTOR); //point delay

				int requestType = inStream.readInt();
				int[] arr;
				switch(requestType) {
					case 0:
						//the client wants us to give it an array
						log("Generating array.");
						Random random = new Random();
						arr = new int[50];
						outStream.writeInt(arr.length);
						for(int i = 0; i < arr.length; i++) {
							arr[i] = random.nextInt(101);
							outStream.writeInt(arr[i]);
						}
						break;
					case 1:
						//input array from client
						log("Reading array.");
						int arraySize = inStream.readInt();
						if(arraySize <= 0) {
							log("Invalid array size: " + arraySize);
							break outerLoop;
						}
						arr = new int[arraySize];
						for(int i = 0; i < arraySize; i++) {
							arr[i] = inStream.readInt();
							if(arr[i] < 0) {
								log("All array values must be >= 0.");
								break outerLoop;
							}
						}
						break;
					default:
						log("Garbage data from client.");
						break outerLoop;
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
							//exit 
							interf.done();
							break outerLoop;
						case 1:
							//set an array value
							int index = inStream.readInt();
							int newVal = inStream.readInt();
							interf.set(index, newVal);
							//log("Set " + index + " to " + newVal);
							//Thread.sleep(prefs.getDelayAmount());
							//outStream.writeInt(1); //continue
							break;
						case 2:
							//mark region
							int start = inStream.readInt();
							int end = inStream.readInt();
							interf.markRegion(start, end);
							break;
						case 3:
							//clear region
							interf.clearRegion();
							break;
						case 4:
							//point
							int addr = inStream.readInt();
							interf.point(addr);
							break;
						default:
							log("Invalid command: " + command);
							break outerLoop;
					}
				}
			}
			log("Finished with client.");
		} catch(IOException ioe) {
			log("IOException!");
			ioe.printStackTrace();
		}/* catch(InterruptedException ie) {
			log("InterruptedException!");
			ie.printStackTrace();
		}*/
		for(ConnectionUpdateListener updateListener : updateListeners) {
			updateListener.disconnected();
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
