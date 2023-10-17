package org.spss.toucananticheat.Blocks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Blocks {
    private static List<String> blocks;
    private final static List<Integer> ores = new ArrayList<>(Arrays.asList(69, 70, 71, 72, 73, 74, 75, 263, 264, 3410, 3411, 3952, 3953, 3954, 3955, 5455, 5456, 6933, 17818, 17819));

    public static String idToString(int id) {
        if (blocks == null) {
            blocks = new ArrayList<>();
            try {
                InputStream inputStream = Blocks.class.getResourceAsStream("/blocks.json");
                String data = readFromInputStream(inputStream);
                JsonObject jsonObj = new JsonParser().parse(data)
                        .getAsJsonObject();
                for (Entry<String, JsonElement> jsonEntries : jsonObj.entrySet()) {
                    JsonArray states = jsonObj.get(jsonEntries.getKey()).getAsJsonObject().get("states")
                            .getAsJsonArray();
                    for (int i = 0; i < states.size(); i++) {
                        blocks.add(jsonEntries.getKey());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return blocks.get(id);
    }

    public static boolean isOre(int id) {
        return ores.contains(id);
    }

    private static String readFromInputStream(InputStream inputStream)
            throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }
}