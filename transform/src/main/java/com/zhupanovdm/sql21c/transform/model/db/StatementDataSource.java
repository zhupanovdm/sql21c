package com.zhupanovdm.sql21c.transform.model.db;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class StatementDataSource {
    private final List<StatementAttribute> attributes = new LinkedList<>();

    public abstract String getAlias();
    public List<StatementAttribute> getAttributes() {
        return Collections.unmodifiableList(attributes);
    }
    public void addAttribute(StatementAttribute attribute) {
        attributes.add(attribute);
    }
}
