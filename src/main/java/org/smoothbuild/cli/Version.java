package org.smoothbuild.cli;

import static com.google.common.base.Strings.padStart;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.io.util.JarFile.jarFile;
import static org.smoothbuild.util.Paths.changeExtension;
import static org.smoothbuild.util.Strings.unlines;

import java.io.IOException;
import java.nio.file.Path;

import javax.inject.Inject;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.SmoothPaths;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.SandboxHashProvider;

public class Version {
  private final Console console;

  @Inject
  public Version(Console console) {
    this.console = console;
  }

  public Integer run() {
    console.println(unlines(
        "smooth build version " + SmoothConstants.VERSION,
        "",
        padded("sandbox", SandboxHashProvider.get()),
        padded("  smooth.jar", SandboxHashProvider.smoothJarHash()),
        padded("  java platform", SandboxHashProvider.javaPlatformHash()),
        padded("funcs.jar", funcsJarHash(SmoothPaths.smoothPaths()))
    ));
    return EXIT_CODE_SUCCESS;
  }

  private String funcsJarHash(SmoothPaths smoothPaths) {
    try {
      Path path = smoothPaths.funcsModule().fullPath();
      return jarFile(changeExtension(path, "jar")).hash().toString();
    } catch (IOException e) {
      return "IO error when reading file";
    }
  }

  private String padded(String name, Hash hash) {
    return padded(name, hash.toString());
  }

  private String padded(String name, String hashValueOrError) {
    return name + padStart(hashValueOrError, 80 - name.length(), ' ');
  }
}
