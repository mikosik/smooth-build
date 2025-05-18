package org.smoothbuild.common.log.base;

/**
 * Enum representing different log levels used in the build process.
 * Log levels are ordered by severity (FATAL being the most severe).
 * Used for filtering and controlling the output of log messages during build execution.
 */
public enum Level {
  /**
   * Critical failure that prevents the build process from continuing execution.
   * The Cause of the failure is outside of the build specification itself.
   * It can be caused by external factors like a network connection or a bug in the tool.
   * For this reason evaluation of a task that reported FATAL Log is not cached.
   */
  FATAL,
  /**
   * Error that indicates a failure preventing a task from completing successfully.
   */
  ERROR,
  /**
   * Potential issue that can cause the task result to be incorrect.
   */
  WARNING,
  /**
   * General informational message.
   */
  INFO;

  public boolean hasSeverityAtLeast(Level level) {
    return this.ordinal() <= level.ordinal();
  }
}
