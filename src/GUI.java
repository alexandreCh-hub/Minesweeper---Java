import javax.swing.*;
import java.awt.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

// Classe GUI qui remplace la classe Game pour l'interface graphique
public class GUI extends JFrame {
    private JPanel panel;
    private JPanel topPanel;
    private JPanel gamePanel;
    private JLabel textLabel;
    private JButton saveButton;
    private JLabel timerLabel;
    private JButton[][] buttonsGrid;
    private Grid squareGrid;
    private GameState state;
    private long time;
    private MouseClick buttonClick;

    private int nbLines = 10;
    private int nbColumn = 10; 
    private int ptMines = 10;
    private String path = null;

    GUI() {
        setTitle("Démineur");
        setSize(800, 800);
        // termine le programme lorsque l'on ferme la fenetre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.choiceGameMode();

        this.displayGrid();
        this.state = GameState.PROGRESS;
        this.buttonClick = new MouseClick(buttonsGrid, squareGrid, textLabel);
    }

    private void choiceGameMode() {
        int choice = JOptionPane.showOptionDialog(this, "Que voulez-vous faire ?", "Bonjour !", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] {"Nouvelle partie", "Charger partie"}, null);

        if (choice == JOptionPane.YES_OPTION) {
            this.initGrid();
        } else if (choice == JOptionPane.NO_OPTION) {
            this.loadGrid();
        } else {
            System.exit(0); 
        }
    }

    public void start() {
        this.time = System.currentTimeMillis();
        boolean end = false;
        while(!end){
            state = this.buttonClick.getState();
            if (state != GameState.PROGRESS || this.squareGrid.isAllDiscovered()) {
                end = true;
            }
		}
		end();
    }

    private void end() {
        this.time = System.currentTimeMillis() - this.time;
        if (this.state != GameState.SAVE) {
			if(this.squareGrid.isAllDiscovered()){
				this.state = GameState.WIN;
                for (int i = 0; i < buttonsGrid.length; i++) {
                    for (int j = 0; j < buttonsGrid[i].length; j++) {
                        Square s = this.squareGrid.getSquare(i, j);
                        if (s.isMine()) {
                            s.markButton();
                        }
                    }
                }
                this.textLabel.setText("Victoire !");
			}else{
				this.state = GameState.LOOSE;
                for (int i = 0; i < buttonsGrid.length; i++) {
                    for (int j = 0; j < buttonsGrid[i].length; j++) {
                        Square s = this.squareGrid.getSquare(i, j);
                        if (s.isMine() && !s.isMarked()) {
                            s.mineButton();
                        }
                    }
                }
                this.textLabel.setText("Défaite !");
			}
            this.time = this.time/1000;
            String message = String.format("%02d:%02d", this.time/60, this.time%60);
            this.topPanel.remove(saveButton);
            this.timerLabel = new JLabel(message);
            this.timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
            this.topPanel.add(timerLabel);
            topPanel.revalidate();
            topPanel.repaint();
        }
    }

    private void initGrid() {
        // création des champs pour entrer le texte
        JDialog dialogBox = new JDialog(this, "Choix du plateau", true);
        dialogBox.setLayout(new GridLayout(4,2));
        dialogBox.add(new JLabel("Nombre de lignes :"));
        JTextField lineField = new JTextField();
        dialogBox.add(lineField);
        dialogBox.add(new JLabel("Nombre de colonnes :"));
        JTextField columnField = new JTextField();
        dialogBox.add(columnField);
        dialogBox.add(new JLabel("Pourcentage de mines :"));
        JTextField minesField = new JTextField();
        dialogBox.add(minesField);

        // création du bouton de validation
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nbLines = Integer.parseInt(lineField.getText());
                nbColumn = Integer.parseInt(columnField.getText());
                ptMines = Integer.parseInt(minesField.getText());

                // Fermeture de la boite de dialogue et affichage de la fenêtre contenant la grille
                dialogBox.dispose();
            }
        });
        dialogBox.add(okButton);
        dialogBox.pack();
        dialogBox.setLocationRelativeTo(this);
        
        dialogBox.setVisible(true);

        this.squareGrid = new Grid(nbColumn, nbLines, ptMines);
    }

    private void loadGrid() {
        JDialog dialogBox = new JDialog(this, "Chargement du plateau", true);
        dialogBox.setLayout(new GridLayout(2,2));
        dialogBox.add(new JLabel("Nom du fichier à charger :"));
        JTextField fileField = new JTextField();
        dialogBox.add(fileField);
 
        // création du bouton de validation
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                path = fileField.getText();
                // Fermeture de la boite de dialogue et affichage de la fenêtre contenant la grille
                dialogBox.dispose();
            }
        });
        dialogBox.add(okButton);
        dialogBox.pack();
        dialogBox.setLocationRelativeTo(this);
        
        dialogBox.setVisible(true);
        this.squareGrid = new Grid(path);
        this.nbLines = squareGrid.getWidth();
        this.nbColumn = squareGrid.getLength();
    }

    private void displayGrid() {
        this.panel = new JPanel(new BorderLayout());
        this.topPanel = new JPanel(new GridLayout(1, 1));
        this.textLabel = new JLabel("Marqueurs restants : " + squareGrid.getMarksLeft());
        this.textLabel.setFont(new Font("Arial", Font.BOLD, 20));
        this.topPanel.add(textLabel);

        saveButton = new JButton("Sauvegarder et quitter");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                saveGrid();
            }
        });
        this.topPanel.add(saveButton);
        this.panel.add(topPanel, BorderLayout.NORTH);

        this.gamePanel = new JPanel(new GridLayout(nbLines, nbColumn));
        this.buttonsGrid = new JButton[nbLines][nbColumn];

        for (int i = 0; i < buttonsGrid.length; i++) {
            for (int j = 0; j < buttonsGrid[i].length; j++) {
                this.buttonsGrid[i][j] = new JButton();
                // on attribut un bouton à une case
                Square s = this.squareGrid.getSquare(i, j);
                s.setButton(buttonsGrid[i][j]);
                buttonsGrid[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));
                this.gamePanel.add(buttonsGrid[i][j]);
                buttonsGrid[i][j].setBackground(Color.LIGHT_GRAY);
            }
        }

        panel.add(gamePanel, BorderLayout.CENTER);
        add(panel);
        setVisible(true);

        // restaurer les cases découvertes et marquées si on charge une partie
        for (int l = 0; l < nbLines; l++) {
            for (int c = 0; c < nbColumn; c++) {
                Square s = this.squareGrid.getSquare(l, c);
                if (s.isMarked()) {
                    s.markButton();
                }
                else if (s.isUncovered()) {
                    s.uncoverButton();
                }
            }
        }
    }

    private void saveGrid() {
        JDialog dialogBox = new JDialog(this, "Sauvegarde du plateau", true);
        dialogBox.setLayout(new GridLayout(2,2));
        dialogBox.add(new JLabel("Emplacement de sauvegarde :"));
        JTextField fileField = new JTextField();
        dialogBox.add(fileField);
 
        JButton saveButton = new JButton("Sauvegarder");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                path = fileField.getText();
                dialogBox.dispose();
            }
        });
        dialogBox.add(saveButton);
        dialogBox.pack();
        dialogBox.setLocationRelativeTo(this);
        dialogBox.setVisible(true);

        this.squareGrid.save(path);
        this.state = GameState.SAVE;
        System.exit(0); 
    }
}
