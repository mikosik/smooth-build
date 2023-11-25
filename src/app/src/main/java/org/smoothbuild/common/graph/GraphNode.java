package org.smoothbuild.common.graph;

import static java.lang.String.join;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Lists.map;

import java.util.Collection;
import org.smoothbuild.common.collect.List;

public record GraphNode<K, V, E>(K key, V value, List<GraphEdge<E, K>> edges) {
  public GraphNode(K key, V value, Collection<GraphEdge<E, K>> edges) {
    this(key, value, listOfAll(edges));
  }

  @Override
  public String toString() {
    String edges = join(",", map(this.edges, e -> e.targetKey().toString()));
    return key.toString() + "->{" + edges + "}";
  }
}
