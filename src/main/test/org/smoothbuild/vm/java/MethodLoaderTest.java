package org.smoothbuild.vm.java;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.sink;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.io.fs.base.PathS.path;
import static org.smoothbuild.io.fs.space.FilePath.filePath;
import static org.smoothbuild.io.fs.space.Space.PRJ;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.io.Okios.copyAllAndClose;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.smoothbuild.nativefunc.ReturnAbc;
import org.smoothbuild.io.fs.space.JPathResolver;
import org.smoothbuild.testing.TestingContext;

public class MethodLoaderTest extends TestingContext {
  @Test
  public void method_is_cached(@TempDir Path tempDir) throws Exception {
    var jPathResolver = mock(JPathResolver.class);
    var fileLoader = mock(FileLoader.class);
    var methodLoader = new MethodLoader(jPathResolver, fileLoader);
    var jar = blobBWithJavaByteCode(ReturnAbc.class);
    var methodB = methodB(methodTB(stringTB(), list()), jar, stringB(ReturnAbc.class.getName()));

    var filePath = filePath(PRJ, path("file/path"));
    var jarPath = tempDir.resolve("file.jar");
    when(fileLoader.filePathOf(jar.hash()))
        .thenReturn(filePath);
    when(jPathResolver.resolve(filePath))
        .thenReturn(jarPath);
    copyAllAndClose(jar.source(), sink(jarPath));

    assertThat(methodLoader.load("", methodB))
        .isSameInstanceAs(methodLoader.load("", methodB));
  }
}
