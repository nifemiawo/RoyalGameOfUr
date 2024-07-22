/* 
This is entire class is copied and repurposed from a video on Youtube posted by choobtorials
on https://www.youtube.com/watch?v=HQoWN28H80w
last accessed on 05/04/2024
*/

import java.net.*;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class ServerPlayer {

    // Fields
    static GameGUI gui;
    private ClientSideConnection csc;
    private int playerID;
    private int opponentID;
    private Boolean buttonsEnabled;
    private Boolean playing = true;
    private PlayerMap currentPlayer;
    private PlayerMap opposition;
    private TreeMap<String, ArrayList<Integer>> pieceLocations = new TreeMap<>();
    private String gameState = "wait";
    private int roll;
    private int selectedMove;
    private int intRecievedFromServer;
    private int opponentSelectedMove;
    private static String winner;
    private static String loser;
    private boolean isLoser;
    private boolean gameOver = false;
    public static String hostAddress;

    /**
     * Constructor that will initialise the hostAddress, create a new game GUI and
     * disable all the buttons
     * 
     * @param hostAddress string that represents the host ip address
     */
    public ServerPlayer(String hostAddress) {
        gui = new GameGUI();
        this.hostAddress = hostAddress;
        disableAllButtons();
    }

    // getters
    public static String getWinner() {
        return winner;
    }

    public static String getLoser() {
        return loser;
    }

    /**
     * method will close and clean up all the client side connections. Called when
     * the game ends or certain exceptions are thrown
     */
    public void cleanUpClient() {
        playing = false;
        csc.cleanUp();
    }

    /**
     * Method will initalise the pieceLocations map and the playerMap objects and
     * add action listeners to all the buttons on the Game Gui
     */
    public void setUp() {

        // Initalises the starting locations of all the pieces to 0
        ArrayList<Integer> player1StartingPositions = new ArrayList<>();
        ArrayList<Integer> player2StartingPositions = new ArrayList<>();
        for (int i = 0; i < Main.numberOfPieces; i++) {
            player1StartingPositions.add(0);
            player2StartingPositions.add(0);
        }

        pieceLocations.put("white", player1StartingPositions);
        pieceLocations.put("black", player2StartingPositions);

        // conditional will set the current player to white if they are player 1 and
        // black if they are player 2
        if (playerID == 1) {
            GameGUI.playerInfo.setText("you're player 1");
            System.out.println("you're player 1");
            opponentID = 2;
            currentPlayer = new PlayerMap("white", "black", pieceLocations);
            opposition = new PlayerMap("black", "white", pieceLocations);
        } else if (playerID == 2) {
            GameGUI.playerInfo.setText("you're player 2");
            System.out.println("you're player 2");
            buttonsEnabled = false;
            toggleButtons();
            opponentID = 1;
            currentPlayer = new PlayerMap("black", "white", pieceLocations);
            opposition = new PlayerMap("white", "black", pieceLocations);
        }

        // displays all the pieces on the board
        setPieces();

        // initialises actionlisterners for the tile buttons
        for (int i = 0; i < GameGUI.boardButtons.length; i++) {
            GameGUI.boardButtons[i].addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (gameState.equals("moving")) {
                        selectedMove = Integer.parseInt(e.getActionCommand());
                        play();
                    }
                }
            });
        }

        // initialises actionlisterners for the dice button
        GameGUI.diceButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GameGUI.playerInfo.setText(" ");
                if (gameState.equals("rolling")) {

                    roll = Board.rollAll();

                    if (roll == 0) {
                        disableAllButtons();
                        gameState = "wait";
                        csc.sendString("no move");
                    } else if (!currentPlayer.checkIfValidChoiceAvailable(roll)) {
                        disableAllButtons();
                        gameState = "wait";
                        csc.sendString("no move");
                    } else {
                        csc.sendString("roll");
                        csc.sendInt(roll);
                        gameState = "moving";
                    }
                    GameGUI.rollInfo.setText("You rolled " + Integer.toString(roll));
                    GameGUI.gameInfo.setText(getInventoryAndFinishedPieces(currentPlayer.getName()) + "\n"
                            + getInventoryAndFinishedPieces(opposition.getName()));
                }
            }
        });

        // initialises actionlisterners for the quit button
        GameGUI.quitButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cleanUpClient();
            }

        });

    }

    /**
     * method will perform the game logic by moving the piece the player selected by
     * the corresponding dice roll. It will then send a string to the server saying
     * wait if the player lands on a rosette or move if the player lands on a normal
     * tile and then sends an int to the server of the selected tile so the other
     * client can update its GUI. It will then set the game state accordingly and
     * update the tiles. It will then call the end game method to see if the player
     * has won. If the player selects an invalid move play will just set the player
     * info field in the GUI to "no pieces at selected tile"
     */
    public void play() {
        // outer conditional checks if the move is valid
        if (pieceLocations.get(currentPlayer.getName()).contains(selectedMove)
                && !pieceLocations.get(currentPlayer.getName()).contains(selectedMove + roll)
                && selectedMove + roll <= 15) {
            // inner condition checks if player lands on a rosette
            if (currentPlayer.move(roll, selectedMove, pieceLocations)) {
                pieceLocations = currentPlayer.getPieceLocation();

                csc.sendString("wait");
                csc.sendInt(selectedMove);
                gameState = "rolling";
                // flow if player doesnt land on rosette
            } else {
                pieceLocations = currentPlayer.getPieceLocation();

                csc.sendString("move");
                csc.sendInt(selectedMove);

                disableAllButtons();
                gameState = "wait";

            }
            setPieces();
            endGame();
            // conditional if the player is moving to the end tile
        } else if (pieceLocations.get(currentPlayer.getName()).contains(selectedMove) && selectedMove + roll == 15) {
            if (currentPlayer.move(roll, selectedMove, pieceLocations)) {
                pieceLocations = currentPlayer.getPieceLocation();

                csc.sendString("wait");
                csc.sendInt(selectedMove);
                gameState = "rolling";

            } else {
                pieceLocations = currentPlayer.getPieceLocation();

                csc.sendString("move");
                csc.sendInt(selectedMove);

                disableAllButtons();
                gameState = "wait";

            }
            setPieces();
            endGame();
            // conditional for when player selects an invalid tile
        } else {
            GameGUI.playerInfo.setText("no pieces at selected tile");
        }
    }

    /**
     * @param playerName the player you want the info on
     * @return a string with the info about how many pieces a given player has at
     *         start and finish
     */
    public String getInventoryAndFinishedPieces(String playerName) {
        int playerInventoryPieces = 0;
        int playerFinishedPieces = 0;
        for (int i = 0; i < pieceLocations.get(playerName).size(); i++) {
            if (pieceLocations.get(playerName).get(i) == 0) {
                playerInventoryPieces++;
            } else if (pieceLocations.get(playerName).get(i) == 15) {
                playerFinishedPieces++;
            }
        }
        String string = playerName + " has " + playerInventoryPieces + " in inventory and " + playerFinishedPieces
                + " bared off";
        return string;
    }

    /**
     * Method which will check if the game is over by checking if all of the current
     * players peices are on the last tile. If true it will send a string to the
     * server which will then be used to end the game
     */
    public void endGame() {
        gameOver = pieceLocations.get(currentPlayer.getName()).stream().allMatch(x -> x == 15);
        if (gameOver) {
            csc.sendString("end");
        }
    }

    /**
     * Updates the GUI with the correct piece positions on the board
     */
    public void setPieces() {
        for (int i = 0; i < pieceLocations.get("white").size(); i++) {
            int index = pieceLocations.get("white").get(i);

            GameGUI.boardButtons[index].add(GameGUI.whitePiece[i]);

        }
        for (int i = 0; i < pieceLocations.get("black").size(); i++) {
            int index = pieceLocations.get("black").get(i);

            if (index >= 0 && index < 5) {
                index = index + 16;
            } else if (index <= 15 && index >= 13) {
                index = index + 8;
            }
            GameGUI.boardButtons[index].add(GameGUI.blackPiece[i]);
        }
        GameGUI.makeVisable();
    }

    /**
     * method that disables all the buttons on the game GUI
     */
    public static void disableAllButtons() {
        for (int i = 0; i < GameGUI.boardButtons.length; i++) {
            GameGUI.boardButtons[i].setEnabled(false);
        }
        GameGUI.diceButton.setEnabled(false);
    }

    /**
     * method that will enable or disable the current players buttons depending on
     * if the buttonsEnable field is true or false
     */
    public void toggleButtons() {
        System.out.println(playerID + " " + buttonsEnabled);
        if (playerID == 1) {
            for (int i = 0; i < 15; i++) {
                GameGUI.boardButtons[i].setEnabled(buttonsEnabled);
            }
            GameGUI.diceButton.setEnabled(buttonsEnabled);
        } else if (playerID == 2) {
            for (int i = 5; i < 13; i++) {
                GameGUI.boardButtons[i].setEnabled(buttonsEnabled);
            }
            for (int i = 16; i < 24; i++) {
                GameGUI.boardButtons[i].setEnabled(buttonsEnabled);
            }
            GameGUI.diceButton.setEnabled(buttonsEnabled);
        }
    }

    /**
     * method that connects the client to the server by creating a new
     * ClientSideConnection object and initalising it to the csc field
     */
    public void connectToServer() {
        csc = new ClientSideConnection();
    }

    /**
     * method will create a new thread where a while loop will run the
     * recieveGameInfo() method which will constantly recieve information from the
     * server about the other players moves and other information like if the game
     * is over and so on. It will also check if game state is rolling and will
     * toggle the buttons accordingly
     */
    public void receiveState() {
        try {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (playing) {
                        csc.receiveGameInfo();
                        if (gameState.equals("rolling")) {
                            buttonsEnabled = true; // this is needed -> everytime the state = rolling -> buttons
                                                   // enabled.
                            toggleButtons();
                        }
                    }
                }

            });
            t.start();
        } catch (Exception e) {
            System.out.println("exception thrown by receiveState() " + e.getMessage());
        }
    }

    /**
     * method will create a pop up window asking the player if they would like to
     * return to the menu or quit after the game is finished or if an exception is
     * thrown
     */
    public void displayPopUp() {
        String message = "";
        if (gameOver) {
            message = winner + " won the game";
        } else {
            message = "There was an IO exception";
        }
        String[] options = { "Return to menu", "quit" };
        var selection = JOptionPane.showOptionDialog(null, message, "Return to menu or quit",
                0, 3, null, options, options[0]);

        if (selection == 0) {
            JOptionPane.showMessageDialog(null, "you chose to play again");
            cleanUpClient();
            GameGUI.closeGameGui();
            new Main();

        } else if (selection == 1) {
            System.exit(0);
        }
    }

    // Inner class ClientSideConnection deals with the server and networking issues.
    private class ClientSideConnection {
        // fields
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        /**
         * Constructor which will initalise the socket with the host address, it will
         * also initialise the dataIn and dataOut objects so data can be sent too and
         * from the server. Finally it will initialise playerID in the ServerPlayer
         * class by reading an int sent by the server
         */
        public ClientSideConnection() {
            System.out.println("---client---");
            try {
                socket = new Socket(hostAddress, 12345);
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                playerID = dataIn.readInt();
                System.out.println("connected as player " + playerID);
            } catch (IOException e) {
                playing = false;
                GameGUI.closeGameGui();
                cleanUp();
                new Main();
                System.out.println("IO exception in client side connection construcotr");
                System.out.println(e.getMessage());
            }
        }

        /**
         * A method that will send strings from the client to the server
         * 
         * @param s the string being sent
         */
        public void sendString(String s) {
            try {
                dataOut.writeUTF(s);
                dataOut.flush();
            } catch (IOException e) {
                System.out.println("IO exception at send string " + e.getMessage());
            }
        }

        /**
         * A method that will send integers from the client to the server
         * 
         * @param i the integer being sent
         */
        public void sendInt(int i) {
            try {
                dataOut.writeInt(i);
                dataOut.flush();
            } catch (IOException e) {
                System.out.println("IO exception at send string " + e.getMessage());
            }
        }

        /**
         * Method will recieve data from the server and will perform the corresponding
         * action depending on what it recieves from the server
         */
        public void receiveGameInfo() {
            try {
                // will read an integer from the server and assigns it to the
                // intRecievedFromServer variable
                intRecievedFromServer = dataIn.readInt();

                if (intRecievedFromServer == 200) {
                    // if the integer recieved is 200 it means that the server has signled that
                    // both players have connected and so player 1 can start so the buttons are
                    // toggles
                    buttonsEnabled = true;
                    toggleButtons();
                } else if (intRecievedFromServer == 100) {
                    // if the integer recieved is 100 it means the server has signled that the game
                    // is over and that the current player has lost and so the client is closed and
                    // a pop up message showing who won will be displayed
                    gameOver = true;
                    winner = opposition.getName();
                    loser = currentPlayer.getName();
                    playing = false;
                    cleanUp();
                    displayPopUp();
                } else if (intRecievedFromServer == 101) {
                    // if the integer recieved is 101 it means the server has signled that the game
                    // is over and that the current player has won and so the client is closed and
                    // a pop up message showing who won will be displayed
                    gameOver = true;
                    winner = currentPlayer.getName();
                    loser = opposition.getName();
                    playing = false;
                    cleanUp();
                    displayPopUp();
                } else if (intRecievedFromServer != 0) {
                    // if the integer recieved is any number apart from 200, 101, 100 or 0 it means
                    // the server has sent what the opposition has rolled
                    // and so the program will then display on the gui what the opponent rolled and
                    // will then read another int from the server which will be what piece the
                    // opposition player selects to move the program will then update the
                    // pieceLocations map and update the GUI with the opponents move
                    GameGUI.playerInfo.setText("opponent rolled " + intRecievedFromServer);
                    opponentSelectedMove = dataIn.readInt();
                    GameGUI.playerInfo.setText("opponent moved from " + opponentSelectedMove + " to "
                            + (opponentSelectedMove + intRecievedFromServer));

                    opposition.move(intRecievedFromServer, opponentSelectedMove, pieceLocations);
                    pieceLocations = opposition.getPieceLocation();
                    setPieces();
                } else {
                    // else statement if the int recieved is 0 or something else which means the
                    // opponent rolled a 0 or has no valid moves with their roll
                    GameGUI.playerInfo.setText("opponent rolled 0 or has no valid moves");
                }
                // will update the gamestate with a string sent from the server
                gameState = dataIn.readUTF();

            } catch (IOException e) {
                if (gameOver) {
                    playing = false;
                    cleanUp();
                } else {
                    playing = false;
                    cleanUp();
                    GameGUI.closeGameGui();
                    new Main();
                    System.out.println("IOException thrown by receiveGameInfo() " + e.getMessage());
                }
            } catch (NullPointerException e) {
                playing = false;
                cleanUp();
                System.out.println("invalid ip");
            }

        }

        /** 
         * Method which clean up the client by closing all data input and output streams as well as the socket
         */
        public void cleanUp() {
            try {
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
                } catch (SocketException e) {
                    System.out.println("Socket Exception when trying to close sockets");
                }

            } catch (IOException e) {
                System.out.println("IO Exception at cleanUp() ");
            }
        }
    }
}
