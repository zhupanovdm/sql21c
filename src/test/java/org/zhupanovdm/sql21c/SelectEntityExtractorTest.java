package org.zhupanovdm.sql21c;

import org.junit.Test;
import org.zhupanovdm.sql21c.model.Entity;
import org.zhupanovdm.sql21c.model.EntityAttribute;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectEntityExtractorTest {

    @Test
    public void testSingleEntity() {
        assertThat(extract("SELECT f from t").getAllEntities().stream().map(Entity::getName))
                .containsExactly("t");

        assertThat(extract("SELECT a1.f from t1 a1, t2").getAllEntities().stream().map(Entity::getName))
                .containsExactlyInAnyOrder("t1", "t2");

        assertThat(extract("SELECT a.f from t a").getAllEntities().stream().map(Entity::getAlias))
                .containsExactly("a");
    }

    @Test
    public void testSourceJoin() {
        assertThat(extract("SELECT t1.f1 from t1 INNER JOIN t2 ON t1.f1 = t2.f1")
                .getAllEntities().stream().map(Entity::getName))
                .containsExactlyInAnyOrder("t1", "t2");

        assertThat(extract("SELECT a1.f1 from t1 a1 INNER JOIN t2 a2 ON a1.f1 = a2.f1")
                .getAllEntities().stream().map(Entity::getAlias))
                .containsExactlyInAnyOrder("a1", "a2");
    }

    @Test
    public void testUnknownAttributes() {
        assertThat(extract("SELECT f from t").getUnknownAttributes())
                .containsExactly("f");

        assertThat(extract("SELECT f from t a").getUnknownAttributes())
                .containsExactly("f");

        assertThat(extract("SELECT f + 1 from t a").getUnknownAttributes())
                .containsExactly("f");

        assertThat(extract("SELECT f from t").getAllEntities())
                .allMatch(entity -> entity.getAttributes().isEmpty());
    }

    @Test
    public void testAttributes() {
        assertThat(extract("SELECT t.f1 + (1 + t.f2) * 2 a1 from t")
                .getAllEntities().stream().flatMap(entity -> entity.getAttributes().stream().map(EntityAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2");

        assertThat(extract("SELECT t1.f1 from t1 INNER JOIN t2 ON t1.f2 = t2.f3")
                .getAllEntities().stream().flatMap(entity -> entity.getAttributes().stream().map(EntityAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2", "f3");
    }

    @Test
    public void testAttributesWhere() {
        assertThat(extract("SELECT 1 from t WHERE t.f1 + t.f2 * 2 > 500")
                .getAllEntities().stream().flatMap(entity -> entity.getAttributes().stream().map(EntityAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2");
    }

    @Test
    public void testAttributesGroupBy() {
        assertThat(extract("SELECT COUNT(*) from t a GROUP BY a.f1, a.f2")
                .getAllEntities().stream().flatMap(entity -> entity.getAttributes().stream().map(EntityAttribute::getName)))
                .containsExactlyInAnyOrder("f1", "f2");
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
