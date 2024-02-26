package org.smoothbuild.cli.base;

import static org.smoothbuild.common.log.Level.ERROR;
import static org.smoothbuild.common.log.Level.FATAL;
import static org.smoothbuild.common.log.Level.INFO;
import static org.smoothbuild.common.log.Level.WARNING;

import com.google.common.collect.ImmutableMap;
import org.smoothbuild.common.log.Level;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

/**
 * Log level converter.
 */
public class LogLevelConverter implements ITypeConverter<Level> {
  private static final ImmutableMap<String, Level> MAP = ImmutableMap.<String, Level>builder()
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
