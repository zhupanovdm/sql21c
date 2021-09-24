package com.zhupanovdm.sql21c.transform;

import com.zhupanovdm.sql21c.transform.model.db.StatementAttribute;
import com.zhupanovdm.sql21c.transform.model.db.StatementDataSource;
import com.zhupanovdm.sql21c.transform.model.mapping.EntityMap;
import com.zhupanovdm.sql21c.transform.model.mapping.AttributeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StatementMapper {
    private final EntityMapRepo repo;

    public StatementMapper(EntityMapRepo repo) {
        this.repo = repo;
    }

    public void map(StatementModel model) {
        List<StatementAttribute> unknown = new ArrayList<>(model.getUnknownFields());
        for (StatementDataSource dataSource : model.getDataSources()) {
            EntityMap entityMap = repo.findByTable(ParserUtils.toEntityName(dataSource.getName()));
            if (entityMap != null) {
                dataSource.setName(ParserUtils.toDboName(entityMap.getEntity()));

                for (StatementAttribute attribute : dataSource.getAttributes()) {
                    entityMap.findByName(ParserUtils.toEntityName(attribute.getName()))
                            .ifPresent(attrMap -> attribute.setName(ParserUtils.toDboName(attrMap.getField())));
                }

                for (int i = unknown.size() - 1; i >= 0; i--) {
                    StatementAttribute attribute = unknown.get(i);
                    Optional<AttributeMap> map = entityMap.findByName(ParserUtils.toEntityName(attribute.getName()));
                    if (map.isPresent()) {
                        attribute.setName(ParserUtils.toDboName(map.get().getField()));
                        unknown.remove(i);
                    }
                }
            }
        }
    }

    public static StatementMapper withFileRepo(String repoFileName) {
        return new StatementMapper(EntityMapRepo.fromFile(repoFileName));
    }
}
