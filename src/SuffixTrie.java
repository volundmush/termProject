import java.util.HashMap;

public class SuffixTrie extends Trie {

    @Override
    public void insert(String word) {
        for (int i = 0; i < word.length(); i++) {
            super.insert(word.substring(i));
        }
    }

    public char getBestGuess(String pattern, boolean[] guessedLetters) {
        HashMap<Character, Integer> frequencyMap = new HashMap<>();

        // Start from the root and search the tree based on the pattern
        Node current = root;
        int start = 0; // Start index of the current segment
        while (start < pattern.length()) {
            int end = start;
            // Find the end of the current segment of known characters
            while (end < pattern.length() && pattern.charAt(end) != ' ') {
                end++;
            }

            // If there's a segment to process (i.e., not just a bunch of wildcards)
            if (end > start) {
                String segment = pattern.substring(start, end);
                current = navigateToNode(current, segment);
                if (current == null) break; // No such segment exists, break early
            }

            // Collect potential guesses from this point if the current node is valid
            if (current != null) {
                collectPotentialGuesses(current, frequencyMap, guessedLetters);
            }

            // Move start past the current known segment and the following wildcards
            start = end;
            while (start < pattern.length() && pattern.charAt(start) == ' ') {
                start++;
            }
        }

        // Find the best guess based on collected frequencies
        return findMostFrequentLetter(frequencyMap, guessedLetters);
    }

    private Node navigateToNode(Node current, String segment) {
        for (int i = 0; i < segment.length(); i++) {
            char ch = segment.charAt(i);
            if(current.children == null) return null;
            if (current.children.containsKey(String.valueOf(ch))) {
                current = current.children.get(String.valueOf(ch));
            } else {
                return null; // Segment not found
            }
        }
        return current;
    }

    private void collectPotentialGuesses(Node node, HashMap<Character, Integer> frequencyMap, boolean[] guessedLetters) {
        if(node.children == null) return;
        for (Node child : node.children.values()) {
            char nextLetter = child.value.charAt(0);
            if (!guessedLetters[nextLetter - 'a']) {
                frequencyMap.put(nextLetter, frequencyMap.getOrDefault(nextLetter, 0) + child.count);
            }
        }
    }

    private char findMostFrequentLetter(HashMap<Character, Integer> frequencyMap, boolean[] guessedLetters) {
        int maxFrequency = -1;
        char bestGuess = '?'; // Default if no valid guess found

        for (char letter = 'a'; letter <= 'z'; letter++) {
            if (!guessedLetters[letter - 'a'] && frequencyMap.containsKey(letter) && frequencyMap.get(letter) > maxFrequency) {
                bestGuess = letter;
                maxFrequency = frequencyMap.get(letter);
            }
        }

        return bestGuess;
    }
}
