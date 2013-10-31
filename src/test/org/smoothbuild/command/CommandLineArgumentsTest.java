package org.smoothbuild.command;

import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.function.base.Name.name;

import org.junit.Test;
import org.smoothbuild.function.base.Name;

public class CommandLineArgumentsTest {
  Name functionName = name("functionName");

  @Test(expected = NullPointerException.class)
  public void nullScriptFileIsForbidden() {
    new CommandLineArguments(null, functionName);
  }

  @Test(expected = NullPointerException.class)
  public void nullFunctionNameIsForbidden() {
    new CommandLineArguments(path("sctipt.smooth"), null);
  }
}
