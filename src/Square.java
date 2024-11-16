import java.util.*;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;


class Square implements Displayable{
    private final EnumMap<Direction,Square> neighbors;
    private Boolean isMine;
    private int numberOfMinesAround;
    private SquareState state;
    private JButton button;

    Square(){
        this.isMine = false;
        this.neighbors = new EnumMap<>(Direction.class);
        this.numberOfMinesAround = 0;
        this.state = SquareState.HIDDEN;
        this.button = null;
    }
    
    //defini cette case et celle passée en paramètre comme étant voisines en fonction de la direction donnée
    public void setNeighbour(Direction d, Square s){
        this.neighbors.put(d,s);
        s.neighbors.put(d.getOpposite(),this);
    }

    public void setState(SquareState s) {
        this.state = s;
    }

    public void setMine(){
        this.isMine = true;
    }

    //ajoute +1 au compteur des cases autour de la mine et defini la case comme etant une mine
    public void defineMine(){
        for(Square s : neighbors.values()){
            s.numberOfMinesAround++;
        }
        this.setMine();
    }

    public boolean isMine(){
	    return isMine;
    }

    public void mark(){
	    this.state = SquareState.MARKED;
    }

    public void unmark(){
	    this.state = SquareState.HIDDEN;
    }

    public boolean isHidden(){
        return this.state == SquareState.HIDDEN;
    }

    public boolean isUncovered(){
        return this.state == SquareState.UNCOVERED;
    }

    public boolean isMarked(){
        return this.state == SquareState.MARKED;
    }

    public int getNumberOfMinesAround() {
        return this.numberOfMinesAround;
    }

    //méthode récursive qui va découvrir toutes les cases voisines à celle sélectionnée si celle ci n'a pas de bombes autour
    public void uncover(){
        this.state = SquareState.UNCOVERED;
        if (this.button != null) {
            this.uncoverButton();
        }
        if(this.numberOfMinesAround == 0){
            for(Square val : this.neighbors.values()){
                if(val != null && val.isHidden()){
                    val.uncover();
                }
            }
        }
    }

    public void setButton(JButton button) {
        this.button = button;
    }
 
    //affiche la case avec le bon symbole et la bonne couleur en fonction de l'état de la case et de la partie
    public void display(GameState gs){
        //Gestion de la couleur
        String color;
        if(gs == GameState.PROGRESS && this.isMarked()){
            color = "\033[33m"; //jaune
        }else if(this.isUncovered() && !(this.isMine())){
            color = "\033[36m"; //bleu
        }else if((gs == GameState.WIN || gs == GameState.LOOSE) && this.isMarked() && this.isMine()){
            color = "\033[32m"; //vert
        }else if(gs == GameState.WIN && this.isMine()){
            color = "\033[32m"; //vert
        }else if(gs == GameState.LOOSE && ((!(this.isMarked()) && this.isMine()) || (this.isMarked() && !(this.isMine())))){
            color = "\033[31m"; //rouge
        }else{
            color = "\033[0m"; //blanc
        }
        System.out.print(color);
        //affichage
        switch(this.state){
        case HIDDEN:
            if((gs == GameState.WIN || gs == GameState.LOOSE) && this.isMine()){
                System.out.print("X");
            }else{
                System.out.print("?");
            }
            break;
        case MARKED:
            System.out.print("M");
            break;
        case UNCOVERED:
            if(this.isMine()){
                System.out.print("X");
            }else if(this.numberOfMinesAround > 0){
                System.out.print(this.numberOfMinesAround);
            }else{
                System.out.print(" ");
            }
            break;
        }
        System.out.print("\033[0m");
    }

    // méthode qui gère l'affichage du bouton correspondant à la case dans l'interface graphique
    public void uncoverButton() {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(Color.WHITE);
        int nbMines = this.getNumberOfMinesAround();
        if (nbMines > 0) {
            changeColor(this.button, nbMines);
            Integer n = nbMines;
            this.button.setText(String.valueOf(n));
        }
        else {
            this.button.setText("");
        }
    }

    public void markButton() {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setText("M");
        button.setForeground(Color.RED);
    }

    public void mineButton() {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.BLACK);
        button.setText("X");
    }

    private void changeColor(JButton button, int number) {
        switch (number) {
            case 1:
                button.setForeground(Color.BLUE);
                break;
            case 2:
                button.setForeground(Color.GREEN);
                break;
            case 3:
                button.setForeground(Color.RED);
                break;
            case 4:
                button.setForeground(Color.ORANGE);
                break;
            case 5:
                button.setForeground(Color.MAGENTA);
                break;
            case 6:
                button.setForeground(Color.CYAN);
                break;
            case 7:
                button.setForeground(Color.BLACK);
                break;
            case 8:
                button.setForeground(Color.DARK_GRAY);
                break;
        }

    }
}
