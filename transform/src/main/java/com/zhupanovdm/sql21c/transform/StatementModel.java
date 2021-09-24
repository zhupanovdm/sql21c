package com.zhupanovdm.sql21c.transform;

import com.zhupanovdm.sql21c.transform.model.db.StatementAttribute;
import com.zhupanovdm.sql21c.transform.model.db.StatementDataSource;

import java.util.Collection;
import java.util.Set;

public interface StatementModel {
    Collection<StatementDataSource> getDataSources();
    Set<StatementAttribute> getUnknownStatementFields();
}
