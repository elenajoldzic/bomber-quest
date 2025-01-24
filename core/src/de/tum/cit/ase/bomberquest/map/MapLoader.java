package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.*;

public class MapLoader {

    public static final int INDESTRUCTIBLE_WALL = 0;
    public static final int DESTRUCTIBLE_WALL = 1;
    public static final int ENTRANCE = 2;
    public static final int ENEMY = 3;
    public static final int EXIT = 4;
    public static final int BOMB_POWER_UP = 5;
    public static final int BLAST_RADIUS_POWER_UP = 6;

    // Updated to store multiple object types per coordinate
    private final Map<String, Set<Integer>> mapData = new HashMap<>();

    /**
     * Loads a map file from a given file path and parses it.
     *
     * @param filePath The path to the map file.
     */
    public void loadMap(String filePath) {
        FileHandle fileHandle = Gdx.files.absolute(filePath);

        if (!fileHandle.exists()) {
            throw new RuntimeException("Map file not found: " + filePath);
        }

        String mapContent = fileHandle.readString();
        String[] lines = mapContent.split("\n");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue; // Skip empty lines and comments
            }

            String[] keyValue = line.split("=");
            if (keyValue.length != 2) {
                throw new IllegalArgumentException("Invalid map line: " + line);
            }

            String coordinates = keyValue[0].trim();
            int objectType = Integer.parseInt(keyValue[1].trim());

            // Add the object type to the set for this coordinate
            mapData.computeIfAbsent(coordinates, k -> new HashSet<>()).add(objectType);
        }

        ensurePowerUpsHidden();
        // Ensure there is at least one exit
        ensureExit();
    }

    /**
     * Ensures there is at least one exit in the map by placing it under a random destructible wall.
     * The destructible wall is preserved.
     */
    private void ensureExit() {
        boolean exitExists = mapData.values().stream()
                .anyMatch(objects -> objects.contains(EXIT));

        if (!exitExists) {
            List<String> destructibleWalls = new ArrayList<>();
            for (Map.Entry<String, Set<Integer>> entry : mapData.entrySet()) {
                if (entry.getValue().contains(DESTRUCTIBLE_WALL)) {
                    destructibleWalls.add(entry.getKey());
                }
            }

            if (destructibleWalls.isEmpty()) {
                throw new RuntimeException("No destructible walls found to place an exit!");
            }

            Random random = new Random();
            String randomWall = destructibleWalls.get(random.nextInt(destructibleWalls.size()));
            mapData.get(randomWall).add(EXIT); // Add exit without removing the destructible wall
        }
    }

    /**
     * Ensures that every power-up is hidden underneath a destructible wall.
     */
    private void ensurePowerUpsHidden() {
        for (Map.Entry<String, Set<Integer>> entry : mapData.entrySet()) {
            Set<Integer> objectTypes = entry.getValue();

            // If there is any power-up, add a destructible wall
            if (objectTypes.contains(BOMB_POWER_UP) || objectTypes.contains(BLAST_RADIUS_POWER_UP)) {
                objectTypes.add(DESTRUCTIBLE_WALL);
            }
        }
    }

    /**
     * Returns the parsed map data.
     *
     * @return A map of coordinates to sets of object types.
     */
    public Map<String, Set<Integer>> getMapData() {
        return mapData;
    }
}
