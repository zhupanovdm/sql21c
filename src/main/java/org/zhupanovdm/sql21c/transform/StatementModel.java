package org.zhupanovdm.sql21c.transform;

import org.zhupanovdm.sql21c.transform.model.db.StatementAttribute;
import org.zhupanovdm.sql21c.transform.model.db.StatementDataSource;

import java.util.Collection;
import java.util.Set;

public interface StatementModel {
    Collection<StatementDataSource> getDataSources();
    Set<StatementAttribute> getUnknownStatementFields();
}
