package org.smoothbuild.common.log.location;

public sealed interface CodeSource
    permits BuiltinSource, CliArgumentSource, FileSource, UnknownSource {
  public String description();
}
