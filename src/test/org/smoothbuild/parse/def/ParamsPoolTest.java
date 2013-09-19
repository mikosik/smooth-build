package org.smoothbuild.parse.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Param.params;
import static org.smoothbuild.function.base.Type.FILE;
import static org.smoothbuild.function.base.Type.STRING;

import org.junit.Test;
import org.smoothbuild.function.base.Param;

import com.google.common.collect.ImmutableMap;

public class ParamsPoolTest {
  Param string = param(STRING, "string1");
  Param stringRequired = param(STRING, "stringRequired", true);
  Param file = param(FILE, "file1");
  Param fileRequired = param(FILE, "fileRequired", true);

  ImmutableMap<String, Param> params = params(string, stringRequired, file, fileRequired);
  ParamsPool paramsPool = new ParamsPool(params);

  @Test
  public void paramCanBeTaken() {
    assertThat(paramsPool.take(string)).isSameAs(string);
  }

  @Test
  public void takingUnknownParamThrowsException() {
    try {
      paramsPool.take(param(STRING, "someName"));
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void paramCannotBeTakenTwice() {
    paramsPool.take(string);
    try {
      paramsPool.take(string);
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void paramCanBeTakenByName() {
    assertThat(paramsPool.takeByName(string.name())).isSameAs(string);
  }

  @Test
  public void takingUnknownParamByNameThrowsException() {
    try {
      paramsPool.takeByName("someName");
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void paramCannotBeTakenByNameTwice() {
    paramsPool.takeByName(string.name());
    try {
      paramsPool.takeByName(string.name());
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void availableRequiredParams() throws Exception {
    assertThat(paramsPool.availableRequiredParams()).containsOnly(stringRequired, fileRequired);
  }

  @Test
  public void availableRequiredParamsDoesNotContainsTakenParams() throws Exception {
    paramsPool.take(stringRequired);
    assertThat(paramsPool.availableRequiredParams()).containsOnly(fileRequired);
  }
}
