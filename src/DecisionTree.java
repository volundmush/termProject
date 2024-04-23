import java.util.HashMap;

public class DecisionTree {
    public static class Node {
        public HashMap<String, Node> children;

        public final byte letter;
        public Node no;

        public Node(byte letter) {
            this.letter = letter;
        }

        private void processGuess(String word, StringBuilder mask, WordGroup group, int goodGuesses, int badGuesses, boolean[] guessedLetters) {
            boolean isCorrect = false;
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == letter) {
                    isCorrect = true;
                    mask.setCharAt(i, (char) letter);
                }
            }
            // we solved the word so just return.
            if(mask.indexOf(" ") == -1) {
                return;
            }
            WordGroup newGroup = new WordGroup(group);
            guessedLetters[letter - 'a'] = true;

            if(isCorrect) {
                goodGuesses++;
                String newMask = mask.toString().intern();
                newGroup.processGoodPattern((char) letter, newMask);
                char nextBestGuess = newGroup.getBestGuess(guessedLetters);
                // we have a new mask;
                if(children == null) {
                    children = new HashMap<>();
                }
                if(children.containsKey(newMask)) {
                    children.get(newMask).processGuess(word, mask, newGroup, goodGuesses, badGuesses, guessedLetters);
                } else {
                    Node child = new Node((byte) nextBestGuess);
                    children.put(newMask, child);
                    child.processGuess(word, mask, newGroup, goodGuesses, badGuesses, guessedLetters);
                }
            } else {
                badGuesses++;
                // we have a bad guess;
                if(badGuesses >= 6) {
                    // bad end, no need to go further.
                    return;
                }
                newGroup.processBadLetter((char) letter);
                char nextBestGuess = newGroup.getBestGuess(guessedLetters);
                if(no == null) {
                    no = new Node((byte) nextBestGuess);
                }
                no.processGuess(word, mask, newGroup, goodGuesses, badGuesses, guessedLetters);
            }
        }
    }

    public Node root;

    private StringBuilder stringToMask(String word) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat(word.length()));
        return sb;
    }

    public DecisionTree(WordGroup group, int length) {
        root = new Node((byte) group.bestFirstGuess);
        for(byte[] bytes : group.words) {
            StringBuilder sb = new StringBuilder();
            for(byte b : bytes) {
                sb.append((char)(b + 'a'));
            }
            String word = sb.toString();
            root.processGuess(word, stringToMask(word), group,0, 0, new boolean[26]);
        }
    }
}