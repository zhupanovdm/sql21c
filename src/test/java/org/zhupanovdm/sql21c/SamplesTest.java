package org.zhupanovdm.sql21c;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.Test;
import org.zhupanovdm.sql21c.model.Entity;
import org.zhupanovdm.sql21c.model.EntityAttribute;

import static org.assertj.core.api.Assertions.assertThat;

public class SamplesTest {

    @Test
    public void test() {
        String resource = ParserUtils.replaceIncorrectParams(TestUtils.resource("samples/sample01.sql"));
        SelectParser selectParser = new SelectParser(resource);
        SelectEntityExtractor xtractor = new SelectEntityExtractor();

        Select parse = selectParser.parse(xtractor);
        System.out.println(SqlFormatter.format(parse.toString()));

        assertThat(xtractor.getAllEntities().stream().map(Entity::getName))
                .containsExactlyInAnyOrder("_Reference266", "[_Document539]", "_Reference167",
                        "[_Document539_VT16871]", "_Reference153", "_Reference129", "[_Reference86]");

        assertThat(xtractor.getAllEntities().stream().map(Entity::getAlias))
                .containsExactlyInAnyOrder("loc", "sh", "org", "sl", "it", "cust", "agr");

        assertThat(xtractor.getAllEntities().stream().flatMap(entity -> entity.getAttributes().stream().map(EntityAttribute::getFullyQualifiedName)))
                .containsExactlyInAnyOrder("loc._Code", "loc._Description", "loc._IDRRef", "sh._Number",
                        "sh._Date_Time", "sh._Fld16841", "sh._Fld16817RRef", "sh._Posted", "sh._Marked",
                        "sh._IDRRef", "sh._Fld16821RRef", "sh._Fld16817RRef", "sh._Fld16816RRef", "sh._Fld16822RRef",
                        "sh._Fld38453", "sh._Fld16816RRef", "org._Code", "org._Description", "org._IDRRef",
                        "sl._Fld16877", "sl._Fld16881", "sl._Document539_IDRRef", "sl._Fld16873RRef",
                        "it._Fld3885", "it._Code", "it._Description", "it._IDRRef", "cust._Code",
                        "cust._Fld3367", "cust._Fld3356", "cust._Fld3361", "cust._Fld3362", "cust._IDRRef",
                        "agr._IDRRef", "agr._Fld2576RRef");

        assertThat(xtractor.getUnknownAttributes()).containsExactlyInAnyOrder("DATETIME", "VARCHAR", "year");
    }

}
