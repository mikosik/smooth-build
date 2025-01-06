package org.smoothbuild.compilerfrontend.lang.name;

import static com.google.common.base.Suppliers.memoize;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Map.mapOfAll;

import com.google.common.base.Supplier;
import java.util.AbstractList;
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
import org.smoothbuild.common.collect.Collection;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.compilerfrontend.lang.base.HasName;

/**
 * List of Named.
 *
 * This class is thread-safe.
 */
public class NList<E extends HasName> extends AbstractList<E> {
  private static final NList<?> EMPTY = nlist(List.list());

  private final Supplier<List<E>> list;
  private final Supplier<Map<Name, E>> map;
  private final Supplier<Map<Name, Integer>> indexMap;

  public static <E extends HasName> NList<E> nlist() {
    // cast is safe as EMPTY is empty
    @SuppressWarnings("unchecked")
    NList<E> result = (NList<E>) EMPTY;
    return result;
  }

  @SafeVarargs
  public static <E extends HasName> NList<E> nlist(E... elems) {
    return nlist(List.list(elems));
  }

  public static <E extends HasName> NList<E> nlist(Collection<E> elements) {
    checkContainsNoDuplicatedNames(elements);
    return new NList<>(
        () -> listOfAll(elements), () -> calculateMap(elements), () -> calculateIndexMap(elements));
  }

  private static <E extends HasName> void checkContainsNoDuplicatedNames(
      Collection<? extends E> list) {
    HashSet<Name> names = new HashSet<>();
    for (E elem : list) {
      var name = elem.name();
      if (names.contains(name)) {
        throw new IllegalArgumentException(
            "List contains two elements with same name = \"" + name + "\".");
      } else {
        names.add(name);
      }
    }
  }

  public static <E extends HasName> NList<E> nlist(Map<Name, E> map) {
    return new NList<>(
        () -> listOfAll(map.values()), () -> map, () -> calculateIndexMap(map.values()));
  }

  /**
   * Creates nlist which allows elements with duplicated names. When {@link #get(Name)}
   * is called and more than one element has given name then the first one is returned.
   */
  public static <E extends HasName> NList<E> nlistWithShadowing(Collection<E> list) {
    return new NList<>(
        () -> listOfAll(list), () -> calculateMap(list), () -> calculateIndexMap(list));
  }

  // visible for testing
  NList(Supplier<List<E>> list, Supplier<Map<Name, E>> map, Supplier<Map<Name, Integer>> indexMap) {
    this.list = memoize(list);
    this.map = memoize(map);
    this.indexMap = memoize(indexMap);
  }

  private static <E extends HasName> Map<Name, Integer> calculateIndexMap(Iterable<E> elements) {
    HashMap<Name, Integer> builder = new HashMap<>();
    var names = new HashSet<Name>();
    int i = 0;
    for (E element : elements) {
      int index = i;
      var name = element.name();
      if (!names.contains(name)) {
        builder.put(name, index);
        names.add(name);
      }
      i++;
    }
    return mapOfAll(builder);
  }

  private static <E extends HasName> Map<Name, E> calculateMap(Iterable<E> elements) {
    HashMap<Name, E> builder = new HashMap<>();
    var names = new HashSet<Name>();
    for (E element : elements) {
      var name = element.name();
      if (!names.contains(name)) {
        builder.put(name, element);
        names.add(name);
      }
    }
    return mapOfAll(builder);
  }

  public <F extends HasName, T extends Throwable> NList<F> map(Function1<E, F, T> mapping)
      throws T {
    return nlist(list().map(mapping));
  }

  public int indexOf(Name name) {
    return indexMap.get().get(name);
  }

  public E get(Name name) {
    return map().get(name);
  }

  public boolean containsName(Name name) {
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
    return "NList(" + list().toString(",") + ")";
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
    return list().stream().parallel();
  }

  @Override
  public <A> A[] toArray(IntFunction<A[]> generator) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public boolean removeIf(Predicate<? super E> filter) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public void replaceAll(UnaryOperator<E> operator) {
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public void sort(Comparator<? super E> c) {
    throw new UnsupportedOperationException();
  }

  // helper methods

  public List<E> list() {
    return list.get();
  }

  public Map<Name, E> map() {
    return map.get();
  }
}
