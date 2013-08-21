package org.smoothbuild.function;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.function.Param.param;

import org.junit.Assert;
import org.junit.Test;

public class ParamsTest {

  @Test
  public void readingParam() {
    Param param1 = param(Type.STRING, "first");
    Param param2 = param(Type.STRING, "second");
    Params params = new Params(param1, param2);

    Param param1Read = params.param("first");
    assertThat(param1Read).isSameAs(param1);
    Param param2Read = params.param("second");
    assertThat(param2Read).isSameAs(param2);
  }

  @Test
  public void readingNonexistentParamFailes() throws Exception {
    Param param1 = param(Type.STRING, "first");
    Params params = new Params(param1);

    try {
      params.param("abc");
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testToString() throws Exception {
    Params params = new Params(param(Type.STRING, "first"), param(Type.STRING, "second"));
    assertThat(params.toString()).isEqualTo("Params(Param(String: first), Param(String: second))");
  }
}
