package org.smoothbuild.lang.base.type;

import java.util.List;

import org.smoothbuild.lang.base.type.api.Type;

public interface TestedTFactory
    <T extends Type, TT extends TestedT<T>, S extends TestedAssignSpec<? extends TT>> {

  public TestingT<T> testingT();

  public TT any();

  public TT blob();

  public TT bool();

  public TT int_();

  public TT nothing();

  public TT string();

  public TT struct();

  public TT varA();

  public TT varB();

  public TT array(TT type);

  public TT array2(TT type);

  public TT func(TT resT, List<TT> paramTestedTs);

  public default S illegalAssignment(TT target, TT source) {
    return testedAssignmentSpec(target, source, false);
  }

  public default S allowedAssignment(TT target, TT source) {
    return testedAssignmentSpec(target, source, true);
  }

  public S testedAssignmentSpec(TT target, TT source, boolean allowed);
}
