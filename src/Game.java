import java.util.Scanner;

class Game{
	private GameState state;
    private Grid g;
	private Scanner input;
	private long time;

    Game(){
		this.state = GameState.PROGRESS;
		this.input = new Scanner(System.in);
		this.choiceGameMode();
    }

	//choisir entre créé une nouvelle partie ou en charger une
	private void choiceGameMode() {
		int mode;
		String path;
		do {
			System.out.println("Entrez 1 pour démarrer une nouvelle partie ou 2 pour charger une partie sauvegardée :");
			mode = this.input.nextInt();
		} while(mode!=1 && mode!=2);
		if (mode == 1) {
			this.initGrid();
		}
		else if (mode == 2) {
			System.out.println("Entrez le nom complet du fichier à charger :");
			this.input.nextLine();
			path = this.input.nextLine();
			this.g = new Grid(path);
		}
	}

	//initialise la grille avec les parametres choisi par l'utilisateur
    private void initGrid(){
	int length, width, percentage;
	
	System.out.println("Entrez le nombre de lignes du plateau :");
	width = this.input.nextInt();
	System.out.println("Entrez le nombre de colonnes du plateau :");
	length = this.input.nextInt();
	System.out.println("Entrez le pourcentage de cases étant une bombe :");
	percentage = this.input.nextInt();
	this.g = new Grid(length,width,percentage);
    }

	//lance le jeu
	public void start(){
		this.time = System.currentTimeMillis();
		while(this.state == GameState.PROGRESS && !(this.g.isAllDiscovered())){//verifie les conditions de victoire ou defaite
			this.g.display(this.state);
			this.pickSquare();
		}
		end();
	}

	//gere la fin de la partie
	private void end(){
		this.time = System.currentTimeMillis() - this.time;
		this.input.close();
		if (this.state != GameState.SAVE) {
			if(this.g.isAllDiscovered()){
				this.state = GameState.WIN;
			}else{
				this.state = GameState.LOOSE;
			}
			this.g.display(this.state);
			if(this.state == GameState.WIN){
				System.out.print("\033[32m");
			}else{
				System.out.print("\033[31m");
			}
				System.out.println(this.state.toString());
				System.out.print("\033[0m");
				this.time = this.time/1000;
				String message = String.format("%02d:%02d", this.time/60, this.time%60);
				System.out.println(message);
		}
		else {
			System.out.println("Partie sauvegardée !");
		}
	}

	//retourne Vrai si les coordonnees sont en dehors du tableau
	private boolean CoordonnesOutOfGrid(int x, int y){
		return x < 0 || x > this.g.getWidth() || y < 0 || y > this.g.getLength();
	}

	//fonction qui verifie si une chaine ne contient que des chiffres
	private boolean isNumber(String chaine){
		return chaine.matches("[0-9]+");
	}

	//recupere la bonne case en fonction des coordonnees entrées par l'utilisateur et lui applique l'action voulue
	//permet de sauvegarder la partie a chaque coup
	private void pickSquare() {
		String[] answer;
		String message, path;
		Character state = 'U';

		System.out.println("Pour découvrir la case ligne 7, colonne 3, écrire : 7 3");
		System.out.println("Par défaut, la case sera découverte, pour la marquer ou enlever un marqueur, rajouter M à la fin");
		System.out.println("Pour sauvegarder la partie en cours et quitter, écrivez S");
		do{
			message = this.input.nextLine();
			answer = message.split(" ");
			if(answer.length > 1 && (!isNumber(answer[0]) || !isNumber(answer[1]) || CoordonnesOutOfGrid(Integer.valueOf(answer[0]), Integer.valueOf(answer[1])))){
				System.out.println("\033[31mCoordonnées incorrectes, veuillez recommencer\033[0m");
			}
		}while(!message.equals("S") && (answer.length < 2 || !isNumber(answer[0]) || !isNumber(answer[1]) || CoordonnesOutOfGrid(Integer.valueOf(answer[0]), Integer.valueOf(answer[1]))));
		if (message.equals("S")) { 
			System.out.println("Entrez le chemin du fichier de sauvegarde:");
			path = this.input.nextLine();
			this.g.save(path);
			this.state = GameState.SAVE;
			return; 
		}

		if(answer.length > 2 && answer[2].equals("M")){
			state = 'M';
		}
		System.out.println("");

		Square s = this.g.getSquare(Integer.valueOf(answer[0]), Integer.valueOf(answer[1]));
		if(state.equals('U')){
			if (s.isMarked()){
				System.out.println("\033[31mLUne case marquée ne peut pas être découverte !\033[0m");
			}
			else{
				if(s.isMine()){
					this.state = GameState.LOOSE;
				}else if(s.isHidden()){
					this.g.uncoverSquare(s);
				}
			}
		}else{
			if(s.isHidden()){
				if(this.g.hasMarksLeft()){
					this.g.markMine();
					s.mark();
				}else{
					System.out.println("\033[31mLe nombre maximal de marqueur est atteint !\033[0m");
				}
			}
			else if(s.isMarked()){
				this.g.removeMarkMine();
				s.unmark();
			}
		}
	}
}