package org.smoothbuild.testing.type;

import java.util.List;
import java.util.function.Predicate;

import org.smoothbuild.lang.type.api.Type;

public interface TestedT<T extends Type> {
  public T type();

  public String name();

  public String q();

  public boolean isFunc(
      Predicate<? super TestedT<? extends Type>> resPredicate,
      List<? extends Predicate<? super TestedT<? extends Type>>> paramPredicates);


  public boolean isArray();

  public boolean isArrayOfArrays();

  public boolean isArrayOf(TestedT<? extends Type> nothing);

  public boolean isTuple();

  public boolean isTupleOfTuple();
}
