package org.smoothbuild.lang.function;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

public class ParamTest {

  @Test
  public void typeName() throws Exception {
    assertThat(param("type", "name").typeName()).isEqualTo("type");
  }

  @Test
  public void name() throws Exception {
    assertThat(param("name").name()).isEqualTo("name");
  }

  @Test
  public void isNotSetInitially() {
    Param<Integer> param = param("name");
    assertThat(param.isSet()).isFalse();
  }

  @Test
  public void isSetAfterSetting() {
    Param<Integer> param = param("name");
    param.set(33);
    assertThat(param.isSet()).isTrue();
  }

  @Test
  public void initiallySetToNull() throws Exception {
    assertThat(param("name").get()).isNull();
  }

  @Test
  public void testToString() throws Exception {
    assertThat(param("type", "name").toString()).isEqualTo("Param(type: name)");
  }

  public static Param<Integer> param(String name) {
    return param("typeName", name);
  }

  public static Param<Integer> param(String typeName, String name) {
    return new Param<Integer>(typeName, name);
  }
}
