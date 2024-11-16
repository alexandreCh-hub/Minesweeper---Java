import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        // GUI pour jouer avec l'interface et Game pour jouer dans le terminal
        Scanner input;
        int choice;
        input = new Scanner(System.in);
        System.out.println("Entrez 1 pour jouer dans le terminal ou 2 pour jouer avec l'interface graphique :");
        do{
            choice = input.nextInt();
            if(choice != 1 && choice != 2){
                System.out.println("\033[31mChoix incorrect, veuillez recommencer\033[0m");
            }
        }while(choice != 1 && choice != 2);
        if (choice == 1) {
            Game demineur = new Game();
            demineur.start();
        }
        else {
            GUI demineur = new GUI();
            demineur.start();
        }
        input.close();
    }
}