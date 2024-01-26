package com.hmetao.code_dictionary.dto;

import com.hmetao.code_dictionary.pojo.BaseTree;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author HMETAO
 * @since 2022-07-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class MenusDTO extends BaseTree<Long> implements Serializable  {

    private static final long serialVersionUID = 1L;

    private String menusName;

    private String path;


}
