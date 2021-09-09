package org.zhupanovdm.sql21c.transform.model.mapping;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
public class EntityMap {
    private String table;
    private String entity;

    private List<AttributeMap> attributes = new LinkedList<>();

    public Optional<AttributeMap> findByName(String attrName) {
        return attributes.stream()
                .filter(map -> map.getName().equalsIgnoreCase(attrName))
                .findFirst();
    }
}
