import java.util.ArrayList;

public class WordGroup {

    // This class will contain all words of the same string length, which do not contain spaces.
    // It is used by Hangman for guessing the next character.

    public ArrayList<byte[]> words = null;

    // Used to store the frequency of unique letters in the words.
    public short[][] frequencyMap = null;

    char bestFirstGuess = '0';

    public WordGroup(int length) {
        words = new ArrayList<>();
        frequencyMap = new short[length][];
        for (int i = 0; i < length; i++) {
            frequencyMap[i] = new short[26];
        }
    }

    // copy constructor
    public WordGroup(WordGroup other) {
        this.words = new ArrayList<>(other.words);
        this.frequencyMap = new short[other.frequencyMap.length][];
        for (int i = 0; i < other.frequencyMap.length; i++) {
            this.frequencyMap[i] = other.frequencyMap[i].clone();
        }
        this.bestFirstGuess = other.bestFirstGuess;
    }

    public void initialize(String currentMask, boolean[] guessedLetters) {
        // Called after all inserts have been made. This will allow us to generate the best guess.
        words.trimToSize();
        bestFirstGuess = getBestGuess(currentMask, guessedLetters);
    }

    public static byte[] stringToByteArray(String word) {
        byte[] result = new byte[word.length()];
        for (int i = 0; i < word.length(); i++) {
            result[i] = (byte)(word.charAt(i) - 'a');
        }
        return result;
    }


    public void insert(String word) {
        byte[] wordBytes = stringToByteArray(word);
        words.add(wordBytes);
        addToFrequencyMap(wordBytes);
    }

    private void addToFrequencyMap(byte[] word) {
        // increment frequencyMap for each letter seen in sequence.
        for (int i = 0; i < word.length; i++) {
            frequencyMap[i][word[i]]++;
        }
    }

    private void removeFromFrequencyMap(byte[] word) {
        // decrement frequencyMap for each letter seen in sequence.
        for (int i = 0; i < word.length; i++) {
            frequencyMap[i][word[i]]--;
        }
    }

    public void processBadLetter(char badLetter) {
        // Iterate through words, removing all which contain badLetter. As they are removed, call removeFromFrequencyMap on them.
        byte checkLetter = (byte)(badLetter - 'a');
        words.removeIf(entry -> {
            for(byte b : entry) {
                if(b == checkLetter) {
                    removeFromFrequencyMap(entry);
                    return true;
                }
            }
            return false;
        });
        words.trimToSize();
    }

    public void processGoodPattern(char goodLetter, String pattern) {
        byte checkLetter = (byte)(goodLetter - 'a');
        boolean[] isGoodPosition = new boolean[pattern.length()];  // Tracks valid positions for checkLetter

        // Mark the positions where goodLetter must appear
        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) == goodLetter) {
                isGoodPosition[i] = true;
            }
        }

        // Filter out words that do not match the exact pattern of goodLetter
        words.removeIf(entry -> {
            for (int i = 0; i < entry.length; i++) {
                // Check if positions of goodLetter in word match the pattern
                if ((entry[i] == checkLetter && !isGoodPosition[i]) || // goodLetter where it shouldn't be
                        (isGoodPosition[i] && entry[i] != checkLetter)) {  // Not goodLetter where it should be
                    removeFromFrequencyMap(entry);
                    return true;  // Remove word as it doesn't match the pattern
                }
            }
            return false;  // Keep the word as it matches the pattern
        });
        words.trimToSize();
    }


    public char getBestGuess(String currentMask, boolean[] guessedLetters) {
        // Using frequencyMap, find the highest index that doesn't appear in guessedLetters. Then generate the character from that index.
        int[] totalFrequency = new int[26];
        for(int i = 0; i < currentMask.length(); i++) {
            if(currentMask.charAt(i) == ' ') {
                for(int j = 0; j < 26; j++) {
                    if(!guessedLetters[j])
                        totalFrequency[j] += frequencyMap[i][j];
                }
            }
        }

        int bestIndex = -1;
        int bestValue = -1;

        for (int i = 0; i < 26; i++) {
            if (totalFrequency[i] > bestValue) {
                bestValue = totalFrequency[i];
                bestIndex = i;
            }
        }

        return (char)(bestIndex + 'a');
    }
}
