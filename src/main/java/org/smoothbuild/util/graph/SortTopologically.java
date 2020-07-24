package org.smoothbuild.util.graph;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.util.Lists.filter;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.graph.SortTopologically.Node.State.BEING_PROCESSED;
import static org.smoothbuild.util.graph.SortTopologically.Node.State.NOT_VISITED;
import static org.smoothbuild.util.graph.SortTopologically.Node.State.PROCESSED;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

public class SortTopologically {
  public static <K, N, E> TopologicalSortingResult<K, N, E> sortTopologically(
      Collection<GraphNode<K, N, E>> nodes) {
    if (nodes.isEmpty()) {
      return new TopologicalSortingResult<>(ImmutableList.of(), null);
    }
    ImmutableList<Node<K, N, E>> wrappedNodes = nodes.stream()
        .map(Node::new)
        .collect(toImmutableList());
    return sortTopologicallyImpl(wrappedNodes);
  }

  public static <K, N, E> TopologicalSortingResult<K, N, E> sortTopologicallyImpl(
      ImmutableList<Node<K, N, E>> nodes) {

    // For each root node (node without incoming edges) algorithm processes it with the following
    // VISITING sub-algorithm.
    // (If there's no root node then any node is chosen as root and algorithm will detect a cycle.)
    //
    // VISITING sub-algorithm:
    //   (1) Mark current node as BEING-PROCESSED.
    //   (2) Add current node to the end of `currentPath`.
    //   (3) For each node reachable via direct edge from that node:
    //      IF current node is marked as BEING-PROCESSED
    //        THEN End algorithm and return current  node and all nodes that succeed it in
    //             `currentPath` as a cycle.
    //      ELSE IF current node is marked as PROCESSED
    //        THEN Do nothing.
    //      ELSE
    //        Invoke VISITING sub-algorithm for that reachable node. This is iterative
    //        implementation of recursive algorithm. Recursion is simulated by stack handled in
    //        `currentPath`.
    //   (4) Mark current node as PROCESSED.
    //   (5) Remove current node from `currentPath`.
    //   (6) Add current node to the beginning of `resultSequence`.
    //
    // If algorithm completes and all nodes are marked as PROCESSED then `currentPath` contains
    // topologically sorted nodes.
    // If some nodes are not marked as PROCESSED then they form an island (or many islands)
    // that doesn't have root node thus contains a cycle(s). Running algorithm just for those
    // nodes will detect that cycle.

    var rootKeys = findRootNodes(nodes);
    if (rootKeys.isEmpty()) {
      rootKeys = Set.of(nodes.iterator().next().key());
    }

    var keyToNode = nodes.stream().collect(toImmutableMap(Node::key, n -> n));
    var currentPath = new LinkedList<PathElem<K, N, E>>();
    var resultSequence = new ArrayDeque<Node<K, N, E>>(nodes.size());

    for (K rootKey : rootKeys) {
      addToPath(currentPath, keyToNode.get(rootKey));
      while (!currentPath.isEmpty()) {
        var pathEnd = currentPath.peekLast();
        int edgeIndex = pathEnd.incrementAndGetEdgeIndex();
        if (edgeIndex < pathEnd.node().edges().size()) {
          K targetKey = pathEnd.node().edges().get(edgeIndex).targetKey();
          var targetNode = keyToNode.get(targetKey);
          switch (targetNode.state()) {
            case NOT_VISITED:
              addToPath(currentPath, targetNode);
              break;
            case BEING_PROCESSED:
              return createCycleResult(currentPath, targetKey);
            case PROCESSED:
              break;
          }
        } else {
          var processedNode = currentPath.removeLast().node();
          processedNode.setState(PROCESSED);
          resultSequence.addFirst(processedNode);
        }
      }
    }

    if (resultSequence.size() == nodes.size()) {
      return createSortedResult(resultSequence);
    } else {
      return findCycleInUnprocessedNodes(nodes);
    }
  }

  private static <K, N, E> Set<K> findRootNodes(Collection<Node<K, N, E>> nodes) {
    var result = nodes.stream().map(Node::key).collect(toSet());
    assertAllEdgesPointToExistingNodes(nodes, result);
    for (var node : nodes) {
      for (var edge : node.edges()) {
        result.remove(edge.targetKey());
      }
    }
    return result;
  }

  private static <K, N, E> void assertAllEdgesPointToExistingNodes(
      Collection<Node<K, N, E>> nodes, Set<K> keys) {
    for (var node : nodes) {
      for (var edge : node.edges()) {
        if (!keys.contains(edge.targetKey())) {
          throw new IllegalArgumentException("Node '" + node.key()
              + "' has edge pointing to node '" + edge.targetKey() + "' which does not exist.");
        }
      }
    }
  }

  private static <K, N, E> void addToPath(LinkedList<PathElem<K, N, E>> currentPath,
      Node<K, N, E> node) {
    node.setState(BEING_PROCESSED);
    currentPath.addLast(new PathElem<>(node));
  }

  private static <K, N, E> TopologicalSortingResult<K, N, E> createSortedResult(
      ArrayDeque<Node<K, N, E>> resultSequence) {
    var graphNodes = resultSequence.stream()
        .map(Node::node)
        .collect(toImmutableList());
    return new TopologicalSortingResult<>(graphNodes, null);
  }

  private static <K, N, E> TopologicalSortingResult<K, N, E> createCycleResult(
      LinkedList<PathElem<K, N, E>> currentPath, K key) {
    var cycle = currentPath.stream()
        .dropWhile(e -> !e.node().key().equals(key))
        .map(elem -> elem.node().edges().get(elem.edgeIndex()))
        .collect(toList());
    return new TopologicalSortingResult<>(null, cycle);
  }

  private static <K, N, E> TopologicalSortingResult<K, N, E> findCycleInUnprocessedNodes(
      ImmutableList<Node<K, N, E>> nodes) {
    var notVisited = filter(nodes, n -> n.state() == NOT_VISITED);
    return sortTopologicallyImpl(notVisited);
  }

  private static class PathElem<K, N, E> {
    private final Node<K, N, E> node;
    private int edgeIndex;

    public PathElem(Node<K, N, E> node) {
      this.node = node;
      this.edgeIndex = -1;
    }

    public Node<K, N, E> node() {
      return node;
    }

    public int edgeIndex() {
      return edgeIndex;
    }

    public int incrementAndGetEdgeIndex() {
      return ++edgeIndex;
    }
  }

  public static class Node<K, N, E> {
    public enum State { NOT_VISITED, BEING_PROCESSED, PROCESSED }

    private final GraphNode<K, N, E> node;
    private State state;

    public Node(GraphNode<K, N, E> node) {
      this.node = node;
      this.state = NOT_VISITED;
    }

    public GraphNode<K, N, E> node() {
      return node;
    }

    public State state() {
      return state;
    }

    public void setState(State state) {
      this.state = state;
    }

    public K key() {
      return node.key();
    }

    public ImmutableList<GraphEdge<E, K>> edges() {
      return node.edges();
    }

    @Override
    public int hashCode() {
      return key().hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o instanceof Node<?, ?, ?> that) {
        return this.node.equals(that.node);
      }
      return false;
    }
  }

  public static record TopologicalSortingResult<K, N, E>(
      List<GraphNode<K, N, E>>sorted,
      List<GraphEdge<E, K>> cycle) {
    public ImmutableList<N> valuesReversed() {
      return map(sorted(), GraphNode::value).reverse();
    }
  }
}
