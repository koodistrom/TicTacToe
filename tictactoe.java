import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
/**
 * the game object is created and run here
 */
class TicTacToe {
    public static void main(String [] args) {

                Game game1 = new Game();
        
    }   
}

/**
 * Game class holds the tictactoe game
 */
class Game extends JFrame {

    /** game boards height */
    private int height = 3;
    /** game boards width */
    private int width = 3;
     /** length of the row reguired to win */
    private int winRowLength = 3;
     /** game board 2d array that holds the information of players symbols' locations */
    private String [][] gameBoard;
     /** keeps track of how many turns have been played */
    private int turnCounter;
     /** true if a player has won */
    private boolean winner;
     /** column coordinate of last symbol put on the game board */
    private int chosenColumn;
    /** line coordinate of last symbol put on the game board */
    private int chosenLine;
    /** symbol that the player in turn uses */
    private String symbol;
    /** random 0 or 1 determines who starts */
    private int startNumber;
    /** true if it is users turn */
    private boolean playerTurn;
    /** column coordinate of a placement selection made in the GUI */
    private int humanChoiseLine;
    /** line coordinate of a placement selection made in the GUI */
    private int humanChoiseColumn;
    /** true if succesful selection on the graphic gameboard is made*/
    private boolean humanChoiseMade;
    /** game window */
    private GUI gameWindow;

    /** object that forwards user selections from the GUI to the game object */
    private HumanPlayer player1 = new HumanPlayer();
    /** computer "AI" */
    private ComPlayer player2 = new ComPlayer();


    /**
     * game object creates new JFrame game window
     */
    public Game() {

        gameWindow = new GUI();
        
    }

    /**
     * disposes the old game window and creates a new one
     */
    public void createNewGame(){
        gameWindow.dispose();
        gameWindow = new GUI();
    }

    /**
     * method creates new empty game board array and resets the variables
     */
    public void start(){
        
        winner = false;
        playerTurn = false;
        humanChoiseMade = false;


        gameBoard = new String [height][width];

        for(int n = 0; n < height; n++){
            for(int m = 0; m < width; m++){
                gameBoard[n][m]= " ";
            }
        }  

        turnCounter = 0;
        startNumber = (int)(Math.random()*2);
    }

    /**
     * prints the game board array
     */
    public void printBoard(){
        for(int n = 0; n < height; n++){
            for(int m = 0; m < width; m++){
                System.out.print("["+gameBoard[n][m]+"]");
            }
            System.out.println();
        }   
    }

    /**
     * assings the turns for players
     * @param player1 HumanPlayer or ComPlayer
     * @param player2 HumanPlayer or ComPlayer
     */
    public void assignTurn(Player player1, Player player2){
        if(!gameEnd()){
            if(turnCounter%2==startNumber){
                symbol = "X";
                playerTurn = true;

            }else{
                symbol = "O";
                player2.play();
                }
        }
    }

    /**
     * checks if winner has been found or if the gameboard is full
     * @return returns true if game is over
     */
    public boolean gameEnd(){
        return winner || turnCounter == height*width;
    }

    /**
     * checks have the players won or game tied and notifies player in the game window
     */
    public void winChecker(){

        winner = new RowChecker().check(chosenLine, chosenColumn, winRowLength, symbol, 0, 5);

        if(turnCounter%2==startNumber&&winner){
            gameWindow.infoText.setText("You lose");
            //System.out.println("You lose");
        }else if(turnCounter%2!=startNumber&&winner){
            gameWindow.infoText.setText("Victory!");
            //System.out.println("Victory!");
        }else if (gameEnd()){
            gameWindow.infoText.setText("Tie");
            //System.out.println("Tie");
        }
    }

    /**
     * checks if given location is on the game board and if it's free
     * @param coordinateLine line coordinate
     * @param coordinateColumn column coordinate
     * @return true if location is on game board and free
     */
    public boolean isValidPlacement(int coordinateLine, int coordinateColumn){
        boolean isValid;
        if(coordinateLine<height && coordinateLine>=0 && coordinateColumn <width && coordinateColumn>=0 && gameBoard[coordinateLine][coordinateColumn].equals(" ")){

            isValid = true;
        }else{
            isValid = false;
        }
        return isValid;
    }

    /**
     * class checks the lengths of rows formed by same symbols in given positions
     * and delivers information about them
     */
    class RowChecker{

        /** is true if measured row has one openspace at the end */
        private boolean openEndFound;
        /** is true if measured row has two openspaces at the end */
        private boolean twoOpenEndsFound;
        /** is true if measured row has one openspace at the start */
        private boolean openStartFound;
        /** is true if measured row has two openspaces at the start */
        private boolean twoOpenStartsFound;

        /** is true if measured row fills the reguirements */
        private boolean found;
        /** holds the ammount of adjacent symbols that fill the conditions given around a specified location */
        private int symbolsAround;

        /**
         * Method checks the lengths of rows formed by same symbols to all directions from a specified location 
         * for a row that is at least the specified length and fills the conditions of open spaces at the end
         * @param coordinateLine number of the line on witch the check is made
         * @param coordinateColumn number of the column on witch the check is made
         * @param lengthTofind minimum length to find
         * @param symbolToFind the symbol that is searched
         * @param condition number given to the condition method that determines how many open places row have to have at the ends to be valid
         * @param symbolsAroundCondition number given to the condition method that determines how many open places row have to have at the ends to be counted in symbolsAround variable
         * @return returns boolean of did the method find row of symbols to match the length and conditions
         */
        public boolean check(int coordinateLine, int coordinateColumn, int lengthTofind, String symbolToFind, int condition, int symbolsAroundCondition){
            symbolsAround = 0;
            found = false;
            //checks vertical lines        
            if(rowLengthChecker(coordinateLine, coordinateColumn, 1, 0, symbolToFind, symbolsAroundCondition) >= lengthTofind && condition(condition)){
                found = true;
            }
    
            //checks horisontal lines        
            else if(rowLengthChecker(coordinateLine, coordinateColumn, 0, 1, symbolToFind, symbolsAroundCondition) >= lengthTofind && condition(condition)){
                found = true;
            }
    
            //checks rising diagonals
            else if(rowLengthChecker(coordinateLine, coordinateColumn, 1, 1, symbolToFind, symbolsAroundCondition) >= lengthTofind  && condition(condition)){
                found = true;
            }
    
            //chacks lowering diagonals
            else if(rowLengthChecker(coordinateLine, coordinateColumn, -1, 1, symbolToFind, symbolsAroundCondition) >= lengthTofind && condition(condition)){
                found = true;
            }
            return found;
        }
    
        /**
         * Method checks the length of a row formed by same symbols from specified location to specified direction
         * @param coordinateLine number of the line on witch the check is made
         * @param coordinateColumn number of the column on witch the check is made
         * @param verticalRise adjusts the direction of the check heightwise. 1 for vertical or rising diagonal 0 for horisontal, and -1 for lowering diagonal
         * @param horisontalGrowth  adjusts direction of the check horizontally. 0 for vertical, 1 for rising diagonal or horisontal or lowering diagonal.
         * @param symbolToFind the symbol that is searched
         * @param symbolsAroundCondition number given to the condition method that determines how many open places row have to have at the ends to be counted in symbolsAround variable
         * @return returns the length of the row
         */
        private int rowLengthChecker(int coordinateLine, int coordinateColumn, int verticalRise, int horisontalGrowth, String symbolToFind, int symbolsAroundCondition){

            int lineStartY;
            int lineStartX;
            int lineStartXAdjacent;
            int lineStartYAdjacent;
            int lineEndY;
            int lineEndX;
            int lineEndXAdjacent;
            int lineEndYAdjacent;

            openStartFound = false;
            twoOpenStartsFound = false;
            openEndFound = false;
            twoOpenEndsFound = false;
    
            int symbolsOnRow=1;
            int y = verticalRise; 
            int x = horisontalGrowth;

            //checks selected row "forward"
            while(coordinateLine+y <height && coordinateColumn+x<width && coordinateLine+y>=0 && coordinateColumn+x>=0 && gameBoard[coordinateLine+y][coordinateColumn+x].equals(symbolToFind)){
                y += verticalRise;
                x += horisontalGrowth;
                symbolsOnRow++;
            }
    
            lineStartX = coordinateColumn+x;
            lineStartY = coordinateLine+y;
            
            y += verticalRise;
            x += horisontalGrowth;
    
            lineStartXAdjacent = coordinateColumn+x;
            lineStartYAdjacent = coordinateLine+y;
    
            if(isValidPlacement(lineStartY,lineStartX)){
                openStartFound = true;
            }

            if(isValidPlacement(lineStartY,lineStartX)&& isValidPlacement(lineStartYAdjacent,lineStartXAdjacent) ){
                twoOpenStartsFound = true;
            }
    
            y = verticalRise;
            x = horisontalGrowth;

            //checks selected row "backward"
            while(coordinateLine-y <height && coordinateColumn-x<width && coordinateLine-y>=0 && coordinateColumn-x>=0 &&gameBoard[coordinateLine-y][coordinateColumn-x].equals(symbolToFind)){
                y += verticalRise;
                x += horisontalGrowth;
                symbolsOnRow++;
            }
    
            lineEndX = coordinateColumn-x;
            lineEndY = coordinateLine-y;
    
            y += verticalRise;
            x += horisontalGrowth;
    
            lineEndXAdjacent = coordinateColumn-x;
            lineEndYAdjacent = coordinateLine-y;
    
            if(isValidPlacement(lineEndY,lineEndX)){
                openEndFound = true;
            }

            if(isValidPlacement(lineEndY,lineEndX)&& isValidPlacement(lineEndYAdjacent,lineEndXAdjacent) ){
                twoOpenEndsFound = true;
            }

            if(condition(symbolsAroundCondition)){
                symbolsAround += symbolsOnRow;
            }

            //System.out.println(symbolsOnRow);
            return symbolsOnRow;
        }

        /**
         * this method has six different options for different conditions regarding open spaces at the ends of the rows that are checked
         * @param condition
         * condition is chosen with number 0-5
         * 0 no condition (always true)
         * 1 one space at either end
         * 2 open space at both ends
         * 3 open space at one end and two open spaces at the other
         * 4 two open spaces at both ends
         * 5 always false (needed for check method to go through all directions)
         * @return returns true if last checked row fulfills the given condition
         */
        private boolean condition(int condition){
            boolean checkConditions = true;
            switch (condition) {
                //no extra conditions chacks just length
                case 0:  checkConditions = true;
                        break;
                // at least one open end
                case 1:  checkConditions = openEndFound || openStartFound;
                        break;
                // both ends open
                case 2:  checkConditions = openEndFound && openStartFound;
                        break;
                // at least one open on other end and two opens on other
                case 3:  checkConditions = (openEndFound && twoOpenStartsFound) || (openStartFound && twoOpenEndsFound);
                        break;
                //both ends have two open
                case 4:  checkConditions = (twoOpenEndsFound && twoOpenStartsFound);
                        break;
                //always false so goes through all directions
                case 5:  checkConditions = false;
                        break;
            }
            
    
            return checkConditions;
        }

        /**
         * Gives ammount of adjacent symbols that have the reguired ammount of open spaces after them around given location at the gameboard
         * @param line line coordinate
         * @param column column coordinate
         * @param symbolToCheck searched symbol
         * @param condition number given to the condition method that determines how many open places row have to have at the ends to be valid
         * @return returns the ammount of  adjacent symbols +4 that fill the given conditions around the given location 
         */
        public int ammountOfSymbolsAround(int line, int column, String symbolToCheck, int condition){
            check(line, column, 0, symbolToCheck, 5, condition);
            return symbolsAround;
        }

    }
    



        /**
     * abstract class that makes sure that both computer and human players have play method
     * 
     */
    abstract class Player{
        abstract void play();
    }

    /**
     * HumanPlayer object forwards user decisions from GUI to gameboard 
     */
    class HumanPlayer extends Player{
        public Scanner input = new Scanner(System.in);

        /**
         * forwards user decisions from GUI to gameboard and ends humanplayers turn by checking for winner, moving turnCounter, and calling assignTurn method
         */
        public void play(){
            

            if(humanChoiseMade){
                chosenLine = humanChoiseLine;
                chosenColumn = humanChoiseColumn;
                gameBoard[chosenLine][chosenColumn] = symbol;
                int i = (chosenLine*width)+chosenColumn;
                gameWindow.buttons[i].setText(symbol); 
                
                //printBoard();
                turnCounter++;
                winChecker();                
                humanChoiseMade = false;
                playerTurn = false;
                assignTurn(player1,player2);
            }
            
        }   
    }

    /**
     * ComPlayer object plays against human player and checks for different situations on the game board and reacts to them
     */
    class ComPlayer extends Player{
        /** object that retrieves information about symbol placements on the gameboard */
        RowChecker checkSituation = new RowChecker();
        /** computer players symbol */
        String me = "O";
        /** opponents symbol */
        String opponent = "X";
        /** true if "AI" has made a selectoin where to put symbol*/
        boolean rightMoveMade;
        /** line coordinate of "AI"'s latest choice (-1 for nothing made yet)*/
        int myLastChoiseLine = -1;
        /** column coordinate of "AI"'s latest choice (-1 for nothing made yet)*/
        int myLastChoiseColumn = -1;

        /**
         * checks for different scenarions in order and reacts accordingly at the end ends turn by  checking for winner, moving turnCounter, and calling assignTurn method
         */
        public void play(){
            rightMoveMade = false;
            int randomArea = (int)((Math.random()*2)+1);

            
            checkIfFirstMove();
            //checks if win possible with next move 
            checkBoard(winRowLength, me, 0);
            checkBoard(winRowLength, opponent, 0);

            //checks if win possible with two next moves 
            checkBoard(winRowLength-1, me, 2);
            checkBoard(winRowLength-1, opponent, 2);

            //checs for placements that could create many potential rows at once
            checkForDangerousCrossings(me);
            checkForDangerousCrossings(opponent);

            //checks for shorter rows with open spaces at the ends
            checkBoard(winRowLength-2, me, 4);

            //random placement near com's last selection
            randomNearBy(randomArea);

            //full random
            random();

            myLastChoiseLine  = chosenLine;
            myLastChoiseColumn = chosenColumn;
            gameBoard[chosenLine][chosenColumn] = symbol;
            int i = (chosenLine*width)+chosenColumn;
            gameWindow.buttons[i].setText(symbol);   

            //printBoard();
            turnCounter++; 
            winChecker();
            assignTurn(player1,player2);
        }  

        /**
         * if no turns have been made com player starts from the middle
         */
        public void checkIfFirstMove(){
            if(turnCounter==0){
                chosenLine = height/2;
                chosenColumn = width/2;
                rightMoveMade = true;
            }
        }

        /**
         * checks board for the possibilities to form reguired length row with reguired ammount of adjacent open spaces
         * if method finds open space that matches the reguirements it chooses the location
         * @param lengthToCheck reguired length of the row
         * @param symbolToCheck symbol that forms the reguired row
         * @param condition  number given to the RowCheck objects condition method that determines how many open places row have to have at the ends to be valid
         */
        public void checkBoard(int lengthToCheck, String symbolToCheck, int condition){
            if(!rightMoveMade){
                for(int n = 0; n < height; n++){
                    for(int m = 0; m < width; m++){
                        if(gameBoard[n][m].equals(" ") && checkSituation.check(n, m, lengthToCheck, symbolToCheck, condition, 0)){
                            chosenLine = n;
                            chosenColumn = m;
                            rightMoveMade = true;

                        }

                    }
                    if(rightMoveMade){
                        n=height;
                    }
                }
            }
        }

        /**
         * checks gameboard for open spaces that have many symbols around it and
         * if method finds open space that matches the reguirements it chooses the location
         */  
        public void checkForDangerousCrossings(String symbolToCheck){
            
            int mostSymbols = 0;
            int line = -1;
            int column = -1;
            if(!rightMoveMade){
                for(int n = 0; n < height; n++){
                    for(int m = 0; m < width; m++){
                        if(gameBoard[n][m].equals(" ")){
                            if(checkSituation.ammountOfSymbolsAround(n,m, symbolToCheck, 3)>mostSymbols){
                                line = n;
                                column = m;
                                mostSymbols = checkSituation.ammountOfSymbolsAround(n,m, symbolToCheck, 3);
                            }
                        }
                    }
                }

                if(mostSymbols>=winRowLength+3){
                    chosenLine = line;
                    chosenColumn = column;
                    
                    rightMoveMade = true;
                }
            }
        }

        /**
         * randomly chooses a free space at the gameboard
         */
        public void random(){

            if(!rightMoveMade){

                do{
                    chosenLine = (int)(Math.random()*height);
                    chosenColumn = (int)(Math.random()*width);
                }while(!gameBoard[chosenLine][chosenColumn].equals(" "));
            }
        }

        /**
         * randomly chooses a spot near by the last place computer selected
         */
        public void randomNearBy(int area){
            if(!rightMoveMade&& myLastChoiseColumn>-1 && myLastChoiseLine>-1){
                int i = 0;
                int line;
                int column;
                do{
                    line = (myLastChoiseLine)+((int)(Math.random()*(area*2+1))-area);
                    column = (myLastChoiseColumn)+((int)(Math.random()*(area*2+1))-area);
                    i++;

                }while(!isValidPlacement(line,column)||i>30);
                if(i!=30){
                    chosenLine = line;
                    chosenColumn = column;
                    rightMoveMade = true;

                }
            }
        }



        
    }


    /**
     * Class for buttons in the gameboard
     */
    class XOButton extends JButton implements ActionListener{
        /**
         * line coordinate of the location that the XOButton represents
         */
        int row;
        /**
         * column coordinate of the location that the XOButton represents
         */
        int column;
        public XOButton( String text, int heightCordinate, int widthCordinate){        
            super(text);
            row = heightCordinate;
            column = widthCordinate;
            this.addActionListener(this);
            setFont(new Font("Arial", Font.BOLD, 50));
        }

        /**
         * action listener for XOButton 
         * checks that the place player selected is free and saves the selection if it is
         */
        public void actionPerformed(ActionEvent e){
            
            if(gameBoard[row][column].equals(" ")&playerTurn){
                
                humanChoiseColumn = column;
                humanChoiseLine = row;

                humanChoiseMade = true;
                player1.play();
                
            }
        }
    
    }

    

    /**
     * graphic user interface class for the game window
     */
    class GUI extends JFrame{
        /** graphic representation of the game board */
        private JPanel graphicGameBoard;
        /** container for the game info displayed */
        private JPanel graphicGameInfo =  new JPanel();
        /** info text win lose etc..- */
        private JLabel infoText;
        /** ammount of buttons needed for the gameboard representation */
        private int ammountOfSquares;
        /** array of buttons for the game board graphical representation */
        private XOButton[] buttons;
        /** place for the user height input */
        private UserSelection heightField;
        /** place for the user width input */
        private UserSelection widthField;
        /** place for the user winning row length input */
        private UserSelection winRowField;
        /** container for all the elements */
        private JPanel mainContainer;
        /** true if game is on and start button turns in to reset button */
        private boolean reset;
        /** true if user set values for the game board are valid */
        private boolean valueasAreValid;


        /**
         * Creates new window
         */
        GUI(){
            super("Tictactoe");

            reset = false;
            setPreferredSize(new Dimension(width*150, height*150+150));
            setMinimumSize(new Dimension(800,600));



            mainContainer = new JPanel();
            mainContainer.setSize(width*150, height*150+150);
            mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.PAGE_AXIS));
            
            setDefaultCloseOperation(EXIT_ON_CLOSE);

            graphicGameInfo.setLayout(new BoxLayout(graphicGameInfo, BoxLayout.LINE_AXIS));

            graphicGameInfo.setPreferredSize(new Dimension(800, 125));
            graphicGameInfo.setMinimumSize(new Dimension(800,125));
            

            JButton startgame = new JButton("start");
            startgame.setSize(100, 75);
            startgame.setFont(new Font("Arial", Font.BOLD, 24)); 
            
            startgame.addActionListener( new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    valueasAreValid = true;
                    if(reset){
                        createNewGame();
                    }else{

                    checkValues();
                    if(valueasAreValid){
                        start();
                        startgame.setText("reset");
                        infoText.setText("Game on!");
                        
                        createGameBoard();
                        assignTurn(player1,player2);
                        reset = true;
                        }
                    }
                }
            });



            infoText = new JLabel("Tic Tac Toe", SwingConstants.LEFT);
            infoText.setFont(new Font("Arial", Font.BOLD, 24));
            infoText.setMinimumSize(new Dimension(800,100));
            infoText.setAlignmentX(JLabel.LEFT_ALIGNMENT);

            winRowField = new UserSelection("Win row length", ""+winRowLength);
            heightField = new UserSelection("Height", ""+height);
            widthField = new UserSelection("Width", ""+width);

            graphicGameInfo.add(heightField);
            graphicGameInfo.add(widthField);
            graphicGameInfo.add(winRowField);
            graphicGameInfo.add(startgame);

            graphicGameBoard =  new JPanel();

            mainContainer.add(infoText);
            mainContainer.add(graphicGameInfo);
            
            mainContainer.add(graphicGameBoard);
            add(mainContainer);


            setVisible(true);
        }

        /**
         * creates the graphic representation of the game board
         */
        public void createGameBoard(){
            mainContainer.remove(graphicGameBoard);
            graphicGameBoard =  new JPanel();
            ammountOfSquares = height * width;
            buttons = new XOButton[ammountOfSquares];
            setMinimumSize(new Dimension(width*150, height*150+150));
            setMaximumSize(new Dimension(width*150, height*150+150));
            graphicGameBoard.setMaximumSize(new Dimension(width*150, height*150));
            graphicGameBoard.setMinimumSize(new Dimension(width*150, height*150));

            graphicGameBoard.setLayout(new GridLayout(height,width));
            for(int i=0; i<ammountOfSquares;i++){
                int heightCordinate = i/width;
                int widthCordinate =i-(heightCordinate*width);
                buttons[i] = new XOButton(gameBoard[heightCordinate][widthCordinate], heightCordinate, widthCordinate);
                graphicGameBoard.add(buttons[i]);
            }
            mainContainer.add(graphicGameBoard);
        }
        /**
         * checks the values entered in the height width and win row length field inputs
         * and informs the player about the faulty inputs
         */
        public void checkValues(){
            try{
                winRowLength= Integer.parseInt(winRowField.getText().trim());
                width= Integer.parseInt(widthField.getText().trim());
                height= Integer.parseInt(heightField.getText().trim());
            }
            catch (Exception e) {
                infoText.setText("sizes and length must be numbers");
                valueasAreValid = false;
            }

            if(winRowLength<3 || winRowLength>height || winRowLength>width ){
                if(height<width){
                    infoText.setText("Win row length must be between 3-"+height);
                }else{
                    infoText.setText("Win row length must be 3-"+width);
                }
                winRowLength = 3;
                width = 3;
                height = 3;

                valueasAreValid = false;
            }
            if(height<3||height>30 || width<3 || width>30 ){
                winRowLength = 3;
                width = 3;
                height = 3;
                infoText.setText("height and width must be between 3-30");
                valueasAreValid = false;
            }
        }


        /**
         * class used for user selection objects(height, width and win row length ) extends JPanel and contains JTextField and JLabel
         */
        class UserSelection extends JPanel{
            /** user enters values here */
            private JTextField field;
            /** tells what value user is setting */
            private JLabel header;

            /**
             * 
             * @param name header for the JTextField
             * @param defaultText default text
             */
            UserSelection(String name, String defaultText){

                setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

                header = new JLabel(name);
                header.setPreferredSize(new Dimension(200, 50));
                header.setMaximumSize(new Dimension(200,50));
                header.setFont(new Font("Arial", Font.BOLD, 24));

                field = new JTextField(defaultText);
                field.setPreferredSize(new Dimension(100, 50));
                field.setMaximumSize(new Dimension(100,50));
                field.setFont(new Font("Arial", Font.BOLD, 24));

                add(header);
                add(field);

            }
            /**
             * 
             * @return reutrns the text in the text field
             */
            public String getText(){

                return field.getText();
            }
    }

    }

}


