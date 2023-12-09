package org.smoothbuild.common.graph;

import static org.smoothbuild.common.collect.List.listOfAll;

import java.util.Collection;
import org.smoothbuild.common.collect.List;

public record GraphNode<K, V, E>(K key, V value, List<GraphEdge<E, K>> edges) {
  public GraphNode(K key, V value, Collection<GraphEdge<E, K>> edges) {
    this(key, value, listOfAll(edges));
  }

  @Override
  public String toString() {
    String edges = this.edges.map(e -> e.targetKey().toString()).toString(",");
    return key.toString() + "->{" + edges + "}";
  }
}
