package org.smoothbuild.lang.function.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.base.ParamOrdering.PARAM_ORDERING;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class ParamOrderingTest {
  @Test
  public void ordering_params() {
    Param param1 = param(STRING, "aaa", false);
    Param param2 = param(STRING, "bbb", false);
    Param param3 = param(STRING, "ccc", false);

    List<Param> actual = PARAM_ORDERING.sortedCopy(ImmutableList.of(param3, param2, param1));
    assertThat(actual).containsExactly(param1, param2, param3);
  }

  @Test
  public void ordering_params_of_different_length() {
    Param param1 = param(STRING, "a", false);
    Param param2 = param(STRING, "aa", false);
    Param param3 = param(STRING, "aaa", false);

    List<Param> actual = PARAM_ORDERING.sortedCopy(ImmutableList.of(param3, param2, param1));
    assertThat(actual).containsExactly(param1, param2, param3);
  }
}
