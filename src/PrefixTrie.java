import java.util.ArrayList;
import java.util.HashMap;

public class PrefixTrie extends Trie {

    public HashMap<Integer, ArrayList<Node>> endNodes = new HashMap<>();

    @Override
    public void insert(String word) {
        Node inserted = root.insert(word);
        if (inserted != null) {
            if (!endNodes.containsKey(word.length())) {
                endNodes.put(word.length(), new ArrayList<>());
            }
            endNodes.get(word.length()).add(inserted);
        }
    }
}
