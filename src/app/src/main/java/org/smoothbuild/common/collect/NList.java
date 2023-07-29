package org.smoothbuild.common.collect;

import static com.google.common.base.Suppliers.memoize;
import static org.smoothbuild.common.collect.Iterables.joinToString;
import static org.smoothbuild.common.collect.Iterables.joinWithCommaToString;

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
 * List of Named.
 *
 * This class is thread-safe.
 */
public class NList<T extends Named> extends AbstractList<T> {
  private static final NList<?> EMPTY = nlist(ImmutableList.of());

  private final Supplier<ImmutableList<T>> list;
  private final Supplier<ImmutableMap<String, T>> map;
  private final Supplier<ImmutableMap<String, Integer>> indexMap;

  public static <T extends Named> NList<T> nlist() {
    // cast is safe as EMPTY is empty
    @SuppressWarnings("unchecked")
    NList<T> result = (NList<T>) EMPTY;
    return result;
  }

  @SafeVarargs
  public static <E extends Named> NList<E> nlist(E... elems) {
    return nlist(Lists.list(elems));
  }

  public static <E extends Named> NList<E> nlist(ImmutableList<E> list) {
    checkContainsNoDuplicatedNames(list);
    return new NList<>(
        () -> list,
        () -> calculateMap(list),
        () -> calculateIndexMap(list));
  }

  private static <E extends Named> void checkContainsNoDuplicatedNames(ImmutableList<E> list) {
    HashSet<String> names = new HashSet<>();
    for (E elem : list) {
      String name = elem.name();
      if (names.contains(name)) {
        throw new IllegalArgumentException(
            "List contains two elements with same name = \"" + name + "\".");
      } else {
        names.add(name);
      }
    }
  }

  public static <E extends Named> NList<E> nlist(ImmutableMap<String, E> map) {
    return new NList<>(
        () -> map.values().asList(),
        () -> map,
        () -> calculateIndexMap(map.values()));
  }

  /**
   * Creates nlist which allows elements with duplicated names. When {@link #get(String)}
   * is called and more than one element has given name then the first one is returned.
   */
  public static <E extends Named> NList<E> nlistWithShadowing(ImmutableList<E> list) {
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

  private static <E extends Named> ImmutableMap<String, Integer> calculateIndexMap(
      Iterable<E> nameds) {
    Builder<String, Integer> builder = ImmutableMap.builder();
    var names = new HashSet<String>();
    int i = 0;
    for (E named : nameds) {
      int index = i;
      var name = named.name();
      if (!names.contains(name)) {
        builder.put(name, index);
        names.add(name);
      }
      i++;
    }
    return builder.build();
  }

  private static <E extends Named> ImmutableMap<String, E> calculateMap(Iterable<E> nameds) {
    Builder<String, E> builder = ImmutableMap.builder();
    var names = new HashSet<String>();
    for (E named : nameds) {
      var name = named.name();
      if (!names.contains(name)) {
        builder.put(name, named);
        names.add(name);
      }
    }
    return builder.build();
  }

  public <R  extends Named> NList<R> map(Function<T, R> mapping) {
    return nlist(Lists.map(list(), mapping));
  }

  public Integer indexOf(String name) {
    ImmutableMap<String, Integer> stringIntegerImmutableMap = indexMap.get();
    return stringIntegerImmutableMap.get(name);
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
    return joinWithCommaToString(list());
  }

  public String valuesToPrettyString() {
    return joinToString(list(), "\n");
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
}
