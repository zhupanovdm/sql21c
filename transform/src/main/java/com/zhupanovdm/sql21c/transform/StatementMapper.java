package com.zhupanovdm.sql21c.transform;

import com.zhupanovdm.sql21c.transform.model.db.StatementAttribute;
import com.zhupanovdm.sql21c.transform.model.db.StatementDataSource;
import com.zhupanovdm.sql21c.transform.model.mapping.AttributeMap;
import com.zhupanovdm.sql21c.transform.model.mapping.EntityMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.zhupanovdm.sql21c.transform.ParserUtils.toDboName;
import static com.zhupanovdm.sql21c.transform.ParserUtils.toEntityName;

public class StatementMapper {
    private final EntityMapRepo repo;

    public StatementMapper(EntityMapRepo repo) {
        this.repo = repo;
    }

    public void map(StatementModel model) {
        List<StatementAttribute> unknown = new ArrayList<>(model.getUnknownFields());
        model.getDataSources().forEach(withMap(doMap(unknown)));
    }

    private Consumer<StatementDataSource> withMap(BiConsumer<StatementDataSource, EntityMap> action) {
        return ds -> repo.findByTable(toEntityName(ds.getName())).ifPresent(map -> action.accept(ds, map));
    }

    private Consumer<StatementAttribute> withMap(EntityMap map, BiConsumer<StatementAttribute, AttributeMap> action) {
        return attr -> map.findByName(toEntityName(attr.getName())).ifPresent(attributeMap -> action.accept(attr, attributeMap));
    }

    private BiConsumer<StatementDataSource, EntityMap> doMap(List<StatementAttribute> unknown) {
        return (ds, map) -> {
            Optional.ofNullable(map.getEntity()).ifPresent(entity -> ds.setName(toDboName(entity)));
            ds.getAttributes().forEach(withMap(map, doMap()));
            for (int i = unknown.size() - 1; i >= 0; i--) {
                mapUnknown(unknown, map, i);
            }
        };
    }

    private BiConsumer<StatementAttribute, AttributeMap> doMap() {
        return (attr, map) -> Optional.ofNullable(map.getField()).ifPresent(s -> attr.setName(toDboName(s)));
    }

    private static void mapUnknown(List<StatementAttribute> attributes, EntityMap entityMap, int i) {
        StatementAttribute attribute = attributes.get(i);
        entityMap.findByName(toEntityName(attribute.getName())).ifPresent(attrMap -> {
            Optional.ofNullable(attrMap.getField()).ifPresent(field -> attribute.setName(toDboName(field)));
            attributes.remove(i);
        });
    }

    public static StatementMapper withFileRepo(String repoFileName) {
        return new StatementMapper(EntityMapRepo.fromFile(repoFileName));
    }
}
