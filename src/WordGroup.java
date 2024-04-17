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
        this.words = new ArrayList<>(other.words);
        frequencyMap = new short[26];
        System.arraycopy(other.frequencyMap, 0, this.frequencyMap, 0, other.frequencyMap.length);
    }


    public void insert(String word) {
        String lower = word.toLowerCase().intern();
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

    public void processGoodPattern(String pattern) {
        // pattern is a word where spaces are wildcards but there may be lowercase letters as well. These lowercase letters are the only ones that matter.
        // Iterate through words, eliminating all words which do not match the pattern. As they are removed, call removeFromFrequencyMap on them.
        // We can trust that pattern and words are the same length.

        // let's first gather up a HashMap of known letters and their positions.
        HashMap<Integer, Character> knownLetters = new HashMap<>();
        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) != ' ') {
                knownLetters.put(i, pattern.charAt(i));
            }
        }

        // Now we can iterate through the words and remove any that don't match the pattern.
        // use knownLetters to check if the word matches the pattern.
        words.removeIf(entry -> {
            for(Map.Entry<Integer, Character> entry2 : knownLetters.entrySet()) {
                if(entry.word.charAt(entry2.getKey()) != entry2.getValue()) {
                    removeFromFrequencyMap(entry.letters);
                    return true;
                }
            }
            return false;
        });
    }

    private static class CharFrequency implements Comparable<CharFrequency> {

        public final char character;
        public final int frequency;

        public CharFrequency(char character, int frequency) {
            this.character = character;
            this.frequency = frequency;
        }

        @Override
        public int compareTo(CharFrequency other) {
            // Sort in descending order of frequency
            return Integer.compare(other.frequency, this.frequency);
        }
    }

    public ArrayList<Character> getSortedFrequencyList(boolean[] guessedLetters) {

        // Prepare a list of CharFrequency objects for sorting
        List<CharFrequency> freqList = new ArrayList<>();
        for (int i = 0; i < frequencyMap.length; i++) {
            if (frequencyMap[i] > 0) {
                freqList.add(new CharFrequency((char) (i + 'a'), frequencyMap[i]));
            }
        }

        // Sort by frequency in descending order
        Collections.sort(freqList);

        // Convert sorted frequencies to a list of characters
        ArrayList<Character> sortedCharacters = new ArrayList<>();
        for (CharFrequency cf : freqList) {
            sortedCharacters.add(cf.character);
        }

        return sortedCharacters;
    }
}
