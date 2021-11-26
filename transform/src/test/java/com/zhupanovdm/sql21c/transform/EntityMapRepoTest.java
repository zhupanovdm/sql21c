package com.zhupanovdm.sql21c.transform;

import org.junit.Test;
import com.zhupanovdm.sql21c.transform.model.mapping.AttributeMap;
import com.zhupanovdm.sql21c.transform.model.mapping.EntityMap;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static com.zhupanovdm.sql21c.transform.TestUtils.resourcePath;

public class EntityMapRepoTest {
    @Test
    public void test() {
        EntityMapRepo repo = new EntityMapRepo();
        repo.load(resourcePath("samples/tableMapping01.json"));

        Optional<EntityMap> table1 = repo.findByTable("table1");
        assertThat(table1.isPresent()).isTrue();
        assertThat(table1.get().getTable()).isEqualTo("table1");
        assertThat(table1.get().getEntity()).isEqualTo("entity1");

        assertThat(table1.get().getAttributes().stream().map(AttributeMap::getName))
                .containsExactlyInAnyOrder("name1", "name2");

        assertThat(table1.get().getAttributes().stream().map(AttributeMap::getField))
                .containsExactlyInAnyOrder("field1", "field2");
    }
}