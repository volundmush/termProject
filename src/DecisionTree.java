import java.util.HashMap;

public class DecisionTree {
    public static class Node {
        public HashMap<String, Node> children;

        public final char letter;
        public Node no;
        public Node parent;

        public Node(Node parent, char letter) {
            this.parent = parent;
            this.letter = letter;
        }

        private void processGuess(String word, String mask, WordGroup group, int goodGuesses, int badGuesses, boolean[] guessedLetters) {
            boolean isCorrect = false;
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == letter) {
                    isCorrect = true;
                    mask = mask.substring(0, i) + letter + mask.substring(i + 1);
                }
            }
            // we solved the word so just return.
            if(mask.equals(word)) {
                return;
            }
            WordGroup newGroup = new WordGroup(group);
            guessedLetters[letter - 'a'] = true;
            if(isCorrect) {
                goodGuesses++;
                newGroup.processGoodPattern(letter, mask);
                char nextBestGuess = newGroup.getBestGuess(guessedLetters);
                // we have a new mask;
                if(children == null) {
                    children = new HashMap<>();
                }
                if(children.containsKey(mask)) {
                    children.get(mask).processGuess(word, mask, newGroup, goodGuesses, badGuesses, guessedLetters);
                } else {
                    Node child = new Node(this, nextBestGuess);
                    children.put(mask, child);
                    child.processGuess(word, mask, newGroup, goodGuesses, badGuesses, guessedLetters);
                }
            } else {
                badGuesses++;
                // we have a bad guess;
                if(badGuesses >= 6) {
                    // bad end, no need to go further.
                    return;
                }
                newGroup.processBadLetter(letter);
                char nextBestGuess = newGroup.getBestGuess(guessedLetters);
                if(no == null) {
                    no = new Node(this, nextBestGuess);
                }
                no.processGuess(word, mask, newGroup, goodGuesses, badGuesses, guessedLetters);
            }
        }
    }

    public Node root;

    private String stringToMask(String word) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public DecisionTree(WordGroup group, int length) {
        root = new Node(null, group.bestFirstGuess);
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