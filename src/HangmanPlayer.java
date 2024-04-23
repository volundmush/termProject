/*

  Authors (group members): Andrew Bastien, Christian Stevens, Ryan LaPolt
  Email addresses of group members: abastien2021@my.fit.edu, cstevens2023@my.fit.edu, rlapolt2022@my.fit.edu
  Group name: 23a (Gallows Humor)

  Course: CSE 2010
  Section: 23

  Description of the overall algorithm:
     Groups input words into different ArrayLists by string length, using a helper class called WordGroup.
     Each WordGroup contains a frequencyMap short[26] array that stores the frequency of each unique letter in the words.

     When a new Word is selected, a GameState is created for it which copies the WordGroup for the known string length.

     Guesses are based off of most frequent letters in the WordGroup's frequencyMap that have yet to be guessed.
     On good and bad guesses, incorrect candidates are filtered out of the words list and the frequencyMap is updated.

     The GameState keeps track of guessed letters and holds on to the (modified) WordGroup for the current word.

*/


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class HangmanPlayer
{

    // Since the only thing we know about our given word is the length, we can store all words of
    // a given length in a hashmap
    private HashMap<Integer, DecisionTree> decisionTrees = new HashMap<>();

    private DecisionTree.Node currentNode;

    private Runtime runtime = null;

    // initialize HangmanPlayer with a file of English words
    public HangmanPlayer(String wordFile)
    {
        this.runtime = Runtime.getRuntime();
        HashSet<String> knownWords = new HashSet<>();
        HashMap<Integer, WordGroup> wordsByLength = new HashMap<>();
        try {
            FileReader hiddenWordFile = new FileReader(wordFile);
            BufferedReader input = new BufferedReader(hiddenWordFile);
            String line;
            runtime.gc();
            while ((line = input.readLine()) != null) {
                // insert into wordsByLength's wordgroup based on line's string length.
                line = line.toLowerCase();
                int length = line.length();
                if(knownWords.contains(line)) {
                    continue;
                }
                wordsByLength.computeIfAbsent(length, k -> new WordGroup()).insert(line);
                knownWords.add(line);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);

        }

        // Initialize all WordGroups
        // let's create a threadpool to initialize all the wordgroups
        ArrayList<Thread> threads = new ArrayList<>();
        boolean[] guessedLetters = new boolean[26];
        for(HashMap.Entry<Integer, WordGroup> entry : wordsByLength.entrySet()) {
            threads.add(new Thread(() -> {
                entry.getValue().initialize(guessedLetters);
                decisionTrees.put(entry.getKey(), new DecisionTree(entry.getValue(), entry.getKey()));
                System.out.println("WordGroup of length " + entry.getKey() + " initialized.");
            }));
        }
        for(Thread t : threads) {
            t.start();
        }
        for(Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.out.println("Error: " + e);
            }
        }
        threads.clear();
        wordsByLength.clear();
        runtime.gc();
    }

    // based on the current (partial or intitially blank) word
    //    guess a letter
    // currentWord: current word, currenWord.length has the length of the hidden word
    // isNewWord: indicates a new hidden word
    // returns the guessed letter
    // assume all letters are in lower case
    public char guess(String currentWord, boolean isNewWord)
    {
        if(isNewWord) {
            this.currentNode = decisionTrees.get(currentWord.length()).root;
        }
        return (char) this.currentNode.letter;
    }

    // feedback on the guessed letter
    // isCorrectGuess: true if the guessed letter is one of the letters in the hidden word
    // currentWord: partially filled or blank word
    //
    // Case       isCorrectGuess      currentWord
    // a.         true                partial word with the guessed letter
    //                                   or the whole word if the guessed letter was the
    //                                   last letter needed
    // b.         false               partial word without the guessed letter
    public void feedback(boolean isCorrectGuess, String currentWord)
    {
        if(isCorrectGuess) {
            // We solved it!
            if(currentWord.indexOf(' ') == -1) {
                return;
            }
            this.currentNode = this.currentNode.children.get(currentWord);
        } else {
            this.currentNode = this.currentNode.no;
        }
    }

}
