package org.smoothbuild.type.impl;

import java.util.Iterator;

import org.smoothbuild.plugin.StringSet;

import com.google.common.collect.ImmutableList;

public class ImmutableStringSet implements StringSet {
  private final ImmutableList<String> elements;

  public ImmutableStringSet(Iterable<String> elements) {
    this.elements = ImmutableList.copyOf(elements);
  }

  @Override
  public Iterator<String> iterator() {
    return elements.iterator();
  }

}
