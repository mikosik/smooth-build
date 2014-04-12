package org.smoothbuild.lang.builtin.java;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.builtin.string.ToBlobFunction.stringToBlob;

import java.io.File;
import java.io.IOException;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.exc.FileSystemError;
import org.smoothbuild.io.temp.TempDirectory;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SString;

import com.tonicsystems.jarjar.Main;

public class JarjarFunction {
  public interface Parameters {
    @Required
    public SString rules();

    @Required
    public SBlob in();
  }

  @SmoothFunction(name = "jarjar")
  public static SBlob execute(NativeApi nativeApi, Parameters params) throws InterruptedException {
    TempDirectory tempDir = nativeApi.createTempDirectory();

    Path rulesPath = path("rules");
    Path inJarPath = path("in.jar");
    Path outJarPath = path("out.jar");

    tempDir.writeFile(rulesPath, stringToBlob(nativeApi, params.rules()));
    tempDir.writeFile(inJarPath, params.in());

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
