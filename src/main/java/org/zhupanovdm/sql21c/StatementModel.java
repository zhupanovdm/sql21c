package org.zhupanovdm.sql21c;

import org.zhupanovdm.sql21c.model.db.StatementDataSource;

import java.util.Collection;

public interface StatementModel {

    Collection<StatementDataSource> getDataSources();
    Collection<String> getUnknownStatementFields();

}
