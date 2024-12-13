package org.smoothbuild.compilerfrontend.lang.base;

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
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.function.Function1;

/**
 * List of Named.
 *
 * This class is thread-safe.
 */
public class NList<E extends Identifiable> extends AbstractList<E> {
  private static final NList<?> EMPTY = nlist(List.list());

  private final Supplier<List<E>> list;
  private final Supplier<Map<Id, E>> map;
  private final Supplier<Map<Id, Integer>> indexMap;

  public static <E extends Identifiable> NList<E> nlist() {
    // cast is safe as EMPTY is empty
    @SuppressWarnings("unchecked")
    NList<E> result = (NList<E>) EMPTY;
    return result;
  }

  @SafeVarargs
  public static <E extends Identifiable> NList<E> nlist(E... elems) {
    return nlist(List.list(elems));
  }

  public static <E extends Identifiable> NList<E> nlist(Collection<E> elements) {
    checkContainsNoDuplicatedNames(elements);
    return new NList<>(
        () -> listOfAll(elements), () -> calculateMap(elements), () -> calculateIndexMap(elements));
  }

  private static <E extends Identifiable> void checkContainsNoDuplicatedNames(
      Collection<? extends E> list) {
    HashSet<Id> ids = new HashSet<>();
    for (E elem : list) {
      var name = elem.id();
      if (ids.contains(name)) {
        throw new IllegalArgumentException(
            "List contains two elements with same name = \"" + name + "\".");
      } else {
        ids.add(name);
      }
    }
  }

  public static <E extends Identifiable> NList<E> nlist(Map<Id, E> map) {
    return new NList<>(
        () -> listOfAll(map.values()), () -> map, () -> calculateIndexMap(map.values()));
  }

  /**
   * Creates nlist which allows elements with duplicated names. When {@link #get(Id)}
   * is called and more than one element has given name then the first one is returned.
   */
  public static <E extends Identifiable> NList<E> nlistWithShadowing(Collection<E> list) {
    return new NList<>(
        () -> listOfAll(list), () -> calculateMap(list), () -> calculateIndexMap(list));
  }

  // visible for testing
  NList(Supplier<List<E>> list, Supplier<Map<Id, E>> map, Supplier<Map<Id, Integer>> indexMap) {
    this.list = memoize(list);
    this.map = memoize(map);
    this.indexMap = memoize(indexMap);
  }

  private static <E extends Identifiable> Map<Id, Integer> calculateIndexMap(Iterable<E> nameds) {
    HashMap<Id, Integer> builder = new HashMap<>();
    var names = new HashSet<Id>();
    int i = 0;
    for (E named : nameds) {
      int index = i;
      var name = named.id();
      if (!names.contains(name)) {
        builder.put(name, index);
        names.add(name);
      }
      i++;
    }
    return mapOfAll(builder);
  }

  private static <E extends Identifiable> Map<Id, E> calculateMap(Iterable<E> nameds) {
    HashMap<Id, E> builder = new HashMap<>();
    var names = new HashSet<Id>();
    for (E named : nameds) {
      var name = named.id();
      if (!names.contains(name)) {
        builder.put(name, named);
        names.add(name);
      }
    }
    return mapOfAll(builder);
  }

  public <F extends Identifiable, T extends Throwable> NList<F> map(Function1<E, F, T> mapping)
      throws T {
    return nlist(list().map(mapping));
  }

  public Integer indexOf(Id id) {
    Map<Id, Integer> stringIntegerImmutableMap = indexMap.get();
    return stringIntegerImmutableMap.get(id);
  }

  public E get(Id id) {
    return map().get(id);
  }

  public boolean containsName(Id id) {
    return map().containsKey(id);
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

  public Map<Id, E> map() {
    return map.get();
  }
}
