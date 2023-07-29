package org.smoothbuild.common.graph;

public record GraphEdge<E, K>(E value, K targetKey) {
}
