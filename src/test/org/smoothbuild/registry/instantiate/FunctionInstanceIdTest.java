package org.smoothbuild.registry.instantiate;

import static nl.jqno.equalsverifier.Warning.NULL_FIELDS;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.Path.path;
import static org.smoothbuild.registry.instantiate.FunctionInstanceId.BUILD_ROOT;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class FunctionInstanceIdTest {

  @Test
  public void resultDir() {
    String functionName = "abc";
    FunctionInstanceId id = new FunctionInstanceId(functionName);
    assertThat(id.resultDir()).isEqualTo(BUILD_ROOT.append(path(functionName)));
  }

  @Test
  public void testEqualsAndHashCode() throws Exception {
    EqualsVerifier.forClass(FunctionInstanceId.class).suppress(NULL_FIELDS).verify();
  }
}
