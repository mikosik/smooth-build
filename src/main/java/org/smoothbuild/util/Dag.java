package org.smoothbuild.util;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class Dag<T> {
  private final T elem;
  private final List<Dag<T>> children;

  public Dag(T elem) {
    this(elem, ImmutableList.of());
  }

  public Dag(T elem, List<Dag<T>> children) {
    this.elem = elem;
    this.children = ImmutableList.copyOf(children);
  }

  public T elem() {
    return elem;
  }

  public List<Dag<T>> children() {
    return children;
  }
}
