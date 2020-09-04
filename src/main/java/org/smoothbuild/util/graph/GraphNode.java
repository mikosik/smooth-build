package org.smoothbuild.util.graph;

import static java.lang.String.join;
import static org.smoothbuild.util.Lists.map;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

public record GraphNode<K, V, E> (K key, V value, ImmutableList<GraphEdge<E, K>>edges) {
  public GraphNode(K key, V value, Collection<GraphEdge<E, K>> edges) {
    this(key, value, ImmutableList.copyOf(edges));
  }

  @Override
  public String toString() {
    String edges = join(",", map(this.edges, e -> e.targetKey().toString()));
    return key.toString() + "->{" + edges + "}";
  }
}
