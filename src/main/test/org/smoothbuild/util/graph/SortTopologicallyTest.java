package org.smoothbuild.util.graph;

import static com.google.common.collect.Collections2.permutations;
import static com.google.common.truth.Truth.assertWithMessage;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.graph.SortTopologically.sortTopologically;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SortTopologicallyTest {
  @Nested
  class sort {
    @Test
    public void empty() {
      assertSortTopologically(
          List.of(),
          List.of(
              List.of()));
    }

    @Test
    public void single_node() {
      var n1 = node(1);
      assertSortTopologically(
          List.of(n1),
          List.of(
              List.of(n1)));
    }

    @Test
    public void two_single_nodes() {
      var n1 = node(1);
      var n2 = node(2);
      assertSortTopologically(
          List.of(n1, n2),
          List.of(
              List.of(n1)));
    }

    @Test
    public void three_single_nodes() {
      var n1 = node(1);
      var n2 = node(2);
      var n3 = node(3);
      assertSortTopologically(
          List.of(n1, n2, n3),
          List.of(
              List.of(n1)));
    }

    @Test
    public void single_line() {
      var n1 = node(1, List.of(2));
      var n2 = node(2, List.of(3));
      var n3 = node(3);
      assertSortTopologically(
          List.of(n1, n2, n3),
          List.of(
              List.of(n1, n2, n3)));
    }

    @Test
    public void single_line_with_island() {
      var n1 = node(1, List.of(2));
      var n2 = node(2, List.of(3));
      var n3 = node(3);
      var n4 = node(4);
      assertSortTopologically(
          List.of(n1, n2, n3, n4),
          List.of(
              List.of(n1, n2, n3)));
    }

    @Test
    public void two_lines() {
      var n1 = node(1, List.of(2));
      var n2 = node(2, List.of(3));
      var n3 = node(3);
      var n4 = node(4, List.of(5));
      var n5 = node(5, List.of(6));
      var n6 = node(6);
      assertSortTopologically(
          List.of(n1, n2, n3, n4, n5, n6),
          List.of(
              List.of(n1, n2, n3),
              List.of(n4, n5, n6)));
    }

    @Test
    public void two_lines_with_island() {
      var n1 = node(1, List.of(2));
      var n2 = node(2, List.of(3));
      var n3 = node(3);
      var n4 = node(4, List.of(5));
      var n5 = node(5, List.of(6));
      var n6 = node(6);
      var n7 = node(7);
      assertSortTopologically(
          List.of(n1, n2, n3, n4, n5, n6, n7),
          List.of(
              List.of(n1, n2, n3),
              List.of(n4, n5, n6)));
    }

    @Test
    public void simple_tree() {
      var n1 = node(1, List.of(2, 3));
      var n2 = node(2);
      var n3 = node(3);
      assertSortTopologically(
          List.of(n1, n2, n3),
          List.of(
              List.of(n1, n2),
              List.of(n1, n3)));
    }

    @Test
    public void two_simple_trees() {
      var n1 = node(1, List.of(2, 3));
      var n2 = node(2);
      var n3 = node(3);
      var n4 = node(4, List.of(5, 6));
      var n5 = node(5);
      var n6 = node(6);
      assertSortTopologically(
          List.of(n1, n2, n3, n4, n5, n6),
          List.of(
              List.of(n1, n2),
              List.of(n1, n3),
              List.of(n4, n5),
              List.of(n4, n6)));
    }

    @Test
    public void simple_tree_with_island() {
      var n1 = node(1, List.of(2, 3));
      var n2 = node(2);
      var n3 = node(3);
      var n4 = node(4);
      assertSortTopologically(
          List.of(n1, n2, n3, n4),
          List.of(
              List.of(n1, n2),
              List.of(n1, n3)));
    }

    @Test
    public void two_roots() {
      var n1 = node(1, List.of(2, 3));
      var n2 = node(2);
      var n3 = node(3);
      var n4 = node(4, List.of(2, 3));
      assertSortTopologically(
          List.of(n1, n2, n3, n4),
          List.of(
              List.of(n1, n2),
              List.of(n1, n3),
              List.of(n4, n2),
              List.of(n4, n3)));
    }

    @Test
    public void parallel_edges() {
      var n1 = node(1, List.of(2, 2));
      var n2 = node(2);
      assertSortTopologically(
          List.of(n1, n2),
          List.of(
              List.of(n1, n2)));
    }

    @Test
    public void parallel_path_between_nodes() {
      var n1 = node(1, List.of(2, 3));
      var n2 = node(2, List.of(4));
      var n3 = node(3, List.of(4));
      var n4 = node(4);
      assertSortTopologically(
          List.of(n1, n2, n3, n4),
          List.of(
              List.of(n1, n2, n4),
              List.of(n1, n3, n4)));
    }
  }

  @Nested
  class detects_cycle {
    @Test
    public void from_single_node_to_itself() {
      var n1 = node(1, List.of(1));
      assertCycleDetected(
          List.of(n1),
          List.of(n1));
    }

    @Test
    public void between_two_nodes_when_no_root_exists() {
      var n1 = node(1, List.of(2));
      var n2 = node(2, List.of(1));
      assertCycleDetected(
          List.of(n1, n2),
          List.of(n1, n2));
    }

    @Test
    public void between_two_nodes_when_island_exists() {
      var n1 = node(1);
      var n2 = node(2, List.of(3));
      var n3 = node(3, List.of(2));
      assertCycleDetected(
          List.of(n1, n2, n3),
          List.of(n2, n3)
      );
    }

    @Test
    public void between_two_nodes_when_root_exists() {
      var n1 = node(1, List.of(2));
      var n2 = node(2, List.of(3));
      var n3 = node(3, List.of(2));
      var nodes = List.of(n1, n2, n3);
      assertCycleDetected(
          nodes,
          List.of(n2, n3)
      );
    }

    private void assertCycleDetected(List<GraphNode<Integer, String, String>> nodes,
        List<GraphNode<Integer, String, String>> expectedCycle) {
      var permutations = permutations(nodes);
      for (List<GraphNode<Integer, String, String>> permutation : permutations) {
        var actual = sortTopologically(permutation).cycle();

        List<GraphEdge<String, Integer>> cycle = buildCycle(expectedCycle);
        for (int i = 0; i < cycle.size(); i++) {
          var rotated = new ArrayList<>(cycle);
          Collections.rotate(rotated, i);
          if (actual.equals(rotated)) {
            return;
          }
        }
        fail("For tested permutation: " + map(permutation, GraphNode::key) + "\n"
            + "actual cycle = " + actual + "\n"
            + "doesn't match expected cycle (even rotated) = " + cycle);
      }
    }

    private List<GraphEdge<String, Integer>> buildCycle(
        List<GraphNode<Integer, String, String>> nodes) {
      return nodes.stream()
          .map(n -> n.edges().get(0))
          .collect(toList());
    }
  }

  @Nested
  class fails {
    @Test
    public void when_edge_points_to_nonexistent_node() {
      var n1 = node(1, List.of(2));
      var nodes = List.of(n1);

      assertCall(() -> sortTopologically(nodes))
          .throwsException(new IllegalArgumentException(
              "Node '1' has edge pointing to node '2' which does not exist."));
    }

    @Test
    public void when_two_nodes_has_same_key() {
      var n1 = node(1);
      var n1b = node(1);
      var nodes = List.of(n1, n1b);

      assertCall(() -> sortTopologically(nodes))
          .throwsException(IllegalArgumentException.class);
    }
  }

  @Test
  public void sorting_algorithm_has_linear_complexity() {
    AtomicInteger key = new AtomicInteger();
    var topLayer = createLayer(key, List.of());
    var nodes = new ArrayList<>(topLayer);
    int layerCount = 100;
    for (int i = 0; i < layerCount; i++) {
      topLayer = createLayer(key, map(topLayer, GraphNode::key));
      nodes.addAll(topLayer);
    }
    assertTimeoutPreemptively(Duration.ofSeconds(5), () -> sortTopologically(nodes));
  }

  private ArrayList<GraphNode<Integer, String, String>> createLayer(AtomicInteger key,
      List<Integer> edges) {
    var layer = new ArrayList<GraphNode<Integer, String, String>>();
    for (int j = 0; j < 10; j++) {
      layer.add(node(key.getAndIncrement(), edges));
    }
    return layer;
  }

  private static void assertSortTopologically(List<GraphNode<Integer, String, String>> nodes,
      List<List<GraphNode<Integer, String, String>>> expectedOrders) {
    var permutations = permutations(nodes);
    for (List<GraphNode<Integer, String, String>> permutation : permutations) {
      var actual = sortTopologically(permutation).sorted();

      String message = "tested permutation: " + map(permutation, GraphNode::key);
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
    return node(n1, new ArrayList<>());
  }

  private static GraphNode<Integer, String, String> node(Integer key, List<Integer> targetKeys) {
    var edges = map(targetKeys, k -> new GraphEdge<>("->" + k, k));
    return new GraphNode<>(key, "node" + key, edges);
  }
}
