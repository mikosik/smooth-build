package org.smoothbuild.testing.type;

import org.smoothbuild.lang.type.api.Type;

import com.google.common.collect.ImmutableList;

public interface TestedTF
    <T extends Type, TT extends TestedT<T>, S extends TestedAssignSpec<? extends TT>> {

  public TestingT<T> testingT();

  public TT any();

  public TT blob();

  public TT bool();

  public TT int_();

  public TT nothing();

  public TT string();

  public TT struct();

  public TT tuple();

  public TT tuple(ImmutableList<TT> items);

  public TT varA();

  public TT varB();

  public TT array(TT type);

  public TT array2(TT type);

  public TT func(TT resT, ImmutableList<TT> paramTestedTs);

  public default S illegalAssignment(TT target, TT source) {
    return testedAssignmentSpec(target, source, false);
  }

  public default S allowedAssignment(TT target, TT source) {
    return testedAssignmentSpec(target, source, true);
  }

  public S testedAssignmentSpec(TT target, TT source, boolean allowed);
}
