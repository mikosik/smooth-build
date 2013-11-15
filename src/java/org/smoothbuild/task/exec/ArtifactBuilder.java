package org.smoothbuild.task.exec;

import static org.smoothbuild.db.DbModule.RESULTS_DIR;
import static org.smoothbuild.db.DbModule.VALUE_DB_DIR;
import static org.smoothbuild.db.hash.HashCodes.toPath;
import static org.smoothbuild.fs.base.Path.path;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.smoothbuild.fs.SmoothDir;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageType;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.Hashed;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.plugin.Value;
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

    if (value instanceof File) {
      storeFile(artifactPath, (File) value);
    } else if (value instanceof FileSet) {
      storeFileSet(artifactPath, (FileSet) value);
    } else if (value instanceof StringValue) {
      storeString(artifactPath, (StringValue) value);
    } else if (value instanceof StringSet) {
      storeStringSet(artifactPath, (StringSet) value);
    } else {
      // TODO remove null check once void functions are disallowed
      if (value != null) {
        throw new ErrorMessageException(new Message(MessageType.FATAL,
            "Bug in smooth binary.\nUnknown value type " + value.getClass().getName()));
      }
    }
  }

  private void storeFile(Path artifactPath, File file) {
    Path targetPath = targetPath(file.content());
    smoothFileSystem.delete(artifactPath);
    smoothFileSystem.createLink(artifactPath, targetPath);
  }

  private void storeFileSet(Path artifactPath, FileSet fileSet) {
    smoothFileSystem.delete(artifactPath);
    for (File file : fileSet) {
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

  private void storeStringSet(Path artifactPath, StringSet stringSet) {
    smoothFileSystem.delete(artifactPath);
    int i = 0;
    for (StringValue string : stringSet) {
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
