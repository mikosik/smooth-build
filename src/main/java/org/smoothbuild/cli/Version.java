package org.smoothbuild.cli;

import static com.google.common.base.Strings.padStart;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.io.util.JarFile.jarFile;
import static org.smoothbuild.util.Paths.changeExtension;

import java.io.IOException;
import java.nio.file.Path;

import javax.inject.Inject;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.SmoothPaths;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.SandboxHashProvider;

public class Version implements Command {
  private final Console console;
  private final SmoothPaths smoothPaths;

  @Inject
  public Version(Console console, SmoothPaths smoothPaths) {
    this.console = console;
    this.smoothPaths = smoothPaths;
  }

  @Override
  public int run(String... names) {
    console.println("smooth build version " + SmoothConstants.VERSION);
    console.println("");
    console.println(padded("sandbox", SandboxHashProvider.get()));
    console.println(padded("  smooth.jar", SandboxHashProvider.smoothJarHash()));
    console.println(padded("  java platform", SandboxHashProvider.javaPlatformHash()));
    console.println(padded("funcs.jar", funcsJarHash()));
    return EXIT_CODE_SUCCESS;
  }

  private String funcsJarHash() {
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
