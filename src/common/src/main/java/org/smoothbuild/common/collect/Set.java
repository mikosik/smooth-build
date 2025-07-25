package org.smoothbuild.common.collect;

import static com.google.common.collect.ImmutableSortedSet.toImmutableSortedSet;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Map.zipToMap;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.Stream;
import org.smoothbuild.common.function.Function1;

/**
 * Immutable set.
 */
public non-sealed class Set<E> implements Collection<E> {
  private final ImmutableSet<E> set;

  @SafeVarargs
  public static <E> Set<E> set(E... elements) {
    return new Set<>(ImmutableSet.copyOf(elements));
  }

  public static <E> Set<E> setOfAll(Iterable<E> iterable) {
    if (iterable instanceof Set<E> set) {
      return set;
    }
    return new Set<>(ImmutableSet.copyOf(iterable));
  }

  protected Set(ImmutableSet<E> set) {
    this.set = set;
  }

  public Set<E> unionWith(Iterable<? extends E> elements) {
    Builder<E> builder = ImmutableSet.builder();
    builder.addAll(set);
    builder.addAll(elements);
    return new Set<>(builder.build());
  }

  public <F, T extends Throwable> Set<F> map(Function1<? super E, F, T> mapper) throws T {
    Builder<F> builder = ImmutableSet.builderWithExpectedSize(set.size());
    for (E element : set) {
      builder.add(mapper.apply(element));
    }
    return new Set<>(builder.build());
  }

  public <T extends Throwable> Set<E> filter(Function1<E, Boolean, T> predicate) throws T {
    Builder<E> builder = ImmutableSet.builder();
    for (E element : set) {
      if (predicate.apply(element)) {
        builder.add(element);
      }
    }
    return new Set<>(builder.build());
  }

  public Set<E> removeAll(Collection<?> toRemove) {
    Builder<E> builder = ImmutableSet.builder();
    for (E element : set) {
      if (!toRemove.contains(element)) {
        builder.add(element);
      }
    }
    return new Set<>(builder.build());
  }

  public Set<E> sort(Comparator<? super E> comparator) {
    return new Set<>(set.stream().collect(toImmutableSortedSet(comparator)));
  }

  @Override
  public <V, T extends Throwable> Map<E, V> toMap(Function1<? super E, V, T> mapper) throws T {
    return zipToMap(this, this.map(mapper));
  }

  // methods from Collection interface

  @Override
  public boolean isEmpty() {
    return set.isEmpty();
  }

  @Override
  public boolean contains(Object object) {
    return set.contains(object);
  }

  @Override
  public Stream<E> stream() {
    return set.stream();
  }

  @Override
  public Iterator<E> iterator() {
    return set.iterator();
  }

  @Override
  public Spliterator<E> spliterator() {
    return set.spliterator();
  }

  @Override
  public int size() {
    return set.size();
  }

  public Set<E> addAll(java.util.Collection<? extends E> collection) {
    Builder<E> builder = ImmutableSet.builderWithExpectedSize(set.size() + collection.size());
    builder.addAll(set);
    builder.addAll(collection);
    return new Set<>(builder.build());
  }

  public Set<E> addAll(Collection<? extends E> collection) {
    return switch (collection) {
      case List<? extends E> l -> addAll(l);
      case Set<? extends E> s -> addAll(s);
    };
  }

  public Set<E> addAll(Set<? extends E> set) {
    return addAll(set.set);
  }

  public Set<E> addAll(List<? extends E> list) {
    return addAll(list.asJdkList());
  }

  @SafeVarargs
  public final Set<E> add(E... toAdd) {
    return addAll(Arrays.stream(toAdd).toList());
  }

  @Override
  public List<E> toList() {
    return listOfAll(set);
  }

  @Override
  public Set<E> toSet() {
    return this;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (!(object instanceof Set<?> that)) {
      return false;
    }
    if (this.size() != that.size()) {
      return false;
    }
    for (var element : that) {
      if (!this.contains(element)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    return set.hashCode();
  }

  public String toString(String prefix, String delimiter, String suffix) {
    return prefix + toString(delimiter) + suffix;
  }

  public String toString(String delimiter) {
    return String.join(delimiter, map(Object::toString));
  }

  @Override
  public String toString() {
    return set.toString();
  }
}
