package com.zhupanovdm.sql21c.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhupanovdm.sql21c.transform.model.mapping.EntityMap;
import com.zhupanovdm.sql21c.transform.model.mapping.TableMappingFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityMapRepo {

    private final ObjectMapper mapper = new ObjectMapper();
    private List<EntityMap> repo;
    private Map<String, EntityMap> tableMap;

    public EntityMap findByTable(String table) {
        return tableMap.get(table.toLowerCase());
    }

    public void load(Path path) {
        TableMappingFile mappingFile;
        try {
            mappingFile = mapper.readValue(path.toFile(), TableMappingFile.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read table mapping file.", e);
        }
        tableMap = new HashMap<>();
        repo = mappingFile.getMapping();
        for (EntityMap em : repo) {
            tableMap.put(em.getTable().toLowerCase(), em);
        }
    }

}
