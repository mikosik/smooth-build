package org.smoothbuild.util;

import static org.smoothbuild.testing.common.TestTreeNode.node;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.TreeNodes.walk;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.thenCalledNever;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;
import org.smoothbuild.testing.common.TestTreeNode;

public class TreeNodesTest {
  private Consumer<TestTreeNode> consumer;
  private List<TestTreeNode> nodes;

  @Test
  public void to_string_for_null_generates_empty_string_with_EOL() {
    when(TreeNodes.treeToString(null));
    thenReturned("\n");
  }

  @Test
  public void to_string_for_childless_root() {
    when(TreeNodes.treeToString(node("name")));
    thenReturned("name\n");
  }

  @Test
  public void to_string_for_tree_with_2_layers() {
    when(TreeNodes.treeToString(
        node("name", list(
            node("child 1"),
            node("child 2")))));
    thenReturned(
        "name\n" +
        "  child 1\n" +
        "  child 2\n");
  }

  @Test
  public void to_string_for_tree_with_3_layers() {
    when(TreeNodes.treeToString(
        node("name", list(
            node("child 1", list(
                node("child A"),
                node("child B")
            )),
            node("child 2")))));
    thenReturned(
        "name\n" +
        "  child 1\n" +
        "    child A\n" +
        "    child B\n" +
        "  child 2\n");
  }

  @Test
  public void to_list_for_null_returns_empty_list() {
    when(TreeNodes.treeToList(null));
    thenReturned(list());
  }

  @Test
  public void to_list_for_childless_root_returns_root_only_list() {
    when(TreeNodes.treeToList(node("name")));
    thenReturned(list(node("name")));
  }

  @Test
  public void to_list_for_tree_with_2_layers() {
    when(TreeNodes.treeToList(
        node("name", list(
            node("child 1"),
            node("child 2")))));
    thenReturned(list(node("name"), node("child 1"), node("child 2")));
  }

  @Test
  public void to_list_for_tree_with_3_layers() {
    when(TreeNodes.treeToList(
        node("name", list(
            node("child 1", list(
                node("child A"),
                node("child B")
            )),
            node("child 2")))));
    thenReturned(list(
        node("name"), node("child 1"), node("child A"), node("child B"), node("child 2")));
  }

  @Test
  public void walk_for_null_never_calls_consumer() {
    given(consumer = mock(Consumer.class));
    when(() -> walk(null, consumer));
    thenCalledNever(onInstance(consumer));
  }

  @Test
  public void walk_visits_all_nodes_in_parent_child_order() {
    given(nodes = new ArrayList<>());
    when(() -> walk(
        node("name", list(
            node("child 1", list(
                node("child A"),
                node("child B")
            )),
            node("child 2"))), nodes::add)
    );
    thenEqual(nodes, list(
        node("name"), node("child 1"), node("child A"), node("child B"), node("child 2")));
  }
}
