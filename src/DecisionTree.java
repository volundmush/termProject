import java.util.ArrayList;

public class DecisionTree {
    public static class Node {
        public final char value;
        public Node yes;
        public Node no;
        public Node parent;

        public Node(Node parent, char value) {
            this.parent = parent;
            this.value = value;
        }

        private void buildTree(WordGroup group, int length, int depth, int goodGuesses, int badGuesses, boolean[] guessedLetters) {
            // GoodGuesses depth depends on the frequencyMap generated at each stage; however, we are only allowed 6 bad guesses.
            // After 6 bad guesses, cut off those branches.
            WordGroup yesGroup = new WordGroup(group);
            yesGroup.processGoodLetter(value);
            ArrayList<Character> yesGuessed = yesGroup.getSortedFrequencyList(guessedLetters);
            if (!yesGuessed.isEmpty() && badGuesses < 6) {
                yes = new Node(this, yesGuessed.get(0));
                guessedLetters[yesGuessed.get(0) - 'a'] = true;
                yes.buildTree(yesGroup, length, depth + 1, goodGuesses + 1, badGuesses, guessedLetters);
                guessedLetters[yesGuessed.get(0) - 'a'] = false;
            }

            WordGroup noGroup = new WordGroup(group);
            noGroup.processBadLetter(value);
            ArrayList<Character> noGuessed = noGroup.getSortedFrequencyList(guessedLetters);
            if (!noGuessed.isEmpty() && badGuesses < 6) {
                no = new Node(this, noGuessed.get(0));
                guessedLetters[noGuessed.get(0) - 'a'] = true;
                no.buildTree(noGroup, length, depth + 1, goodGuesses, badGuesses + 1, guessedLetters);
                guessedLetters[noGuessed.get(0) - 'a'] = false;
            }
        }
    }

    public Node root;

    public DecisionTree(WordGroup group, int length) {
        boolean[] guessedLetters = new boolean[26];
        ArrayList<Character> guessed = group.getSortedFrequencyList(guessedLetters);
        root = new Node(null, guessed.get(0));
        guessedLetters[guessed.get(0) - 'a'] = true;
        root.buildTree(group, length, 0, 0, 0, guessedLetters);
    }
}
