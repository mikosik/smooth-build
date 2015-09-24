package org.smoothbuild.cli.work.build;

import static java.util.Arrays.asList;
import static org.smoothbuild.lang.function.base.Name.name;

import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Name;

public class CommandLineArgumentsTest {
  List<Name> functions = asList(name("functionName"));

  @Test(expected = NullPointerException.class)
  public void null_function_name_is_forbidden() {
    new CommandLineArguments(null);
  }
}
