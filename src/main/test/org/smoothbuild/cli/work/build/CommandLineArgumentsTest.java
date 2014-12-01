package org.smoothbuild.cli.work.build;

import static java.util.Arrays.asList;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.function.base.Name.name;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;

public class CommandLineArgumentsTest {
  List<Name> functions = asList(name("functionName"));
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
