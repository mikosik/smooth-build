package org.smoothbuild.lang.base.type;

import java.util.List;

public interface TestedTFactory<T extends TestedT<?>, S extends TestedAssignSpec<? extends T>> {
  public T any();

  public T blob();

  public T int_();

  public T nothing();

  public T string();

  public T struct();

  public T varA();

  public T varB();

  public T array(T type);

  public T array2(T type);

  public T func(T resT, List<T> paramTestedTs);

  public default S illegalAssignment(T target, T source) {
    return testedAssignmentSpec(target, source, false);
  }

  public default S allowedAssignment(T target, T source) {
    return testedAssignmentSpec(target, source, true);
  }

  public S testedAssignmentSpec(T target, T source, boolean allowed);
}
