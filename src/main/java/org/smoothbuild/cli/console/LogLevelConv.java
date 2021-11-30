package org.smoothbuild.cli.console;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.FATAL;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import com.google.common.collect.ImmutableMap;

import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

/**
 * Log level converter.
 */
public class LogLevelConv implements ITypeConverter<Level> {
  private static final ImmutableMap<String, Level> MAP =
      ImmutableMap.<String, Level>builder()
          .put("f", FATAL)
          .put("fatal", FATAL)
          .put("e", ERROR)
          .put("error", ERROR)
          .put("w", WARNING)
          .put("warning", WARNING)
          .put("i", INFO)
          .put("info", INFO)
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
