package org.smoothbuild.lang.base;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.lang.base.ParameterOrdering.PARAMETER_ORDERING;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.TestingTypesDb;

public class ParameterOrderingTest {
  private final ConcreteType string = new TestingTypesDb().string();
  private Parameter parameter1;
  private Parameter parameter2;
  private Parameter parameter3;

  @Test
  public void ordering_params() {
    given(parameter1 = new Parameter(string, "aaa", null));
    given(parameter2 = new Parameter(string, "bbb", null));
    given(parameter3 = new Parameter(string, "ccc", null));
    when(PARAMETER_ORDERING.sortedCopy(list(parameter3, parameter2, parameter1)));
    thenReturned(contains(parameter1, parameter2, parameter3));
  }

  @Test
  public void ordering_params_of_different_length() {
    given(parameter1 = new Parameter(string, "a", null));
    given(parameter2 = new Parameter(string, "aa", null));
    given(parameter3 = new Parameter(string, "aaa", null));
    when(PARAMETER_ORDERING.sortedCopy(list(parameter3, parameter2, parameter1)));
    thenReturned(contains(parameter1, parameter2, parameter3));
  }
}
