package org.smoothbuild.base;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.parse.err.OverridenImportError;
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
    smoothRunner.run("run");

    // then
    messages.assertOnlyProblem(OverridenImportError.class);
  }
}
