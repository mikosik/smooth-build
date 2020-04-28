package org.smoothbuild.cli.console;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.FATAL;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import com.google.common.collect.ImmutableMap;

import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

public class LogLevelConverter implements ITypeConverter<Level> {
  private static final ImmutableMap<String, Level> MAP =
      ImmutableMap.<String, Level>builder()
          .put("fatal", FATAL)
          .put("f", FATAL)
          .put("error", ERROR)
          .put("e", ERROR)
          .put("warning", WARNING)
          .put("w", WARNING)
          .put("info", INFO)
          .put("i", INFO)
          .build();

  @Override
  public Level convert(String value) {
    Level level = MAP.get(value);
    if (level != null) {
      return level;
    } else {
      throw new TypeConversionException("expected one of {" + String.join(",", MAP.keySet())
          + "} (case-sensitive) but was '" + value + "'");
    }
  }
}
