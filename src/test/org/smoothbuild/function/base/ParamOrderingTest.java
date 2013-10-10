package org.smoothbuild.function.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.function.base.ParamOrdering.PARAM_ORDERING;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.testing.function.base.ParamTester.param;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class ParamOrderingTest {
  @Test
  public void ordering_params() {
    Param param1 = param(STRING, "aaa");
    Param param2 = param(STRING, "bbb");
    Param param3 = param(STRING, "ccc");

    List<Param> actual = PARAM_ORDERING.sortedCopy(ImmutableList.of(param3, param2, param1));
    assertThat(actual).containsExactly(param1, param2, param3);
  }
}
