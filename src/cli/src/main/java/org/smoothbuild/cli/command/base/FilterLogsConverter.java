package org.smoothbuild.cli.command.base;

import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.common.log.base.Level.FATAL;
import static org.smoothbuild.common.log.base.Level.INFO;
import static org.smoothbuild.common.log.base.Level.WARNING;

import com.google.common.collect.ImmutableMap;
import org.smoothbuild.common.log.base.Level;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.TypeConversionException;

/**
 * Log level converter.
 */
public class FilterLogsConverter implements ITypeConverter<Level> {
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
