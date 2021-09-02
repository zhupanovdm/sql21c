package org.zhupanovdm.sql21c.transform;

import org.zhupanovdm.sql21c.transform.model.db.StatementDataSource;

import java.util.Collection;

public interface StatementModel {

    Collection<StatementDataSource> getDataSources();
    Collection<String> getUnknownStatementFields();

}
