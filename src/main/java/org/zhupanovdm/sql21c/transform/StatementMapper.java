package org.zhupanovdm.sql21c.transform;

import org.zhupanovdm.sql21c.transform.model.db.StatementAttribute;
import org.zhupanovdm.sql21c.transform.model.db.StatementDataSource;
import org.zhupanovdm.sql21c.transform.model.mapping.AttributeMap;
import org.zhupanovdm.sql21c.transform.model.mapping.EntityMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.zhupanovdm.sql21c.transform.ParserUtils.toDboName;
import static org.zhupanovdm.sql21c.transform.ParserUtils.toEntityName;

public class StatementMapper {
    private final EntityMapRepo repo;

    public StatementMapper(EntityMapRepo repo) {
        this.repo = repo;
    }

    public void map(StatementModel model) {
        List<StatementAttribute> unknown = new ArrayList<>(model.getUnknownStatementFields());
        for (StatementDataSource dataSource : model.getDataSources()) {
            EntityMap entityMap = repo.findByTable(toEntityName(dataSource.getName()));
            if (entityMap != null) {
                dataSource.setName(toDboName(entityMap.getEntity()));

                for (StatementAttribute attribute : dataSource.getAttributes()) {
                    entityMap.findByName(toEntityName(attribute.getName()))
                            .ifPresent(attrMap -> attribute.setName(toDboName(attrMap.getField())));
                }

                for (int i = unknown.size() - 1; i >= 0; i--) {
                    StatementAttribute attribute = unknown.get(i);
                    Optional<AttributeMap> map = entityMap.findByName(toEntityName(attribute.getName()));
                    if (map.isPresent()) {
                        attribute.setName(toDboName(map.get().getField()));
                        unknown.remove(i);
                    }
                }
            }
        }

    }
}
