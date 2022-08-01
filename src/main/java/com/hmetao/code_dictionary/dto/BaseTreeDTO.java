package com.hmetao.code_dictionary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseTreeDTO<T> implements Serializable {
    private T id;

    private T parentId;

    List<? extends BaseTreeDTO<T>> children;

    public BaseTreeDTO(T id, T parentId) {
        this.id = id;
        this.parentId = parentId;
    }

    public static <T> List<? extends BaseTreeDTO<T>> buildTree(List<? extends BaseTreeDTO<T>> tree, T parentId) {
        return tree.stream()
                // 找出是他儿子的
                .filter(node -> Objects.equals(node.getParentId(), parentId))
                // 递归找儿子
                .peek(node -> {
                    List<? extends BaseTreeDTO<T>> children = buildTree(tree, node.getId());
                    node.setChildren(children.isEmpty() ? null : children);
                })
                .collect(Collectors.toList());
    }


    public static <T> List<? extends BaseTreeDTO<T>> buildTree(List<? extends BaseTreeDTO<T>> tree, T parentId, Consumer<BaseTreeDTO<T>> nodeExtend) {
        return tree.stream()
                // 找出是他儿子的
                .filter(node -> Objects.equals(node.getParentId(), parentId))
                // 递归找儿子
                .peek(node -> {
                    List<? extends BaseTreeDTO<T>> children = buildTree(tree, node.getId(), nodeExtend);
                    node.setChildren(children.isEmpty() ? null : children);
                    // 对每个node的额外操作
                    if (nodeExtend != null)
                        nodeExtend.accept(node);
                })
                .collect(Collectors.toList());
    }

}
