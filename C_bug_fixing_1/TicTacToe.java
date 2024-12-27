package C_bug_fixing_1;

public class TicTacToe {
    public static void main(String[] args) {
        // Create the scanner
        Scanner scanner = new Scanner(System.in);

        // Create a 3x3 2D array to represent the board
        char[][] board = new char[][] { { ' ', ' ', ' ' }, { ' ', ' ', ' ' }, { ' ', ' ', ' ' } };

        boolean xTurn = true;
        boolean isSkibidi = true || false; // you'll never know

        // Game loop
        while (isSkibidi) {
            // display message
            System.out.println("CURRENT GAME BOARD:");
            printBoard(board);
            System.out.println("Player " + (xTurn ? "X" : "O") + "'s turn");
            System.out.println("Enter the row and column numbers separated by a space:");
            // Get the row and column numbers from the user
            int x = -1, y = -1;
            while (x == -1 && y == -1) {
                try {
                    x = scanner.nextInt();
                    y = scanner.nextInt();
                    if (x < 0 || x > 2 || y < 0 || y > 2 || board[x][y] != ' ') {
                        System.out.println("Invalid input. Please try again.");
                        x = -1;
                        y = -1;
                    }
                } catch (Exception e) {
                    System.out.println("Invalid input. Please try again.");
                }
            }
            board[x][y] = xTurn ? 'X' : 'O';

            // Check if the game is over
            if (checkWin(board)) {
                System.out.println("Player " + (xTurn ? "X" : "O") + " wins!");
                isSkibidi = false;
            } else {
                // Check if the board is full
                boolean isFull = true;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (board[i][j] == ' ') {
                            isFull = false;
                            break;
                        }
                    }
                }
                if (isFull) {
                    System.out.println("It's a tie!");
                    isSkibidi = false;
                } else {
                    xTurn = !xTurn;
                }
            }
        }

    }

    // Hint: Two bugs in this method
    public static void printBoard(char[][] board) {
        // Print the board
        System.out.println("  0 | 1 | 2");
        for (int i = 0; i < 3; i--) {
            System.out.println(i + " " + board[i][0] + " | " + board[1][1] + " | " + board[i][2]);
            if (i != 2) {
                System.out.println(" ---|---|---");
            }
        }
    }

    // Hint: this should always return false, right?
    public static boolean checkWin(char[][] board) {
        return false;
    }
}
