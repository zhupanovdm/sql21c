package com.zhupanovdm.sql21c.transform;

import com.zhupanovdm.sql21c.transform.model.db.StatementAttribute;
import com.zhupanovdm.sql21c.transform.model.db.StatementTable;
import com.zhupanovdm.sql21c.transform.model.mapping.AttributeMap;
import com.zhupanovdm.sql21c.transform.model.mapping.EntityMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.zhupanovdm.sql21c.transform.ParserUtils.toDboName;
import static com.zhupanovdm.sql21c.transform.ParserUtils.toEntityName;

public class StatementMapper {
    private final EntityMapRepo repo;

    public StatementMapper(EntityMapRepo repo) {
        this.repo = repo;
    }

    public void map(SelectStatementModel model) {
        List<StatementAttribute> unknown = new ArrayList<>(model.getUnknownAttributes());

        model.getSubSelects().forEach(subSelect -> map(subSelect.getModel()));

        model.getTables().forEach(table -> {
            Optional<EntityMap> found = repo.findByTable(toEntityName(table.getName()));
            found.ifPresent(entityMap -> doMap(table, entityMap, unknown));
        });
    }

    private static void doMap(StatementTable table, EntityMap entityMap, List<StatementAttribute> unknown) {
        String entity = entityMap.getEntity();
        if (entity != null) {
            table.setName(toDboName(entity));
        }

        table.getAttributes().forEach(attribute -> {
            Optional<AttributeMap> found = entityMap.findByName(toEntityName(attribute.getName()));
            found.ifPresent(attributeMap -> doMap(attribute, attributeMap));
        });

        for (int i = unknown.size() - 1; i >= 0; i--) {
            doMap(unknown, i, entityMap);
        }
    }

    private static void doMap(StatementAttribute attribute, AttributeMap attributeMap) {
        String field = attributeMap.getField();
        if (field != null) {
            attribute.setName(toDboName(field));
        }
    }

    private static void doMap(List<StatementAttribute> attributes, int i, EntityMap entityMap) {
        StatementAttribute attribute = attributes.get(i);
        entityMap.findByName(toEntityName(attribute.getName())).ifPresent(attributeMap -> {
            doMap(attribute, attributeMap);
            attributes.remove(i);
        });
    }

    public static StatementMapper withFileRepo(String repoFileName) {
        return new StatementMapper(EntityMapRepo.fromFile(repoFileName));
    }
}
