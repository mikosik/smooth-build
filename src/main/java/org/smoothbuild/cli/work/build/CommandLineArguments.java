package org.smoothbuild.cli.work.build;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.ImmutableSet;

public class CommandLineArguments {
  private final Path scriptFile;
  private final ImmutableSet<Name> functions;

  public CommandLineArguments(Path scriptFile, Collection<Name> functions) {
    this.scriptFile = checkNotNull(scriptFile);
    this.functions = ImmutableSet.copyOf(functions);
  }

  public Path scriptFile() {
    return scriptFile;
  }

  public ImmutableSet<Name> functionsToRun() {
    return functions;
  }
}
