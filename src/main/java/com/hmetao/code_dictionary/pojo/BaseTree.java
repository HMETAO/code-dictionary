package com.hmetao.code_dictionary.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseTree<T> implements Serializable {
    private T id;

    private T parentId;

    List<? extends BaseTree<T>> children;

    public BaseTree(T id, T parentId) {
        this.id = id;
        this.parentId = parentId;
    }

    public static <T> List<? extends BaseTree<T>> buildTree(List<? extends BaseTree<T>> tree, T parentId) {
        return tree.stream()
                // 找出是他儿子的
                .filter(node -> Objects.equals(node.getParentId(), parentId))
                // 递归找儿子
                .peek(node -> {
                    List<? extends BaseTree<T>> children = buildTree(tree, node.getId());
                    node.setChildren(children.isEmpty() ?  new ArrayList<>() : children);
                })
                .collect(Collectors.toList());
    }


    public static <T> List<? extends BaseTree<T>> buildTree(List<? extends BaseTree<T>> tree, T parentId, Consumer<BaseTree<T>> nodeExtend) {
        return tree.stream()
                // 找出是他儿子的
                .filter(node -> Objects.equals(node.getParentId(), parentId))
                // 递归找儿子
                .peek(node -> {
                    List<? extends BaseTree<T>> children = buildTree(tree, node.getId(), nodeExtend);
                    node.setChildren(children.isEmpty() ? new ArrayList<>() : children);
                    // 对每个node的额外操作
                    if (nodeExtend != null)
                        nodeExtend.accept(node);
                })
                .collect(Collectors.toList());
    }

}
