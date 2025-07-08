package org.smoothbuild.common.collect;

import static java.util.Arrays.fill;
import static org.smoothbuild.common.collect.Map.zipToMap;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.collect.Set.setOfAll;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.stream.Stream;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.common.function.Function2;
import org.smoothbuild.common.tuple.Tuple2;

/**
 * Immutable list.
 */
public final class List<E> implements Collection<E> {
  private final E[] array;

  @SafeVarargs
  public static <E> List<E> list(E... elements) {
    return new List<>(elements.clone());
  }

  public static <R> List<R> listOfAll(Collection<? extends R> collection) {
    return switch (collection) {
      case List<? extends R> list -> upCast(list);
      case Set<? extends R> set -> {
        @SuppressWarnings("unchecked")
        List<R> newList = (List<R>) set.toList();
        yield newList;
      }
    };
  }

  /**
   * Upcast List which is always safe because List is immutable.
   */
  private static <R> List<R> upCast(List<? extends R> list) {
    @SuppressWarnings("unchecked")
    var cast = (List<R>) list;
    return cast;
  }

  public static <R> List<R> listOfAll(java.util.Collection<? extends R> jdkCollection) {
    @SuppressWarnings("unchecked")
    R[] array1 = (R[]) jdkCollection.toArray();
    return new List<>(array1);
  }

  public static <E> List<E> nCopiesList(int size, E element) {
    @SuppressWarnings("unchecked")
    E[] array = (E[]) new Object[size];
    fill(array, element);
    return new List<>(array);
  }

  public static <E, T extends Throwable> List<E> generateList(int size, Function0<E, T> supplier)
      throws T {
    return generateList(size, i -> supplier.apply());
  }

  public static <E, T extends Throwable> List<E> generateList(
      int size, Function1<Integer, E, T> supplier) throws T {
    @SuppressWarnings("unchecked")
    E[] array = (E[]) new Object[size];
    for (int i = 0; i < array.length; i++) {
      array[i] = supplier.apply(i);
    }
    return new List<>(array);
  }

  private List(E[] array) {
    this.array = array;
  }

  @Override
  public boolean isEmpty() {
    return array.length == 0;
  }

  @Override
  public boolean contains(Object object) {
    for (var element : array) {
      if (Objects.equals(element, object)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Iterator<E> iterator() {
    return Iterators.forArray(array);
  }

  @Override
  public Spliterator<E> spliterator() {
    return Arrays.spliterator(array);
  }

  public E get(int index) {
    if (index < 0 || array.length <= index) {
      throw new NoSuchElementException("index = " + index + ", list.size() = " + array.length);
    }
    return array[index];
  }

  public E last() {
    if (array.length == 0) {
      throw new NoSuchElementException();
    }
    return array[array.length - 1];
  }

  public int indexOf(E element) {
    for (int i = 0, arrayLength = array.length; i < arrayLength; i++) {
      if (Objects.equals(array[i], element)) {
        return i;
      }
    }
    return -1;
  }

  @Override
  public int size() {
    return array.length;
  }

  public List<E> subList(int fromIndex, int toIndex) {
    return list(Arrays.copyOfRange(array, fromIndex, toIndex));
  }

  public List<E> addAll(java.util.Collection<? extends E> collection) {
    return addAll(collection, collection.size());
  }

  public List<E> addAll(Collection<? extends E> collection) {
    return switch (collection) {
      case List<? extends E> l -> addAll(l);
      case Set<? extends E> s -> addAll(s);
    };
  }

  public List<E> addAll(Set<? extends E> set) {
    return addAll(set, set.size());
  }

  private List<E> addAll(Iterable<? extends E> iterable, int size) {
    var resultArray = Arrays.copyOf(array, array.length + size);
    int i = array.length;
    for (var element : iterable) {
      resultArray[i] = element;
      i++;
    }
    return new List<>(resultArray);
  }

  public List<E> addAll(List<? extends E> list) {
    return add(list.array);
  }

  @SafeVarargs
  public final List<E> add(E... toAdd) {
    var resultArray = Arrays.copyOf(array, array.length + toAdd.length);
    System.arraycopy(toAdd, 0, resultArray, array.length, toAdd.length);
    return new List<>(resultArray);
  }

  public List<E> reverse() {
    @SuppressWarnings("unchecked")
    E[] reversed = (E[]) new Object[array.length];
    var i = array.length - 1;
    for (E e : array) {
      reversed[i] = e;
      i--;
    }
    return list(reversed);
  }

  public List<E> rotate(int distance) {
    var length = array.length;
    if (length == 0) {
      return this;
    }
    var distanceModulo = distance < 0 ? ((distance % length) + length) % length : distance % length;
    if (distanceModulo == 0) {
      return this;
    }
    @SuppressWarnings("unchecked")
    E[] rotated = (E[]) new Object[length];
    var i = distanceModulo;
    for (E e : array) {
      rotated[i] = e;
      i = (i + 1) % length;
    }
    return list(rotated);
  }

  public List<E> sortUsing(Comparator<? super E> comparator) {
    var copy = array.clone();
    Arrays.sort(copy, comparator);
    return new List<>(copy);
  }

  public <R, T extends Throwable> List<R> map(Function1<? super E, R, T> mapper) throws T {
    @SuppressWarnings("unchecked")
    var mapped = (R[]) new Object[array.length];
    for (int i = 0; i < array.length; i++) {
      mapped[i] = mapper.apply(array[i]);
    }
    return new List<>(mapped);
  }

  public <R, T extends Throwable> List<R> flatMap(Function1<? super E, Iterable<R>, T> mapper)
      throws T {
    var resultList = new ArrayList<>(array.length);
    for (E e : array) {
      Iterables.addAll(resultList, mapper.apply(e));
    }
    @SuppressWarnings("unchecked")
    var resultArray = (R[]) resultList.toArray(Object[]::new);
    return new List<>(resultArray);
  }

  public <T extends Throwable> List<E> filter(Function1<E, Boolean, T> predicate) throws T {
    var builder = new ArrayList<E>();
    for (E element : array) {
      if (predicate.apply(element)) {
        builder.add(element);
      }
    }
    return listOfAll(builder);
  }

  public <T extends Throwable> List<E> dropWhile(Function1<E, Boolean, T> predicate) throws T {
    int i = 0;
    while (i < array.length && predicate.apply(array[i])) {
      i++;
    }
    var builder = new ArrayList<E>();
    while (i < array.length) {
      builder.add(array[i]);
      i++;
    }
    return listOfAll(builder);
  }

  public <T extends Throwable> List<E> takeWhile(Function1<E, Boolean, T> predicate) throws T {
    int i = 0;
    var builder = new ArrayList<E>();
    while (i < array.length && predicate.apply(array[i])) {
      builder.add(array[i]);
      i++;
    }
    return listOfAll(builder);
  }

  public <D, R, T extends Throwable> List<R> zip(
      Iterable<D> that, Function2<? super E, ? super D, R, T> biFunction) throws T {
    var zipped = new ArrayList<R>();
    var thatIterator = that.iterator();
    for (int i = 0; i < this.size(); i++) {
      if (thatIterator.hasNext()) {
        zipped.add(biFunction.apply(this.get(i), thatIterator.next()));
      } else {
        throwZippingException(i);
      }
    }
    if (thatIterator.hasNext()) {
      throwZippingException(this.size() + Iterators.size(thatIterator));
    }
    @SuppressWarnings("unchecked")
    var zippedArray = (R[]) zipped.toArray();
    return new List<>(zippedArray);
  }

  private void throwZippingException(int thatSize) {
    throw new IllegalArgumentException(
        "Cannot zip with Iterable of different size: expected " + size() + ", got " + thatSize);
  }

  public List<Tuple2<E, Integer>> zipWithIndex() {
    @SuppressWarnings("unchecked")
    var zipped = (Tuple2<E, Integer>[]) new Tuple2[array.length];

    for (int i = 0; i < this.size(); i++) {
      zipped[i] = tuple(get(i), i);
    }
    return new List<>(zipped);
  }

  public boolean startsWith(List<E> prefix) {
    var thatArray = prefix.array;
    if (this.array.length < thatArray.length) {
      return false;
    }
    for (int i = 0; i < thatArray.length; i++) {
      if (!Objects.equals(this.array[i], thatArray[i])) {
        return false;
      }
    }
    return true;
  }

  @Override
  public <V, T extends Throwable> Map<E, V> toMap(Function1<? super E, V, T> valueMapper) throws T {
    return zipToMap(this, map(valueMapper));
  }

  public <K, V, T1 extends Throwable, T2 extends Throwable> Map<K, V> toMap(
      Function1<? super E, K, T1> keyMapper, Function1<? super E, V, T2> valueMapper)
      throws T1, T2 {
    return zipToMap(map(keyMapper), map(valueMapper));
  }

  public static <E> Maybe<List<E>> pullUpMaybe(List<? extends Maybe<E>> list) {
    if (list.anyMatches(Maybe::isNone)) {
      return none();
    } else {
      return some(list.map(Maybe::get));
    }
  }

  @Override
  public List<E> toList() {
    return this;
  }

  @Override
  public Set<E> toSet() {
    return setOfAll(this);
  }

  public <R, T extends Throwable> R construct(Function1<List<E>, R, T> constructor) throws T {
    return constructor.apply(this);
  }

  @Override
  public Stream<E> stream() {
    return Arrays.stream(array);
  }

  public java.util.List<E> asJdkList() {
    return new java.util.AbstractList<>() {
      @Override
      public E get(int index) {
        return List.this.get(index);
      }

      @Override
      public int size() {
        return List.this.size();
      }
    };
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof List<?> that && Arrays.equals(this.array, that.array);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(array);
  }

  public String toString(String prefix, String delimiter, String suffix) {
    return prefix + toString(delimiter) + suffix;
  }

  public String toString(String delimiter) {
    return String.join(delimiter, map(Object::toString));
  }

  @Override
  public String toString() {
    return Arrays.toString(array);
  }
}
