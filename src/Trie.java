import java.util.*;

public class Trie {

    private record CharFrequency(char character, int frequency) implements Comparable<CharFrequency> {

        @Override
        public int compareTo(CharFrequency other) {
            // Sort in descending order of frequency
            return Integer.compare(other.frequency, this.frequency);
        }
    }

    public static class TrieNode {
        HashMap<String, TrieNode> children;
        String value;
        TrieNode parent;

        public TrieNode(String value, TrieNode parent) {
            this.parent = parent;
            this.value = value;
        }

        public String toWord() {
            StringBuilder sb = new StringBuilder();
            TrieNode node = this;
            // Go up the tree to build the word.
            while(true) {
                sb.insert(0, node.value);
                node = node.parent;
                if(node.parent == null) {
                    break;
                }
            }
            return sb.toString();
        }

    }

    public record TriePair(String word, Trie.TrieNode node) {}

    private final TrieNode root;

    public HashMap<Integer, ArrayList<TrieNode>> endNodesByDepth = new HashMap<>();

    public Trie() {
        root = new TrieNode(null,null);
    }

    public void insert(String word) {
        TrieNode node = root;
        TrieNode lastNode = null;
        int charIndex = 0;

        while (charIndex < word.length()) {
            lastNode = node; // Keep track of the last node for adding new nodes

            // Find the longest prefix that matches the start of the remaining word
            String remainingWord = word.substring(charIndex).intern(); // Intern the substring
            String longestPrefix = "";
            if(node.children != null) {
                for (String key : node.children.keySet()) {
                    if (remainingWord.startsWith(key)) {
                        longestPrefix = key;
                        break; // Break as soon as the longest prefix is found
                    }
                }
            }

            if (!longestPrefix.isEmpty()) {
                // A matching prefix is found, jump to the corresponding node
                node = node.children.get(longestPrefix);
                charIndex += longestPrefix.length();
            } else {
                // No longer prefix matches, need to add a new node
                break;
            }
        }

        if (charIndex < word.length()) {
            String suffix = word.substring(charIndex).intern(); // Intern the suffix
            node = new TrieNode(suffix, lastNode);
            if (lastNode.children == null) {
                lastNode.children = new HashMap<>();
            }
            lastNode.children.put(suffix, node);
        }

        // Mark the end of the word and add to endNodesByDepth
        endNodesByDepth.computeIfAbsent(word.length(), k -> new ArrayList<>()).add(node);
    }

    public ArrayList<TriePair> getPossibleEndNodes(int length) {
        ArrayList<TriePair> possibleEndNodes = new ArrayList<>();

        for(TrieNode node : endNodesByDepth.get(length)) {
            possibleEndNodes.add(new TriePair(node.toWord(), node));
        }

        return possibleEndNodes;
    }

    public ArrayList<Character> getSortedFrequencyList(ArrayList<TriePair> possibleEndNodes, boolean[] guessedLetters) {
        int[] frequencyArray = new int[26]; // 26 letters in the alphabet

        // Count frequencies of characters in remaining possible end nodes
        for (TriePair p : possibleEndNodes) {
            for (char c : p.word().toCharArray()) {
                if (!guessedLetters[c - 'a']) {
                    frequencyArray[c - 'a']++;
                }
            }
        }

        // Prepare a list of CharFrequency objects for sorting
        List<CharFrequency> freqList = new ArrayList<>();
        for (int i = 0; i < frequencyArray.length; i++) {
            if (frequencyArray[i] > 0) {
                freqList.add(new CharFrequency((char) (i + 'a'), frequencyArray[i]));
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
