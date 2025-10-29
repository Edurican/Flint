package com.edurican.flint.core.api.controller.v1;


import com.edurican.flint.core.api.controller.v1.request.ExampleRequestDto;
import com.edurican.flint.core.api.controller.v1.response.ExampleResponseDto;
import com.edurican.flint.core.domain.ExampleData;
import com.edurican.flint.core.domain.ExampleResult;
import com.edurican.flint.core.domain.ExampleService;
import com.edurican.flint.core.support.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExampleController {

    private final ExampleService exampleExampleService;

    public ExampleController(ExampleService exampleExampleService) {
        this.exampleExampleService = exampleExampleService;
    }

    @GetMapping("/get/{exampleValue}")
    public ApiResponse<ExampleResponseDto> exampleGet(@PathVariable String exampleValue,
                                                      @RequestParam String exampleParam) {
        ExampleResult result = exampleExampleService.processExample(new ExampleData(exampleValue, exampleParam));
        return ApiResponse.success(new ExampleResponseDto(result.data()));
    }

    @PostMapping("/post")
    public ApiResponse<ExampleResponseDto> examplePost(@RequestBody ExampleRequestDto request) {
        ExampleResult result = exampleExampleService.processExample(request.toExampleData());
        return ApiResponse.success(new ExampleResponseDto(result.data()));
    }

}
