package org.smoothbuild.function;

import static nl.jqno.equalsverifier.Warning.NULL_FIELDS;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.function.Param.param;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class ParamTest {
  @Test
  public void type() throws Exception {
    assertThat(param(Type.STRING, "name").type()).isEqualTo(Type.STRING);
  }

  @Test
  public void name() throws Exception {
    assertThat(param(Type.STRING, "name").name()).isEqualTo("name");
  }

  @Test
  public void equalsAndHashCode() throws Exception {
    EqualsVerifier.forClass(Param.class).suppress(NULL_FIELDS).verify();
  }

  @Test
  public void testToString() throws Exception {
    assertThat(param(Type.STRING, "name").toString()).isEqualTo("Param(String: name)");
  }
}
