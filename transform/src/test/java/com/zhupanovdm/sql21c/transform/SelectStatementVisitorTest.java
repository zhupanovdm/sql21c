package com.zhupanovdm.sql21c.transform;

import com.zhupanovdm.sql21c.transform.model.db.StatementAttribute;
import com.zhupanovdm.sql21c.transform.model.db.StatementTable;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectStatementVisitorTest {
    @Test
    public void statementDataSourceFrom() {
        assertThat(visit("SELECT f FROM t").getTables().stream().map(StatementTable::getName))
                .containsExactly("t");

        assertThat(visit("SELECT a.f FROM t a").getTables().stream().map(StatementTable::getAlias))
                .containsExactly("a");

        assertThat(visit("SELECT a1.f FROM t1 a1, t2 a2").getTables().stream().map(StatementTable::getName))
                .containsExactlyInAnyOrder("t1", "t2");
    }

    @Test
    public void statementDataSourceFromAlias() {
        assertThat(visit("SELECT t1.f FROM t1 a1, t2 a2").getTables().stream().map(StatementTable::getAlias))
                .containsExactlyInAnyOrder("a1", "a2");
    }

    @Test
    public void statementDataSourceFromBraced() {
        assertThat(visit("SELECT 1 FROM [t], [ds.t], ['ds.t2']").getTables().stream().map(StatementTable::getName))
                .containsExactlyInAnyOrder("[t]", "[ds.t]", "['ds.t2']");

        assertThat(visit("SELECT 1 FROM ds.t").getTables().stream().map(StatementTable::getName))
                .containsExactlyInAnyOrder("t");

        assertThat(visit("SELECT 1 FROM [ds].[t]").getTables().stream().map(StatementTable::getName))
                .containsExactlyInAnyOrder("[t]");

        assertThat(visit("SELECT 1 FROM [Документ.СписаниеТоваров], ['Документ.ПоступлениеТоваров']").getTables().stream().map(StatementTable::getName))
                .containsExactlyInAnyOrder("[Документ.СписаниеТоваров]", "['Документ.ПоступлениеТоваров']");
    }

    @Test
    public void statementDataSourceFromBracedAlias() {
        assertThat(visit("SELECT 1 FROM [t] a, [ds.t] b, ['ds.t2'] c").getTables().stream().map(StatementTable::getAlias))
                .containsExactlyInAnyOrder("a", "b", "c");
    }

    @Test
    public void statementDataSourceFromSubSelect() {
        SelectStatementModel model = visit("SELECT a1.f2 fa1 FROM (SELECT TOP 1 a2.f2 FROM t2 a2) a1");
        assertThat(model.getTables()).isEmpty();
        assertThat(model.getSubSelects()).hasSize(1);
        assertThat(model
                .getSubSelects().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f2");
    }

    @Test
    public void statementDataSourceJoin() {
        assertThat(visit("SELECT t1.f1 FROM t1 INNER JOIN t2 ON t1.f1 = t2.f1")
                .getTables().stream().map(StatementTable::getName))
                .containsExactlyInAnyOrder("t1", "t2");

        assertThat(visit("SELECT a1.f1 FROM t1 a1 INNER JOIN t2 a2 ON a1.f1 = a2.f1")
                .getTables().stream().map(StatementTable::getAlias))
                .containsExactlyInAnyOrder("a1", "a2");
    }

    @Test
    public void unknownStatementFields() {
        assertThat(visit("SELECT f FROM t").getUnknownAttributes().stream().map(StatementAttribute::getName))
                .containsExactly("f");

        assertThat(visit("SELECT f FROM t a").getUnknownAttributes().stream().map(StatementAttribute::getName))
                .containsExactly("f");

        assertThat(visit("SELECT f + 1 FROM t a").getUnknownAttributes().stream().map(StatementAttribute::getName))
                .containsExactly("f");

        assertThat(visit("SELECT f FROM t").getTables())
                .allMatch(statementDataSource -> statementDataSource.getAttributes().isEmpty());
    }

    @Test
    public void attributes() {
        assertThat(visit("SELECT t.f1 + (1 + t.f2) * 2 a1 FROM t")
                .getTables().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2");

        assertThat(visit("SELECT t1.f1 FROM t1 INNER JOIN t2 ON t1.f2 = t2.f3 AND t1.f4 = t2.f5")
                .getTables().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2", "f3", "f4", "f5");
    }

    @Test
    public void attributesBraced() {
        assertThat(visit("SELECT [t1].[f1] + (1 + a2.[f2]) * 2 fa FROM [t1], [t2] a2")
                .getTables().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("[f1]", "[f2]");

        assertThat(visit("SELECT t.[f1-a], t.['f1-b'] FROM t")
                .getTables().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("[f1-a]", "['f1-b']");
    }

    @Test
    public void attributesWhere() {
        assertThat(visit("SELECT 1 FROM t WHERE t.f1 + t.f2 * 2 > 500")
                .getTables().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2");
    }

    @Test
    public void attributesGroupBy() {
        assertThat(visit("SELECT COUNT(*) FROM t a GROUP BY a.f1, a.f2")
                .getTables().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2");
    }

    @Test
    public void attributesCaseWhen() {
        assertThat(visit("SELECT CASE WHEN t.f1 IN (1, 2) THEN t.f2 + t.f3 ELSE t.f4 END AS a FROM t")
                .getTables().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2", "f3", "f4");

        assertThat(visit("SELECT 1 FROM t WHERE t.f1 AND CASE WHEN t.f2 IS NULL THEN '' ELSE t.f3 END")
                .getTables().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2", "f3");
    }

    @Test
    public void attributesBetween() {
        assertThat(visit("SELECT 1 as a FROM t WHERE t.f1 BETWEEN t.f2 AND t.f3")
                .getTables().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2", "f3");
    }

    @Test
    public void attributesUnaryStmt() {
        assertThat(visit("SELECT - t.f1 FROM t")
                .getTables().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactly("f1");
    }

    @Test
    public void attributesJoinCondition() {
        assertThat(visit("SELECT 1 FROM t1 a1 INNER JOIN t2 a2 ON a1.f1 = a2.f2 AND a2.f2 = 1")
                .getTables().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2", "f2");
    }

    @Test
    public void attributesOrderBy() {
        assertThat(visit("SELECT 1 FROM t ORDER BY t.f1, t.f2")
                .getTables().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2");
    }

    @Test
    public void attributesHaving() {
        assertThat(visit("SELECT COUNT(*) FROM t a GROUP BY a.f1, a.f2 HAVING sum(t.f3) <> sum(t.f4)")
                .getTables().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2", "f3", "f4");
    }

    @Test
    public void attributesVariableAssignmentExpr() {
        assertThat(visit("SELECT @PARAM1 = f1 FROM t a")
                .getUnknownAttributes().stream().map(StatementAttribute::getName))
                .containsExactly("f1");
    }

    @Test
    public void attributesCastArgument() {
        assertThat(visit("SELECT CAST(a.f1 AS NUMERIC(21, 3)) AS fa1 FROM t a")
                .getTables().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1");
    }

    @Test
    public void testAttributesInnerStatement() {
        SelectStatementModel model = visit("SELECT (SELECT TOP 1 a2.f2 FROM t2 a2) fa1");

        assertThat(model.getTables()).isEmpty();
        assertThat(model.getSubSelects()).hasSize(1);

        assertThat(model.getSubSelects().stream().flatMap(subSelect -> subSelect.getModel().getTables().stream()).map(StatementTable::getName))
                .containsExactly("t2");
        assertThat(model.getSubSelects().stream().flatMap(subSelect -> subSelect.getModel().getTables().stream()).map(StatementTable::getAlias))
                .containsExactly("a2");
        assertThat(model.getSubSelects().stream()
                .flatMap(subSelect -> subSelect.getModel().getTables().stream())
                .flatMap(table -> table.getAttributes().stream()).map(StatementAttribute::getName))
                .containsExactly("f2");
    }

    @Test
    public void testExistsExprInnerStatement() {
        SelectStatementModel model = visit("SELECT 1 WHERE EXISTS(SELECT TOP 1 1 FROM t2)");

        assertThat(model.getTables()).isEmpty();
        assertThat(model.getSubSelects()).hasSize(1);
    }

    @Test
    public void testAllColumns() {
        SelectStatementModel model = visit("SELECT * FROM t");

        assertThat(model.getTables()).hasSize(1);
        assertThat(model.getSubSelects()).isEmpty();
    }

    private SelectStatementModel visit(String statement) {
        SelectStatementVisitor visitor = new SelectStatementVisitor();
        new SqlSelectStatementParser(statement).parse(visitor);
        return visitor.getModel();
    }
}
