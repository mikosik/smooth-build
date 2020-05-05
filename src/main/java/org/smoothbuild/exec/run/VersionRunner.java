package org.smoothbuild.exec.run;

import static com.google.common.base.Strings.padStart;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.install.InstallationPaths.installationPaths;
import static org.smoothbuild.io.util.JarFile.jarFile;
import static org.smoothbuild.util.Strings.unlines;

import java.io.IOException;
import java.nio.file.Path;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.install.BuildVersion;
import org.smoothbuild.install.InstallationHashes;

public class VersionRunner {
  private final Console console;

  @Inject
  public VersionRunner(Console console) {
    this.console = console;
  }

  public Integer run() {
    Path slibJarPath = installationPaths().slibModule().nativ().path();
    console.println(unlines(
        "smooth build version " + BuildVersion.VERSION,
        "",
        padded("sandbox", InstallationHashes.sandboxHash()),
        padded("  smooth.jar", InstallationHashes.smoothJarHash()),
        padded("  java platform", InstallationHashes.javaPlatformHash()),
        padded("slib.jar", jarHash(slibJarPath))
    ));
    return EXIT_CODE_SUCCESS;
  }

  private String jarHash(Path jar) {
    try {
      return jarFile(jar).hash().toString();
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
