package org.smoothbuild.command;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.ImmutableList;

public class CommandLineArguments {
  private final Path scriptFile;
  private final ImmutableList<Name> functions;

  public CommandLineArguments(Path scriptFile, List<Name> functions) {
    this.scriptFile = checkNotNull(scriptFile);
    this.functions = ImmutableList.copyOf(functions);
  }

  public Path scriptFile() {
    return scriptFile;
  }

  public ImmutableList<Name> functionsToRun() {
    return functions;
  }
}
