package dict;

import java.io.*;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;


/**
 * Implements a persistent dictionary that can be held entirely in memory.
 * When flushed, it writes the entire dictionary back to a file.
 * <p>
 * The file format has one keyword per line:
 * <pre>word:def</pre>
 * <p>
 * Note that an empty definition list is allowed (in which case the entry would have the form: <pre>word:</pre>
 *
 * @author talm
 */
public class InMemoryDictionary extends TreeMap<String, String> implements PersistentDictionary {
    private static final long serialVersionUID = 1L; // (because we're extending a serializable class)
    private File dictFile;

    public InMemoryDictionary(File dictFile) throws IOException {
        super();
        this.dictFile = dictFile;
        open();
    }

    @Override
    public void open() throws IOException {
        clear();

        if (!dictFile.exists()) { // if file doesn't exist break
            return;
        }
        String line;
        FileReader fileReader = new FileReader(dictFile);
        BufferedReader reader = new BufferedReader(fileReader);

        while ((line = reader.readLine()) != null) { // while we have lines to read
            String[] parts = line.split(":", 2); // split by ":"

            // place parts in dictionary
            if (parts.length == 2) {
                String key = parts[0];
                String value = parts[1];
                put(key, value);
            }
        }
    }


    @Override
    public void close() throws IOException {
        FileWriter fileWriter = new FileWriter(dictFile);
        BufferedWriter writer = new BufferedWriter(fileWriter);

        for (Map.Entry<String, String> entry : entrySet()) { //for each entry, write key and value
            String line = entry.getKey() + ":" + entry.getValue();
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }
}
