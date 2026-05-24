package com.rice.lfcdemo.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleVo {
    private Long id;

    private String title;

    private String summary;

    private String categoryName;
}
