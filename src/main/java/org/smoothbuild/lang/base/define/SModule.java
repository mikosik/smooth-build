package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.FileLocation.fileLocation;
import static org.smoothbuild.util.io.Paths.changeExtension;

import java.nio.file.Path;

import org.smoothbuild.util.io.Paths;

import com.google.common.collect.ImmutableList;

public record SModule(Space space, Path file, ImmutableList<SModule> referenced) {

  public String name() {
    return Paths.removeExtension(file.getFileName().toString());
  }

  public FileLocation smoothFile() {
    return fileLocation(this, file);
  }

  public FileLocation nativeFile() {
    return fileLocation(this, changeExtension(file, "jar"));
  }
}
