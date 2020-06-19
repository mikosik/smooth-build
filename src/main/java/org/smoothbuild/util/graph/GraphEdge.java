package org.smoothbuild.util.graph;

public record GraphEdge<E, K>(E value, K targetKey) {
}
