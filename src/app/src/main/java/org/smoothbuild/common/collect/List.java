package org.smoothbuild.common.collect;

import io.vavr.control.Option;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import org.smoothbuild.common.function.ThrowingBiFunction;
import org.smoothbuild.common.function.ThrowingFunction;

public final class List<E> extends AbstractList<E> {
  private final E[] array;

  @SafeVarargs
  public static <E> List<E> list(E... elements) {
    return new List<>(elements.clone());
  }

  public static <R> List<R> list(Collection<? extends R> collection) {
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

  public List<E> sortUsing(Comparator<? super E> comparator) {
    var copy = array.clone();
    Arrays.sort(copy, comparator);
    return new List<>(copy);
  }

  public <R, T extends Throwable> List<R> map(ThrowingFunction<? super E, R, T> mapper) throws T {
    @SuppressWarnings("unchecked")
    var mapped = (R[]) new Object[array.length];
    for (int i = 0; i < array.length; i++) {
      mapped[i] = mapper.apply(array[i]);
    }
    return new List<>(mapped);
  }

  public <T extends Throwable> List<E> filter(ThrowingFunction<E, Boolean, T> predicate) throws T {
    var builder = new ArrayList<E>();
    for (E element : array) {
      if (predicate.apply(element)) {
        builder.add(element);
      }
    }
    return list(builder);
  }

  public <T extends Throwable> List<E> dropWhile(ThrowingFunction<E, Boolean, T> predicate)
      throws T {
    int i = 0;
    while (i < array.length && predicate.apply(array[i])) {
      i++;
    }
    var builder = new ArrayList<E>();
    while (i < array.length) {
      builder.add(array[i]);
      i++;
    }
    return list(builder);
  }

  public <T extends Throwable> List<E> takeWhile(ThrowingFunction<E, Boolean, T> predicate)
      throws T {
    int i = 0;
    var builder = new ArrayList<E>();
    while (i < array.length && predicate.apply(array[i])) {
      builder.add(array[i]);
      i++;
    }
    return list(builder);
  }

  public <D, R, T extends Throwable> List<R> zip(
      Iterable<D> that, ThrowingBiFunction<? super E, ? super D, R, T> biFunction) throws T {
    var zipped = new ArrayList<R>();
    var thisIterator = this.iterator();
    var thatIterator = that.iterator();
    while (thisIterator.hasNext() && thatIterator.hasNext()) {
      zipped.add(biFunction.apply(thisIterator.next(), thatIterator.next()));
    }
    @SuppressWarnings("unchecked")
    var zippedArray = (R[]) zipped.toArray();
    return new List<>(zippedArray);
  }

  public <T extends Throwable> boolean anyMatches(ThrowingFunction<E, Boolean, T> predicate)
      throws T {
    for (E element : array) {
      if (predicate.apply(element)) {
        return true;
      }
    }
    return false;
  }

  public static <E> Option<List<E>> pullUpOption(List<Option<E>> list) {
    if (list.anyMatches(Option::isEmpty)) {
      return Option.none();
    } else {
      return Option.some(list.map(Option::get));
    }
  }

  public String toString(String prefix, String delimiter, String suffix) {
    return prefix + String.join(delimiter, map(Object::toString)) + suffix;
  }

  public String toString(String delimiter) {
    return String.join(delimiter, map(Object::toString));
  }

  @Override
  public String toString() {
    return Arrays.toString(array);
  }
}
