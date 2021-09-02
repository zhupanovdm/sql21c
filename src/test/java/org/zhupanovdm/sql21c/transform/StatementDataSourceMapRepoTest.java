package org.zhupanovdm.sql21c.transform;

import org.junit.Test;
import org.zhupanovdm.sql21c.transform.model.mapping.AttributeMap;
import org.zhupanovdm.sql21c.transform.model.mapping.EntityMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.zhupanovdm.sql21c.transform.TestUtils.resourcePath;

public class StatementDataSourceMapRepoTest {

    @Test
    public void test() {
        EntityMapRepo repo = new EntityMapRepo();
        repo.load(resourcePath("samples/tableMapping01.json"));

        EntityMap table1 = repo.findByTable("table1");
        assertThat(table1.getTable()).isEqualTo("table1");
        assertThat(table1.getEntity()).isEqualTo("entity1");

        assertThat(table1.getAttributes().stream().map(AttributeMap::getName))
                .containsExactlyInAnyOrder("name1", "name2");

        assertThat(table1.getAttributes().stream().map(AttributeMap::getField))
                .containsExactlyInAnyOrder("field1", "field2");
    }

}