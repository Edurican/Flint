package com.edurican.flint.core.api.controller.v1;


import com.edurican.flint.core.api.controller.v1.request.ExampleRequestDto;
import com.edurican.flint.core.api.controller.v1.response.ExampleResponseDto;
import com.edurican.flint.core.domain.ExampleData;
import com.edurican.flint.core.domain.ExampleResult;
import com.edurican.flint.core.domain.ExampleService;
import com.edurican.flint.core.support.error.CoreException;
import com.edurican.flint.core.support.response.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExampleController {

    private final ExampleService exampleExampleService;

    public ExampleController(ExampleService exampleExampleService) {
        this.exampleExampleService = exampleExampleService;
    }

    @GetMapping("/get/{exampleValue}")
    @Operation(summary = "테스트", description = "테스트 설명")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExampleResponseDto.class))})
    })
    public ApiResult<ExampleResponseDto> exampleGet(@PathVariable String exampleValue,
                                                    @RequestParam String exampleParam) {
        ExampleResult result = exampleExampleService.processExample(new ExampleData(exampleValue, exampleParam));
        return ApiResult.success(new ExampleResponseDto(result.data()));
    }

    @PostMapping("/post")
    public ApiResult<ExampleResponseDto> examplePost(@RequestBody ExampleRequestDto request) {
        ExampleResult result = exampleExampleService.processExample(request.toExampleData());
        return ApiResult.success(new ExampleResponseDto(result.data()));
    }

}
