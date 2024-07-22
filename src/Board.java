import java.util.TreeMap;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Board {
    public int numberOfPieces = Main.numberOfPieces;

    boolean playing = true;

    public TreeMap<String, ArrayList<Integer>> pieceLocations = new TreeMap<>();

    PlayerMap player1;
    PlayerMap player2;

    PlayerMap current;
    PlayerMap opponent;
    GameGUI gui = new GameGUI();
    int selectedMove;
    int currentRoll;

    String gameState = "rolling";

    public Board(PlayerMap player1, PlayerMap player2) {  

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<Integer> player1StartingPositions = new ArrayList<>();
        ArrayList<Integer> player2StartingPositions = new ArrayList<>();
        for (int i = 0; i < numberOfPieces; i++) {
            player1StartingPositions.add(0);
            player2StartingPositions.add(0);
        }

        this.player1 = player1;
        this.player2 = player2;

        pieceLocations.put(this.player1.getName(), player1StartingPositions);
        pieceLocations.put(this.player2.getName(), player2StartingPositions);

        player1.setPieceLocations(pieceLocations);
        player2.setPieceLocations(pieceLocations);

        current = this.player1;
        opponent = this.player2;

        setInfoField();
        setPieces();
        GameGUI.rollInfo.setText("Roll Dice");
        GameGUI.makeVisable();

        enablePlayer1Buttons(true);
        enablePlayer2Buttons(false);

        printInfo();

        addActionListeners();
    }

    public void addActionListeners() {
        GameGUI.diceButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                GameGUI.playerInfo.setText(" ");
                if (gameState.equals("rolling")) {
                    currentRoll = rollAll();
                    if (currentRoll == 0) {
                        System.out.println("you rolled 0 skipping to next player");
                        GameGUI.playerInfo.setText("you rolled 0 skipping to next player");
                        switchPlayer();
                        printInfo();
                        GameGUI.rollInfo.setText("Roll Dice");
                    } else if (!current.checkIfValidChoiceAvailable(currentRoll)) {
                        System.out.println("You rolled a: " + currentRoll);
                        System.out.println("no valid choices available skipping to next player");
                        GameGUI.playerInfo.setText("no valid choices available skipping to next player");
                        switchPlayer();
                        printInfo();
                        GameGUI.rollInfo.setText("Roll Dice");
                    } else {
                        System.out.println("You rolled a: " + currentRoll);
                        gameState = "selecting";
                        GameGUI.rollInfo.setText("You rolled a: " + currentRoll);
                    }
                } else {
                    System.out.println("select player to move");
                    GameGUI.playerInfo.setText("select player to move");
                }
            }
        });

        for (int i = 0; i < GameGUI.boardButtons.length; i++) {
            GameGUI.boardButtons[i].addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (gameState.equals("selecting")) {
                        selectedMove = Integer.parseInt(e.getActionCommand());
                        play();

                        printInfo();
                    } else {
                        //System.out.println("roll dice first");
                        GameGUI.playerInfo.setText("roll dice first");
                    }
                }
            });
        }
    }

    public void switchPlayer() {
        if (current.equals(player1)) {
            current = player2;
            opponent = player1;
            enablePlayer1Buttons(false);
            enablePlayer2Buttons(true);
        } else {
            current = player1;
            opponent = player2;
            enablePlayer1Buttons(true);
            enablePlayer2Buttons(false);
        }
    }

    public void enablePlayer1Buttons(Boolean enabled) {
        for (int i = 0; i < 5; i++) {
            GameGUI.boardButtons[i].setEnabled(enabled);
        }
        for (int i = 13; i < 16; i++) {
            GameGUI.boardButtons[i].setEnabled(enabled);
        }
    }

    public void enablePlayer2Buttons(Boolean enabled) {
        for (int i = 16; i < 24; i++) {
            GameGUI.boardButtons[i].setEnabled(enabled);
        }
    }

    public void printInfo() {
        System.out.println("Playing: " + current.getName());
        System.out.println("you currently have pieces at: " + pieceLocations.get(current.getName()));
        System.out.println("your opponent has pieces at: " + pieceLocations.get(opponent.getName()));
    }

    public void setInfoField() {
        GameGUI.gameInfo.setText(getInventoryAndFinishedPieces(player1.getName()) + "\n"
                + getInventoryAndFinishedPieces(player2.getName()));
    }

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

    public void play() {
        if (pieceLocations.get(current.getName()).contains(selectedMove)
                && !pieceLocations.get(current.getName()).contains(selectedMove + currentRoll)
                && selectedMove + currentRoll <= 15) {
            if (current.move(currentRoll, selectedMove, pieceLocations)) {
                pieceLocations = current.getPieceLocation();
                endGame();
            } else {
                pieceLocations = current.getPieceLocation();
                endGame();
                switchPlayer();
            }
            setPieces();

            gameState = "rolling";
            System.out.println();
            GameGUI.rollInfo.setText("Roll Dice");
        } else if (pieceLocations.get(current.getName()).contains(selectedMove) && selectedMove + currentRoll == 15) {
            if (current.move(currentRoll, selectedMove, pieceLocations)) {
                pieceLocations = current.getPieceLocation();
                endGame();
            } else {
                pieceLocations = current.getPieceLocation();
                endGame();
                switchPlayer();
            }
            setPieces();
            GameGUI.rollInfo.setText("Roll Dice");
        } else {
            System.out.println("no pieces at selected tile");
            GameGUI.playerInfo.setText("no pieces at selected tile");
            System.out.println();
        }
        setInfoField();

    }

    public void endGame() {
        Boolean gameOver = pieceLocations.get(current.getName()).stream().allMatch(x -> x == 15);
        if (gameOver) {
            JOptionPane.showMessageDialog(null, current.getName() + " has won", "WINNER",
                    JOptionPane.INFORMATION_MESSAGE);
            System.out.println("game over " + current.getName() + " has won");
            int reply = JOptionPane.showConfirmDialog(null, "Would you like to quit?", "Close?",
                    JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                System.exit(0);
            } else{
                GameGUI.boardFrame.dispose();
                new Main();
            }

        }
    }

    public void setPieces() {
        for (int i = 0; i < pieceLocations.get(player1.getName()).size(); i++) {
            int index = pieceLocations.get(player1.getName()).get(i);

            GameGUI.boardButtons[index].add(GameGUI.whitePiece[i]);

            // GameGUI.boardButtons[i].revalidate();
            // GameGUI.boardButtons[i].repaint();
        }

        for (int i = 0; i < pieceLocations.get(player2.getName()).size(); i++) {
            int index = pieceLocations.get(player2.getName()).get(i);

            if (index >= 0 && index < 5) {
                index = index + 16;
            } else if (index <= 15 && index >= 13) {
                index = index + 8;
            }

            GameGUI.boardButtons[index].add(GameGUI.blackPiece[i]);
            // GameGUI.boardButtons[i].revalidate();
            // GameGUI.boardButtons[i].repaint();
        }

        GameGUI.makeVisable();
    }

    public static int rollAll() {
        Dice dice = new Dice();
        int counter = 0;
        for (int n = 0; n < 4; n++) {
            counter += dice.roll();
        }
        return counter;
    }
}