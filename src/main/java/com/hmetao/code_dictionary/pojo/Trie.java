package com.hmetao.code_dictionary.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

@Data
@AllArgsConstructor
public class Trie {
    private HashMap<String, Trie> nodes;

    private Integer type;

    private Boolean snippet;

    private String parentId;

    private String id;

    public Trie(Integer type, Boolean snippet, String parentId, String id) {
        this.id = id;
        this.type = type;
        this.snippet = snippet;
        this.parentId = parentId;
        this.nodes = new HashMap<>();
    }

    public Trie() {
        this.nodes = new HashMap<>();
        this.id = "0";
    }

    public Trie append(String word, Integer type, Boolean snippet, String parentId, String id) {
        if (checkNotContainerTrieNode(type, snippet, this, word)) {
            nodes.put(word, new Trie(type, snippet, parentId, id));
        }
        return nodes.get(word);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrieDTO {
        private String parentId;

        private String label;

        private Boolean curSnippet;

        private Integer curType;
    }

    public void insert(String[] words, Boolean snippet, Integer type, Function<TrieDTO, Trie> fun) {
        Trie read = this;
        int n = words.length;
        for (int i = 0; i < n; i++) {
            // 当前类型属性
            Boolean curSnippet = i == n - 1 ? snippet : false;
            Integer curType = i == n - 1 ? type : null;
            // 判断是否存在
            HashMap<String, Trie> children = read.nodes;
            if (checkNotContainerTrieNode(curType, curSnippet, read, words[i])) {
                children.put(words[i], fun.apply(new TrieDTO(read.id, words[i], curSnippet, curType)));
            }
            read = children.get(words[i]);
        }
    }

    private boolean checkNotContainerTrieNode(Integer type, Boolean snippet, Trie trie, String word) {
        // 判断是否存在，或 是否存在完全相同
        if (!trie.nodes.containsKey(word)) return true;
        Trie node = trie.nodes.get(word);
        return !node.snippet.equals(snippet) || !Objects.equals(node.type, type);
    }
}
