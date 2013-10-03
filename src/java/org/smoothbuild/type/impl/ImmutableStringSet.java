package org.smoothbuild.type.impl;

import java.util.Iterator;

import org.smoothbuild.type.api.StringSet;

import com.google.common.collect.ImmutableSet;

public class ImmutableStringSet implements StringSet {
  private final ImmutableSet<String> set;

  public ImmutableStringSet(Iterable<String> set) {
    this.set = ImmutableSet.copyOf(set);
  }

  @Override
  public Iterator<String> iterator() {
    return set.iterator();
  }

  @Override
  public boolean contains(String string) {
    return set.contains(string);
  }
}
