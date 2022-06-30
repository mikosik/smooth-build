package org.smoothbuild.util.collect;

import static com.google.common.base.Suppliers.memoize;
import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.AbstractList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * This class is thread-safe.
 */
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
    checkContainsNoDuplicatedNames(list);
    return new NList<>(
        () -> list,
        () -> calculateMap(list),
        () -> calculateIndexMap(list));
  }

  private static <E extends Nameable> void checkContainsNoDuplicatedNames(ImmutableList<E> list) {
    HashSet<String> names = new HashSet<>();
    for (E elem : list) {
      if (elem.nameO().isPresent()) {
        String name = elem.nameO().get();
        if (names.contains(name)) {
          throw new IllegalArgumentException(
              "List contains two elements with same name = \"" + name + "\".");
        } else {
          names.add(name);
        }
      }
    }
  }

  public static <E extends Nameable> NList<E> nList(ImmutableMap<String, E> map) {
    return new NList<>(
        () -> map.values().asList(),
        () -> map,
        () -> calculateIndexMap(map.values()));
  }

  public static <E extends Nameable> NList<E> nListWithNonUniqueNames(ImmutableList<E> list) {
    return new NList<>(
        () -> list,
        () -> calculateMap(list),
        () -> calculateIndexMap(list));
  }

  // visible for testing
  NList(
      Supplier<ImmutableList<T>> list,
      Supplier<ImmutableMap<String, T>> map,
      Supplier<ImmutableMap<String, Integer>> indexMap) {
    this.list = memoize(list);
    this.map = memoize(map);
    this.indexMap = memoize(indexMap);
  }

  private static <E extends Nameable> ImmutableMap<String, Integer> calculateIndexMap(
      Iterable<E> nameables) {
    Builder<String, Integer> builder = ImmutableMap.builder();
    var names = new HashSet<String>();
    int i = 0;
    for (E nameable : nameables) {
      int index = i;
      nameable.nameO().ifPresent(n -> {
        if (!names.contains(n)) {
          builder.put(n, index);
          names.add(n);
        }
      });
      i++;
    }
    return builder.build();
  }

  private static <E extends Nameable> ImmutableMap<String, E> calculateMap(Iterable<E> nameables) {
    Builder<String, E> builder = ImmutableMap.builder();
    var names = new HashSet<String>();
    for (E nameable : nameables) {
      nameable.nameO().ifPresent(n -> {
        if (!names.contains(n)) {
          builder.put(n, nameable);
          names.add(n);
        }
      });
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

  public ImmutableList<T> list() {
    return list.get();
  }

  public ImmutableMap<String, T> map() {
    return map.get();
  }

  public ImmutableMap<String, Integer> indexMap() {
    return indexMap.get();
  }
}
