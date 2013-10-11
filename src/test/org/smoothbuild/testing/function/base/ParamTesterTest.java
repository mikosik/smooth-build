package org.smoothbuild.testing.function.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.testing.function.base.ParamTester.params;

import java.util.Map;

import org.junit.Test;
import org.smoothbuild.function.base.Param;

public class ParamTesterTest {

  @Test
  public void creatingParamsMap() throws Exception {
    String name1 = "name1";
    String name2 = "name2";
    Param param1 = param(STRING, name1, false);
    Param param2 = param(STRING, name2, false);

    Map<String, Param> params = params(param1, param2);
    assertThat(params.get(name1)).isSameAs(param1);
    assertThat(params.get(name2)).isSameAs(param2);
  }

  @Test
  public void creatingParamsMapWithDuplicateParamNamesThrowsException() throws Exception {
    Param param1 = param(STRING, "name");

    try {
      params(param1, param1);
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }
}
