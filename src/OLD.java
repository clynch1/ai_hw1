
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
public class OLD {
    //******************************************************************************
    //Method:       main
    //Description:  This method calls all other methods.  
    //Parameters:   none
    //Returns:      userSelection 									
    //Throws:       None
    //Calls:        keyboardInput class, TextFileClass 
    
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
    public static int                   child;                                                          //this is the current child 
    public static double[][]            arcs;                                                           //this is the lengths of all arcs 
    public static double[]              treasure;                                                       //this is the treasure value
    //public static Stack<Integer>        closed = new Stack<Integer>();                                  //this is the nodes that I have finalized 
    //public static Stack<Integer>        working = new Stack<Integer>();                                 //this is the to use while searching the graph 
    //public static Stack<Double>        distance = new Stack<Double>();                                  //this holds the distance corresponding to the nodes in working
    public static LinkedList<Integer>   closed = new LinkedList<Integer>();                             //this is the nodes that I have finalized 
    public static LinkedList<Integer>   working = new LinkedList<Integer>();                            //this is the to use while searching the graph
    public static LinkedList<Integer>   currentChildren = new LinkedList<Integer>();                    //this is the the children of the current State
    public static LinkedList<Integer>   shortestPath = new LinkedList<Integer>();                       //this is the linked list to store the current shortest path
    public static LinkedList            visitedNodes;                                                   //this is the linked list to store nodes that have been visited
    //public static int                   currentState;                                                   //this is the current state
    
            
    public static void main(String[] args) {

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
        
        if(depthFirstSearch(startState) == 1){
            System.out.println("This is the shortest path" + shortestPath);
            System.out.println("The distance is: " + largestDistance);
            System.out.println("The treasure is: " + largestTreasure);
            System.out.println("This is the best score: " + bestScore);
        } else {
            System.out.println("There is no path.");
        }//end of else


    }//end of public static void main
    
    //******************************************************************************
    //Method:       scoreCalculator
    //Description:  This method calculates the weight for the passed in values.  
    //Parameters:   location = this is the treasure location whose weight needs 
    //                      to be calculated.
    //Returns:       									
    //Throws:       
    //Calls:        treasure[], distanceWeight, treasureWeight
    public static double scoreCalculator(double treasure, double distance) {
        double score = (treasure * treasureWeight) - (distance * distanceWeight); 
        
        return score;
    }//end of weightCalculator
    
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
    //Method:       deaothFirstSearch
    //Description:   
    //Parameters:   location = this is the treasure location whose weight needs 
    //                      to be calculated.
    //Returns:       									
    //Throws:       
    //Calls:        treasure[], distanceWeight, treasureWeight
    public static int depthFirstSearch(int currentState) {
        LinkedList<Integer> arranged = new LinkedList<Integer>();                                               //this is to use while arranging working
        currentChildren  = new LinkedList<Integer>();                                                           //this is the current children of currentState
        System.out.println("");
        System.out.println("IN depthfirst");
        
        for(int x = numberOfNodes -1; x >= 0; x--){
            if(arcs[currentState][x] != 0){
                currentChildren.push(x);                
            }//end of if 
        }//end of for
        

        
        if(currentState == endState){
            closed.push(currentState);
            
            if(shortestPath.size() == 0){
                shortestPath = closed; 
                largestDistance = distanceCalculator(shortestPath);
                largestTreasure = treasureCalculator(shortestPath);
                bestScore = scoreCalculator(largestTreasure, largestDistance);
            }//end of if
            else{
                if(scoreCalculator(treasureCalculator(closed), distanceCalculator(closed)) < bestScore){
                    //go back to choice
                }//end of if
                if(scoreCalculator(treasureCalculator(closed), distanceCalculator(closed)) > bestScore){
                    shortestPath = closed; 
                    largestDistance = distanceCalculator(shortestPath);
                    largestTreasure = treasureCalculator(shortestPath);
                    bestScore = scoreCalculator(largestTreasure, largestDistance);
                }//end of if
            }//end of else 

            //add a method to check for them being the same
            return 1;
        }//end of if 
        else{ 
            closed.push(currentState);  
            
            System.out.println("This is closed: " + closed);
            System.out.println("This is working: " + working);

             

            while(llContains(closed) != currentChildren.size()){                                                    //currentState has children that are not in closed
                System.out.println(currentState + " has children that are not in closed.");                                                                
                if(llContains(working) > 0){                                                                        //child of currentState is in working                                                                               
                    System.out.println("There is a child of current state: " + currentState + " in working ");
                    if(llContains(working) == currentChildren.size()){                                              //check to see if all children are in working
                        System.out.println("All children are in working");
                        while(llContains(working) != 0){                                                            //there are children in working, take them out and place them in arranged 
                            int child = childSearch(currentState, working, 0);                                      //find the child
                            int index = llSearch(working, child);                                                   //find the index of child in working                      
                            working.remove(index);                                                                  //remove child from working
                            System.out.println("Removed " + child);
                            arranged.push(child);                                                                   //add child to arraned
                        }//end of while 
                        while(arranged != null){                                                                    //while there are elements in arranged take them out and put them into working
                            working.push(arranged.getLast());                                                       //push the last node on arranged into working
                            arranged.removeLast();                                                                  //remove the last node of arranged
                        }//end of while                 
                    }//end of if
                    else{                                                                                           //there is at least one child in working
                        while(llContains(working) != 0){                                                            //there are children in working, take them out and place them in arranged 
                            System.out.println("Not all children are in working");
                            int child = childSearch(currentState, working, 0);                                      //find the child
                            System.out.println("This is the child in working: " + child);
                            int index = llSearch(working, child);                                                   //find the index of child in working
                            System.out.println("Workinglength is: " + working.size());
                            System.out.println("This is the index for child: " + index);
                            working.remove(index);                                                                  //remove child from working
                        }//end of while 
                        
                        for(int x = numberOfNodes -1; x >= 0; x--){                                                 //this finds the children and placed the onto working in decending order
                            if(arcs[currentState][x] != 0 && llCheck(working, x) != 1 && llCheck(closed, x) != 1){  //push children to working in lowest ID on top order
                                working.push(x);                
                                System.out.println("Found Child at:" + x);
                            }//end of if 
                        }//end of for  
                        System.out.println("This is working: " + working);
                    }//end of else
                        
                }//end of if
                else{                                                                                               //there are no children in working
                    System.out.println("There are not children of " + currentState + " in working.");
                    for(int x = numberOfNodes -1; x >= 0; x--){
                        if(arcs[currentState][x] != 0 && llCheck(working, x) != 1 && llCheck(closed, x) != 1){      //push children to working in lowest ID on top order
                            working.push(x);                
                            System.out.println("Found Child at:" + x);
                        }//end of if 
                    }//end of for 
                    System.out.println("This is working: " + working);
                }//end of else
                child = working.pop();                                                                              //this needs to be pulling a child off this node
                if(llCheck(closed, child) != 1){
                    if(depthFirstSearch(child) == 1){
                        return 1;
                    }
                }//end of if
            }//end of while
            return 0;
        }//end of else

    }//end of depthFirstSearch
    
    //******************************************************************************
    //Method:           childSearch
    //Description:	This method Finds the children of the nodes that is passed
    //                  in.  It checks to see if the node has already been visited
    //                  If the node has been visited it looks for other children.  
    //                  If it finds a child it return the child.  If it does not 
    //                  find any elegable children it return a flag.
    //Parameters:	node - this is the node whose children are searched for
    //Returns:          toReturn - this can be two types of things:
    //                      100000 - this is a flag case, meaning there are no 
    //                      valid children to choose
    //                      x - this is the child found for the passed in node.
    //Throws:       None
    //Calls:        numberOfNodes, stackCheck, arcs[][], working, currentDistance
    public static int childSearch(int node, LinkedList<Integer> ll, int searchType){
        //System.out.println("Searching for childredn of node: " + node);
        
        int foundChildren = 100000;                                                                      //this is the default return value for no children       

        
        for(int x = numberOfNodes -1; x >= 0; x--){                                                     //this loops through a whole row of arcs
            if(arcs[node][x] != 0 && llCheck(ll, x) != searchType){                                     //if the loaction in the array is not 0 and it is: 0 = contains no children, 1 = contains children
                foundChildren = x;                                                                      //return child found
            }//end of if 
        }//end of for
        return foundChildren;
    }//end of child search}

    public static int llCheck(LinkedList ll, int child) {
        if(ll != null && ll.contains(child)){
                return 1;
        }//end of if
        else{
            return 100000;
        }//end of else        
    }//end of llCheck
    
    public static int llContains(LinkedList toSearch) {
        //System.out.println("In llContains");
        int counter = 0;
        //System.out.println("This is the size of currentChildren: " + currentChildren.size());
        for(int x = currentChildren.size() -1; x >= 0; x--){
            //System.out.println("child to be found at: " + x);
            int childToBeFound = currentChildren.get(x);
            //System.out.println("Child to be found is: " + childToBeFound);
            if(toSearch != null && toSearch.contains(childToBeFound)){
                //System.out.println("The child was found");
                counter ++;
            }//end of if
        }//end of for loop
           return counter;        
    }//end of llCheck
    
    public static int llSearch(LinkedList ll, int child) {
        if(ll != null && ll.contains(child)){
                return ll.indexOf(child);
        }//end of if
        else{
            return 0;
        }//end of else        
    }//end of llSearch
    
    //******************************************************************************
    //Method:           stackCheck
    //Description:	This method checks to see if a passed in value is in the
    //                  passed in stack.
    //Parameters:	stack - this is the stack that needs to be searched 
    //                  child - this is what is being searched for 
    //Returns:          int - this returns two cases:
    //                      0 - this means that the number is not in the stack
    //                      1 - this means that the stack is either null, or the
    //                      child is already in the stack
    //Throws:       None
    //Calls:        None
    public static int stackCheck(Stack stack, int child) {
        if(stack != null && stack.contains(child)){
                return 1;
        }//end of if
        else{
            return 0;
        }//end of else        
    }//end of llCheck
    
    //******************************************************************************
    //Method:           printArray
    //Description:	This method prints out the the user's array
    //Parameters:	length - this is the length of the array.  This is passed in so 
    //                  that the for loop knows how many times to loop.
    //                  userArray - this is the array made from the text file, this is
    //                  passed in to be printed out
    //Returns:	userSelection 									
    //Throws:       None
    //Calls:        None
    public static void printArray() {
        for(int y = 0; y < numberOfNodes; y++){
            System.out.println("This is y: " + y);
            for(int x = 0; x < numberOfNodes; x++){
                System.out.println("This is x: " + x);
                 System.out.println(arcs[y][x]);
            }//end of for
        }//end of for loop
    }//end of printArray
        
//    //******************************************************************************
//    //Method:           childSearch
//    //Description:	This method Finds the children of the nodes that is passed
//    //                  in.  It checks to see if the node has already been visited
//    //                  If the node has been visited it looks for other children.  
//    //                  If it finds a child it return the child.  If it does not 
//    //                  find any elegable children it return a flag.
//    //Parameters:	node - this is the node whose children are searched for
//    //Returns:          toReturn - this can be two types of things:
//    //                      100000 - this is a flag case, meaning there are no 
//    //                      valid children to choose
//    //                      x - this is the child found for the passed in node.
//    //Throws:       None
//    //Calls:        numberOfNodes, stackCheck, arcs[][], working, currentDistance
//    public static int childAssign(int node){
//        System.out.println("Searching for childredn of node: " + node);
//        
//        int foundChildren = 0;
//        
//        for(int x = numberOfNodes -1; x >= 0; x--){
//            if(arcs[node][x] != 0 && stackCheck(working, x) != 1 && stackCheck(closed, x) != 1){
//                working.push(x);                
//                System.out.println("Found Child at:" + x);
//                distance.push(arcs[node][x]);
//                System.out.println("Distance for x: " + x + " is: " + arcs[node][x]);
//                foundChildren = 1;
//            }//end of if 
//        }//end of for
//        return foundChildren;
//    }//end of child search}
//    
}//end of public class clynch1
