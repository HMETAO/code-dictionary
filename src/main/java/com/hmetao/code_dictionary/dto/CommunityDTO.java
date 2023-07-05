package com.hmetao.code_dictionary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author HMETAO
 * @since 2023-07-04
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "Community对象", description = "")
public class CommunityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private CommunityUserDTO userInfo;

    private CommunitySnippetDTO snippetInfo;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime createTime;

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class CommunitySnippetDTO {
    private Long id;

    private Integer type;

    private String title;

    private String snippet;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class CommunityUserDTO {

    private Long id;

    private String username;

    private String avatar;

}
