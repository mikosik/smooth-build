package org.smoothbuild.parse.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Param.params;
import static org.smoothbuild.function.base.Type.STRING;

import org.junit.Test;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Type;

import com.google.common.collect.ImmutableMap;

public class ParamsPoolTest {
  Param string1 = param(Type.STRING, "string1");
  Param string2 = param(Type.STRING, "string2");
  Param file1 = param(Type.FILE, "file1");
  Param file2 = param(Type.FILE, "file2");

  ImmutableMap<String, Param> params = params(string1, string2, file1, file2);
  ParamsPool paramsPool = new ParamsPool(params);

  @Test
  public void paramCanBeTaken() {
    assertThat(paramsPool.take(string1)).isSameAs(string1);
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
    paramsPool.take(string1);
    try {
      paramsPool.take(string1);
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void paramCanBeTakenByName() {
    assertThat(paramsPool.takeByName(string1.name())).isSameAs(string1);
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
    paramsPool.takeByName(string1.name());
    try {
      paramsPool.takeByName(string1.name());
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void availableForType() throws Exception {
    assertThat(paramsPool.availableForType(STRING)).containsOnly(string1, string2);
  }

  @Test
  public void availableForTypeDoesNotContainTakenParam() throws Exception {
    paramsPool.take(string1);
    assertThat(paramsPool.availableForType(STRING)).containsOnly(string2);
  }

  @Test
  public void availableForTypeDoesNotContainTakenByNameParam() throws Exception {
    paramsPool.takeByName(string1.name());
    assertThat(paramsPool.availableForType(STRING)).containsOnly(string2);
  }

}
