package com.zhupanovdm.sql21c.transform.model.db;

import com.zhupanovdm.sql21c.transform.SelectStatementModel;
import com.zhupanovdm.sql21c.transform.SelectStatementVisitor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.sf.jsqlparser.statement.select.SubSelect;

@EqualsAndHashCode(of = "subSelect", callSuper = false)
public class StatementSubSelect extends StatementDataSource {
    private final SubSelect subSelect;

    @Getter
    private final SelectStatementModel model;

    public StatementSubSelect(SubSelect subSelect, SelectStatementModel parent) {
        this.model = new SelectStatementModel(parent);
        this.subSelect = subSelect;
        this.subSelect.getSelectBody().accept(new SelectStatementVisitor(this.model));
    }

    @Override
    public String getAlias() {
        return subSelect.getAlias() == null ? null : subSelect.getAlias().getName();
    }

    @Override
    public String toString() {
        return "StatementSubSelect";
    }
}
