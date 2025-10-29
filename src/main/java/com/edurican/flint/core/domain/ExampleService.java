package com.edurican.flint.core.domain;

import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.error.ErrorType;
import org.springframework.stereotype.Service;

@Service
public class ExampleService {

    public ExampleResult processExample(ExampleData exampleData) {
        return new ExampleResult(exampleData.value());
    }

    public ExampleResult processFailExample(ExampleData exampleData) {
        throw new CoreException(ErrorType.DEFAULT_ERROR);
    }

}
