package com.zhupanovdm.sql21c;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.zhupanovdm.sql21c.transform.SelectEntityExtractor;
import com.zhupanovdm.sql21c.transform.SqlSelectStatementParser;
import com.zhupanovdm.sql21c.transform.StatementMapper;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.zhupanovdm.sql21c.transform.ParserUtils.fixIncorrectParams;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

public class Sql21cCliApplication {
    public static void main(String[] args) {
        CommandLine cl = parseCl(args);
        if (cl == null) {
            return;
        }

        String inputFilePath = cl.getOptionValue("input");
        String mapFilePath = cl.getOptionValue("map");
        String outputFilePath = cl.getOptionValue("output");

        SelectEntityExtractor extractor = new SelectEntityExtractor();

        String input;
        try {
            input = Files.readString(Path.of(inputFilePath));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Select stmt = new SqlSelectStatementParser(fixIncorrectParams(input)).parse(extractor);
        StatementMapper.withFileRepo(mapFilePath).map(extractor);

        String result = SqlFormatter.format(stmt.toString());
        try {
            Files.writeString(Path.of(outputFilePath), result, CREATE, WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static CommandLine parseCl(String[] args) {
        Options options = new Options();

        Option input = new Option("i", "input", true, "input file path");
        input.setRequired(true);
        options.addOption(input);

        Option map = new Option("m", "map", true, "map file path");
        map.setRequired(true);
        options.addOption(map);

        Option output = new Option("o", "output", true, "output file");
        output.setRequired(true);
        options.addOption(output);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            return parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("sql21c", options);
            return null;
        }
    }
}