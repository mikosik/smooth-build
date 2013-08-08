package org.smoothbuild.lang.function;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

public class ParamTest {

  @Test
  public void type() throws Exception {
    assertThat(param("name").type()).isEqualTo(Type.STRING);
  }

  @Test
  public void name() throws Exception {
    assertThat(param("name").name()).isEqualTo("name");
  }

  @Test
  public void isNotSetInitially() {
    Param<String> param = param("name");
    assertThat(param.isSet()).isFalse();
  }

  @Test
  public void isSetAfterSetting() {
    Param<String> param = param("name");
    param.set("abc");
    assertThat(param.isSet()).isTrue();
  }

  @Test
  public void initiallySetToNull() throws Exception {
    assertThat(param("name").get()).isNull();
  }

  @Test
  public void testToString() throws Exception {
    assertThat(param("name").toString()).isEqualTo("Param(String: name)");
  }

  public static Param<String> param(String name) {
    return new Param<String>(Type.STRING, name);
  }
}
