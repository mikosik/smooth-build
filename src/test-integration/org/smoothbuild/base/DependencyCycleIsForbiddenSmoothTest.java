package org.smoothbuild.base;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.parse.err.CycleInCallGraphError;
import org.smoothbuild.testing.integration.IntegrationTestCase;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class DependencyCycleIsForbiddenSmoothTest extends IntegrationTestCase {
  @Test
  public void test() throws IOException {
    // given
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("function2: run;");
    builder.addLine("function1: function2;");
    builder.addLine("run:       function1;");
    script(builder.build());

    // when
    smoothApp.run("run");

    // then
    messages.assertOnlyProblem(CycleInCallGraphError.class);
  }
}
