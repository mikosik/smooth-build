package org.smoothbuild.lang.type;

import static org.smoothbuild.fs.base.PathUtils.CURRENT_DIR;
import static org.smoothbuild.fs.base.PathUtils.WORKING_DIR;
import static org.smoothbuild.fs.base.PathUtils.toCanonical;

import org.smoothbuild.fs.base.PathUtils;

/**
 * Path within a project.
 */
public class Path {
  private static final Path PROJECT_ROOT_PATH = path(WORKING_DIR);
  private static final Path CURRENT_DIR_PATH = path(CURRENT_DIR);

  private final String value;

  public static Path path(String value) {
    return new Path(value);
  }

  public static Path projectRootPath() {
    return PROJECT_ROOT_PATH;
  }

  public static Path currentDirPath() {
    return CURRENT_DIR_PATH;
  }

  private Path(String value) {
    checkIsValid(value);
    this.value = toCanonical(value);
  }

  public String value() {
    return value;
  }

  public Path parent() {
    return new Path(PathUtils.parentOf(value));
  }

  public Path append(Path path) {
    return new Path(PathUtils.append(value, path.value()));
  }

  private static void checkIsValid(String value) {
    String message = PathUtils.validationError(value);
    if (message != null) {
      throw new IllegalArgumentException(message);
    }
  }

  @Override
  public final boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object instanceof Path) {
      Path that = (Path) object;
      return this.value.equals(that.value);
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return "'" + value + "'";
  }
}
