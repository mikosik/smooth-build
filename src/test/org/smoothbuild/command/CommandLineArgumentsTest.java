package org.smoothbuild.command;

import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.plugin.api.Path.path;

import org.junit.Test;
import org.smoothbuild.function.base.Name;

public class CommandLineArgumentsTest {
  Name functionName = simpleName("functionName");

  @Test(expected = NullPointerException.class)
  public void nullScriptFileIsForbidden() {
    new CommandLineArguments(null, functionName);
  }

  @Test(expected = NullPointerException.class)
  public void nullFunctionNameIsForbidden() {
    new CommandLineArguments(path("sctipt.smooth"), null);
  }
}
