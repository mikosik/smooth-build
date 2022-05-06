package org.smoothbuild.testing.type;

import java.util.List;
import java.util.function.Predicate;

public interface TestedT<T> {
  public T type();

  public String name();

  public String q();

  public boolean isFunc(
      Predicate<? super TestedT<T>> resPredicate,
      List<? extends Predicate<? super TestedT<T>>> paramPredicates);


  public boolean isArray();

  public boolean isArrayOfArrays();

  public boolean isArrayOf(TestedT<T> nothing);

  public boolean isTuple();

  public boolean isTupleOfTuple();
}
