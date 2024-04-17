/*

  Authors (group members): Andrew Bastien, Christian Stevens, Ryan LaPolt
  Email addresses of group members: abastien2021@my.fit.edu, <missing>, <missing>
  Group name: 23a

  Course: CSE 2010
  Section: 23

  Description of the overall algorithm:
     Uses a Trie data structure where each node is of variable length of prefix.
     All words are loaded into the Trie. Nodes where a word ends are indexed by word length.
     The most frequently found characters in the remaining possible end nodes are guessed first.

*/


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class HangmanPlayer
{

    // Since the only thing we know about our given word is the length, we can store all words of
    // a given length in a hashmap

    private HashMap<Integer, WordGroup> wordsByLength = new HashMap<>();

    private Runtime runtime = null;

    private class GameState {
        boolean[] guessedLetters = new boolean[26];
        WordGroup wordGroup = null;
        ArrayList<Character> freq = null;

        Character lastGuess = null;

        public GameState(String currentWord) {
            WordGroup prev = wordsByLength.get(currentWord.length());
            wordGroup = new WordGroup(prev);
            freq = wordGroup.getSortedFrequencyList(guessedLetters);
        }
    }

    private GameState gameState = null;


    // initialize HangmanPlayer with a file of English words
    public HangmanPlayer(String wordFile)
    {
        this.runtime = Runtime.getRuntime();
        try {
            FileReader hiddenWordFile = new FileReader(wordFile);
            BufferedReader input = new BufferedReader(hiddenWordFile);
            String line;
            runtime.gc(); // Suggest a garbage collection
            long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
            while ((line = input.readLine()) != null) {
                // insert into wordsByLength's wordgroup based on line's string length.
                int length = line.length();
                wordsByLength.computeIfAbsent(length, k -> new WordGroup()).insert(line);
            }
            long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
            System.out.println("Memory used: " + (usedMemoryAfter - usedMemoryBefore));
        } catch (Exception e) {
            System.out.println("Error: " + e);

        }
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
            gameState = new GameState(currentWord);
        }

        int index = 0;
        // while character at index is in guessedLetters, increase...
        while(gameState.guessedLetters[gameState.freq.get(index) - 'a']) {
            index++;
        }

        gameState.lastGuess = gameState.freq.get(index);
        gameState.guessedLetters[gameState.lastGuess - 'a'] = true;
        return gameState.lastGuess;
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
        int count = gameState.wordGroup.words.size();
        if(isCorrectGuess) {
            gameState.wordGroup.processGoodPattern(currentWord);
        } else {
            // Iterate through GameState.possibleEndNodes and remove any nodes that contain the bad letter in the key.
            gameState.wordGroup.processBadLetter(gameState.lastGuess);
        }
        int afterCount = gameState.wordGroup.words.size();
        if(afterCount == 0) {
            System.out.println("No words found.");

        }

        // Regenerate our frequency list.
        if(afterCount != count) {
            gameState.freq = gameState.wordGroup.getSortedFrequencyList(gameState.guessedLetters);
        }

    }

}
