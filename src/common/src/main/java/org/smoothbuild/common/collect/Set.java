package org.smoothbuild.common.collect;

import static com.google.common.collect.ImmutableSortedSet.toImmutableSortedSet;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Map.zipToMap;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.smoothbuild.common.function.Function1;

public class Set<E> implements java.util.Set<E> {
  private final ImmutableSet<E> set;

  @SafeVarargs
  public static <E> Set<E> set(E... elements) {
    return new Set<>(ImmutableSet.copyOf(elements));
  }

  public static <E> Set<E> setOfAll(Iterable<E> iterable) {
    return new Set<>(ImmutableSet.copyOf(iterable));
  }

  private Set(ImmutableSet<E> set) {
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
    return setOfAll(builder.build());
  }

  public Set<E> withRemovedAll(Collection<?> toRemove) {
    Builder<E> builder = ImmutableSet.builder();
    for (E element : set) {
      if (!toRemove.contains(element)) {
        builder.add(element);
      }
    }
    return setOfAll(builder.build());
  }

  public Set<E> sort(Comparator<? super E> comparator) {
    return new Set<>(set.stream().collect(toImmutableSortedSet(comparator)));
  }

  public List<E> toList() {
    return listOfAll(set);
  }

  public <V, T extends Throwable> Map<E, V> toMap(Function1<E, V, T> mapper) throws T {
    return zipToMap(this, this.map(mapper));
  }

  // Methods from java.util.Set

  @Override
  public Iterator<E> iterator() {
    return set.iterator();
  }

  @Override
  public void forEach(Consumer<? super E> consumer) {
    set.forEach(consumer);
  }

  @Override
  public Object[] toArray() {
    return set.toArray();
  }

  @Override
  public <A> A[] toArray(@NotNull A[] array) {
    return set.toArray(array);
  }

  @Override
  public <A> A[] toArray(IntFunction<A[]> generator) {
    return set.toArray(generator);
  }

  @Override
  public boolean add(E element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(@NotNull Collection<?> collection) {
    return set.containsAll(collection);
  }

  @Override
  public boolean addAll(@NotNull Collection<? extends E> collection) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(@NotNull Collection<?> collection) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(@NotNull Collection<?> collection) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeIf(Predicate<? super E> filter) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Spliterator<E> spliterator() {
    return set.spliterator();
  }

  @Override
  public Stream<E> stream() {
    return set.stream();
  }

  @Override
  public Stream<E> parallelStream() {
    return set.parallelStream();
  }

  @Override
  public int size() {
    return set.size();
  }

  @Override
  public boolean isEmpty() {
    return set.isEmpty();
  }

  @Override
  public boolean contains(Object object) {
    return set.contains(object);
  }

  @Override
  public boolean equals(Object object) {
    return set.equals(object);
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
