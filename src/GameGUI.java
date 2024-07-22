import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameGUI {
    public static JFrame boardFrame;
    private JPanel boardPanel;
    private static JPanel dicePanel;
    private static JPanel gameInfoPanel;
    public static JTextField playerInfo;
    public static JTextField gameInfo;
    public static JTextField rollInfo;
    public static JButton[] boardButtons = new JButton[24];
    public static JButton diceButton, quitButton;
    public static JLabel[] blackPiece = new JLabel[Main.numberOfPieces], whitePiece = new JLabel[Main.numberOfPieces];
    private GridLayout experimentLayout = new GridLayout(3, 8);//Custom layout for the game.
    private static ImageIcon rosetteTileIcon, tileIcon1, tileIcon2, tileIcon4, diceIcon, diceUpIcon, blackPieceIcon,
            whitePieceIcon;

            
    /**
     * Constructor for the GameGUI.
     * 1. Initialises the icons used in the GUI.
     * 2. Initialises the various GUI components such as buttons, labels, abd more.
     * 3. Sets up the board buttons for the game.
     * 4. Toggles the visibility and state of the dice and board buttons.
     */
    public GameGUI() {
        initialiseIcons();
        initialiseGUIComponents();
        setupBoardButtons();
        toggleDiceAndBoardButtons();
    }

    /**
     * Disposes the GameGUI.
     */
    public static void closeGameGui(){
        boardFrame.dispose();
    }

    /**
     * This method takes the sprites and adds them as resized icons.
     */
    private void initialiseIcons() {
        rosetteTileIcon = new ImageIcon(
                new ImageIcon("../sprites/Rosette.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        tileIcon1 = new ImageIcon(
                new ImageIcon("../sprites/Tile1.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        tileIcon2 = new ImageIcon(
                new ImageIcon("../sprites/Tile2.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        tileIcon4 = new ImageIcon(
                new ImageIcon("../sprites/Tile4.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        diceIcon = new ImageIcon(
                new ImageIcon("../sprites/DiceDown.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        diceUpIcon = new ImageIcon(
                new ImageIcon("../sprites/DiceUp.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        blackPieceIcon = new ImageIcon(
                new ImageIcon("../sprites/BlackPiece.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
        whitePieceIcon = new ImageIcon(
                new ImageIcon("../sprites/WhitePiece.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));

        for (int i = 0; i < Main.numberOfPieces; i++) {
            blackPiece[i] = new JLabel(blackPieceIcon);
            whitePiece[i] = new JLabel(whitePieceIcon);
        }
    }

    /**
     * This method initialises and specifies the components required for the main design of the board. 
     */
    private void initialiseGUIComponents() {

        //Initialising the main board components.
        boardFrame = new JFrame("Game Board");
        boardPanel = new JPanel();
        dicePanel = new JPanel();
        gameInfoPanel = new JPanel();
        rollInfo = new JTextField();
        rollInfo.setEditable(false);
        rollInfo.setHorizontalAlignment(rollInfo.CENTER);
        gameInfo = new JTextField();
        gameInfo.setEditable(false);
        playerInfo = new JTextField();
        playerInfo.setEditable(false);
        quitButton = createStyledButton("Return To Menu");
        
        //Setting the grid layout and make sure the frame closes.
        boardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        boardFrame.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        boardFrame.add(boardPanel, c);
        c.gridx = 1;
        c.gridy = 0;
        boardFrame.add(dicePanel, c);
        c.gridx = 0;
        c.gridy = 1;
        boardFrame.add(gameInfoPanel, c);

        //Setting up dicePanel components and dimensions.
        dicePanel.setPreferredSize(new Dimension(250, 500));
        diceButton = new JButton(diceIcon);
        dicePanel.setBackground(new Color(196, 164, 132));
        dicePanel.setLayout(new GridLayout(3, 1));
        dicePanel.add(diceButton);
        dicePanel.add(rollInfo);
        dicePanel.add(quitButton);

        //Closes the GUI and brings the user back to the main menu.
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                closeGameGui();
                new Main();
            }
        });

        //Setting up the components and dimensions for the gameInfoPanel.
        gameInfoPanel.setPreferredSize(new Dimension(1100, 100));
        gameInfoPanel.setLayout(new BoxLayout(gameInfoPanel, BoxLayout.Y_AXIS));
        gameInfoPanel.add(gameInfo);
        gameInfoPanel.setBackground(new Color(196, 164, 132));
        gameInfoPanel.add(playerInfo);

        //Final boardPanel setup is done after all components in boardPanel are defined.
        boardPanel.setLayout(experimentLayout);
        boardPanel.setVisible(true);
        boardFrame.pack();
        boardFrame.setSize(new Dimension(1375, 675));
        boardFrame.setMinimumSize(new Dimension(1375, 675));
        boardFrame.setVisible(true);
    }

    /**
     * Custom method to create a button in keeping the game's design theme.
     * @param text which is the number of the button.
     * @return a specially formatted JButton.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(240, 248, 255)); 
        button.setForeground(new Color(150, 75, 0)); 
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBorder(BorderFactory.createLineBorder(new Color(70, 0, 0), 2));
        button.setPreferredSize(new Dimension(200, 50));
        return button;
      }

    /**
     * Method to setup board buttons to represent the tiles and represented in a way which the backend can communicate with.
     */
    private void setupBoardButtons() {
        for (int i = 0; i < 24; i++) {
            boardButtons[i] = new JButton();
            boardButtons[i].setBackground(new Color(196, 164, 132));
            boardButtons[i].setLayout(new BorderLayout());
            boardButtons[i].setMargin(new Insets(0, 0, 0, 0));
        }

        //Adding buttons to the board in a way which properly works with the backend.
        for (int i = 4; i >= 0; i--) {
            boardPanel.add(boardButtons[i]);
        }

        for (int i = 15; i >= 13; i--) {
            boardPanel.add(boardButtons[i]);
        }

        for (int i = 5; i <= 12; i++) {
            boardPanel.add(boardButtons[i]);
        }

        for (int i = 20; i >= 16; i--) {
            boardPanel.add(boardButtons[i]);
        }

        for (int i = 23; i >= 21; i--) {
            boardPanel.add(boardButtons[i]);
        }

        //Assigning each tile to an icon.
        //Top row of tiles.
        boardButtons[4].setIcon(rosetteTileIcon);
        boardButtons[4].setActionCommand("4");
        boardButtons[3].setIcon(tileIcon4);
        boardButtons[3].setActionCommand("3");
        boardButtons[2].setIcon(tileIcon2);
        boardButtons[2].setActionCommand("2");
        boardButtons[1].setIcon(tileIcon4);
        boardButtons[1].setActionCommand("1");
        boardButtons[0].setActionCommand("0");
        boardButtons[15].setActionCommand("15");
        boardButtons[14].setIcon(rosetteTileIcon);
        boardButtons[14].setActionCommand("14");
        boardButtons[13].setIcon(tileIcon1);
        boardButtons[13].setActionCommand("13");
        boardButtons[4].setDisabledIcon(rosetteTileIcon);
        boardButtons[3].setDisabledIcon(tileIcon4);
        boardButtons[2].setDisabledIcon(tileIcon2);
        boardButtons[1].setDisabledIcon(tileIcon4);
        boardButtons[14].setDisabledIcon(rosetteTileIcon);
        boardButtons[13].setDisabledIcon(tileIcon1);

        ////Middle row of tiles.
        boardButtons[5].setIcon(tileIcon4);
        boardButtons[5].setActionCommand("5");
        boardButtons[6].setIcon(tileIcon2);
        boardButtons[6].setActionCommand("6");
        boardButtons[7].setIcon(tileIcon1);
        boardButtons[7].setActionCommand("7");
        boardButtons[8].setIcon(rosetteTileIcon);
        boardButtons[8].setActionCommand("8");
        boardButtons[9].setIcon(tileIcon2);
        boardButtons[9].setActionCommand("9");
        boardButtons[10].setIcon(tileIcon1);
        boardButtons[10].setActionCommand("10");
        boardButtons[11].setIcon(tileIcon4);
        boardButtons[11].setActionCommand("11");
        boardButtons[12].setIcon(tileIcon2);
        boardButtons[12].setActionCommand("12");
        boardButtons[5].setDisabledIcon(tileIcon4);
        boardButtons[6].setDisabledIcon(tileIcon2);
        boardButtons[7].setDisabledIcon(tileIcon1);
        boardButtons[8].setDisabledIcon(rosetteTileIcon);
        boardButtons[9].setDisabledIcon(tileIcon2);
        boardButtons[10].setDisabledIcon(tileIcon1);
        boardButtons[11].setDisabledIcon(tileIcon4);
        boardButtons[12].setDisabledIcon(tileIcon2);

        //Bottom row of tiles.
        boardButtons[20].setIcon(rosetteTileIcon);
        boardButtons[20].setActionCommand("4");
        boardButtons[19].setIcon(tileIcon4);
        boardButtons[19].setActionCommand("3");
        boardButtons[18].setIcon(tileIcon2);
        boardButtons[18].setActionCommand("2");
        boardButtons[17].setIcon(tileIcon4);
        boardButtons[17].setActionCommand("1");
        boardButtons[16].setActionCommand("0");
        boardButtons[23].setActionCommand("15");
        boardButtons[22].setIcon(rosetteTileIcon);
        boardButtons[22].setActionCommand("14");
        boardButtons[21].setIcon(tileIcon1);
        boardButtons[21].setActionCommand("13");
        boardButtons[20].setDisabledIcon(rosetteTileIcon);
        boardButtons[19].setDisabledIcon(tileIcon4);
        boardButtons[18].setDisabledIcon(tileIcon2);
        boardButtons[17].setDisabledIcon(tileIcon4);
        boardButtons[22].setDisabledIcon(rosetteTileIcon);
        boardButtons[21].setDisabledIcon(tileIcon1);

        boardPanel.setVisible(true);
    }

    /**
     * Method to enable and disable buttons. Used when the user's turn ends to ensure dice and piece movements are not allowed.
     */
    private void toggleDiceAndBoardButtons() {
        diceButton.addActionListener(new ActionListener() {
            private boolean diceToggled = false;

            @Override
            public void actionPerformed(ActionEvent e) {

                if (!diceToggled) {
                    diceButton.setIcon(diceUpIcon);
                    diceToggled = true;
                } else {
                    diceButton.setIcon(diceIcon);
                    diceToggled = false;
                }
            }
        });
    }

    public static void makeVisable() {
        boardFrame.revalidate();
        boardFrame.repaint();
    }
}
