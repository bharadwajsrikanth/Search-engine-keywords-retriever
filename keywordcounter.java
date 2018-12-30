import java.io.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class keywordcounter {
    public static void main(String[] args){

        String inputFile = args[0];

        String searchKeyword, readLine;
        Integer searchKeywordCount, queryNumber;

        File outputFile = new File("output_file.txt");
        BufferedWriter bufferedWriter=null;

        HashMap<String, Node> keywordsMap = new HashMap<String, Node>();
        FibonacciHeap keywordsHeap = new FibonacciHeap();

        try {

            //Read the input file
            FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            bufferedWriter = new BufferedWriter( new FileWriter(outputFile));

            //Set regex patterns for search keyword and the query
            Pattern inputPattern = Pattern.compile("([$])([a-zA-Z0-9\\-#\\.\\(\\)\\/%&]+\\s)([0-9]+)");
            Pattern inputQuery = Pattern.compile("[0-9]+");

            //Read each line in the input file
            while((readLine = bufferedReader.readLine()) != null) {

                //Get the matcher of the line read from the input file
                Matcher inputPatternMatcher = inputPattern.matcher(readLine);
                Matcher inputQueryMatcher = inputQuery.matcher(readLine);

                if(inputPatternMatcher.find()){ //if the line is keyword and its count
                    searchKeyword = inputPatternMatcher.group(2); //separate out keyword
                    searchKeywordCount = Integer.parseInt(inputPatternMatcher.group(3)); //separate out the count

                    //if keyword is not added in the hashtable, add it. else increment the search count
                    if(!keywordsMap.containsKey(searchKeyword)){
                        Node newNode = new Node(searchKeyword, searchKeywordCount);
                        keywordsHeap.insert(newNode);
                        keywordsMap.put(searchKeyword, newNode);
                    }else{
                        Node curNode = keywordsMap.get(searchKeyword);
                        keywordsHeap.increaseKeywordCount(curNode, searchKeywordCount);
                    }

                }
                else if(inputQueryMatcher.find()){ //if the line is query
                    queryNumber = Integer.parseInt(inputQueryMatcher.group(0)); //get the query
                    String[] removedNodesKeywords = new String[queryNumber];

                    for(int i = 0; i < queryNumber; i++){ //get query number of keywords by doing removemax from the heap
                        Node root = keywordsHeap.removeMax();
                        if(root == null){
                            System.out.println("Search query is larger than the number of available keywords.");
                            System.exit(0);
                        }
                        else if(i != queryNumber - 1){
                            bufferedWriter.write(root.searchKeyword + ","); //log the keyword to the output file
                        }
                        else
                            bufferedWriter.write(root.searchKeyword);
                        removedNodesKeywords[i] = root.searchKeyword;
                    }
                    bufferedWriter.newLine();
                    for(String keyword : removedNodesKeywords){
                        Node node = keywordsMap.get(keyword);
                        node.degree = 0;
                        node.child = null;
                        keywordsHeap.insert(node); //Add the removed keyword back to the heap
                    }

                }
                else {
                    break;
                }
            }
        }
        //handling exceptions
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            inputFile + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + inputFile + "'");

        }
        try{
            bufferedWriter.close();
            System.out.println("Results are logged in file \"" + outputFile + "\"");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error writing file '"
                            + outputFile + "'");

        }
    }
}
