package org.smoothbuild.builtin.java;

import static org.smoothbuild.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.testing.common.JarTester;
import org.smoothbuild.testing.type.impl.FakeFileSet;
import org.smoothbuild.testing.type.impl.FileTester;
import org.smoothbuild.type.api.File;

public class UnjarSmoothTest extends IntegrationTestCase {

  @Test
  public void testUnjaring() throws Exception {
    // given
    Path path1 = path("a/fileA.txt");
    Path path2 = path("b/fileB.txt");
    File jarFile = JarTester.jaredFiles(fileSystem, path1.value(), path2.value());

    script("run : file(" + jarFile.path() + ") | unjar | save('out');");

    // when
    smoothApp.run("run");

    // then
    messages.assertNoProblems();

    FakeFileSet outFiles = fileSet(path("out"));
    outFiles.contains(path1);
    outFiles.contains(path2);
    FileTester.assertContentContainsFilePath(outFiles.file(path1));
  }
}
