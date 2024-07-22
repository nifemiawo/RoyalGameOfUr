import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame {
  private JPanel menuPanel;
  public JButton playComputer, easyButton, hardButton;
  public JButton playFriend, confirm;
  public JTextField inputIP;
  public JCheckBox isHost;
  JButton playNetwork;
  private JButton quit;

  public Menu() {
    // Setting up main frame
    setTitle("Royal Game of Ur");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    setBackground(Color.WHITE);

    // Welcome label
    JLabel welcomeLabel = new JLabel("Welcome to the Royal Game of Ur", SwingConstants.CENTER);
    welcomeLabel.setForeground(new Color(150, 75,0));                                   
    welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
    add(welcomeLabel, BorderLayout.NORTH);

    // Menu panel
    menuPanel = new JPanel(new GridLayout(4, 1, 10, 10));
    menuPanel.setBackground(new Color(150,75,0));
    add(menuPanel, BorderLayout.CENTER);
    this.setResizable(false);

    // Creating buttons
    playComputer = createStyledButton("Play AI");
    playFriend = createStyledButton("Play a friend");
    playNetwork = createStyledButton("Play Network");
    quit =createStyledButton("Quit");
    confirm = createStyledButton("Confirm");
    easyButton = createStyledButton("Easy");
    hardButton = createStyledButton("Hard");


    playComputer.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // game logic method calls
        menuPanel.removeAll();
        menuPanel.revalidate();
        menuPanel.repaint();

        JLabel difficultyLabel = createStyledLabel("Please select your difficulty");
        menuPanel.add(difficultyLabel);
        menuPanel.add(easyButton);
        menuPanel.add(hardButton);
      }
    });

    playFriend.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
     
      }
    });

    playNetwork.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // game logic method calls
        menuPanel.removeAll();
        menuPanel.revalidate();
        menuPanel.repaint();
        inputIP = new JTextField();
        JLabel IPlabel = createStyledLabel("Enter the host IP");
        isHost = new JCheckBox();
        JLabel hostLabel = createStyledLabel("Are you the host?");
        menuPanel.add(IPlabel);
        menuPanel.add(inputIP);
        menuPanel.add(hostLabel);
        menuPanel.add(isHost);
        menuPanel.add(confirm);
      }
    });

    quit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });

    // Adding buttons to the menu panel
    menuPanel.add(playComputer);
    menuPanel.add(playFriend);
    menuPanel.add(playNetwork);
    menuPanel.add(quit);

    pack(); // Sizes the frame so that all its contents are at or above their preferred
            // sizes
    setLocationRelativeTo(null); // Centers the window on the screen
    setVisible(true); // Makes the GUI visible
  }

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
  private JLabel createStyledLabel(String text) {
    JLabel label = new JLabel(text, SwingConstants.CENTER);
    label.setForeground(Color.WHITE); 
    label.setFont(new Font("Arial", Font.BOLD, 17));
    label.setPreferredSize(new Dimension(200, 50));
    return label;
  }

  /*public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        new Menu();
      }
    });
  }
*/}

