package com.korit.library.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookImageDto {
    private int imageId;
    private String bookCode;
    private String saveName;
    private String originName;
}
