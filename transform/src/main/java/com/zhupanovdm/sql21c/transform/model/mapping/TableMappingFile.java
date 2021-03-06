package com.zhupanovdm.sql21c.transform.model.mapping;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
public class TableMappingFile {
    private List<EntityMap> mapping = new LinkedList<>();
}
