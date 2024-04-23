import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class InternedStrings {
    public static byte[] data; // The large block of memory storing all strings

    public static ArrayList<Interned> intern(List<String> words) {
        words.sort((a, b) -> b.length() - a.length()); // Sort by descending length

        StringBuilder sb = new StringBuilder();
        HashMap<String, Interned> seen = new HashMap<>(); // Map to store seen strings and their indices

        for (String word : words) {
            int pos = sb.indexOf(word); // Check if already a substring
            if (pos == -1) { // If not found, add it
                pos = sb.length();
                sb.append(word);
            }
            seen.put(word, new Interned(pos, (byte) word.length())); // Store the string and its index
        }

        data = sb.toString().getBytes(); // Convert all concatenated strings to bytes
        return new ArrayList<>(seen.values());
    }

    public static class Interned {
        public final int start;
        public final byte length;

        public Interned(int start, byte length) {
            this.start = start;
            this.length = length;
        }
    }
}
