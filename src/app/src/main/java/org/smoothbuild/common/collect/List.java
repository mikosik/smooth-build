package org.smoothbuild.common.collect;

import io.vavr.control.Option;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import org.smoothbuild.common.function.ThrowingBiFunction;
import org.smoothbuild.common.function.ThrowingFunction;

public class List<E> extends AbstractList<E> {
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

  public List<E> append(Collection<? extends E> list) {
    @SuppressWarnings("unchecked")
    E[] toAppend = (E[]) list.toArray();
    var appended = Arrays.copyOf(array, array.length + toAppend.length);
    System.arraycopy(toAppend, 0, appended, array.length, toAppend.length);
    return new List<>(appended);
  }

  public List<E> sortUsing(Comparator<? super E> comparator) {
    var copy = array.clone();
    Arrays.sort(copy, comparator);
    return new List<>(copy);
  }

  public <R, T extends Throwable> List<R> map(ThrowingFunction<? super E, R, T> func) throws T {
    @SuppressWarnings("unchecked")
    var mapped = (R[]) new Object[array.length];
    for (int i = 0; i < array.length; i++) {
      mapped[i] = func.apply(array[i]);
    }
    return new List<>(mapped);
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
