package org.zhupanovdm.sql21c.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@AllArgsConstructor
public class DataAttribute {

    @Getter
    private DataSource source;
    @Getter
    private String name;

    @Override
    public String toString() {
        return "DataAttribute(" + source.getReference() + "." + name + ")";
    }

}
