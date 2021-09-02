package org.zhupanovdm.sql21c;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import net.sf.jsqlparser.statement.select.Select;
import org.zhupanovdm.sql21c.transform.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;

public class Application {

    public static void main(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("Arguments not specified.");
        }

        String resource = ParserUtils.replaceIncorrectParams(read(args[0]));
        SelectParser selectParser = new SelectParser(resource);
        SelectEntityExtractor extractor = new SelectEntityExtractor();

        Select parse = selectParser.parse(extractor);

        EntityMapRepo repo = new EntityMapRepo();
        repo.load(Path.of(args[1]));

        StatementMapper statementMapper = new StatementMapper(repo);
        statementMapper.map(extractor);

        String statement = SqlFormatter.format(parse.toString());

        try {
            Files.writeString(Path.of(args[2]), statement, CREATE_NEW, WRITE);
        } catch (IOException e) {
            throw new RuntimeException("Cant write output", e);
        }

    }

    private static String read(String file) {
        try {
            return Files.readString(Path.of(file));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load resource", e);
        }
    }

}
