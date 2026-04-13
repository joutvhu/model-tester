package com.joutvhu.model.tester.domain;

import java.util.List;
import java.util.Map;

public record ModelRecord(
    Long id,
    String name,
    List<String> tags,
    Map<String, Object> metadata
) {
}
