package org.smoothbuild.lang.function.base;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.lang.function.base.ParameterOrdering.PARAMETER_ORDERING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypeSystem;

public class ParameterOrderingTest {
  private static final Type STRING = new TypeSystem().string();

  private Parameter parameter1;
  private Parameter parameter2;
  private Parameter parameter3;

  @Test
  public void ordering_params() {
    given(parameter1 = new Parameter(STRING, new Name("aaa"), null));
    given(parameter2 = new Parameter(STRING, new Name("bbb"), null));
    given(parameter3 = new Parameter(STRING, new Name("ccc"), null));
    when(PARAMETER_ORDERING.sortedCopy(asList(parameter3, parameter2, parameter1)));
    thenReturned(contains(parameter1, parameter2, parameter3));
  }

  @Test
  public void ordering_params_of_different_length() {
    given(parameter1 = new Parameter(STRING, new Name("a"), null));
    given(parameter2 = new Parameter(STRING, new Name("aa"), null));
    given(parameter3 = new Parameter(STRING, new Name("aaa"), null));
    when(PARAMETER_ORDERING.sortedCopy(asList(parameter3, parameter2, parameter1)));
    thenReturned(contains(parameter1, parameter2, parameter3));
  }
}
