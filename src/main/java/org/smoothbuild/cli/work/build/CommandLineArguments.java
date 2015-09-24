package org.smoothbuild.cli.work.build;

import java.util.Collection;

import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.ImmutableSet;

public class CommandLineArguments {
  private final ImmutableSet<Name> functions;

  public CommandLineArguments(Collection<Name> functions) {
    this.functions = ImmutableSet.copyOf(functions);
  }

  public ImmutableSet<Name> functionsToRun() {
    return functions;
  }
}
