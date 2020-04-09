package org.smoothbuild.cli;

import static com.google.common.base.Strings.padStart;
import static com.google.common.collect.ObjectArrays.concat;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.SmoothConstants.SMOOTH_HOME_ENV_VARIABLE;
import static org.smoothbuild.io.util.JarFile.jarFile;
import static org.smoothbuild.util.Paths.changeExtension;
import static org.smoothbuild.util.Strings.unlines;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

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
    console.println(unlines(concat(
        base(),
        funcsHash()
    )));
    return EXIT_CODE_SUCCESS;
  }

  private String[] base() {
    return new String[] {
        "smooth build version " + SmoothConstants.VERSION,
        "",
        padded("sandbox", SandboxHashProvider.get()),
        padded("  smooth.jar", SandboxHashProvider.smoothJarHash()),
        padded("  java platform", SandboxHashProvider.javaPlatformHash())
    };
  }

  private String funcsHash() {
    Optional<SmoothPaths> smoothPaths = CommandHelper.smoothPaths();
    return smoothPaths
        .map(paths -> padded("funcs.jar", funcsJarHash(paths)))
        .orElse("Cannot provide hashes as Environment variable '"
            + SMOOTH_HOME_ENV_VARIABLE + "' not set.");
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
