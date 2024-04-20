import java.util.*;

public class WordGroup {

    // This class will contain all words of the same string length, which do not contain spaces.
    // It is used by Hangman for guessing the next character.

    public static class Word {
        public final String word;
        public final boolean[] letters;

        public Word(String word) {
            this.word = word;
            this.letters = containsLetters(word);
        }

        private static boolean[] containsLetters(String word) {
            boolean[] letters = new boolean[26];
            for (char c : word.toCharArray()) {
                letters[c - 'a'] = true;
            }
            return letters;
        }
    }

    public ArrayList<Word> words = null;

    // Used to store the frequency of unique letters in the words.
    public short[] frequencyMap = null;

    public WordGroup() {
        words = new ArrayList<>();
        frequencyMap = new short[26];
    }

    // copy constructor
    public WordGroup(WordGroup other) {
        if(other == null) {
            return;
        }
        this.words = new ArrayList<>(other.words);
        frequencyMap = new short[26];
        System.arraycopy(other.frequencyMap, 0, this.frequencyMap, 0, other.frequencyMap.length);
    }


    public void insert(String word) {
        String lower = word.intern();
        Word newWord = new Word(lower);
        words.add(newWord);
        addToFrequencyMap(newWord.letters);
    }

    private void addToFrequencyMap(boolean[] letters) {
        // increment frequencyMap for each letter seen in sequence.
        for (int i = 0; i < 26; i++) {
            if (letters[i]) {
                frequencyMap[i]++;
            }
        }
    }

    private void removeFromFrequencyMap(boolean[] letters) {
        // decrement frequencyMap for each letter seen in sequence.
        for (int i = 0; i < 26; i++) {
            if (letters[i]) {
                frequencyMap[i]--;
            }
        }
    }

    public void processBadLetter(char badLetter) {
        // Iterate through words, removing all which contain badLetter. As they are removed, call removeFromFrequencyMap on them.
        words.removeIf(entry -> {
            if (entry.letters[badLetter - 'a']) {
                removeFromFrequencyMap(entry.letters);
                return true;
            }
            return false;
        });
    }

    public void processGoodPattern(char goodLetter, String pattern) {
        // pattern is a word where spaces are wildcards but there may be lowercase letters as well. These lowercase letters are the only ones that matter.
        // Iterate through words, eliminating all words which do not match the pattern. As they are removed, call removeFromFrequencyMap on them.
        // We can trust that pattern and words are the same length.

        ArrayList<Integer> checkPos = new ArrayList<>();

        // let's first gather up the newly-known goodLetter positions.
        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) == goodLetter) {
                checkPos.add(i);
            }
        }

        // Now we can iterate through the words and remove any that don't match the pattern.
        // use knownLetters to check if the word matches the pattern.
        words.removeIf(entry -> {
            // first, check if the word does not contain goodLetter...
            if (!entry.letters[goodLetter - 'a']) {
                removeFromFrequencyMap(entry.letters);
                return true;
            }
            // If that passes, check if the word doesn't match the pattern.
            for(int pos : checkPos) {
                if(entry.word.charAt(pos) != goodLetter) {
                    removeFromFrequencyMap(entry.letters);
                    return true;
                }
            }
            return false;
        });
    }


    public char getBestGuess(boolean[] guessedLetters) {
        // Using frequencyMap, find the highest index that doesn't appear in guessedLetters. Then generate the character from that index.

        int bestIndex = -1;
        int bestValue = -1;

        for (int i = 0; i < 26; i++) {
            if (!guessedLetters[i] && frequencyMap[i] > bestValue) {
                bestValue = frequencyMap[i];
                bestIndex = i;
            }
        }

        return (char)(bestIndex + 'a');
    }
}
