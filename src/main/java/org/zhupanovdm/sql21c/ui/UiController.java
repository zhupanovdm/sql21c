package org.zhupanovdm.sql21c.ui;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import net.sf.jsqlparser.statement.select.Select;
import org.zhupanovdm.sql21c.transform.*;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UiController {

    @FXML
    private TextField fieldMappingFile;

    @FXML
    private TextArea textScript;

    @FXML
    private TextArea textResult;

    @FXML
    private Label labelStatus;

    @FXML
    protected void onTransformButtonClick() {
        TransformTask transformTask = new TransformTask(textScript.getText(), fieldMappingFile.getText());
        transformTask.setOnSucceeded(event -> {
            textResult.setText(transformTask.getValue());
            labelStatus.setText("OK");
        });

        transformTask.setOnFailed(event -> labelStatus.setText(transformTask.getException().getMessage()));

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(transformTask);
        executorService.shutdown();
    }

    private static class TransformTask extends Task<String> {
        private final String file;
        private final String sql;

        private TransformTask(String sql, String file) {
            this.file = file;
            this.sql = sql;
        }

        @Override
        protected String call() {
            String resource = ParserUtils.replaceIncorrectParams(sql);
            SelectParser selectParser = new SelectParser(resource);
            SelectEntityExtractor extractor = new SelectEntityExtractor();

            Select parse = selectParser.parse(extractor);

            EntityMapRepo repo = new EntityMapRepo();
            repo.load(Path.of(file));

            StatementMapper statementMapper = new StatementMapper(repo);
            statementMapper.map(extractor);

            return SqlFormatter.format(parse.toString());
        }
    }


}