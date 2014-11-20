package org.smoothbuild.builtin.java;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.ARTIFACTS_PATH;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class JarTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  @Before
  public void before() {
    createInjector(new IntegrationTestModule()).injectMembers(this);
  }

  @Test
  public void jar_function() throws Exception {
    // given
    Path root = path("dir");
    Path path1 = path("dir/fileA.txt");
    Path path2 = path("dir/fileB.txt");
    fileSystem.createFileContainingItsPath(root, path1);
    fileSystem.createFileContainingItsPath(root, path2);

    script(fileSystem, "run : files(" + root + ") | jar ;");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();

    byte[] buffer = new byte[2048];
    int fileCount = 0;
    Path artifactPath = ARTIFACTS_PATH.append(path("run"));
    InputStream inputStream = fileSystem.openInputStream(artifactPath);
    try (JarInputStream jarInputStream = new JarInputStream(inputStream)) {
      JarEntry entry = null;
      while ((entry = jarInputStream.getNextJarEntry()) != null) {
        fileCount++;
        Path path = path(entry.getName());
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
          int len = 0;
          while ((len = jarInputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
          }
          assertEquals(new String(outputStream.toByteArray(), CHARSET), path.value());
        }
      }
    }
    assertEquals(fileCount, 2);
  }
}
