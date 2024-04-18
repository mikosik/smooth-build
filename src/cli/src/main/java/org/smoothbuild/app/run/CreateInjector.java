package org.smoothbuild.app.run;

import static com.google.inject.Stage.PRODUCTION;
import static org.smoothbuild.app.layout.BucketIds.INSTALL;
import static org.smoothbuild.app.layout.BucketIds.LIBRARY;
import static org.smoothbuild.app.layout.BucketIds.PROJECT;
import static org.smoothbuild.app.layout.Layout.BIN_DIR_NAME;
import static org.smoothbuild.app.layout.Layout.STANDARD_LIBRARY_DIR_NAME;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.collect.Set.setOfAll;
import static org.smoothbuild.common.log.base.Level.INFO;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.smoothbuild.app.AppWiring;
import org.smoothbuild.app.layout.SmoothBucketWiring;
import org.smoothbuild.app.report.ReportWiring;
import org.smoothbuild.app.run.eval.report.ReportMatchers;
import org.smoothbuild.common.bucket.base.BucketId;
import org.smoothbuild.common.bucket.wiring.DiskBucketWiring;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.common.init.InitWiring;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.common.log.report.ReportMatcher;
import org.smoothbuild.compilerbackend.CompilerBackendWiring;
import org.smoothbuild.virtualmachine.wire.VirtualMachineWiring;

public class CreateInjector {
  public static Injector createInjector(Path projectDir, PrintWriter out, Level logLevel) {
    return createInjector(projectDir, out, logLevel, ReportMatchers.ALL);
  }

  public static Injector createInjector(
      Path projectDir, PrintWriter out, Level logLevel, ReportMatcher reportMatcher) {
    var installationDir = installationDir();
    Map<BucketId, Path> bucketIdToPath = map(
        PROJECT, projectDir,
        LIBRARY, installationDir.resolve(STANDARD_LIBRARY_DIR_NAME),
        INSTALL, installationDir.resolve(BIN_DIR_NAME));
    return Guice.createInjector(
        PRODUCTION,
        new AppWiring(),
        new CompilerBackendWiring(),
        new VirtualMachineWiring(),
        new SmoothBucketWiring(setOfAll(bucketIdToPath.keySet())),
        new DiskBucketWiring(bucketIdToPath),
        new ReportWiring(out, reportMatcher, logLevel));
  }

  public static Injector createInjector(PrintWriter out) {
    var installationDir = installationDir();
    Map<BucketId, Path> bucketIdToPath = map(
        LIBRARY, installationDir.resolve(STANDARD_LIBRARY_DIR_NAME),
        INSTALL, installationDir.resolve(BIN_DIR_NAME));
    return Guice.createInjector(
        PRODUCTION,
        new SmoothBucketWiring(setOfAll(bucketIdToPath.keySet())),
        new DiskBucketWiring(bucketIdToPath),
        new ReportWiring(out, ReportMatchers.ALL, INFO),
        new InitWiring());
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
