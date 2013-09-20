package org.smoothbuild.builtin.compress;

import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.FileSet;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.Required;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.api.SmoothFunction;

public class ZipFunction {

  public interface Parameters {
    @Required
    public FileSet fileSet();

    // add missing parameters: level, comment, method
  }

  @SmoothFunction("zip")
  public static File execute(Sandbox sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private final Sandbox sandbox;
    private final Parameters params;

    private final byte[] buffer = new byte[Constants.BUFFER_SIZE];

    public Worker(Sandbox sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public File execute() {
      MutableFile output = sandbox.createFile(path("output.zip"));
      try (ZipOutputStream zipOutputStream = new ZipOutputStream(output.openOutputStream());) {
        for (File file : params.fileSet()) {
          addEntry(zipOutputStream, file);
        }
      } catch (IOException e) {
        throw new FileSystemException(e);
      }

      return output;
    }

    private void addEntry(ZipOutputStream zipOutputStream, File file) throws IOException {
      ZipEntry entry = new ZipEntry(file.path().value());
      zipOutputStream.putNextEntry(entry);

      try (InputStream inputStream = file.openInputStream();) {
        int readCount = inputStream.read(buffer);
        while (readCount > 0) {
          zipOutputStream.write(buffer, 0, readCount);
          readCount = inputStream.read(buffer);
        }
      }

      zipOutputStream.closeEntry();
    }
  }
}
