/* 
This is entire class is copied and repurposed from a video on Youtube posted by choobtorials
on https://www.youtube.com/watch?v=HQoWN28H80w
last accessed on 05/04/2024
*/

import java.io.*;
import java.net.*;

public class Server {
    // fields
    private ServerSocket ss;
    private int numberOfPlayers;
    private ServerSideConnection player1;
    private ServerSideConnection player2;
    private boolean playing = true;

    /**
     * Constuctor which will initialise number of players to 0 and will create a new
     * socket
     */
    public Server() {
        ss = null;
        System.out.println("---game server---");
        numberOfPlayers = 0;
        try {
            ss = new ServerSocket(12345);
        } catch (Exception e) {
            terminateServer();
            System.out.println("exception from server constructor");
            System.out.println(e.getMessage());
        }
    }

    /**
     * method which when called will terminate the server by closing all sockets and
     * connections to clients
     */
    public void terminateServer() {
        playing = false;
        try {
            if (ss != null) {
                ss.close();
            }
            if (player1 != null) {
                player1.cleanUpServerSideConnection();
            }
            if (player2 != null) {
                player2.cleanUpServerSideConnection();
            }
        } catch (Exception e) {
            System.out.println("exception at terminateServer " + e.getMessage());
        }
    }

    // getter
    public int getNumberOfPlayers() {
        return this.numberOfPlayers;
    }

    /**
     * method which will accept connection to the server. When a client connects it
     * will incrament the numberOfPlayers field and will create a new
     * serverSideConnection for that player. The program will stop acception
     * connections when numberOfPlayers == 2. It will then create threads for each
     * serverSideConnection (player)
     */
    public void acceptConnections() {
        try {
            System.out.println("waiting  for connections");
            while (numberOfPlayers < 2) {
                Socket s = ss.accept();
                numberOfPlayers++;

                System.out.println("player " + numberOfPlayers + " has joined");
                ServerSideConnection ssc = new ServerSideConnection(s, numberOfPlayers);

                if (numberOfPlayers == 1) {
                    player1 = ssc;
                } else if (numberOfPlayers == 2) {
                    player2 = ssc;

                    // sends information to player 1 so they can start playing once player 2 has
                    // connected
                    player1.sendInt(200);
                    player1.sendString("rolling");

                }
                Thread thread = new Thread(ssc);
                thread.start();
            }
            System.out.println("all players connected");
        } catch (IOException e) {
            System.out.println("io exception from accept connection" + e.getMessage());
            terminateServer();
        }
    }

    // ServerSideConnection inner class which creates a connection so the server can
    // send and recieve infromation to and from the client
    private class ServerSideConnection implements Runnable {
        // fields
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int playerId;
        private int player1Int;
        private int player2Int;
        private String player1String;
        private String player2String;

        /**
         * constuctor which will initalise the socket and player id field and intialise
         * the dataIn and dataOut from the input and output streams from the socket
         */
        public ServerSideConnection(Socket s, int id) {
            socket = s;
            playerId = id;
            try {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                System.out.println("IO exception from serversideconnection consturctor " + e.getMessage());
            }
        }

        /**
         * method which will close all the input and output streams and the sockets
         */
        public void cleanUpServerSideConnection() {
            try {
                if (dataIn != null) {
                    dataIn.close();
                }
                if (dataOut != null) {
                    dataOut.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
                System.out.println("exception in cleanUpServerSideClient");
            }
        }

        /**
         * method which will recieve and send information from the two player clients
         * depending on the state of the game
         */
        @Override
        public void run() {
            try {
                // when client first connects server will send their player id to them
                dataOut.writeInt(playerId);
                dataOut.flush();

                while (playing) {
                    // conditional to see if incoming information is being send from player 1 or 2
                    if (playerId == 1) {
                        // reads string from player 1
                        player1String = dataIn.readUTF();
                        // conditional to see what action was performed by player 1 and will send the
                        // corresponing info to player 1 and 2 depending on the game state
                        if (player1String.equals("roll")) {
                            player1Int = dataIn.readInt();
                            player2.sendInt(player1Int);
                        } else if (player1String.equals("move")) {
                            player1Int = dataIn.readInt();
                            player2.sendInt(player1Int);
                            player2.sendString("rolling");
                        } else if (player1String.equals("wait")) {
                            player1Int = dataIn.readInt();
                            player2.sendInt(player1Int);
                            player2.sendString("wait");
                        } else if (player1String.equals("no move")) {
                            player2.sendInt(0);
                            player2.sendString("rolling");
                        } else if (player1String.equals("end")) {
                            player2.sendInt(100);
                            player2.sendString("wait");
                            player1.sendInt(101);
                            player1.sendString("wait");
                        }
                    } else if (playerId == 2) {
                        // reads string from player 2
                        player2String = dataIn.readUTF();
                        // conditional to see what action was performed by player 2 and will send the
                        // corresponing info to player 1 and 2 depending on the game state
                        if (player2String.equals("roll")) {
                            player2Int = dataIn.readInt();
                            player1.sendInt(player2Int);
                        } else if (player2String.equals("move")) {
                            player2Int = dataIn.readInt();
                            player1.sendInt(player2Int);
                            player1.sendString("rolling");
                        } else if (player2String.equals("wait")) {
                            player2Int = dataIn.readInt();
                            player1.sendInt(player2Int);
                            player1.sendString("wait");
                        } else if (player2String.equals("no move")) {
                            player1.sendInt(0);
                            player1.sendString("rolling");
                        } else if (player2String.equals("end")) {
                            player1.sendInt(100);
                            player1.sendString("wait");
                            player2.sendInt(101);
                            player2.sendString("wait");
                        }
                    }
                }

            } catch (Exception e) {
                terminateServer();
                System.out.println("IO exception from run in server side connection " + e.getMessage());

                try {
                    cleanUpServerSideConnection();
                } catch (Exception ex) {
                    System.out.println("Exception when trying to close readers and sockets " + e.getMessage());
                }
            }
        }

        /**
         * A method that will send strings from the server to the client
         * 
         * @param string the string being sent
         */
        public void sendString(String string) {
            try {
                dataOut.writeUTF(string);
                dataOut.flush();
            } catch (IOException e) {
                terminateServer();
                System.out.println("IO Exception from sendString()");
            }
        }

        /**
         * A method that will send integers from the server to the client
         * 
         * @param i the integer being sent
         */
        public void sendInt(int i) {
            try {
                dataOut.writeInt(i);
                dataOut.flush();
            } catch (IOException e) {
                terminateServer();
                System.out.println("IO Exception from sendString()");
            }
        }
    }
}
