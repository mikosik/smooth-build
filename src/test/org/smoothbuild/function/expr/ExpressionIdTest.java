package org.smoothbuild.function.expr;

import static nl.jqno.equalsverifier.Warning.NULL_FIELDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.plugin.Path.path;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class ExpressionIdTest {

  @Test
  public void resultDir() {
    String functionName = "abc";
    ExpressionId id = new ExpressionId(functionName);
    assertThat(id.resultDir()).isEqualTo(BUILD_DIR.append(path(functionName)));
  }

  @Test
  public void testEqualsAndHashCode() throws Exception {
    EqualsVerifier.forClass(ExpressionId.class).suppress(NULL_FIELDS).verify();
  }
}
