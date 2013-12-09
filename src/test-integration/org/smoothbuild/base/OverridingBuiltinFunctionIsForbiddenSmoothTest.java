package org.smoothbuild.base;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.parse.err.OverridenBuiltinFunctionError;
import org.smoothbuild.testing.integration.IntegrationTestCase;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class OverridingBuiltinFunctionIsForbiddenSmoothTest extends IntegrationTestCase {
  @Test
  public void test() throws IOException {
    // given
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("file: 'abc';");
    builder.addLine("run: 'def';");
    script(builder.build());

    // when
    build("run");

    // then
    userConsole.messageGroup().assertOnlyProblem(OverridenBuiltinFunctionError.class);
  }
}
