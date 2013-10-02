package org.smoothbuild.builtin.java;

import static org.smoothbuild.plugin.api.Path.path;

import org.junit.Test;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.common.JarTester;
import org.smoothbuild.testing.plugin.internal.FileTester;
import org.smoothbuild.testing.plugin.internal.TestFile;
import org.smoothbuild.testing.plugin.internal.TestFileSet;

public class UnjarSmoothTest extends IntegrationTestCase {

  @Test
  public void testUnjaring() throws Exception {
    // given
    Path fileA = path("a/fileA.txt");
    Path fileB = path("b/fileB.txt");
    TestFile jarFile = file(path("input.jar"));
    JarTester.jarFiles(jarFile, fileA.value(), fileB.value());

    script("run : file(" + jarFile.path() + ") | unjar | save('out');");

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();

    TestFileSet outFiles = fileSet(path("out"));
    outFiles.contains(fileA);
    outFiles.contains(fileB);
    FileTester.assertContentContainsFilePath(outFiles.file(fileA));
  }
}
