package org.smoothbuild.lang.function.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.function.base.Parameter.parameter;
import static org.smoothbuild.lang.function.base.ParameterOrdering.PARAMETER_ORDERING;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class ParameterOrderingTest {
  @Test
  public void ordering_params() {
    Parameter parameter1 = parameter(STRING, "aaa", false);
    Parameter parameter2 = parameter(STRING, "bbb", false);
    Parameter parameter3 = parameter(STRING, "ccc", false);

    List<Parameter> actual = PARAMETER_ORDERING.sortedCopy(ImmutableList.of(parameter3, parameter2,
        parameter1));
    assertThat(actual).containsExactly(parameter1, parameter2, parameter3);
  }

  @Test
  public void ordering_params_of_different_length() {
    Parameter parameter1 = parameter(STRING, "a", false);
    Parameter parameter2 = parameter(STRING, "aa", false);
    Parameter parameter3 = parameter(STRING, "aaa", false);

    List<Parameter> actual = PARAMETER_ORDERING.sortedCopy(ImmutableList.of(parameter3, parameter2,
        parameter1));
    assertThat(actual).containsExactly(parameter1, parameter2, parameter3);
  }
}
