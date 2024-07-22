import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.swing.JCheckBox;
import javax.swing.UIManager;

public class Main {
    public static int numberOfPieces = 5;
    public static String player1Name = "White";
    public static String player2Name = "Black";
    public static TreeMap<String, ArrayList<Integer>> pieceLocations = new TreeMap<>();
    private static Server s;
    private static ServerPlayer player;

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Checks for flag if the program is running AI vs AI simulation
        if (args.length == 1 && args[0].equals("-t")){
            new AIvsAIBoard(new AI(player1Name, player2Name, pieceLocations), new EasyAI(player2Name, player1Name, pieceLocations));
        }
        //Else normal player experience
        else{
            new Main();
        }
    }

    public Main() {
        s = null;
        player = null;
        Menu menu = new Menu();
        menu.playFriend.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                menu.dispose();
                new Board(new PlayerMap(player1Name, player2Name, pieceLocations),
                        new PlayerMap(player2Name, player1Name, pieceLocations));
            }

        });

        menu.easyButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Easy mode");
                new AIBoard(new PlayerMap(player1Name, player2Name, pieceLocations),
                new EasyAI(player2Name, player1Name, pieceLocations));
                menu.dispose();
            }

        });

        menu.hardButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Hard mode");
                new AIBoard(new PlayerMap(player1Name, player2Name, pieceLocations),
                new AI(player2Name, player1Name, pieceLocations));
                menu.dispose();
                //here
            }

        });

        menu.confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menu.isHost.isSelected()) {
                    if (s != null) {
                        s.terminateServer();
                    }
                    if (player != null) {
                        player.cleanUpClient();
                    }

                    menu.dispose();

                    s = new Server();

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                s.acceptConnections();
                            } catch (Exception e) {
                                s.terminateServer();
                                s = new Server();
                                s.acceptConnections();
                            }
                        };
                    });
                    thread.start();

                    player = new ServerPlayer(menu.inputIP.getText().trim());
                    try {
                        player.connectToServer();
                        player.setUp();
                        player.receiveState();

                    } catch (Exception ex) {
                        System.out.println("invalid address" + ex.getMessage());
                    }

                    System.out.println("user is host");
                } else {
                    menu.dispose();

                    ServerPlayer player = new ServerPlayer(menu.inputIP.getText().trim());
                    player.connectToServer();
                    player.setUp();
                    player.receiveState();
                    System.out.println("User is not host");
                }
            }
        });

    }

}
