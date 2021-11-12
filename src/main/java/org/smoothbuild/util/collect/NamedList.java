package org.smoothbuild.util.collect;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.AbstractList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class NamedList<T extends Nameable> extends AbstractList<T> {
  private static final NamedList<?> EMPTY = new NamedList<>(ImmutableList.of());

  private final ImmutableList<T> list;
  private final ImmutableMap<String, T> map;
  private final ImmutableMap<String, Integer> indexMap;

  public static <E extends Nameable> NamedList<E> namedList(E... elements) {
    return new NamedList<>(list(elements));
  }

  public static <E extends Nameable> NamedList<E> namedList(ImmutableList<E> list) {
    return new NamedList<>(list);
  }

  public static <E extends Nameable> NamedList<E> namedList(ImmutableMap<String, E> map) {
    return new NamedList<>(map.values().asList(), map);
  }

  public static <E extends Nameable> NamedList<E> namedListWithDuplicates(ImmutableList<E> list) {
    var withoutDuplicates = list.stream().collect(toImmutableSet());
    return new NamedList<>(list, calculateMap(withoutDuplicates),
        calculateIndexMap(withoutDuplicates));
  }

  private NamedList(ImmutableList<T> list) {
    this(list, calculateMap(list), calculateIndexMap(list));
  }

  private NamedList(ImmutableList<T> list, ImmutableMap<String, T> map) {
    this(list, map, calculateIndexMap(list));
  }

  private NamedList(ImmutableList<T> list, ImmutableMap<String, T> map,
      ImmutableMap<String, Integer> indexMap) {
    this.list = list;
    this.map = map;
    this.indexMap = indexMap;
  }

  public static <T extends Nameable> NamedList<T> empty() {
    // cast is safe as EMPTY is empty
    return (NamedList<T>) EMPTY;
  }

  private static <E extends Nameable> ImmutableMap<String, Integer> calculateIndexMap(
      Iterable<E> nameables) {
    Builder<String, Integer> builder = ImmutableMap.builder();
    int i = 0;
    for (E nameable : nameables) {
      int index = i;
      nameable.nameO().ifPresent(n -> builder.put(n, index));
      i++;
    }
    return builder.build();
  }

  private static <E extends Nameable> ImmutableMap<String, E> calculateMap(Iterable<E> nameables) {
    Builder<String, E> builder = ImmutableMap.builder();
    for (E nameable : nameables) {
      nameable.nameO().ifPresent(n -> builder.put(n, nameable));
    }
    return builder.build();
  }

  public <R  extends Nameable> NamedList<R> map(Function<T, R> mapping) {
    return new NamedList<>(Lists.map(list, mapping));
  }

  public T get(String name) {
    return map.get(name);
  }

  public boolean containsName(String name) {
    return map.containsKey(name);
  }

  public ImmutableMap<String, Integer> indexMap() {
    return indexMap;
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

  // overriden methods from List<T>

  @Override
  public T get(int index) {
    return list.get(index);
  }

  @Override
  public int size() {
    return list.size();
  }

  @Override
  public void forEach(Consumer<? super T> consumer) {
    list.forEach(consumer);
  }

  @Override
  public Spliterator<T> spliterator() {
    return list.spliterator();
  }

  @Override
  public Stream<T> stream() {
    return list.stream();
  }

  @Override
  public Stream<T> parallelStream() {
    return list.parallelStream();
  }

  @Override
  public <T1> T1[] toArray(IntFunction<T1[]> generator) {
    return list.toArray(generator);
  }

  @Deprecated
  @Override
  public boolean removeIf(Predicate<? super T> filter) {
    return list.removeIf(filter);
  }

  @Deprecated
  @Override
  public void replaceAll(UnaryOperator<T> operator) {
    list.replaceAll(operator);
  }

  @Deprecated
  @Override
  public void sort(Comparator<? super T> c) {
    list.sort(c);
  }

  // helper methods
}
