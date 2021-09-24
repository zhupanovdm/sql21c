# SQL21C
SQL to 1C

SQL statement transformation utility tool for 1C:Enterprise 8. Only SELECT SQL statement is recently supported.
Tool is capable to transform table and fields symbols via provided namings JSON mapping file. 

## Example

```java 
    SqlSelectStatementParser parser = new SqlSelectStatementParser("SELECT _IDRRef FROM dbo._acc39 WHERE _code IN ('62.01', '62.02')");
    SelectEntityExtractor extractor = new SelectEntityExtractor();

    Select parse = parser.parse(extractor);

    EntityMapRepo repo = new EntityMapRepo().load(resourcePath("mapping.json"));
    StatementMapper statementMapper = new StatementMapper(repo);
    statementMapper.map(extractor);
    
    System.out.println(SqlFormatter.format(stmt.toString()));
```

The statement
```roomsql
SELECT _IDRRef FROM dbo._acc39 WHERE _code IN ('62.01', '62.02')
```

via mapping file (mapping.json)
```json
{
  "mapping": [
    {
      "table": "_Acc39",
      "entity": "ПланСчетов.Хозрасчетный",
      "attributes": [
        {
          "name" :  "_Code",
          "field" : "Код"
        },
        {
          "name" :  "_Description",
          "field" : "Наименование"
        },
        {
          "name" :  "_IDRRef",
          "field" : "Ссылка"
        }
      ]
    }
  ]
}
```

will be transformed to
```roomsql 
SELECT
  Ссылка
FROM
  dbo.[ ПланСчетов.Хозрасчетный ]
WHERE
  Код IN ('62.01', '62.02')
```

## Build

`mvn clean package`

## Project structure

- `cli` package - CLI application
- `gui` package - GUI application (JFX)
- `transform` package - SQL statements parsing ang transformation library
