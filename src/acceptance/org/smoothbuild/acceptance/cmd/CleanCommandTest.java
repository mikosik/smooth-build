package org.smoothbuild.acceptance.cmd;

import static org.testory.Testory.thenEqual;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class CleanCommandTest extends AcceptanceTestCase {
  @Test
  public void build_command_without_function_argument_prints_error() throws Exception {
    givenScript("result: 'abc';");
    whenSmoothClean("some arguments");
    thenReturnedCode(1);
    Assert.assertEquals(unknownArguments(), output());
    thenEqual(output(), unknownArguments());
  }

  private static String unknownArguments() {
    StringBuilder builder = new StringBuilder();
    builder.append(" + CLEAN\n");
    builder.append("   + ERROR: Unknown arguments: [some, arguments]\n");
    builder.append(" + FAILED :(\n");
    builder.append("   + 1 error(s)\n");
    return builder.toString();
  }
}
