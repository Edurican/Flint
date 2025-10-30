package com.edurican.flint.core.api.controller.v1.request;

import com.edurican.flint.core.domain.ExampleData;
import lombok.Data;

public record ExampleRequestDto(String data) {
    public ExampleData toExampleData() {
        return new ExampleData(data, data);
    }
}
