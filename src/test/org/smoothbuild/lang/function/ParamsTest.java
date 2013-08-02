package org.smoothbuild.lang.function;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.function.ParamTest.param;

import org.junit.Assert;
import org.junit.Test;

public class ParamsTest {

  @Test
  public void readingParam() {
    Param<Integer> param1 = param("first");
    Param<Integer> param2 = param("second");
    Params params = new Params(param1, param2);

    @SuppressWarnings("unchecked")
    Param<Integer> param1Read = (Param<Integer>) params.param("first");
    assertThat(param1Read).isSameAs(param1);
    @SuppressWarnings("unchecked")
    Param<Integer> param2Read = (Param<Integer>) params.param("second");
    assertThat(param2Read).isSameAs(param2);
  }

  @Test
  public void readingNonexistentParamFailes() throws Exception {
    Param<Integer> param1 = param("first");
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
    Params params = new Params(param("typeFirst", "first"), param("typeSecond", "second"));
    assertThat(params.toString()).isEqualTo(
        "Params(Param(typeFirst: first), Param(typeSecond: second))");
  }
}
