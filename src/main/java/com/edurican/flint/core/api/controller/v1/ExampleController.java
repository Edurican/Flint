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

    @GetMapping("/fail/{exampleValue}")
    @Operation(summary = "실패 테스트", description = "실패 테스트 설명")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExampleResponseDto.class))}),
            @ApiResponse(responseCode = "500", description = "잘못된 요청", content = @Content)
    })
    public ApiResult<ExampleResponseDto> exampleGetFail(@PathVariable String exampleValue,
                                                    @RequestParam String exampleParam) {
        try {
            ExampleResult result = exampleExampleService.processFailExample(new ExampleData(exampleValue, exampleParam));
            return ApiResult.success(new ExampleResponseDto(result.data()));
        } catch (CoreException e) {
            return ApiResult.error(e.getErrorType());
        }
    }

    @PostMapping("/post")
    public ApiResult<ExampleResponseDto> examplePost(@RequestBody ExampleRequestDto request) {
        ExampleResult result = exampleExampleService.processExample(request.toExampleData());
        return ApiResult.success(new ExampleResponseDto(result.data()));
    }

}
