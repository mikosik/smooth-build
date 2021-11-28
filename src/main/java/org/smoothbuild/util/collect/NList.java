package org.smoothbuild.util.collect;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.AbstractList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class NList<T extends Nameable> extends AbstractList<T> {
  private static final NList<?> EMPTY = nList(ImmutableList.of());

  private final Supplier<ImmutableList<T>> list;
  private final Supplier<ImmutableMap<String, T>> map;
  private final Supplier<ImmutableMap<String, Integer>> indexMap;

  public static <T extends Nameable> NList<T> nList() {
    // cast is safe as EMPTY is empty
    return (NList<T>) EMPTY;
  }

  public static <E extends Nameable> NList<E> nList(E... elems) {
    return nList(Lists.list(elems));
  }

  public static <E extends Nameable> NList<E> nList(ImmutableList<E> list) {
    return new NList<>(
        () -> list,
        () -> calculateMap(list),
        () -> calculateIndexMap(list));
  }

  public static <E extends Nameable> NList<E> nList(ImmutableMap<String, E> map) {
    return new NList<>(
        () -> map.values().asList(),
        () -> map,
        () -> calculateIndexMap(map.values()));
  }

  public static <E extends Nameable> NList<E> nListWithDuplicates(ImmutableList<E> list) {
    var withoutDuplicates = list.stream().collect(toImmutableSet());
    return new NList<>(
        () -> list,
        () -> calculateMap(withoutDuplicates),
        () -> calculateIndexMap(withoutDuplicates));
  }

  private NList(
      Supplier<ImmutableList<T>> list,
      Supplier<ImmutableMap<String, T>> map,
      Supplier<ImmutableMap<String, Integer>> indexMap) {
    this.list = list;
    this.map = map;
    this.indexMap = indexMap;
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

  public <R  extends Nameable> NList<R> map(Function<T, R> mapping) {
    return nList(Lists.map(list(), mapping));
  }

  public T get(String name) {
    return map().get(name);
  }

  public boolean containsName(String name) {
    return map().containsKey(name);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof NList<?> that
        && list().equals(that.list());
  }

  @Override
  public int hashCode() {
    return Objects.hash(list());
  }

  @Override
  public String toString() {
    return "NList(" + valuesToString() + ")";
  }

  public String valuesToString() {
    return toCommaSeparatedString(list());
  }

  // overriden methods from List<T>

  @Override
  public T get(int index) {
    return list().get(index);
  }

  @Override
  public int size() {
    return list().size();
  }

  @Override
  public void forEach(Consumer<? super T> consumer) {
    list().forEach(consumer);
  }

  @Override
  public Spliterator<T> spliterator() {
    return list().spliterator();
  }

  @Override
  public Stream<T> stream() {
    return list().stream();
  }

  @Override
  public Stream<T> parallelStream() {
    return list().parallelStream();
  }

  @Override
  public <T1> T1[] toArray(IntFunction<T1[]> generator) {
    return list().toArray(generator);
  }

  @Deprecated
  @Override
  public boolean removeIf(Predicate<? super T> filter) {
    return list().removeIf(filter);
  }

  @Deprecated
  @Override
  public void replaceAll(UnaryOperator<T> operator) {
    list().replaceAll(operator);
  }

  @Deprecated
  @Override
  public void sort(Comparator<? super T> c) {
    list().sort(c);
  }

  // helper methods

  private ImmutableList<T> list() {
    return list.get();
  }

  private ImmutableMap<String, T> map() {
    return map.get();
  }

  public ImmutableMap<String, Integer> indexMap() {
    return indexMap.get();
  }
}
