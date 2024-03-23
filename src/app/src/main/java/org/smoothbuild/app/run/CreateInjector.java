package org.smoothbuild.app.run;

import static com.google.inject.Stage.PRODUCTION;
import static org.smoothbuild.app.layout.Layout.BIN_DIR_NAME;
import static org.smoothbuild.app.layout.Layout.STANDARD_LIBRARY_DIR_NAME;
import static org.smoothbuild.app.layout.SmoothBucketId.BINARY;
import static org.smoothbuild.app.layout.SmoothBucketId.PROJECT;
import static org.smoothbuild.app.layout.SmoothBucketId.STANDARD_LIBRARY;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.base.Level.INFO;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.smoothbuild.app.layout.BinaryBucketWiring;
import org.smoothbuild.app.layout.ProjectBucketWiring;
import org.smoothbuild.app.layout.StandardLibraryBucketWiring;
import org.smoothbuild.app.report.ReportWiring;
import org.smoothbuild.app.run.eval.report.ReportMatchers;
import org.smoothbuild.app.wire.AppWiring;
import org.smoothbuild.common.bucket.base.BucketId;
import org.smoothbuild.common.bucket.wiring.DiskBucketWiring;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.common.log.report.ReportMatcher;
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
        STANDARD_LIBRARY, installationDir.resolve(STANDARD_LIBRARY_DIR_NAME),
        BINARY, installationDir.resolve(BIN_DIR_NAME));
    return Guice.createInjector(
        PRODUCTION,
        new AppWiring(),
        new VirtualMachineWiring(),
        new ProjectBucketWiring(),
        new StandardLibraryBucketWiring(),
        new BinaryBucketWiring(),
        new DiskBucketWiring(bucketIdToPath),
        new ReportWiring(out, reportMatcher, logLevel));
  }

  public static Injector createInjector(PrintWriter out) {
    var installationDir = installationDir();
    Map<BucketId, Path> bucketIdToPath = map(
        STANDARD_LIBRARY, installationDir.resolve(STANDARD_LIBRARY_DIR_NAME),
        BINARY, installationDir.resolve(BIN_DIR_NAME));
    return Guice.createInjector(
        PRODUCTION,
        new StandardLibraryBucketWiring(),
        new BinaryBucketWiring(),
        new DiskBucketWiring(bucketIdToPath),
        new ReportWiring(out, ReportMatchers.ALL, INFO));
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
