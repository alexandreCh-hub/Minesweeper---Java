import java.lang.Math;
import java.util.Random;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

class Grid implements Displayable{
    private int length;
    private int width;
    private int percentageOfMines;
    private int numberOfMines;
    private int numberOfMarksLeft;
    private int numberOfUncoveredSquareToWin;
    private int numberOfUncoveredSquare;
    private Square[][] grid;

    //creer une grille vide
    Grid(int length, int width, int percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Pourcentage impossible");
        }
        this.length = length;
        this.width = width;
        this.percentageOfMines = percentage;
        this.grid = new Square[width][length];
        this.numberOfMines = calculateNumberOfMines();
        this.numberOfMarksLeft = this.numberOfMines;
        this.numberOfUncoveredSquareToWin = this.length * this.width - this.numberOfMines;
        this.numberOfUncoveredSquare = 0;
        this.generateBoard();
    }

    //charger une grille
    Grid(String path) {
        this.numberOfMines = 0;
        this.numberOfMarksLeft = this.numberOfMines;
        this.numberOfUncoveredSquare = 0;
        this.load(path);
        this.findMines();
        this.numberOfUncoveredSquareToWin = this.length * this.width - this.numberOfMines;
    }

    //calcul le nombre de mines en fonction de la taille de la grille et du pourcentage de mines souhaité
    private int calculateNumberOfMines() {
        int numberOfSquares = this.width * this.length;
        int numberOfMine = Math.round(numberOfSquares * this.percentageOfMines / 100);
        return (numberOfMine < 1) ? 1 : numberOfMine;
    }

    //retourne Vrai si toutes les cases qui ne sont pas des mines sont découvertes qui est la condition pour gagner
    public boolean isAllDiscovered(){
        return this.numberOfUncoveredSquareToWin - this.numberOfUncoveredSquare <= 0;
    }

    //appelle la methode uncover de Square et recalcule le nombre de cases restantes à decouvrir pour gagner
    public void uncoverSquare(Square s){
        s.uncover();
        this.numberOfUncoveredSquare = 0;
        for(int i=0;i<this.width;i++){
            for(int j=0;j<this.length;j++){
                if(this.grid[i][j].isUncovered()){
                    this.numberOfUncoveredSquare++;
                }
            }
        }
    }

    //cree le tableau de cases et défini les voisins de chaque case
    private void generateBoard() {
        for (int l = 0; l < this.width; l++) {
            for (int c = 0; c < this.length; c++) {
                this.grid[l][c] = new Square();
                Square currentSquare = this.grid[l][c];
                this.defineNeighbors(currentSquare, l, c);
            }
        }
        this.generateMines();
    }

    //defini aléatoirement des cases comme etant des mines pour avoir le bon nombre de mines dans le tableau
    private void generateMines() {
        Random rand = new Random();
        Square s;

        for (int nb = 0; nb < this.numberOfMines; nb++) {
            do {
                int randomY = rand.nextInt(this.width);
                int randomX = rand.nextInt(this.length);
                s = this.grid[randomY][randomX];
            } while (s.isMine());
            s.defineMine();
        }
    }

    //defini tous les voisins d'une case en fonction de ses coordonnees
    private void defineNeighbors(Square currentSquare, int line, int column) {
        Square s;
        if (line > 0 && column > 0) {
            s = this.grid[line-1][column-1];
            currentSquare.setNeighbour(Direction.TOPLEFT, s);
        }
        if (line > 0 && column < this.length-1) {
            s = this.grid[line-1][column+1];
            currentSquare.setNeighbour(Direction.TOPRIGHT, s);
        }
        if (column > 0) {
            s = this.grid[line][column-1];
            currentSquare.setNeighbour(Direction.LEFT, s);
        }
        if (line > 0) {
            s = this.grid[line-1][column];
            currentSquare.setNeighbour(Direction.TOP, s);
        }
    }

    //retire 1 au nombre de marqueur restant
    public void markMine() {
        if(this.numberOfMarksLeft <= 0){
            throw new IllegalArgumentException("Le nombre de marqueur ne peut pas être négatif");
        }
        this.numberOfMarksLeft -= 1;

    }

    //ajoute 1 au nombre de marqueur restant
    public void removeMarkMine() {
        if(this.numberOfMarksLeft > this.numberOfMines){
            throw new IllegalArgumentException("Le nombre de marqueur est superieur au nombre de mines");
        }
        this.numberOfMarksLeft += 1;
    }

    public boolean hasMarksLeft(){
        return this.numberOfMarksLeft > 0;
    }

    public int getMarksLeft() {
        return this.numberOfMarksLeft;
    }

    public Square getSquare(int line, int column) {
        return this.grid[line][column];
    }

    public int getLength(){
        return this.length;
    }

    public int getWidth(){
        return this.width;
    }

    //affiche tous les cases du tableau et les indices sur les bords
    public void display(GameState gs) {
        System.out.println("Marqueurs restants: \033[36m" + this.numberOfMarksLeft+"\033[0m");
        System.out.print(" ");
        if(this.width > 10){
            System.out.print(" ");
        }
        for (int i = 0; i < this.length; i++){ System.out.print(" " + i);}
        System.out.println("");
        System.out.print(" ");
        if(this.width > 10){
            System.out.print(" ");
        }
        for (int j = 0; j < this.length; j++){ 
            System.out.print(" _");
            if(j > 9){
                System.out.print("_");
            }
        }
        System.out.println("");
        for (int l = 0; l < this.width; l++) {
            System.out.print(l);
            if(this.width > 10 && l < 10){
                System.out.print(" ");
            }
            System.out.print("|");
            for (int c = 0; c < this.length; c++) {
                if(this.length > 10 && c > 9){
                    System.out.print(" ");
                }
                this.grid[l][c].display(gs);
                //this.grid[l][c].test();
                System.out.print("|");
            }
            System.out.println("");
        }
    }


    public void save(String path) {
        try {            
            File file = new File(path);
            // créer le fichier s'il n'existe pas
            if (!file.exists()) {
                file.createNewFile();
            }        
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(this.width + "\n");
            bw.write(this.length + "\n");
            for (int l = 0; l < this.width; l++) {
                for (int c = 0; c < this.length; c++) {
                    if (this.grid[l][c].isUncovered()){
                        bw.write('1');
                    }
                    else if (this.grid[l][c].isHidden()) {
                        if (this.grid[l][c].isMine()) {
                            bw.write('2');
                        }
                        else{
                            bw.write('3');
                        }
                    }
                    else if (this.grid[l][c].isMarked()){
                        if (this.grid[l][c].isMine()) {
                            bw.write('4');
                        }
                        else{
                            bw.write('5');
                        }
                    }
                }
                bw.write("\n");
            }
            bw.close();
                        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(String path) {
        // lire dans le fichier
        // créer un nouveau tableau en prenant la longueur et la largeur au début du fichier
        // stocker chaque case dans le tableau
        // recalculer nombres de mines etc
        // rappeler la méthode defineNeighbours
        try
        {
          File file = new File(path);    
          FileReader fr = new FileReader(file);  
          BufferedReader br = new BufferedReader(fr);     
          String line;
          char nb;
          Square s;

          this.width = Integer.parseInt(br.readLine());
          this.length = Integer.parseInt(br.readLine());
          this.grid = new Square[width][length];
          for (int l=0; l<width; l++) {
            line = br.readLine();
            for (int c=0; c<length; c++) {
                nb = line.charAt(c);  
                this.grid[l][c] = new Square();
                s = this.grid[l][c];
                defineNeighbors(s, l, c);
                if (nb == '1') {
                    s.setState(SquareState.UNCOVERED);
                    this.numberOfUncoveredSquare += 1;
                }
                else if (nb == '2' || nb == '3') {
                    s.setState(SquareState.HIDDEN);
                    if (nb == '2') {
                        s.setMine();
                    }
                }
                else if (nb == '4' || nb == '5') {
                    s.setState(SquareState.MARKED);
                    this.numberOfMarksLeft -= 1;
                    if (nb == '4') {
                        s.setMine();
                    }
                }
            }
          }
          fr.close();
        }
        catch(IOException e)
        {
          e.printStackTrace();
        }
    }

    private void findMines() {
        for (int l = 0; l < this.width; l++) {
            for (int c = 0; c < this.length; c++) {
                if (this.grid[l][c].isMine()) {
                    this.numberOfMines += 1;
                    this.grid[l][c].defineMine();
                } 
            }
        }
        this.numberOfMarksLeft += numberOfMines;
    }
}