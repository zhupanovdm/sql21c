package org.zhupanovdm.sql21c.transform;

import org.junit.Test;
import org.zhupanovdm.sql21c.transform.model.db.StatementAttribute;
import org.zhupanovdm.sql21c.transform.model.db.StatementDataSource;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectStatementDataSourceExtractorTest {

    @Test
    public void testSingleEntity() {
        assertThat(extract("SELECT f FROM t").getDataSources().stream().map(StatementDataSource::getName))
                .containsExactly("t");

        assertThat(extract("SELECT a1.f FROM t1 a1, t2").getDataSources().stream().map(StatementDataSource::getName))
                .containsExactlyInAnyOrder("t1", "t2");

        assertThat(extract("SELECT a.f FROM t a").getDataSources().stream().map(StatementDataSource::getAlias))
                .containsExactly("a");
    }

    @Test
    public void testSourceJoin() {
        assertThat(extract("SELECT t1.f1 FROM t1 INNER JOIN t2 ON t1.f1 = t2.f1")
                .getDataSources().stream().map(StatementDataSource::getName))
                .containsExactlyInAnyOrder("t1", "t2");

        assertThat(extract("SELECT a1.f1 FROM t1 a1 INNER JOIN t2 a2 ON a1.f1 = a2.f1")
                .getDataSources().stream().map(StatementDataSource::getAlias))
                .containsExactlyInAnyOrder("a1", "a2");
    }

    @Test
    public void testUnknownAttributes() {
        assertThat(extract("SELECT f FROM t").getUnknownStatementFields())
                .containsExactly("f");

        assertThat(extract("SELECT f FROM t a").getUnknownStatementFields())
                .containsExactly("f");

        assertThat(extract("SELECT f + 1 FROM t a").getUnknownStatementFields())
                .containsExactly("f");

        assertThat(extract("SELECT f FROM t").getDataSources())
                .allMatch(statementDataSource -> statementDataSource.getAttributes().isEmpty());
    }

    @Test
    public void testAttributes() {
        assertThat(extract("SELECT t.f1 + (1 + t.f2) * 2 a1 FROM t")
                .getDataSources().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2");

        assertThat(extract("SELECT t1.f1 FROM t1 INNER JOIN t2 ON t1.f2 = t2.f3 AND t1.f4 = t2.f5")
                .getDataSources().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2", "f3", "f4", "f5");
    }

    @Test
    public void testAttributesWhere() {
        assertThat(extract("SELECT 1 FROM t WHERE t.f1 + t.f2 * 2 > 500")
                .getDataSources().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2");
    }

    @Test
    public void testAttributesGroupBy() {
        assertThat(extract("SELECT COUNT(*) FROM t a GROUP BY a.f1, a.f2")
                .getDataSources().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2");
    }

    @Test
    public void testAttributesCaseExpression() {
        assertThat(extract("SELECT CASE WHEN t.f1 IN (1, 2) THEN t.f2 + t.f3 ELSE t.f4 END AS a FROM t")
                .getDataSources().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2", "f3", "f4");
    }

    @Test
    public void testAttributesBetweenExpression() {
        assertThat(extract("SELECT 1 as a FROM t WHERE t.f1 BETWEEN t.f2 AND t.f3")
                .getDataSources().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2", "f3");
    }

    @Test
    public void testAttributesOrderBy() {
        assertThat(extract("SELECT 1 FROM t ORDER BY t.f1, t.f2")
                .getDataSources().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2");
    }

    @Test
    public void testAttributesCase() {
        assertThat(extract("SELECT 1 FROM t WHERE t.f1 AND CASE WHEN t.f2 IS NULL THEN '' ELSE t.f3 END")
                .getDataSources().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2", "f3");
    }

    @Test
    public void testAttributesInnerStatement() {
        // TODO: Inner select statement
    }

    private SelectEntityExtractor extract(String statement) {
        SelectEntityExtractor extractor = new SelectEntityExtractor();
        new SelectParser(statement).parse(extractor);
        return extractor;
    }

}
