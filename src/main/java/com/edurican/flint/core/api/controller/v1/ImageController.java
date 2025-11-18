package com.edurican.flint.core.api.controller.v1;

import com.edurican.flint.core.domain.ImageFileService;
import com.edurican.flint.core.support.response.ApiResult;
import io.minio.errors.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageFileService imageFileService;

    @PostMapping
    @Operation(summary = "사진 업로드", description = "사진 업로드 테스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    })
    public ApiResult<String> uploadImage(@Valid @RequestParam("file") MultipartFile multipartFile) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String fileUrl = imageFileService.saveImageFile(multipartFile);
        return ApiResult.success(fileUrl);
    }

    @DeleteMapping
    @Operation(summary = "사진 삭제", description = "사진 삭제 테스트")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "테스트 완료", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = String.class))})
    })
    public ApiResult<String> deleteImage(@Valid @RequestParam("filename") String filename) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        imageFileService.deleteImageFile(filename);
        return ApiResult.success("사진 삭제 완료");
    }
}
