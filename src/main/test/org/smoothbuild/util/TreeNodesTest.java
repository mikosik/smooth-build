package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.testing.common.TestTreeNode.node;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.TreeNodes.walk;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TreeNodesTest {
  @Test
  public void to_string_for_null_generates_empty_string_with_EOL() {
    assertThat(TreeNodes.treeToString(null))
        .isEqualTo("\n");
  }

  @Test
  public void to_string_for_childless_root() {
    assertThat(TreeNodes.treeToString(node("name")))
        .isEqualTo("name\n");
  }

  @Test
  public void to_string_for_tree_with_2_layers() {
    assertThat(TreeNodes.treeToString(
        node("name", list(
            node("child 1"),
            node("child 2")))))
        .isEqualTo(
        "name\n" +
        "  child 1\n" +
        "  child 2\n");
  }

  @Test
  public void to_string_for_tree_with_3_layers() {
    assertThat(TreeNodes.treeToString(
        node("name", list(
            node("child 1", list(
                node("child A"),
                node("child B")
            )),
            node("child 2")))))
        .isEqualTo(
        "name\n" +
        "  child 1\n" +
        "    child A\n" +
        "    child B\n" +
        "  child 2\n");
  }

  @Test
  public void to_list_for_null_returns_empty_list() {
    assertThat(TreeNodes.treeToList(null))
        .isEmpty();
  }

  @Test
  public void to_list_for_childless_root_returns_root_only_list() {
    assertThat(TreeNodes.treeToList(node("name")))
        .containsExactly(node("name"));
  }

  @Test
  public void to_list_for_tree_with_2_layers() {
    assertThat(TreeNodes.treeToList(
        node("name", list(
            node("child 1"),
            node("child 2")))))
        .containsExactly(node("name"), node("child 1"), node("child 2"))
        .inOrder();
  }

  @Test
  public void to_list_for_tree_with_3_layers() {
    assertThat(TreeNodes.treeToList(
        node("name", list(
            node("child 1", list(
                node("child A"),
                node("child B")
            )),
            node("child 2")))))
        .containsExactly(
            node("name"), node("child 1"), node("child A"), node("child B"), node("child 2"))
        .inOrder();
  }

  @Test
  public void walk_for_null_never_calls_consumer() {
    @SuppressWarnings("unchecked")
    Consumer<MyNode> consumer = Mockito.mock(Consumer.class);
    walk(null, consumer);
    verifyNoInteractions(consumer);
  }

  private static class MyNode implements TreeNode<MyNode> {
    @Override
    public List<MyNode> children() {
      return null;
    }
  }
  @Test
  public void walk_visits_all_nodes_in_parent_child_order() {
    List<Object> nodes = new ArrayList<>();
    walk(
        node("name", list(
            node("child 1", list(
                node("child A"),
                node("child B")
            )),
            node("child 2"))), nodes::add);
    assertThat(nodes)
        .containsExactly(
            node("name"), node("child 1"), node("child A"), node("child B"), node("child 2"))
        .inOrder();
  }
}
