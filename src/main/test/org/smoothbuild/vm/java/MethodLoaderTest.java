package org.smoothbuild.vm.java;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.ClassLoader.getSystemClassLoader;
import static okio.Okio.sink;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.io.Okios.copyAllAndClose;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.smoothbuild.nativefunc.ReturnAbc;
import org.smoothbuild.testing.TestingContext;

public class MethodLoaderTest extends TestingContext {
  @Test
  public void method_is_cached(@TempDir Path tempDir) throws Exception {
    var methodLoader = new MethodLoader();
    var jar = blobBWithJavaByteCode(ReturnAbc.class);
    var methodB = methodB(methodTB(stringTB(), list()), jar, stringB(ReturnAbc.class.getName()));
    var jarPath = tempDir.resolve("file.jar");
    copyAllAndClose(jar.source(), sink(jarPath));

    var classLoaderProv = new ClassLoaderProv(getSystemClassLoader(), nativeApi());
    assertThat(methodLoader.load("", methodB, classLoaderProv))
        .isSameInstanceAs(methodLoader.load("", methodB, classLoaderProv));
  }
}
