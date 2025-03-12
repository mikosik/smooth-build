package org.smoothbuild.common.collect;

import java.util.stream.Stream;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function1;

public abstract sealed interface Collection<E> extends Iterable<E> permits List, Set {
  public int size();

  public boolean isEmpty();

  public boolean contains(Object object);

  public default boolean containsAll(Collection<E> collection) {
    for (var element : collection) {
      if (!contains(element)) {
        return false;
      }
    }
    return true;
  }

  public default <T extends Throwable> void foreach(Consumer1<? super E, T> consumer) throws T {
    for (E e : this) {
      consumer.accept(e);
    }
  }

  public default <T extends Throwable> boolean anyMatches(Function1<E, Boolean, T> predicate)
      throws T {
    for (E element : this) {
      if (predicate.apply(element)) {
        return true;
      }
    }
    return false;
  }

  public List<E> toList();

  public Set<E> toSet();

  public <V, T extends Throwable> Map<E, V> toMap(Function1<? super E, V, T> valueMapper) throws T;

  public Stream<E> stream();
}
