package org.smoothbuild.lang.builtin.compress;

import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.builtin.file.PathArgValidator.validatedPath;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.exc.FileSystemException;
import org.smoothbuild.lang.function.value.Array;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.function.value.StringValue;
import org.smoothbuild.lang.plugin.FileBuilder;
import org.smoothbuild.lang.plugin.Required;
import org.smoothbuild.lang.plugin.Sandbox;
import org.smoothbuild.lang.plugin.SmoothFunction;

public class ZipFunction {

  public interface Parameters {
    @Required
    public Array<File> files();

    public StringValue output();

    // add missing parameters: level, comment, method
  }

  @SmoothFunction(name = "zip")
  public static File execute(Sandbox sandbox, Parameters params) {
    return new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private static final Path DEFAULT_OUTPUT = path("output.zip");
    private final Sandbox sandbox;
    private final Parameters params;

    private final byte[] buffer = new byte[Constants.BUFFER_SIZE];

    public Worker(Sandbox sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public File execute() {
      FileBuilder fileBuilder = sandbox.fileBuilder();
      fileBuilder.setPath(outputPath());

      try (ZipOutputStream zipOutputStream = new ZipOutputStream(fileBuilder.openOutputStream());) {
        for (File file : params.files()) {
          addEntry(zipOutputStream, file);
        }
      } catch (IOException e) {
        throw new FileSystemException(e);
      }

      return fileBuilder.build();
    }

    private Path outputPath() {
      if (params.output() == null) {
        return DEFAULT_OUTPUT;
      } else {
        return validatedPath("output", params.output());
      }
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
