package org.smoothbuild.util;

import static java.util.stream.Collectors.joining;

import com.google.common.collect.ImmutableList;

public class IndexedScope<E> {
  private final IndexedScope<E> outerScope;
  private final ImmutableList<E> elements;

  public IndexedScope(ImmutableList<E> elements) {
    this(null, elements);
  }

  public IndexedScope(IndexedScope<E> outerScope, ImmutableList<E> elements) {
    this.outerScope = outerScope;
    this.elements = elements;
  }

  public E get(int index) {
    if (index < elements.size()) {
      return elements.get(index);
    } else if (outerScope == null) {
      throw new IndexOutOfBoundsException(index);
    } else {
      return outerScope.get(index - elements.size());
    }
  }

  @Override
  public String toString() {
    String outer = outerScope == null ? "" : outerScope + "\n";
    String inner = prettyPrint();
    return outer + inner;
  }

  private String prettyPrint() {
    return elements.stream()
        .map(s -> indent() + s)
        .collect(joining("\n"));
  }

  private String indent() {
    return outerScope == null ? "" : outerScope.indent() + "  ";
  }
}
