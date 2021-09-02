package org.zhupanovdm.sql21c.model.mapping;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
public class EntityMap {
    private String table;
    private String entity;

    private List<AttributeMap> attributes = new LinkedList<>();
}
