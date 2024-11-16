import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.Color;

// Classe qui déclenche un événement à chaque clique souris
public class MouseClick extends MouseAdapter {
    private JButton[][] buttonsGrid;
    private Grid squareGrid;
    private JLabel text;
    private GameState state;

    MouseClick(JButton[][] buttonsGrid, Grid squareGrid, JLabel text) {
        this.state = GameState.PROGRESS;
        this.buttonsGrid = buttonsGrid;
        this.squareGrid = squareGrid;
        this.text = text;

        for (int i = 0; i < buttonsGrid.length; i++) {
            for (int j = 0; j < buttonsGrid[i].length; j++) {
                buttonsGrid[i][j].addMouseListener(this);
            }
        }
    }
    
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (this.state != GameState.PROGRESS || this.squareGrid.isAllDiscovered()) {
            return;
        }

        JButton clickedButton = (JButton) e.getSource();
        int line = 0, column = 0;

        // Trouver quel bouton a été cliqué
        for (int i = 0; i < this.buttonsGrid.length; i++) {
            for (int j = 0; j < this.buttonsGrid[i].length; j++) {
                if (this.buttonsGrid[i][j] == clickedButton) {
                    line = i;
                    column = j;
                }
            }
        }

        // Vérifier si on a fait un clique gauche ou droit
        if (SwingUtilities.isLeftMouseButton(e)) {
            // Découvrir la case
            uncoverSquare(clickedButton, line, column);
        }
        else if (SwingUtilities.isRightMouseButton(e)) {
            // Marquer ou démarquer la case
            markSquare(clickedButton, line, column);
        }
    }


    private void uncoverSquare(JButton button, int l, int c) {
        Square s = this.squareGrid.getSquare(l, c);
        if (s.isHidden()) {
            if (s.isMine()) {
                s.mineButton();
                button.setBackground(Color.RED);
                this.state = GameState.LOOSE;
            }
            else {
                this.squareGrid.uncoverSquare(s);
            }
        }  
    }

    private void markSquare(JButton button, int l, int c) {
        Square s = this.squareGrid.getSquare(l, c);

        if (s.isHidden()) {
            // modifier le texte en fonction du nombre de marqueurs placés
            if (squareGrid.hasMarksLeft()) {
                s.markButton();
                this.squareGrid.markMine();
                s.mark();
            }
        }
        else if (s.isMarked()) {
            button.setText("");
            this.squareGrid.removeMarkMine();
            s.unmark();
        }
        this.text.setText("Marqueurs restants : " + squareGrid.getMarksLeft());
    }

    public GameState getState(){
        return this.state;
    }

    /* on étend la classe MouseAdapter plutôt que la classe MouseListener car cette dernière nécessite l'implémentation de toutes ses méthodes alors qu'elles
    sont fournies dans la classe MouseAdapter */
}