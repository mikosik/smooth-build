package org.smoothbuild.builtin.file;

import static org.smoothbuild.builtin.file.PathArgValidator.validatedPath;
import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.message.message.MessageType.FATAL;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.builtin.file.err.DirParamIsAFileError;
import org.smoothbuild.builtin.file.err.DirParamSubdirIsAFileError;
import org.smoothbuild.builtin.file.err.EitherFileOrFilesMustBeProvidedError;
import org.smoothbuild.builtin.file.err.FileAndFilesSpecifiedError;
import org.smoothbuild.builtin.file.err.FileOutputIsADirError;
import org.smoothbuild.builtin.file.err.FileOutputSubdirIsAFileError;
import org.smoothbuild.builtin.file.err.WriteToSmoothDirError;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.Required;
import org.smoothbuild.plugin.SmoothFunction;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.task.exec.SandboxImpl;

import com.google.common.io.ByteStreams;

public class SaveFunction {
  public interface Parameters {
    public File file();

    public FileSet files();

    @Required
    public StringValue dir();
  }

  @SmoothFunction(name = "save")
  public static void execute(SandboxImpl sandbox, Parameters params) {
    new Worker(sandbox, params).execute();
  }

  private static class Worker {
    private final SandboxImpl sandbox;
    private final Parameters params;

    public Worker(SandboxImpl sandbox, Parameters params) {
      this.sandbox = sandbox;
      this.params = params;
    }

    public void execute() {
      Path dirPath = validatedPath("dir", params.dir());
      checkDirParam(dirPath);

      File file = params.file();
      FileSet files = params.files();
      if (file == null && files == null) {
        throw new ErrorMessageException(new EitherFileOrFilesMustBeProvidedError());
      }
      if (file != null && files != null) {
        throw new ErrorMessageException(new FileAndFilesSpecifiedError());
      }

      if (file != null) {
        save(dirPath, file);
      } else {
        save(dirPath, files);
      }
    }

    private void save(Path dirPath, File file) {
      checkFilePath(dirPath, file.path());
      saveImpl(dirPath, file);
    }

    private void save(Path dirPath, FileSet files) {
      for (File file1 : files) {
        checkFilePath(dirPath, file1.path());
      }
      for (File file1 : files) {
        saveImpl(dirPath, file1);
      }
    }

    private void saveImpl(Path dirPath, File file) {
      Path destination = dirPath.append(file.path());

      try (InputStream inputStream = file.openInputStream();
          OutputStream outputStream = sandbox.projectFileSystem().openOutputStream(destination);) {
        ByteStreams.copy(inputStream, outputStream);
      } catch (IOException e) {
        throw new FileSystemException(e);
      }
    }

    private void checkDirParam(Path dirPath) {
      if (dirPath.isRoot()) {
        return;
      }
      if (dirPath.firstPart().equals(BUILD_DIR)) {
        throw new ErrorMessageException(new WriteToSmoothDirError(dirPath));
      }

      Path path = dirPath;
      FileSystem fileSystem = sandbox.projectFileSystem();
      while (!path.isRoot()) {
        switch (fileSystem.pathState(path)) {
          case FILE:
            if (path.equals(dirPath)) {
              throw new ErrorMessageException(new DirParamIsAFileError("dir", path));
            } else {
              throw new ErrorMessageException(new DirParamSubdirIsAFileError("dir", dirPath, path));
            }
          case DIR:
            return;
          case NOTHING:
            path = path.parent();
            break;
          default:
            throw new ErrorMessageException(new Message(FATAL,
                "Broken 'save' function implementation: unreachable case"));
        }
      }
    }

    private void checkFilePath(Path dirPath, Path filePath) {
      if (dirPath.isRoot() && filePath.firstPart().equals(BUILD_DIR)) {
        throw new ErrorMessageException(new WriteToSmoothDirError(filePath));
      }

      Path fullPath = dirPath.append(filePath);
      FileSystem fileSystem = sandbox.projectFileSystem();
      switch (fileSystem.pathState(fullPath)) {
        case FILE:
          return;
        case DIR:
          throw new ErrorMessageException(new FileOutputIsADirError(dirPath, filePath));
        case NOTHING:
          break;
        default:
          throw new RuntimeException("unreachable case");
      }

      Path path = fullPath.parent();
      while (!path.equals(dirPath)) {
        switch (fileSystem.pathState(path)) {
          case FILE:
            throw new ErrorMessageException(new FileOutputSubdirIsAFileError(dirPath, filePath,
                path));
          case DIR:
            return;
          case NOTHING:
            path = path.parent();
            break;
          default:
            throw new ErrorMessageException(new Message(FATAL,
                "Broken 'save' function implementation: unreachable case"));
        }
      }
    }
  }
}
