package org.smoothbuild.base;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.parse.err.OverridenImportError;
import org.smoothbuild.testing.integration.IntegrationTestCase;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class OverridingImportedNameIsForbiddenSmoothTest extends IntegrationTestCase {
  @Test
  public void test() throws IOException {
    // given
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("file: 'abc';");
    builder.addLine("run: 'def';");
    script(builder.build());

    // when
    smoothApp.run("run");

    // then
    userConsole.assertOnlyProblem(OverridenImportError.class);
  }
}
