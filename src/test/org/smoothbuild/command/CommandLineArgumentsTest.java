package org.smoothbuild.command;

import static org.smoothbuild.function.base.Name.name;
import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.io.fs.base.Path;

import com.google.common.collect.ImmutableList;

public class CommandLineArgumentsTest {
  ImmutableList<Name> functions = ImmutableList.of(name("functionName"));
  Path path = path("sctipt.smooth");

  @Test(expected = NullPointerException.class)
  public void null_script_file_is_forbidden() {
    new CommandLineArguments(null, functions);
  }

  @Test(expected = NullPointerException.class)
  public void null_function_name_is_forbidden() {
    new CommandLineArguments(path, null);
  }
}
