package com.zhupanovdm.sql21c.transform;

import org.junit.Test;
import com.zhupanovdm.sql21c.transform.model.db.StatementTable;
import com.zhupanovdm.sql21c.transform.model.db.StatementAttribute;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectStatementVisitorSamplesTest {
    @Test
    public void test() {
        String resource = ParserUtils.fixIncorrectParams(TestUtils.resource("samples/selectStatement01.sql"));
        SqlSelectStatementParser selectParser = new SqlSelectStatementParser(resource);
        SelectStatementVisitor visitor = new SelectStatementVisitor();
        selectParser.parse(visitor);
        SelectStatementModel model = visitor.getModel();

        assertThat(model.getTables().stream().map(StatementTable::getName))
                .containsExactlyInAnyOrder("_Reference266", "[_Document539]", "_Reference167",
                        "[_Document539_VT16871]", "_Reference153", "_Reference129", "[_Reference86]");

        assertThat(model.getTables().stream().map(StatementTable::getAlias))
                .containsExactlyInAnyOrder("loc", "sh", "org", "sl", "it", "cust", "agr");

        assertThat(model.getTables().stream().flatMap(statementDataSource -> statementDataSource.getAttributes().stream().map(StatementAttribute::getFullyQualifiedName)))
                .containsExactlyInAnyOrder("loc._Code", "loc._Description", "loc._IDRRef", "sh._Number",
                        "sh._Date_Time", "sh._Fld16841", "sh._Fld16817RRef", "sh._Posted", "sh._Marked",
                        "sh._IDRRef", "sh._Fld16821RRef", "sh._Fld16817RRef", "sh._Fld16816RRef", "sh._Fld16822RRef",
                        "sh._Fld38453", "sh._Fld16816RRef", "org._Code", "org._Description", "org._IDRRef",
                        "sl._Fld16877", "sl._Fld16881", "sl._Document539_IDRRef", "sl._Fld16873RRef",
                        "it._Fld3885", "it._Code", "it._Description", "it._IDRRef", "cust._Code",
                        "cust._Fld3367", "cust._Fld3356", "cust._Fld3361", "cust._Fld3362", "cust._IDRRef",
                        "agr._IDRRef", "agr._Fld2576RRef", "loc._Code", "sh._Fld16828", "sh._Date_Time",
                        "sl._Fld16879", "sl._Fld16879", "sl._Fld16881");

        assertThat(model.getUnknownAttributes().stream().map(StatementAttribute::getName)).containsExactlyInAnyOrder("DATETIME", "VARCHAR", "year");
    }
}
