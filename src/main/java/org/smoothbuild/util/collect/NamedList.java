package org.smoothbuild.util.collect;

import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class NamedList<T extends Nameable> {
  private static final NamedList<?> EMPTY = new NamedList<>(ImmutableList.of());

  private final ImmutableList<T> list;
  private final ImmutableMap<String, T> map;
  private final ImmutableMap<String, Integer> indexMap;

  public static <E extends Nameable> NamedList<E> namedList(ImmutableList<E> list) {
    return new NamedList<>(list);
  }

  private NamedList(ImmutableList<T> list) {
    this.list = list;
    this.map = calculateMap(list);
    this.indexMap = calculateIndexMap(list);
  }

  public static <T extends Nameable> NamedList<T> empty() {
    // cast is safe as EMPTY is empty
    return (NamedList<T>) EMPTY;
  }

  public ImmutableList<T> list() {
    return list;
  }

  public <R  extends Nameable> NamedList<R> map(Function<T, R> mapping) {
    return new NamedList<>(Lists.map(list, mapping));
  }

  public T get(String name) {
    return map.get(name);
  }

  public boolean contains(String name) {
    return map.containsKey(name);
  }

  public int size() {
    return list.size();
  }

  public ImmutableMap<String, Integer> indexMap() {
    return indexMap;
  }

  private static <E extends Nameable> ImmutableMap<String, Integer> calculateIndexMap(
      ImmutableList<E> nameables) {
    Builder<String, Integer> builder = ImmutableMap.builder();
    for (int i = 0; i < nameables.size(); i++) {
      int index = i;
      nameables.get(i).nameO().ifPresent(n -> builder.put(n, index));
    }
    return builder.build();
  }

  private static <E extends Nameable> ImmutableMap<String, E> calculateMap(
      ImmutableList<E> nameables) {
    Builder<String, E> builder = ImmutableMap.builder();
    for (E nameable : nameables) {
      nameable.nameO().ifPresent(n -> builder.put(n, nameable));
    }
    return builder.build();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof NamedList<?> that
        && list.equals(that.list);
  }

  @Override
  public int hashCode() {
    return Objects.hash(list);
  }

  @Override
  public String toString() {
    return "NamedList(" + valuesToString() + ")";
  }

  public String valuesToString() {
    return toCommaSeparatedString(list);
  }
}
