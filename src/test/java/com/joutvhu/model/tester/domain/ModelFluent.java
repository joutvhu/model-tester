package com.joutvhu.model.tester.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ModelFluent {
    private Long id;
    private String title;
    private boolean active;

    // Manual fluent setter for testing custom logic
    public ModelFluent description(String description) {
        return this;
    }
}
