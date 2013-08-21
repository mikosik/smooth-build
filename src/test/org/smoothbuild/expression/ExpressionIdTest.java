package org.smoothbuild.expression;

import static nl.jqno.equalsverifier.Warning.NULL_FIELDS;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.expression.ExpressionId.BUILD_ROOT;
import static org.smoothbuild.lang.type.Path.path;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;

public class ExpressionIdTest {

  @Test
  public void resultDir() {
    String functionName = "abc";
    ExpressionId id = new ExpressionId(functionName);
    assertThat(id.resultDir()).isEqualTo(BUILD_ROOT.append(path(functionName)));
  }

  @Test
  public void testEqualsAndHashCode() throws Exception {
    EqualsVerifier.forClass(ExpressionId.class).suppress(NULL_FIELDS).verify();
  }
}
