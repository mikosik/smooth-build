package org.smoothbuild.lang.type.api;

import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

public interface VarSet<T extends Type> {
  public boolean contains(T type);

  public boolean containsAll(VarSet<T> subset);

  public boolean isEmpty();

  public Stream<T> stream();

  public ImmutableList<T> asList();
}
