package org.smoothbuild.util;

import static java.util.stream.Collectors.joining;

import com.google.common.collect.ImmutableList;

public class IndexedScope<E> {
  private final IndexedScope<E> outerScope;
  private final ImmutableList<E> elems;

  public IndexedScope(ImmutableList<E> elems) {
    this(null, elems);
  }

  public IndexedScope(IndexedScope<E> outerScope, ImmutableList<E> elems) {
    this.outerScope = outerScope;
    this.elems = elems;
  }

  public E get(int index) {
    if (index < elems.size()) {
      return elems.get(index);
    } else if (outerScope == null) {
      throw new IndexOutOfBoundsException(index);
    } else {
      return outerScope.get(index - elems.size());
    }
  }

  @Override
  public String toString() {
    String outer = outerScope == null ? "" : outerScope + "\n";
    String inner = prettyPrint();
    return outer + inner;
  }

  private String prettyPrint() {
    return elems.stream()
        .map(s -> indent() + s)
        .collect(joining("\n"));
  }

  private String indent() {
    return outerScope == null ? "" : outerScope.indent() + "  ";
  }
}
