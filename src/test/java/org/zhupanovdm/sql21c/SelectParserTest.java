package org.zhupanovdm.sql21c;

import org.junit.Test;
import org.zhupanovdm.sql21c.model.DataAttribute;
import org.zhupanovdm.sql21c.model.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectParserTest {

    @Test
    public void testSingleSource() {
        assertThat(new SelectParser("SELECT field from table").getAllDataSources())
                .containsExactly(new DataSource("table", null));

        assertThat(new SelectParser("SELECT source.field from table as source").getAllDataSources())
                .containsExactly(new DataSource("table", "source"));
    }

    @Test
    public void testSourceJoin() {
        assertThat(new SelectParser(
                "SELECT source.field from table as source\n" +
                        "LEFT JOIN table2 as source2 ON source.d = source2.d").getAllDataSources())
                .containsExactlyInAnyOrder(
                        new DataSource("table", "source"),
                        new DataSource("table2", "source2"));

        assertThat(new SelectParser(
                "SELECT source.field from table as source\n" +
                        "LEFT JOIN table2 as source2 ON source.d = source2.d\n" +
                        "LEFT JOIN table3 ON source.d = table3.d").getAllDataSources())
                .containsExactlyInAnyOrder(
                        new DataSource("table", "source"),
                        new DataSource("table2", "source2"),
                        new DataSource("table3", null));
    }

    @Test
    public void testAttributes() {
        SelectParser selectNoSourceField = new SelectParser("SELECT field from table");
        assertThat(selectNoSourceField.getAttributesNoSource()).containsExactly("field");
        assertThat(selectNoSourceField.getAllAttributes()).isEmpty();

        SelectParser selectNoSourceExpression = new SelectParser("SELECT field * 2 + 1 from table");
        assertThat(selectNoSourceExpression.getAttributesNoSource()).containsExactly("field");
        assertThat(selectNoSourceExpression.getAllAttributes()).isEmpty();

        DataSource source = new DataSource("table", "source");
        SelectParser selectFromSource = new SelectParser("SELECT source.field as field1 from table as source");
        assertThat(selectFromSource.getAttributesNoSource()).isEmpty();
        assertThat(selectFromSource.getAllAttributes()).containsExactly(new DataAttribute(source,"field"));

        assertThat(new SelectParser("SELECT source.field as field1 from table as source").getAllAttributes())
                .containsExactly(new DataAttribute(source,"field"));

        assertThat(new SelectParser("SELECT source.field + 1 as field1 from table as source").getAllAttributes())
                .containsExactly(new DataAttribute(source,"field"));

        assertThat(new SelectParser("SELECT (source.field + 1) + source.fieldAlt as field1 from table as source").getAllAttributes())
                .containsExactlyInAnyOrder(new DataAttribute(source,"field"), new DataAttribute(source,"fieldAlt"));

        assertThat(new SelectParser("SELECT fn(source.field, 1, source.fieldAlt) as field1 from table as source").getAllAttributes())
                .containsExactlyInAnyOrder(new DataAttribute(source,"field"), new DataAttribute(source,"fieldAlt"));

    }

    @Test
    public void testAttributesJoin() {
        DataSource source = new DataSource("table", "source");
        DataSource source2 = new DataSource("table2", "source2");

        assertThat(new SelectParser(
                "SELECT source.field from table as source\n" +
                        "LEFT JOIN table2 as source2 ON source.d1 = source2.d2").getAllAttributes())
                .containsExactlyInAnyOrder(
                        new DataAttribute(source, "field"),
                        new DataAttribute(source2, "d2"),
                        new DataAttribute(source,"d1"));

        DataSource table2 = new DataSource("table2", null);
        SelectParser select = new SelectParser(
                    "SELECT table2.field from table\n" +
                                    "LEFT JOIN table2 ON d1 = d2");
        assertThat(select.getAllAttributes()).containsExactly(new DataAttribute(table2, "field"));
        assertThat(select.getAttributesNoSource()).containsExactlyInAnyOrder("d1", "d2");
    }

    @Test
    public void testAttributesWhere() {
        DataSource source = new DataSource("table", "source");
        assertThat(new SelectParser(
                "SELECT 1 from table as source WHERE source.f = 1 and source.b = 5").getAllAttributes())
                .containsExactlyInAnyOrder(
                        new DataAttribute(source,"f"),
                        new DataAttribute(source,"b"));
    }

    @Test
    public void testAttributesGroupBy() {
        DataSource source = new DataSource("table", "source");
        assertThat(new SelectParser(
                "SELECT COUNT(*) from table as source GROUP BY source.a, source.b").getAllAttributes())
                .containsExactlyInAnyOrder(
                        new DataAttribute(source,"a"),
                        new DataAttribute(source,"b"));
    }

    @Test
    public void testAttributesInnerStatement() {
        // TODO: Inner select statement
    }


}
