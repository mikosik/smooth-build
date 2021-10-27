package org.smoothbuild.util.collect;

import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;
import static org.smoothbuild.util.collect.Named.named;

import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class NamedList<T> {
  private final ImmutableList<Named<T>> list;
  private final ImmutableMap<String, T> map;
  private final ImmutableMap<String, Integer> indexMap;

  public static <E> NamedList<E> namedList(ImmutableList<Named<E>> list) {
    return new NamedList<>(list);
  }

  public NamedList(ImmutableList<Named<T>> list) {
    this.list = list;
    this.map = calculateMap(list);
    this.indexMap = calculateIndexMap(list);
  }

  public ImmutableList<Named<T>> list() {
    return list;
  }

  public ImmutableMap<String, T> map() {
    return map;
  }

  public boolean contains(String name) {
    return map.containsKey(name);
  }

  public T getObject(int index) {
    return list.get(index).object();
  }

  public int size() {
    return list.size();
  }

  public ImmutableMap<String, Integer> indexMap() {
    return indexMap;
  }

  public <R> NamedList<R> mapObjects(Function<T, R> mapper) {
    return new NamedList<>(Lists.map(list, n -> named(n.name(), mapper.apply(n.object()))));
  }

  private static <E> ImmutableMap<String, Integer> calculateIndexMap(
      ImmutableList<Named<E>> nameds) {
    Builder<String, Integer> builder = ImmutableMap.builder();
    for (int i = 0; i < nameds.size(); i++) {
      int index = i;
      nameds.get(i).name().ifPresent(n -> builder.put(n, index));
    }
    return builder.build();
  }

  private static <E> ImmutableMap<String, E> calculateMap(ImmutableList<Named<E>> nameds) {
    Builder<String, E> builder = ImmutableMap.builder();
    for (Named<E> named : nameds) {
      named.name().ifPresent(n -> builder.put(n, named.object()));
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
