package org.smoothbuild.lang.base.type;

import java.util.List;
import java.util.function.Predicate;

import org.smoothbuild.lang.base.type.api.Type;

public interface TestedT<T extends Type> {
  public T type();

  public default String name() {
    return type().name();
  }

  public default String q() {
    return type().q();
  }

  public boolean isFunc(
      Predicate<? super TestedT<? extends Type>> resPredicate,
      List<? extends Predicate<? super TestedT<? extends Type>>> paramPredicates);


  public boolean isArray();

  public boolean isArrayOfArrays();

  public boolean isArrayOf(TestedT<? extends Type> nothing);

  public boolean isTuple();

  public boolean isTupleOfTuple();
}
