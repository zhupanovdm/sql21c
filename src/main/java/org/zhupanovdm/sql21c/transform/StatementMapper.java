package org.zhupanovdm.sql21c.transform;

import org.zhupanovdm.sql21c.transform.model.db.StatementAttribute;
import org.zhupanovdm.sql21c.transform.model.db.StatementDataSource;
import org.zhupanovdm.sql21c.transform.model.mapping.EntityMap;

import java.util.NoSuchElementException;

import static org.zhupanovdm.sql21c.transform.ParserUtils.withoutBraces;

public class StatementMapper {

    private final EntityMapRepo repo;

    public StatementMapper(EntityMapRepo repo) {
        this.repo = repo;
    }

    public void map(StatementModel model) {
        for (StatementDataSource dataSource : model.getDataSources()) {
            EntityMap entityMap = repo.findByTable(withoutBraces(dataSource.getName()));
            if (entityMap != null) {
                dataSource.setName(entityMap.getEntity());

                for (StatementAttribute attribute : dataSource.getAttributes()) {
                    attribute.setName(entityMap.getAttributes()
                            .stream()
                            .filter(attributeMap -> attributeMap.getName().equals(withoutBraces(attribute.getName())))
                            .findFirst()
                            .orElseThrow(() -> new NoSuchElementException("Cannot map " + attribute.getFullyQualifiedName()))
                                    .getField());
                }
            }
        }
    }

}
