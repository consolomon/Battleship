package battleship;
import java.io.IOException;
import java.util.Scanner;

import static battleship.Main.Sector.*;

public class Main {

    enum Sector {

        ZERO(" ", 0),
        A("A", 1), B("B", 2), C("C", 3), D("D", 4),
        E("E", 5), F("F", 6), G("G", 7), H("H", 8),
        I("I", 9), J("J", 10),
        AC("O", 0),
        BS("O", 1),
        SU("O", 2),
        CR("O", 3),
        DE("O", 4),
        EMPTY("~"),
        MISSED("M"),
        HIT("X");



        String value;
        int id;

        Sector(String value, int id) {
            this.value = value;
            this.id = id;
        }
        Sector(String value) {
            this.value = value;
        }


    }

    Sector[] valuesArr = Sector.values();

    public Sector intToLetter(int id) {
        return valuesArr[id];
    }

    public static String literals = new String(" ABCDEFGHIJKLMNOPQRSTUVWXYZ");

    public class Player {

        Sector[][] field;
        Sector[][] fieldStrike;
        int[] dur;
        String name;

        public Player(String name) {

            this.field = new Sector[11][11];
            this.fieldStrike = new Sector[11][11];
            this.dur = new int[]{5, 4, 3, 3, 2};
            this.name = name;
        }
    }

    Player player1 = new Player("Player 1");
    Player player2 = new Player("Player 2");

    public static String[] shipNames = {"Aircraft Carrier", "Battleship", "Submarine", "Cruiser", "Destroyer"};

    public static void main(String[] args) {
        // Write your code here

        Main game = new Main();

        game.fieldSetup(game.player1.field);
        game.fieldSetup(game.player2.field);

        game.fieldSetup(game.player1.fieldStrike);
        game.fieldSetup(game.player2.fieldStrike);

       System.out.println("Player 1, place your ships on the game field\n");
        game.fieldDraw(game.player1.field);
        game.player1.field = game.placingShips(game.player1.field);
        promptEnterKey(false);

        System.out.println("\n".repeat(99));

        System.out.println("Player 2, place your ships on the game field\n");
        game.fieldDraw(game.player2.field);
        game.player2.field = game.placingShips(game.player2.field);
        promptEnterKey(false);

        //System.out.println("\n".repeat(99));

        game.combatLoop();

    }

    public void fieldSetup(Sector[][] field) {
        for (int i = 0; i < 11; i++) {
            field[i][0] = intToLetter(i);
            for(int j = 1; j < 11; j++) {
                field[i][j] = (i == 0) ? intToLetter(j) : EMPTY;
            }
        }
    }

    public void fieldDraw(Sector[][] field) {
        for (int i = 0; i < 11; i++) {
            System.out.print(field[i][0].value + " ");
            for(int j = 1; j < 11; j++) {
                System.out.print( (i == 0) ? field[i][j].id + " " : field[i][j].value + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    public class Coord {
        public int x;
        public int y;

        public Coord() {
        }
    }

    public Coord getCoord(String stringCoord) {

        Coord coord = new Coord();
        coord.y = literals.indexOf(stringCoord.charAt(0));
        coord.x = Integer.parseInt(stringCoord.substring(1));
        return coord;
    }

    public boolean coordsIsCorrect(Coord c1, Coord c2, int shipLength, String shipName, Sector[][] field) {

        if (c1.x < 1 || c1.y < 1 || c2.x < 1 || c2.y < 1 || c1.x > 10 || c1.y > 10 || c2.x > 10 || c2.y > 10) {
            System.out.println("Error! Wrong ship location! Try again:\n");
            return false;
        } else if (c1.x != c2.x && c1.y != c2.y) {
            System.out.println("Error! Wrong ship location! Try again:\n");
            return false;
        } else {
            char direction = c1.x != c2.x ? 'x' : 'y';
            int head = direction == 'x' ? Math.max(c1.x, c2.x) : Math.max(c1.y, c2.y);
            int tail = direction == 'y' ? Math.min(c1.y, c2.y) : Math.min(c1.x, c2.x);
            if (shipLength != head - tail + 1) {
                System.out.println("Error! Wrong length of the " + shipName + "! Try again:\n");
                return false;
            } else {
                if (direction == 'x') {
                    for (int i = tail - 1; i < head + 2; i++) {
                        int p = (i > 10) ? i - 1 : i;
                        int q = c1.y;
                        int offset = q < 10 ? 1 : 0;
                        if (field[q - 1][p].value.equals("O") || field[q][p].value.equals("O") || field[q + offset][p].value.equals("O")) {
                            System.out.println("Error! You placed it too close to another one. Try again:\n");
                            return false;
                        }
                    }
                    return true;
                }
                if (direction == 'y') {
                    for (int i = tail - 1; i < head + 2; i++) {
                        int p = (i > 10) ? i - 1 : i;
                        int q = c1.x;
                        int offset = q < 10 ? 1 : 0;
                        if (field[p][q - 1].value.equals("O") || field[p][q].value.equals("O") || field[p][q + offset].value.equals("O")) {
                            System.out.println("Error! You placed it too close to another one. Try again:\n");
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
    public Sector[][] shipSetup(Coord c1, Coord c2, Sector ship,  Sector[][]field) {
        char direction = c1.x != c2.x ? 'x' : 'y';
        int head = direction == 'x' ? Math.max(c1.x, c2.x) : Math.max(c1.y, c2.y);
        int tail = direction == 'y' ? Math.min(c1.y, c2.y) : Math.min(c1.x, c2.x);

        if (direction == 'x') {
            for (int j = tail; j < head + 1; j++) {
                int i = c1.y;
                field[i][j] = ship;
            }
            return field;
        } else {
            for (int i = tail; i < head + 1; i++) {
                int j = c1.x;
                field[i][j] = ship;
            }
            return field;
        }
    }

    public Sector[][] placingShips(Sector[][] field) {
        Main placing = new Main();
        Scanner inPlacing = new Scanner(System.in);

        for (int i = 11; i < 16; i++) {
            boolean shipIsPlaced = false;
            System.out.println("Enter the coordinates of " + shipNames[i - 11] +" (5 cells):\n");
            while (!shipIsPlaced) {
                String[] inputCoords = inPlacing.nextLine().split(" ");

                Coord c1 = getCoord(inputCoords[0]);
                Coord c2 = getCoord(inputCoords[1]);
                if (placing.coordsIsCorrect(c1, c2, player1.dur[i - 11], shipNames[i - 11], field)) {
                    field = shipSetup(c1, c2, valuesArr[i], field);
                    shipIsPlaced = true;
                    fieldDraw(field);
                }
            }
        }
        return field;
    }

    public boolean shotIsCorrect(String inputCoords) {
        Coord shotCoord = getCoord(inputCoords);
        if (shotCoord.x > 10 || shotCoord.x < 1 || shotCoord.y > 10 || shotCoord.y < 1) {
            System.out.println("Error! You entered the wrong coordinates! Try again:\n");
            return false;
        } else {
            return true;
        }
    }

    public boolean shipCheking (int[] dur) {
        int sum = 0;
        for (int durability : dur) {
            sum += durability;
        }
        boolean b = sum <= 0;
        System.out.println( b ? "You sank the last ship. You won. Congratulations!" : "You sank a ship!");
        promptEnterKey(b);
        return b;
    }

    public boolean striking(Coord shotCoord, Player player1, Player player2) {

        switch (player2.field[shotCoord.y][shotCoord.x]) {

            case EMPTY: {
                player2.field[shotCoord.y][shotCoord.x] = MISSED;
                player1.fieldStrike[shotCoord.y][shotCoord.x] = MISSED;
                System.out.println("You missed!\n");
                promptEnterKey(false);
                return false;
            }

            case HIT:
            case MISSED: {
                System.out.println("You have already striked in this Sector!");
                promptEnterKey(false);
                return false;
            }

            default: {
                player1.dur[player2.field[shotCoord.y][shotCoord.x].id] -= 1;

                if (player1.dur[player2.field[shotCoord.y][shotCoord.x].id] > 0){
                    System.out.println("You hit a ship!");
                    player2.field[shotCoord.y][shotCoord.x] = HIT;
                    player1.fieldStrike[shotCoord.y][shotCoord.x] = HIT;
                    promptEnterKey(false);
                    return false;
                } else {
                    player2.field[shotCoord.y][shotCoord.x] = HIT;
                    player1.fieldStrike[shotCoord.y][shotCoord.x] = HIT;
                    return shipCheking(player1.dur);
                }
            }
        }
    }

    public boolean turn(Player player1 , Player player2) {
        Scanner inCombat = new Scanner(System.in);
        System.out.println("\n".repeat(99));

        fieldDraw(player1.fieldStrike);
        System.out.println("---------------------");
        fieldDraw(player1.field);

        System.out.println(player1.name + ", it's your turn:\n");
        boolean b = false;
        boolean shotIsDone = false;
        while (!shotIsDone) {
            String inputCoords = inCombat.nextLine();
            if (shotIsCorrect(inputCoords)) {
                b = striking(getCoord(inputCoords), player1, player2);
                shotIsDone = true;
            }
        }
        return b;
    }

    public void combatLoop() {

        boolean gameIsFinished = false;
        while (!gameIsFinished) {
            gameIsFinished = turn(player1, player2);
            if (!gameIsFinished) {
                gameIsFinished = turn(player2, player1);
            }
        }
    }

    public static void promptEnterKey(boolean gameIsFinished) {
        System.out.println(gameIsFinished ? "Press Enter" : "Press Enter and pass the move to another player");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}


