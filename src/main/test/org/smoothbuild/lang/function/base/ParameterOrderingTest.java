package org.smoothbuild.lang.function.base;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.function.base.Parameter.parameter;
import static org.smoothbuild.lang.function.base.ParameterOrdering.PARAMETER_ORDERING;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class ParameterOrderingTest {
  private Parameter parameter1;
  private Parameter parameter2;
  private Parameter parameter3;

  @Test
  public void ordering_params() {
    given(parameter1 = parameter(STRING, "aaa", false));
    given(parameter2 = parameter(STRING, "bbb", false));
    given(parameter3 = parameter(STRING, "ccc", false));
    when(PARAMETER_ORDERING.sortedCopy(asList(parameter3, parameter2, parameter1)));
    thenReturned(contains(parameter1, parameter2, parameter3));
  }

  @Test
  public void ordering_params_of_different_length() {
    given(parameter1 = parameter(STRING, "a", false));
    given(parameter2 = parameter(STRING, "aa", false));
    given(parameter3 = parameter(STRING, "aaa", false));
    when(PARAMETER_ORDERING.sortedCopy(asList(parameter3, parameter2, parameter1)));
    thenReturned(contains(parameter1, parameter2, parameter3));
  }
}
