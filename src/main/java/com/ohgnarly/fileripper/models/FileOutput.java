package com.ohgnarly.fileripper.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter @Setter
public class FileOutput {
    private String fileName;
    private List<Map<String, Object>> records;
}
