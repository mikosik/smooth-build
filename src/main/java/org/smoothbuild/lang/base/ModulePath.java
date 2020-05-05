package org.smoothbuild.lang.base;

import static org.smoothbuild.util.Paths.changeExtension;

import java.nio.file.Path;
import java.util.Objects;

import org.smoothbuild.util.Paths;

/**
 * This class is immutable.
 */
public class ModulePath {
  private final Space space;
  private final ShortablePath smooth;
  private final ShortablePath nativ;

  public ModulePath(Space space, Path fullPath, String shortPath) {
    this.space = space;
    this.smooth = new ShortablePath(fullPath, shortPath);
    this.nativ = new ShortablePath(
        fullPath == null ? null : changeExtension(fullPath, "jar"),
        shortPath == null ? null : changeExtension(shortPath, "jar"));
  }

  public String name() {
    return Paths.removeExtension(smooth.path().getFileName().toString());
  }

  public Space space() {
    return space;
  }

  public ShortablePath smooth() {
    return smooth;
  }

  public ShortablePath nativ() {
    return nativ;
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof ModulePath) {
      ModulePath that = (ModulePath) object;
      return Objects.equals(this.smooth, that.smooth) &&
          Objects.equals(this.nativ, that.nativ);
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return Objects.hash(smooth, nativ);
  }
  @Override
  public String toString() {
    return smooth.shorted() + "(" + smooth.path() + ")";
  }
}
