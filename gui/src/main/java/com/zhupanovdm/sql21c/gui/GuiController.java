package com.zhupanovdm.sql21c.gui;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.zhupanovdm.sql21c.transform.SelectEntityExtractor;
import com.zhupanovdm.sql21c.transform.SqlSelectStatementParser;
import com.zhupanovdm.sql21c.transform.StatementMapper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import net.sf.jsqlparser.statement.select.Select;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.zhupanovdm.sql21c.transform.ParserUtils.fixIncorrectParams;

public class GuiController {
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
            SelectEntityExtractor extractor = new SelectEntityExtractor();
            Select stmt = new SqlSelectStatementParser(fixIncorrectParams(sql)).parse(extractor);
            StatementMapper.withFileRepo(file).map(extractor);
            return SqlFormatter.format(stmt.toString());
        }
    }
}