package org.smoothbuild.cli.command.base;

import static org.smoothbuild.cli.layout.Aliases.INSTALL_ALIAS;
import static org.smoothbuild.cli.layout.Aliases.LIBRARY_ALIAS;
import static org.smoothbuild.cli.layout.Aliases.PROJECT_ALIAS;
import static org.smoothbuild.cli.layout.Layout.BIN_DIR_NAME;
import static org.smoothbuild.cli.layout.Layout.STANDARD_LIBRARY_DIR_NAME;
import static org.smoothbuild.common.collect.Map.map;

import java.net.URISyntaxException;
import java.nio.file.Path;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.filesystem.base.Alias;

public class CreateInjector {
  public static Map<Alias, Path> createAliasPathMap(Path projectDir) {
    return createAliasPathMap().put(PROJECT_ALIAS, projectDir);
  }

  public static Map<Alias, Path> createAliasPathMap() {
    var installationDir = installationDir();
    return map(
        LIBRARY_ALIAS, installationDir.resolve(STANDARD_LIBRARY_DIR_NAME),
        INSTALL_ALIAS, installationDir.resolve(BIN_DIR_NAME));
  }

  private static Path installationDir() {
    return smoothJarPath().getParent();
  }

  private static Path smoothJarPath() {
    try {
      var uri = CreateInjector.class
          .getProtectionDomain()
          .getCodeSource()
          .getLocation()
          .toURI();
      return Path.of(uri).getParent();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }
}
