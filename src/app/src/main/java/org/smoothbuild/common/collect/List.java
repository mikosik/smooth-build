package org.smoothbuild.common.collect;

import static org.smoothbuild.common.collect.Map.zipToMap;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import com.google.common.collect.Iterators;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import org.smoothbuild.common.function.Function0;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.common.function.Function2;
import org.smoothbuild.common.tuple.Tuple2;

public final class List<E> extends AbstractList<E> {
  private final E[] array;

  @SafeVarargs
  public static <E> List<E> list(E... elements) {
    return new List<>(elements.clone());
  }

  public static <R> List<R> listOfAll(Collection<? extends R> collection) {
    if (collection instanceof List<? extends R> list) {
      @SuppressWarnings("unchecked")
      var cast = (List<R>) list;
      return cast;
    } else {
      @SuppressWarnings("unchecked")
      R[] array1 = (R[]) collection.toArray();
      return new List<>(array1);
    }
  }

  public static <E, T extends Throwable> List<E> list(int size, Function0<E, T> supplier) throws T {
    @SuppressWarnings("unchecked")
    E[] array = (E[]) new Object[size];
    for (int i = 0; i < array.length; i++) {
      array[i] = supplier.get();
    }
    return new List<>(array);
  }

  private List(E[] array) {
    this.array = array;
  }

  @Override
  public E get(int index) {
    return array[index];
  }

  @Override
  public int size() {
    return array.length;
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    return list(Arrays.copyOfRange(array, fromIndex, toIndex));
  }

  public List<E> appendAll(Collection<? extends E> list) {
    @SuppressWarnings("unchecked")
    E[] toAppend = (E[]) list.toArray();
    return append(toAppend);
  }

  @SafeVarargs
  public final List<E> append(E... toAppend) {
    var appended = Arrays.copyOf(array, array.length + toAppend.length);
    System.arraycopy(toAppend, 0, appended, array.length, toAppend.length);
    return new List<>(appended);
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

  public <T extends Throwable> boolean anyMatches(Function1<E, Boolean, T> predicate) throws T {
    for (E element : array) {
      if (predicate.apply(element)) {
        return true;
      }
    }
    return false;
  }

  public Set<E> toSet() {
    return Set.set(array);
  }

  public <V, T extends Throwable> Map<E, V> toMap(Function1<? super E, V, T> valueMapper) throws T {
    return zipToMap(this, map(valueMapper));
  }

  public <K, V, T1 extends Throwable, T2 extends Throwable> Map<K, V> toMap(
      Function1<? super E, K, T1> keyMapper, Function1<? super E, V, T2> valueMapper)
      throws T1, T2 {
    return zipToMap(map(keyMapper), map(valueMapper));
  }

  public static <E> Maybe<List<E>> pullUpMaybe(List<Maybe<E>> list) {
    if (list.anyMatches(Maybe::isNone)) {
      return none();
    } else {
      return some(list.map(Maybe::get));
    }
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
