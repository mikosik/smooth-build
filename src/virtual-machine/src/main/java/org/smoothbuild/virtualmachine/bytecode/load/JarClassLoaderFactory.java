package org.smoothbuild.virtualmachine.bytecode.load;

import static java.lang.ClassLoader.getSystemClassLoader;
import static okio.Okio.buffer;
import static org.smoothbuild.common.function.Function1.memoizer;
import static org.smoothbuild.common.reflect.ClassLoaders.mapClassLoader;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.fileContent;
import static org.smoothbuild.virtualmachine.bytecode.helper.FileStruct.filePath;
import static org.smoothbuild.virtualmachine.evaluate.plugin.UnzipBlob.unzipBlob;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Result;
import org.smoothbuild.common.dagger.PerCommand;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;

/**
 * Factory for creating classLoaders that load classes from jar file provided as BlobB.
 * This class is thread-safe.
 */
@PerCommand
public class JarClassLoaderFactory {
  private final BytecodeFactory bytecodeFactory;
  private final ClassLoader parentClassLoader;
  private final Function1<BBlob, Result<ClassLoader>, IOException> memoizer;

  @Inject
  public JarClassLoaderFactory(BytecodeFactory bytecodeFactory) {
    this(bytecodeFactory, getSystemClassLoader());
  }

  public JarClassLoaderFactory(BytecodeFactory bytecodeFactory, ClassLoader parentClassLoader) {
    this.bytecodeFactory = bytecodeFactory;
    this.parentClassLoader = parentClassLoader;
    this.memoizer = memoizer(this::newClassLoader);
  }

  public Result<ClassLoader> classLoaderFor(BBlob jar) throws IOException {
    return memoizer.apply(jar);
  }

  private Result<ClassLoader> newClassLoader(BBlob jar) throws IOException {
    return unzipBlob(bytecodeFactory, jar, s -> true)
        .mapOk(this::newClassLoader)
        .mapErr(error -> "Error unpacking jar with native code: " + error);
  }

  private ClassLoader newClassLoader(BArray files) throws BytecodeException {
    var filesMap = files.elements(BTuple.class).toMap(f -> filePath(f).toJavaString(), x -> x);
    return newClassLoader(filesMap);
  }

  private ClassLoader newClassLoader(Map<String, BTuple> filesMap) {
    return mapClassLoader(parentClassLoader, path -> {
      BTuple file = filesMap.get(path);
      return file == null ? null : buffer(fileContent(file).source()).inputStream();
    });
  }
}
