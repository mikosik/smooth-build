package org.smoothbuild.builtin.java;

import static org.smoothbuild.builtin.string.ToBlobFunction.stringToBlob;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.File;
import java.io.IOException;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.io.util.TempDirectory;
import org.smoothbuild.lang.plugin.Name;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SString;

import com.tonicsystems.jarjar.Main;

public class JarjarFunction {
  @SmoothFunction
  public static Blob jarjar( //
      NativeApi nativeApi, //
      @Required @Name("rules") SString rules, //
      @Required @Name("in") Blob in) {
    TempDirectory tempDir = nativeApi.createTempDirectory();

    Path rulesPath = path("rules");
    Path inJarPath = path("in.jar");
    Path outJarPath = path("out.jar");

    tempDir.writeFile(rulesPath, stringToBlob(nativeApi, rules));
    tempDir.writeFile(inJarPath, in);

    File rulesFile = new File(tempDir.asOsPath(rulesPath));
    File inJarFile = new File(tempDir.asOsPath(inJarPath));
    File outJarFile = new File(tempDir.asOsPath(outJarPath));

    try {
      Main main = new Main();
      main.process(rulesFile, inJarFile, outJarFile);
    } catch (IOException e) {
      throw new FileSystemError(e);
    }

    return tempDir.readContent(outJarPath);
  }
}
