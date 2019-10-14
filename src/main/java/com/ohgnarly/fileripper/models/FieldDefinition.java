package com.ohgnarly.fileripper.models;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FieldDefinition {
    private String fieldName;
    private Integer startPosition;
    private Integer fieldLength;
}
