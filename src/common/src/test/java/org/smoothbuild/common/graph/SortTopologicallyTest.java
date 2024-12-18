package org.smoothbuild.common.graph;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.graph.SortTopologically.sortTopologically;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.collect.Collections2;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;

public class SortTopologicallyTest {
  @Nested
  class sort {
    @Test
    void empty() {
      assertSortTopologically(list(), list(list()));
    }

    @Test
    void single_node() {
      var n1 = node(1);
      assertSortTopologically(list(n1), list(list(n1)));
    }

    @Test
    void two_single_nodes() {
      var n1 = node(1);
      var n2 = node(2);
      assertSortTopologically(list(n1, n2), list(list(n1)));
    }

    @Test
    void three_single_nodes() {
      var n1 = node(1);
      var n2 = node(2);
      var n3 = node(3);
      assertSortTopologically(list(n1, n2, n3), list(list(n1)));
    }

    @Test
    void single_line() {
      var n1 = node(1, list(2));
      var n2 = node(2, list(3));
      var n3 = node(3);
      assertSortTopologically(list(n1, n2, n3), list(list(n1, n2, n3)));
    }

    @Test
    void single_line_with_island() {
      var n1 = node(1, list(2));
      var n2 = node(2, list(3));
      var n3 = node(3);
      var n4 = node(4);
      assertSortTopologically(list(n1, n2, n3, n4), list(list(n1, n2, n3)));
    }

    @Test
    void two_lines() {
      var n1 = node(1, list(2));
      var n2 = node(2, list(3));
      var n3 = node(3);
      var n4 = node(4, list(5));
      var n5 = node(5, list(6));
      var n6 = node(6);
      assertSortTopologically(
          list(n1, n2, n3, n4, n5, n6), list(list(n1, n2, n3), list(n4, n5, n6)));
    }

    @Test
    void two_lines_with_island() {
      var n1 = node(1, list(2));
      var n2 = node(2, list(3));
      var n3 = node(3);
      var n4 = node(4, list(5));
      var n5 = node(5, list(6));
      var n6 = node(6);
      var n7 = node(7);
      assertSortTopologically(
          list(n1, n2, n3, n4, n5, n6, n7), list(list(n1, n2, n3), list(n4, n5, n6)));
    }

    @Test
    void simple_tree() {
      var n1 = node(1, list(2, 3));
      var n2 = node(2);
      var n3 = node(3);
      assertSortTopologically(list(n1, n2, n3), list(list(n1, n2), list(n1, n3)));
    }

    @Test
    void two_simple_trees() {
      var n1 = node(1, list(2, 3));
      var n2 = node(2);
      var n3 = node(3);
      var n4 = node(4, list(5, 6));
      var n5 = node(5);
      var n6 = node(6);
      assertSortTopologically(
          list(n1, n2, n3, n4, n5, n6),
          list(list(n1, n2), list(n1, n3), list(n4, n5), list(n4, n6)));
    }

    @Test
    void simple_tree_with_island() {
      var n1 = node(1, list(2, 3));
      var n2 = node(2);
      var n3 = node(3);
      var n4 = node(4);
      assertSortTopologically(list(n1, n2, n3, n4), list(list(n1, n2), list(n1, n3)));
    }

    @Test
    void two_roots() {
      var n1 = node(1, list(2, 3));
      var n2 = node(2);
      var n3 = node(3);
      var n4 = node(4, list(2, 3));
      assertSortTopologically(
          list(n1, n2, n3, n4), list(list(n1, n2), list(n1, n3), list(n4, n2), list(n4, n3)));
    }

    @Test
    void parallel_edges() {
      var n1 = node(1, list(2, 2));
      var n2 = node(2);
      assertSortTopologically(list(n1, n2), list(list(n1, n2)));
    }

    @Test
    void parallel_path_between_nodes() {
      var n1 = node(1, list(2, 3));
      var n2 = node(2, list(4));
      var n3 = node(3, list(4));
      var n4 = node(4);
      assertSortTopologically(list(n1, n2, n3, n4), list(list(n1, n2, n4), list(n1, n3, n4)));
    }
  }

  @Nested
  class detects_cycle {
    @Test
    void to_itself() {
      var n1 = node(1, list(1));
      assertCycleDetected(list(n1), list(n1));
    }

    @Test
    void to_itself_when_it_has_also_dangling_node() {
      var n1 = node(1);
      var n2 = node(2, list(1, 2));

      assertCycleDetected(list(n1, n2), list(n2));
    }

    @Test
    void between_two_nodes_when_no_root_exists() {
      var n1 = node(1, list(2));
      var n2 = node(2, list(1));
      assertCycleDetected(list(n1, n2), list(n1, n2));
    }

    @Test
    void between_two_nodes_when_island_exists() {
      var n1 = node(1);
      var n2 = node(2, list(3));
      var n3 = node(3, list(2));
      assertCycleDetected(list(n1, n2, n3), list(n2, n3));
    }

    @Test
    void between_two_nodes_when_root_exists() {
      var n1 = node(1, list(2));
      var n2 = node(2, list(3));
      var n3 = node(3, list(2));
      var nodes = list(n1, n2, n3);
      assertCycleDetected(nodes, list(n2, n3));
    }

    private void assertCycleDetected(
        List<GraphNode<Integer, String, String>> nodes,
        List<GraphNode<Integer, String, String>> expectedCycle) {
      var permutations = permutations(nodes);
      for (List<GraphNode<Integer, String, String>> permutation : permutations) {
        var actual = sortTopologically(permutation).cycle().toJdkList();

        List<GraphEdge<String, Integer>> cycle = buildCycle(expectedCycle);
        var rotated = cycle.toJdkList();
        for (int i = 0; i < cycle.size(); i++) {
          Collections.rotate(rotated, 1);
          if (actual.equals(rotated)) {
            return;
          }
        }
        fail("For tested permutation: " + permutation.map(GraphNode::key) + "\n"
            + "actual cycle = " + actual + "\n"
            + "doesn't match expected cycle (even rotated) = " + cycle);
      }
    }

    private List<GraphEdge<String, Integer>> buildCycle(
        List<GraphNode<Integer, String, String>> nodes) {
      ArrayList<GraphEdge<String, Integer>> result = new ArrayList<>();
      for (int i = 0; i < nodes.size(); i++) {
        Integer nextNodeKey = nodes.get((i + 1) % nodes.size()).key();
        var edge = nodes.get(i).edges().stream()
            .filter(e -> e.targetKey().equals(nextNodeKey))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("nodes doesn't form a cycle"));
        result.add(edge);
      }
      return listOfAll(result);
    }
  }

  @Nested
  class fails {
    @Test
    void when_edge_points_to_nonexistent_node() {
      var n1 = node(1, list(2));
      var nodes = list(n1);

      assertCall(() -> sortTopologically(nodes))
          .throwsException(new IllegalArgumentException(
              "Node '1' has edge pointing to node '2' which does not exist."));
    }

    @Test
    void when_two_nodes_has_same_key() {
      var n1 = node(1);
      var n1b = node(1);
      var nodes = list(n1, n1b);

      assertCall(() -> sortTopologically(nodes)).throwsException(IllegalArgumentException.class);
    }
  }

  @Test
  void sorting_algorithm_has_linear_complexity() {
    AtomicInteger key = new AtomicInteger();
    List<GraphNode<Integer, String, String>> topLayer = createLayer(key, list());
    var nodes = new ArrayList<>(topLayer.toJdkList());
    int layerCount = 100;
    for (int i = 0; i < layerCount; i++) {
      topLayer = createLayer(key, topLayer.map(GraphNode::key));
      nodes.addAll(topLayer.toJdkList());
    }
    assertTimeoutPreemptively(Duration.ofSeconds(5), () -> sortTopologically(listOfAll(nodes)));
  }

  private List<GraphNode<Integer, String, String>> createLayer(
      AtomicInteger key, List<Integer> edges) {
    var layer = new ArrayList<GraphNode<Integer, String, String>>();
    for (int j = 0; j < 10; j++) {
      layer.add(node(key.getAndIncrement(), edges));
    }
    return listOfAll(layer);
  }

  private static void assertSortTopologically(
      List<GraphNode<Integer, String, String>> nodes,
      List<List<GraphNode<Integer, String, String>>> expectedOrders) {
    var permutations = permutations(nodes);
    for (List<GraphNode<Integer, String, String>> permutation : permutations) {
      var actual = sortTopologically(permutation).sorted();

      String message = "tested permutation: " + permutation.map(GraphNode::key);
      assertWithMessage(message).that(actual).containsExactlyElementsIn(nodes);
      for (List<GraphNode<Integer, String, String>> expectedOrder : expectedOrders) {
        assertWithMessage(message)
            .that(actual)
            .containsAtLeastElementsIn(expectedOrder)
            .inOrder();
      }
    }
  }

  private static GraphNode<Integer, String, String> node(int n1) {
    return node(n1, list());
  }

  private static GraphNode<Integer, String, String> node(Integer key, List<Integer> targetKeys) {
    var edges = targetKeys.map(k -> new GraphEdge<>("->" + k, k));
    return new GraphNode<>(key, "node" + key, edges);
  }

  public static <E> Collection<List<E>> permutations(List<E> elements) {
    return Collections2.permutations(elements.toJdkList()).stream()
        .map(List::listOfAll)
        .toList();
  }
}
