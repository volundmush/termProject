import java.util.HashMap;

public class Trie {
    public static class Node {
        public HashMap<String, Node> children;
        public Node parent;
        public String value;
        public int count = 0;  // Initialize count to 0; it increments when a word/suffix exactly ends here.

        public Node(Node parent, String value) {
            this.parent = parent;
            this.value = value;
        }

        public Node insert(String suffix) {
            Node current = this;
            Node createdNode = null;  // To keep track of the deepest new node created

            while (!suffix.isEmpty()) {
                Node next = null;
                String nextKey = null;
                int longestMatch = 0;

                if (current.children == null) {
                    current.children = new HashMap<>();
                }

                // Find the longest prefix match in the children
                for (HashMap.Entry<String, Node> entry : current.children.entrySet()) {
                    String key = entry.getKey();
                    int matchLength = getMatchLength(suffix, key);
                    if (matchLength > longestMatch) {
                        longestMatch = matchLength;
                        nextKey = key;
                        next = entry.getValue();
                    }
                }

                if (nextKey == null) {
                    // No match, so add a new node for this suffix
                    Node newNode = new Node(current, suffix);
                    newNode.count = 1;  // Starting a new node, a new suffix is completely inserted here.
                    current.children.put(suffix, newNode);
                    return newNode;  // Return this new node as it is the deepest created
                } else if (longestMatch == nextKey.length() && longestMatch == suffix.length()) {
                    // Exact match, just update the count
                    next.count++;
                    return null;  // No new node created, return null
                } else if (longestMatch < nextKey.length()) {
                    // Partial match, need to split the node
                    Node splitNode = new Node(current, nextKey.substring(0, longestMatch));
                    current.children.put(splitNode.value, splitNode);
                    current.children.remove(nextKey);

                    Node existingSuffixNode = new Node(splitNode, nextKey.substring(longestMatch));
                    existingSuffixNode.children = next.children;  // Carry over the children
                    existingSuffixNode.count = next.count;        // Carry over the count
                    splitNode.children = new HashMap<>();
                    splitNode.children.put(existingSuffixNode.value, existingSuffixNode);

                    next.parent = splitNode;

                    if (longestMatch < suffix.length()) {
                        String remainder = suffix.substring(longestMatch);
                        Node remainderNode = new Node(splitNode, remainder);
                        remainderNode.count = 1;  // New suffix being added.
                        splitNode.children.put(remainder, remainderNode);
                        return remainderNode;  // Return this new node as it is the deepest created
                    } else {
                        splitNode.count = 1; // The split node now represents the end of the suffix.
                        return splitNode;  // Return the split node as it is the new node representing the end
                    }
                } else {
                    // Continue down the tree
                    current = next;
                    suffix = suffix.substring(longestMatch);
                }
            }
            return createdNode;  // Return the last created node if any
        }

        private int getMatchLength(String suffix, String key) {
            int max = Math.min(suffix.length(), key.length());
            for (int i = 0; i < max; i++) {
                if (suffix.charAt(i) != key.charAt(i)) {
                    return i;
                }
            }
            return max;
        }
    }

    public Node root = new Node(null, "");

    public void insert(String word) {
        //
    }

    public String getFullWord(Node leaf) {
        StringBuilder sb = new StringBuilder();
        // walk up the parentage to get the full word
        while (leaf != null) {
            sb.insert(0, leaf.value);
            leaf = leaf.parent;
        }
        return sb.toString();
    }

    public char getBestBasicGuess(boolean[] guessedLetters) {
        // Using frequencyMap, find the highest index that doesn't appear in guessedLetters. Then generate the character from that index.
        int[] frequencyMap = new int[26];

        for(Node child : root.children.values()) {
            frequencyMap[child.value.charAt(0) - 'a'] = child.count;
        }

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
