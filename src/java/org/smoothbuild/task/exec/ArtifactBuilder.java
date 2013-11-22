package org.smoothbuild.task.exec;

import static org.smoothbuild.io.cache.CacheModule.RESULTS_DIR;
import static org.smoothbuild.io.cache.CacheModule.VALUE_DB_DIR;
import static org.smoothbuild.io.cache.hash.HashCodes.toPath;
import static org.smoothbuild.io.fs.base.Path.path;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.Hashed;
import org.smoothbuild.lang.type.StringValue;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageType;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.Taskable;
import org.smoothbuild.util.Empty;

import com.google.common.collect.Maps;

public class ArtifactBuilder {
  private final TaskGenerator taskGenerator;
  private final FileSystem smoothFileSystem;
  private final Map<Name, Result> artifacts;

  @Inject
  public ArtifactBuilder(TaskGenerator taskGenerator, @SmoothDir FileSystem smoothFileSystem) {
    this.taskGenerator = taskGenerator;
    this.smoothFileSystem = smoothFileSystem;
    this.artifacts = Maps.newHashMap();
  }

  public void addArtifact(Function function) {
    Name name = function.name();
    Result result = taskGenerator.generateTask(new TaskableCall(function));
    artifacts.put(name, result);
  }

  public void runBuild() {
    try {
      for (Entry<Name, Result> artifact : artifacts.entrySet()) {
        Name name = artifact.getKey();
        Value value = artifact.getValue().result();

        store(name, value);
      }
    } catch (BuildInterruptedException e) {
      // Nothing to do. Just quit the build process.
    }
  }

  private void store(Name name, Value value) {
    Path artifactPath = RESULTS_DIR.append(path(name.value()));

    if (value.type() == Type.FILE) {
      storeFile(artifactPath, (File) value);
    } else if (value.type() == Type.FILE_ARRAY) {
      @SuppressWarnings("unchecked")
      Array<File> fileArray = (Array<File>) value;
      storeFileArray(artifactPath, fileArray);
    } else if (value.type() == Type.STRING) {
      storeString(artifactPath, (StringValue) value);
    } else if (value.type() == Type.STRING_ARRAY) {
      @SuppressWarnings("unchecked")
      Array<StringValue> stringArray = (Array<StringValue>) value;
      storeStringArray(artifactPath, stringArray);
    } else {
      throw new ErrorMessageException(new Message(MessageType.FATAL,
          "Bug in smooth binary.\nUnknown value type " + value.getClass().getName()));
    }
  }

  private void storeFile(Path artifactPath, File file) {
    Path targetPath = targetPath(file.content());
    smoothFileSystem.delete(artifactPath);
    smoothFileSystem.createLink(artifactPath, targetPath);
  }

  private void storeFileArray(Path artifactPath, Array<File> fileArray) {
    smoothFileSystem.delete(artifactPath);
    for (File file : fileArray) {
      Path linkPath = artifactPath.append(file.path());
      Path targetPath = targetPath(file.content());
      smoothFileSystem.createLink(linkPath, targetPath);
    }
  }

  private void storeString(Path artifactPath, StringValue string) {
    Path targetPath = targetPath(string);
    smoothFileSystem.delete(artifactPath);
    smoothFileSystem.createLink(artifactPath, targetPath);
  }

  private void storeStringArray(Path artifactPath, Array<StringValue> stringArray) {
    smoothFileSystem.delete(artifactPath);
    int i = 0;
    for (StringValue string : stringArray) {
      Path filePath = path(Integer.valueOf(i).toString());
      Path linkPath = artifactPath.append(filePath);
      Path targetPath = targetPath(string);
      smoothFileSystem.createLink(linkPath, targetPath);
      i++;
    }
  }

  private static Path targetPath(Hashed hashed) {
    return VALUE_DB_DIR.append(toPath(hashed.hash()));
  }

  private static class TaskableCall implements Taskable {
    private final Function function;

    public TaskableCall(Function function) {
      this.function = function;
    }

    @Override
    public Task generateTask(TaskGenerator taskGenerator) {
      CodeLocation ignoredCodeLocation = null;
      return function.generateTask(taskGenerator, Empty.stringTaskResultMap(), ignoredCodeLocation);
    }
  }
}
