import java.util.LinkedList;
import java.util.Stack;
import java.util.StringTokenizer;  

//Program:	clynch1
//Course:	COSC470
//Description:	The purpose of this program is to search a graph and come up with
//              two solutions, one for the shortest path and another for the most
//              reward.  Every node has a "treasure" value and every arck/path
//              has a length value.  This program promts the user to be able to
//              decide what percent of each option to use, and then finds the 
//              pathe with those peramiters.
//Author:	Connor Lynch
//Revised:	2/14/17
//Language:	Java
//IDE:		NetBeans 8.0.1
//Notes:	
//******************************************************************************
//******************************************************************************
public class clynch1 {

    
    public static                       TextFileClass textFromFile = new TextFileClass();               //loads the main project perams
    public static                       KeyboardInputClass keyboardInput = new KeyboardInputClass();    //takes keyboard input
    public static int                   numberOfNodes;                                                  //this is the number of total nodes
    public static int                   startState;                                                     //this is the node that we will start with   
    public static int                   endState;                                                       //this is the nod that we will end at
    public static double                distanceWeight;                                                 //this is how much the distance will be weighted
    public static double                treasureWeight;                                                 //this is how much the treasure will be weighted 
    public static double                largestTreasure;                                                //this is how much the treasure of currentShortestPath is
    public static double                largestDistance;                                                //this is the distance of currentShortestPath
    public static double                bestScore;                                                      //this is the score of shortestPath
    public static double[][]            arcs;                                                           //this is the lengths of all arcs 
    public static double[]              treasure;                                                       //this is the treasure value
    public static LinkedList<Integer>   shortestPath = new LinkedList<Integer>();                       //this is the linked list to store the current shortest path
    public static LinkedList<LinkedList>    paths = new LinkedList<LinkedList>();                       //this contains all shortest paths

    
    //******************************************************************************
    //Method:           main
    //Description:      This method runs the program.  It has the user select a 
    //                  text file and puts the contents into global variables.
    //                  It then calls depthFirstSearch to find all the paths
    //                  and set the shortest.  The shortest path and all of its
    //                  attributes are then printed out.  The user can run the 
    //                  program as many times as desired.
    //Parameters:       none
    //Returns:          userSelection 									
    //Throws:           None
    //Calls:            keyboardInput class, TextFileClass, stringTokenizer,
    //                  depthFirstSearch, distanceCalulator, treasureCalculator       
    public static void main(String[] args) {
        boolean exit = false;        
        while(exit != true){
            int textCheck = 0;
            while(textCheck == 0){
                textFromFile.getFileName("Enter Name of Text File Containing The Information:");
                textFromFile.getFileContents();
                if (textFromFile.text.length > 0) {
                    textCheck = 1;
                }//end of if
            }//end of while        
            
            int parsedInt;                                                                          //this is a holder for parsed int values
            double parsedDouble;                                                                    //this is a holder for parsed double valuese
            
            parsedInt = Integer.parseInt(textFromFile.text[0]);
                numberOfNodes = parsedInt;
            parsedInt = Integer.parseInt(textFromFile.text[1]);
                startState = parsedInt;
            parsedInt = Integer.parseInt(textFromFile.text[2]);
                endState = parsedInt;
            parsedDouble = Double.parseDouble(textFromFile.text[3]);
                distanceWeight = parsedDouble;
            parsedDouble = Double.parseDouble(textFromFile.text[4]);
                treasureWeight = parsedDouble;        

            arcs = new double[numberOfNodes][numberOfNodes];
            treasure = new double[numberOfNodes];

            int y = 0;                                                                              //this is to give the array the correct y cordinate
            for(int i=5; i < numberOfNodes + 5; i++){
                StringTokenizer st = new StringTokenizer(textFromFile.text[i]); 
                double holder;                                                                      //this is a holder for parsed double values                
                for(int x = 0; x < numberOfNodes; x++){
                    holder = Double.parseDouble(st.nextToken(", "));    
                    arcs[y][x] = holder;
                }//end of for
                y++;
            }//end of for loop
            y = 0;                                                                                  //this is to give the array the correct y cordinate
            for(int i = numberOfNodes + 5; i < textFromFile.lineCount; i++){
                StringTokenizer st = new StringTokenizer(textFromFile.text[i]);
                double holder;                                                                      //this is a holder for the parsed double value
                holder = Double.parseDouble(st.nextToken());
                treasure[y] = holder; 
                y++;
            }//end of for loop   

            shortestPath.push(startState);
            depthFirstSearch(startState, shortestPath);

            if(shortestPath != null){
                if(paths.size() == 1){
                    System.out.print("This is the shortest path: ");
                    printLL(shortestPath);
                    System.out.println("Its score was: " + bestScore);
                    System.out.println("Its distance was: " + distanceCalculator(shortestPath));
                    System.out.println("Its treasure was: " + treasureCalculator(shortestPath));
                }//end of if
                else{
                    System.out.print("There are " + paths.size() + " shortest paths: ");
                    for(int x = paths.size() - 1; x >= 0; x--){
                        printLL(paths.get(x));
                        System.out.println("Its distance was: " + distanceCalculator(paths.get(x)));
                        System.out.println("Its treasure was: " + treasureCalculator(paths.get(x)));
                    }//end of for 
                    System.out.println("Their score was: " + bestScore);
                }//end of else
            } else {
                System.out.println("There is no path.");
            }//end of else
            
            char userInput = keyboardInput.getCharacter(true, 'A', "YN", 1, "\n" + "Load new file? (Y,N)");
            if(userInput == 'N'){
                exit = true;
            }//end of if
        }//end of while
    }//end of public static void main
    
    //******************************************************************************
    //Method:           scoreCalculator
    //Description:      This method calculates the total score for the values 
    //                  that are passed in.  
    //Parameters:       treasure - this is the treasure value 
    //                  distance - this is the distance value
    //Returns:       	double - calulated score			
    //Throws:           none
    //Calls:            distanceWeight, treasureWeight
    public static double scoreCalculator(double treasure, double distance) {
        return (treasure * treasureWeight) - (distance * distanceWeight);
    }//end of scoreCalculator
    
    //******************************************************************************
    //Method:           distanceCalculator
    //Description:      This method calculates the distance of a passed in 
    //                  linked list.  
    //Parameters:       LinkedList path - this is the path whose distance needs
    //                  to be calculated.
    //Returns:       	double - calulated distance			
    //Throws:           none
    //Calls:            none
    public static double distanceCalculator(LinkedList<Integer> path) {
        double totalDistance = 0; 
        for(int x = path.size() -1; x >= 1; x--){
            int from = path.get(x);
            int to = path.get(x - 1);
            double currentDistance = arcs[from][to];
            totalDistance = totalDistance + currentDistance;
        }//end of for loop        
        return totalDistance;
    }//end of weightCalculator
    
    //******************************************************************************
    //Method:           trasureCalculator
    //Description:      This method calculates the treasure of a passed in 
    //                  linked list.  
    //Parameters:       LinkedList path - this is the path whose treasure needs
    //                  to be calculated.
    //Returns:       	double - calulated treasure			
    //Throws:           none
    //Calls:            none
    public static double treasureCalculator(LinkedList<Integer> path) {
        double totalTreasure = 0; 
        for(int x = path.size() -1; x >= 0; x--){
            int node = path.get(x);
            double nodeTreasure = treasure[node];
            totalTreasure = totalTreasure + nodeTreasure;
        }//end of for loop
        return totalTreasure;
    }//end of weightCalculator
    
    //******************************************************************************
    //Method:           depthFirstSearch
    //Description:      This method takes a passed in current state and current 
    //                  path and finds all the possible paths to the end state.
    //                  It does this by using recurions and traversing the arcs[][]
    //                  to find eligable children and then creating a path with them.
    //Parameters:       currentState - this is the node that it is currently at 
    //                  currentPath - this is the current shortes path
    //Returns:       	none							
    //Throws:           none
    //Calls:            depthFirstSearch, treasureCalculator, distanceCalculator
    //                  scoreCalculator
    public static void depthFirstSearch(int currentState, LinkedList<Integer> currentPath) {
        if(currentState == endState){                                                                                                           //has reached end state
            if(scoreCalculator(treasureCalculator(currentPath), distanceCalculator(currentPath)) > bestScore || shortestPath.size() ==0){       //current solution is better than previous or there is no previous
                shortestPath = currentPath;                                                                                                         //set all the values to those of the current set
                paths.clear();
                paths.add(currentPath);
                bestScore = scoreCalculator(treasureCalculator(shortestPath), distanceCalculator(shortestPath));
                return;
            }//end of if
            if(scoreCalculator(treasureCalculator(currentPath), distanceCalculator(currentPath)) < bestScore){                                  //Current state is not as good
                return;
              }//end of if
            if(scoreCalculator(treasureCalculator(currentPath), distanceCalculator(currentPath)) == bestScore){                                 //the two are equal
                paths.add(currentPath);
            }//end of if 
            return;
        }//end of if
        int nextState;                                 
        for(int x = 0; x < numberOfNodes ; x++){                                                                //this loops through a whole row of arcs
            if(arcs[currentState][x] != 0 && (currentPath.contains(x) != true)){
                LinkedList<Integer>   nextPath = new LinkedList<Integer>(); 
                    for(int y = 0; y < currentPath.size(); y++){
                        int value = currentPath.get(y);
                        nextPath.offerLast(value);            
                    }//end of for                                    
                nextState = x;               
                nextPath.push(nextState);
                depthFirstSearch(nextState, nextPath);
            }//end of if 
        }//end of for
        return;                                    
    }//end of depthFirstSearch

    //******************************************************************************
    //Method:           printLL
    //Description:	This method prints out LinkedLists of ints and separates
    //                  them with a -.
    //Parameters:	ll - this is the LinkedList that needs to be printed
    //Returns:          none									
    //Throws:           None
    //Calls:            None
    public static void printLL(LinkedList ll) {
        for(int y = ll.size() - 1; y >= 0; y--){
            System.out.print(ll.get(y));
            if(y != 0){
                System.out.print("-");
            }//end of if            
        }//end of for   
        System.out.println();
    }//end of printArray
}//end of public class clynch1
      
//------------------------------------------------------------------------------Ittereative(not eorking)----------------------------------------------------------        
        
//        open.push(currentTable);
//        while(!open.isEmpty()){
//            currentTable = open.removeFirst();                                   //CHANGE BACK TO GET FIRST
//                                                                                System.out.println("*******************************************In While************************************************************");
//                                                                                System.out.println("current state: ");
//                                                                                printTable(currentTable);
//            int goalCheck = tableCompair(currentTable, finalTable);
//            if(goalCheck == 16){
//                                                                                System.out.println("**************************goal****************************************");
//                                                                                //return current path
//                                                                                numberInOpen = open.size();
//                                                                                numberInClosed = closed.size();
//                return;
//            }//end of if
//            else{
//                LinkedList<Tile[][]> newTables = new LinkedList<Tile[][]>();            
//                newTables = makeMove(currentTable);
//                closed.addFirst(currentTable);
//                
//                while(!newTables.isEmpty()){
//                    Tile[][] newTable = newTables.removeLast();
////                                                                                System.out.println("New table");
////                                                                                printTable(newTable);
//                    boolean inOpen = false;
//                    if(open != null){
//                        for(int x = 0; x < open.size(); x++){
//                            Tile[][] tableFromOpen = open.get(x);
//                            int tableCheck = tableCompair(newTable, tableFromOpen);
////                                                                                System.out.println("");
////                                                                                System.out.println("?????????????????????????????????????????????????????????????");
////                                                                                System.out.println("Comparing:");
////                                                                                printTable(newTable);
////                                                                                System.out.println("-------------------------------------------------------------");
////                                                                                printTable(tableFromOpen);
//                            if(tableCheck == 16){
//                                inOpen = true;
//                                break;
//                            }//end of if 
//                        }//end of for
//                    }//end of if
////                                                                                System.out.println("in open " + inOpen);
//                    boolean inClosed = false;
//                    if(closed != null){
//                        for(int x = 0; x < closed.size(); x++){
//                            Tile[][] tableFromClosed = closed.get(x);
//                            int tableCheck = tableCompair(newTable, tableFromClosed);
////                                                                                System.out.println("?????????????????????????????????????????????????????????????");
////                                                                                System.out.println("Comparing:");
////                                                                                printTable(newTable);
////                                                                                System.out.println("-----------------------------------------------------------------");
////                                                                                printTable(tableFromClosed);
//                            if(tableCheck == 16){
//                                inClosed = true;
//                                break;
//                            }//end of if 
//                        }//end of for 
//                    }//end of if 
////                                                                                System.out.println("in closed " + inClosed);
//                    if(!inOpen && !inClosed){
////                        open.addLast(newTable);
//                        open.add(newTable);
////                                                                                System.out.println("Added new table to open ");
////                                                                                printTable(newTable);
//                    }//end of if
//    //                open.add(newTable);
//    //                System.out.println("added new table to open");
//                }//end of while
//                
//            }//end of else
//        }//end of while

//------------------------------------------------------------------------------Recursive(not correct)-----------------------------------------------------------------------------------


//
//        System.out.println("*******************************************In BreathFirst************************************************************");
//        System.out.println("current state: ");
//        printTable(currentTable);
//        int goalCheck = tableCompair(currentTable, finalTable);
//        if(goalCheck == 16){
//            System.out.println("**************************goal****************************************");
//            //return current path
//            numberInOpen = open.size();
//            numberInClosed = closed.size();
//            return;
//        }//end of if
//        if(firstTimeInBreathFirst){                                                                            //puts the first state into the open linked list
//            open.add(currentTable);                                                                             //adds the current table to open
//        }//end of if 
//        System.out.println("Open size" + open.size() );
//        while(!open.isEmpty()){
//            System.out.println("IN while");
//            open.removeFirst();                                                                                 //removes the first table in open
//            LinkedList<Tile[][]> newTables = new LinkedList<Tile[][]>();            
//            newTables = makeMove(currentTable);
//            closed.add(currentTable);
//            System.out.println("closed length " + closed.size());
//            while(!newTables.isEmpty()){
//                Tile[][] newTable = newTables.removeLast();
//                System.out.println("New table");
//                printTable(newTable);
//                boolean inOpen = false;
//                if(open != null){
//                    for(int x = 0; x < open.size(); x++){
//                        Tile[][] tableFromOpen = open.get(x);
//                        int tableCheck = tableCompair(newTable, tableFromOpen);
////                        System.out.println("");
////                        System.out.println("?????????????????????????????????????????????????????????????");
////                        System.out.println("Comparing:");
////                        printTable(newTable);
////                        System.out.println("-------------------------------------------------------------");
////                        printTable(tableFromOpen);
//                        if(tableCheck == 16){
//                            inOpen = true;
//                            break;
//                        }//end of if 
//                    }//end of for
//                }//end of if
//                System.out.println("in open " + inOpen);
//                boolean inClosed = false;
//                if(closed != null){
//                    for(int x = 0; x < closed.size(); x++){
//                        Tile[][] tableFromClosed = closed.get(x);
//                        int tableCheck = tableCompair(newTable, tableFromClosed);
////                        System.out.println("?????????????????????????????????????????????????????????????");
////                        System.out.println("Comparing:");
////                        printTable(newTable);
////                        System.out.println("-----------------------------------------------------------------");
////                        printTable(tableFromClosed);
//                        if(tableCheck == 16){
//                            inClosed = true;
//                            break;
//                        }//end of if 
//                    }//end of for 
//                }//end of if 
////                System.out.println("in closed " + inClosed);
//                if(!inOpen && !inClosed){
//                    open.addFirst(newTable);
////                    open.add(newTable);
//                    System.out.println("Added new table to open ");
//                    printTable(newTable);
//                }//end of if
////                open.add(newTable);
////                System.out.println("added new table to open");
//            }//end of while
//            Tile[][] nextTable = open.peekFirst();                                                              //set the nextTable to the first value in open
//            System.out.println("-----------------------------------------------------------------------------------------------------------");
//            System.out.println("This is the nextTable");
//            printTable(nextTable);
//            breathFirstSearch(nextTable, closed, open);
//        }//end of while
//        
//        System.out.println("did not enter while");
//        return;