package org.smoothbuild.common.graph;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.common.graph.SortTopologically.Node.State.BEING_PROCESSED;
import static org.smoothbuild.common.graph.SortTopologically.Node.State.NOT_VISITED;
import static org.smoothbuild.common.graph.SortTopologically.Node.State.PROCESSED;

import java.util.ArrayDeque;
import java.util.LinkedList;
import org.smoothbuild.common.collect.Collection;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Set;

public class SortTopologically {
  public static <K, N, E> TopologicalSortingRes<K, N, E> sortTopologically(
      Collection<GraphNode<K, N, E>> nodes) {
    if (nodes.isEmpty()) {
      return new TopologicalSortingRes<>(list(), null);
    }
    List<Node<K, N, E>> wrappedNodes = listOfAll(nodes).map(Node::new);
    assertAllEdgesPointToExistingNodes(wrappedNodes);
    return sortTopologicallyImpl(wrappedNodes);
  }

  private static <K, N, E> void assertAllEdgesPointToExistingNodes(List<Node<K, N, E>> nodes) {
    var keys = nodes.map(Node::key).toSet();
    for (var node : nodes) {
      for (var edge : node.edges()) {
        if (!keys.contains(edge.targetKey())) {
          throw new IllegalArgumentException("Node '" + node.key() + "' has edge pointing to node '"
              + edge.targetKey() + "' which does not exist.");
        }
      }
    }
  }

  public static <K, N, E> TopologicalSortingRes<K, N, E> sortTopologicallyImpl(
      List<Node<K, N, E>> nodes) {

    // For each root node (node without incoming edges) algorithm processes it with the following
    // VISITING sub-algorithm.
    // (If there's no root node then any node is chosen as root and algorithm will detect a cycle.)
    //
    // VISITING sub-algorithm:
    //   (1) Mark current node as BEING-PROCESSED.
    //   (2) Add current node to the end of `currentPath`.
    //   (3) For each node reachable via direct edge from that node AND present in input nodes:
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
    // If some nodes are not marked as PROCESSED then our graph contains cycle.
    // Running algorithm just for those not-PROCESSED nodes will detect that cycle.

    var rootKeys = findRootNodes(nodes);
    if (rootKeys.isEmpty()) {
      rootKeys = set(nodes.get(0).key());
    }

    var keyToNode = nodes.toMap(Node::key, n -> n);
    var currentPath = new LinkedList<PathElem<K, N, E>>();
    var resultSeq = new ArrayDeque<Node<K, N, E>>(nodes.size());

    for (K rootKey : rootKeys) {
      addToPath(currentPath, keyToNode.get(rootKey));
      while (!currentPath.isEmpty()) {
        var last = currentPath.peekLast();
        int edgeIndex = last.incrementAndGetEdgeIndex();
        if (edgeIndex < last.node().edges().size()) {
          K targetKey = last.node().edges().get(edgeIndex).targetKey();
          var targetNode = keyToNode.get(targetKey);
          if (targetNode != null) {
            switch (targetNode.state()) {
              case NOT_VISITED:
                addToPath(currentPath, targetNode);
                break;
              case BEING_PROCESSED:
                return createCycleRes(currentPath, targetKey);
              case PROCESSED:
                break;
            }
          }
        } else {
          var processedNode = currentPath.removeLast().node();
          processedNode.setState(PROCESSED);
          resultSeq.addFirst(processedNode);
        }
      }
    }

    if (resultSeq.size() == nodes.size()) {
      return createSortedRes(resultSeq);
    } else {
      return findCycleInUnprocessedNodes(nodes);
    }
  }

  private static <K, N, E> Set<K> findRootNodes(List<Node<K, N, E>> nodes) {
    var result = nodes.map(Node::key).toSet();
    for (var node : nodes) {
      result = result.removeAll(node.edges().map(GraphEdge::targetKey));
    }
    return result;
  }

  private static <K, N, E> void addToPath(
      LinkedList<PathElem<K, N, E>> currentPath, Node<K, N, E> node) {
    node.setState(BEING_PROCESSED);
    currentPath.addLast(new PathElem<>(node));
  }

  private static <K, N, E> TopologicalSortingRes<K, N, E> createSortedRes(
      ArrayDeque<Node<K, N, E>> resultSeq) {
    var graphNodes = listOfAll(resultSeq).map(Node::node);
    return new TopologicalSortingRes<>(graphNodes, null);
  }

  private static <K, N, E> TopologicalSortingRes<K, N, E> createCycleRes(
      LinkedList<PathElem<K, N, E>> currentPath, K key) {
    var cycle = listOfAll(currentPath)
        .dropWhile(e -> !e.node().key().equals(key))
        .map(elem -> elem.node().edges().get(elem.edgeIndex()));
    return new TopologicalSortingRes<>(null, cycle);
  }

  private static <K, N, E> TopologicalSortingRes<K, N, E> findCycleInUnprocessedNodes(
      List<Node<K, N, E>> nodes) {
    var notVisited = nodes.filter(n -> n.state() == NOT_VISITED);
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
    public enum State {
      NOT_VISITED,
      BEING_PROCESSED,
      PROCESSED
    }

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

    public List<GraphEdge<E, K>> edges() {
      return node.edges();
    }

    @Override
    public int hashCode() {
      return key().hashCode();
    }

    @Override
    public boolean equals(Object object) {
      if (this == object) {
        return true;
      }
      return object instanceof Node<?, ?, ?> that && this.node.equals(that.node);
    }
  }

  public static record TopologicalSortingRes<K, N, E>(
      List<GraphNode<K, N, E>> sorted, List<GraphEdge<E, K>> cycle) {
    public List<N> valuesReversed() {
      return sorted().map(GraphNode::value).reverse();
    }
  }
}
