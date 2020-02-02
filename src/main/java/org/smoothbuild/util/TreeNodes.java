package org.smoothbuild.util;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TreeNodes {
  public static String treeToString(TreeNode<?> tree) {
    if (tree == null) {
      return "\n";
    } else {
      return treeToString("", tree);
    }
  }

  private static String treeToString(String indent, TreeNode<?> tree) {
    return indent + tree + "\n"
        + treeToString("  " + indent, tree.children());
  }

  private static String treeToString(String indent, List<? extends TreeNode<?>> nodes) {
    return nodes.stream()
        .map(n -> treeToString(indent, n))
        .collect(joining());
  }

  public static <T extends TreeNode<T>> List<T> treeToList(T tree) {
    List<T> result = new ArrayList<>();
    walk(tree, result::add);
    return result;
  }

  public static <T extends TreeNode<T>> void walk(T tree, Consumer<T> consumer) {
    if (tree != null) {
      consumer.accept(tree);
      tree.children().forEach(n -> walk(n, consumer));
    }
  }
}
