package org.smoothbuild.task.exec;

import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.Taskable;
import org.smoothbuild.task.exec.save.ArtifactSaver;
import org.smoothbuild.util.Empty;

import com.google.common.collect.Maps;

public class ArtifactBuilder {
  private final TaskGenerator taskGenerator;
  private final ArtifactSaver artifactSaver;
  private final Map<Name, Result> artifacts;

  @Inject
  public ArtifactBuilder(TaskGenerator taskGenerator, ArtifactSaver artifactSaver) {
    this.taskGenerator = taskGenerator;
    this.artifactSaver = artifactSaver;
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
        SValue value = artifact.getValue().value();

        artifactSaver.save(name, value);
      }
    } catch (BuildInterruptedException e) {
      // Nothing to do. Just quit the build process.
    }
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
