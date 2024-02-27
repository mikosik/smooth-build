package org.smoothbuild.common.collect;

import static com.google.common.base.Suppliers.memoize;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Map.mapOfAll;

import com.google.common.base.Supplier;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import org.smoothbuild.common.function.Function1;

/**
 * List of Named.
 *
 * This class is thread-safe.
 */
public class NList<E extends Named> extends AbstractList<E> {
  private static final NList<?> EMPTY = nlist(List.list());

  private final Supplier<List<E>> list;
  private final Supplier<Map<String, E>> map;
  private final Supplier<Map<String, Integer>> indexMap;

  public static <E extends Named> NList<E> nlist() {
    // cast is safe as EMPTY is empty
    @SuppressWarnings("unchecked")
    NList<E> result = (NList<E>) EMPTY;
    return result;
  }

  @SafeVarargs
  public static <E extends Named> NList<E> nlist(E... elems) {
    return nlist(List.list(elems));
  }

  public static <E extends Named> NList<E> nlist(Collection<E> elements) {
    checkContainsNoDuplicatedNames(elements);
    return new NList<>(
        () -> listOfAll(elements), () -> calculateMap(elements), () -> calculateIndexMap(elements));
  }

  private static <E extends Named> void checkContainsNoDuplicatedNames(
      Collection<? extends E> list) {
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

  public static <E extends Named> NList<E> nlist(Map<String, E> map) {
    return new NList<>(
        () -> listOfAll(map.values()), () -> map, () -> calculateIndexMap(map.values()));
  }

  /**
   * Creates nlist which allows elements with duplicated names. When {@link #get(String)}
   * is called and more than one element has given name then the first one is returned.
   */
  public static <E extends Named> NList<E> nlistWithShadowing(Collection<E> list) {
    return new NList<>(
        () -> listOfAll(list), () -> calculateMap(list), () -> calculateIndexMap(list));
  }

  // visible for testing
  NList(
      Supplier<List<E>> list,
      Supplier<Map<String, E>> map,
      Supplier<Map<String, Integer>> indexMap) {
    this.list = memoize(list);
    this.map = memoize(map);
    this.indexMap = memoize(indexMap);
  }

  private static <E extends Named> Map<String, Integer> calculateIndexMap(Iterable<E> nameds) {
    HashMap<String, Integer> builder = new HashMap<>();
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
    return mapOfAll(builder);
  }

  private static <E extends Named> Map<String, E> calculateMap(Iterable<E> nameds) {
    HashMap<String, E> builder = new HashMap<>();
    var names = new HashSet<String>();
    for (E named : nameds) {
      var name = named.name();
      if (!names.contains(name)) {
        builder.put(name, named);
        names.add(name);
      }
    }
    return mapOfAll(builder);
  }

  public <F extends Named, T extends Throwable> NList<F> map(Function1<E, F, T> mapping) throws T {
    return nlist(list().map(mapping));
  }

  public Integer indexOf(String name) {
    Map<String, Integer> stringIntegerImmutableMap = indexMap.get();
    return stringIntegerImmutableMap.get(name);
  }

  public E get(String name) {
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
    return object instanceof NList<?> that && list().equals(that.list());
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
    return list().toString(",");
  }

  public String valuesToPrettyString() {
    return list().toString("\n");
  }

  // overridden methods from java.util.List

  @Override
  public E get(int index) {
    return list().get(index);
  }

  @Override
  public int size() {
    return list().size();
  }

  @Override
  public void forEach(Consumer<? super E> consumer) {
    list().forEach(consumer);
  }

  @Override
  public Spliterator<E> spliterator() {
    return list().spliterator();
  }

  @Override
  public Stream<E> stream() {
    return list().stream();
  }

  @Override
  public Stream<E> parallelStream() {
    return list().parallelStream();
  }

  @Override
  public <A> A[] toArray(IntFunction<A[]> generator) {
    return list().toArray(generator);
  }

  @Deprecated
  @Override
  public boolean removeIf(Predicate<? super E> filter) {
    return list().removeIf(filter);
  }

  @Deprecated
  @Override
  public void replaceAll(UnaryOperator<E> operator) {
    list().replaceAll(operator);
  }

  @Deprecated
  @Override
  public void sort(Comparator<? super E> c) {
    list().sort(c);
  }

  // helper methods

  public List<E> list() {
    return list.get();
  }

  public Map<String, E> map() {
    return map.get();
  }
}