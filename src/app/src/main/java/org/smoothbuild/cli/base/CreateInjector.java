package org.smoothbuild.cli.base;

import static com.google.inject.Stage.PRODUCTION;
import static org.smoothbuild.filesystem.install.InstallationPaths.BIN_DIR_NAME;
import static org.smoothbuild.filesystem.install.InstallationPaths.STD_LIB_DIR_NAME;
import static org.smoothbuild.filesystem.space.Space.BINARY;
import static org.smoothbuild.filesystem.space.Space.PROJECT;
import static org.smoothbuild.filesystem.space.Space.STANDARD_LIBRARY;
import static org.smoothbuild.out.log.Level.INFO;

import java.io.PrintWriter;
import java.nio.file.Path;

import org.smoothbuild.common.filesystem.disk.DiskFileSystemModule;
import org.smoothbuild.filesystem.install.BinaryFileSystemModule;
import org.smoothbuild.filesystem.install.StandardLibraryFileSystemModule;
import org.smoothbuild.filesystem.project.ProjectFileSystemModule;
import org.smoothbuild.filesystem.space.SpaceModule;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.report.ReportModule;
import org.smoothbuild.run.eval.EvaluatorSModule;
import org.smoothbuild.run.eval.report.TaskMatcher;
import org.smoothbuild.run.eval.report.TaskMatchers;
import org.smoothbuild.vm.bytecode.BytecodeModule;
import org.smoothbuild.vm.evaluate.EvaluatorBModule;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class CreateInjector {
  public static Injector createInjector(Path projectDir, Path installationDir, PrintWriter out,
      Level logLevel) {
    return createInjector(projectDir, installationDir, out, logLevel, TaskMatchers.ALL);
  }

  public static Injector createInjector(Path projectDir, Path installationDir, PrintWriter out,
      Level logLevel, TaskMatcher taskMatcher) {
    var spaceToPath = ImmutableMap.of(
        PROJECT, projectDir,
        STANDARD_LIBRARY, installationDir.resolve(STD_LIB_DIR_NAME),
        BINARY, installationDir.resolve(BIN_DIR_NAME));
    return Guice.createInjector(PRODUCTION,
        new EvaluatorBModule(),
        new EvaluatorSModule(taskMatcher),
        new BytecodeModule(),
        new ProjectFileSystemModule(),
        new StandardLibraryFileSystemModule(),
        new BinaryFileSystemModule(),
        new DiskFileSystemModule(),
        new SpaceModule(spaceToPath),
        new ReportModule(out, logLevel));
  }

  public static Injector createInjector(Path installationDir, PrintWriter out) {
    var spaceToPath = ImmutableMap.of(
        STANDARD_LIBRARY, installationDir.resolve(STD_LIB_DIR_NAME),
        BINARY, installationDir.resolve(BIN_DIR_NAME));
    return Guice.createInjector(PRODUCTION,
        new StandardLibraryFileSystemModule(),
        new BinaryFileSystemModule(),
        new DiskFileSystemModule(),
        new SpaceModule(spaceToPath),
        new ReportModule(out, INFO));
  }
}
