package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.readFileContentAlgorithmHash;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.exec.java.JavaCodeLoader;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.io.fs.space.FileResolver;
import org.smoothbuild.io.util.JarFile;
import org.smoothbuild.plugin.NativeApi;

import okio.BufferedSource;

public class ReadFileContentAlgorithm extends Algorithm {
  private final FilePath nativeFile;
  private final JavaCodeLoader javaCodeLoader;
  private final FileResolver fileResolver;

  public ReadFileContentAlgorithm(Spec spec, FilePath nativeFile,
      JavaCodeLoader javaCodeLoader, FileResolver fileResolver) {
    super(spec, false);
    this.nativeFile = nativeFile;
    this.javaCodeLoader = javaCodeLoader;
    this.fileResolver = fileResolver;
  }

  @Override
  public Hash hash() {
    return readFileContentAlgorithmHash(nativeFile);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) throws IOException {
    Blob content = createContent(nativeApi, nativeFile);
    if (content == null) {
      return new Output(null, nativeApi.messages());
    }
    javaCodeLoader.storeJarFile(new JarFile(nativeFile, content.hash()));
    return new Output(content, nativeApi.messages());
  }

  private Blob createContent(NativeApi nativeApi, FilePath filePath) {
    try (BufferedSource source = fileResolver.source(filePath)) {
      return nativeApi.factory().blob(sink -> sink.writeAll(source));
    } catch (FileNotFoundException e) {
      nativeApi.log().error("Cannot find file " + nativeFile.q() + ".");
      return null;
    } catch (IOException e) {
      nativeApi.log().error("Error reading file " + nativeFile.q() + ".");
      return null;
    }
  }
}
