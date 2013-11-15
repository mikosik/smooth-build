package org.smoothbuild.task.exec;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.function.base.Function;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.Taskable;
import org.smoothbuild.util.Empty;

import com.google.common.collect.Lists;

public class ArtifactBuilder {
  private final TaskGenerator taskGenerator;
  private final List<Result> artifacts = Lists.newArrayList();

  @Inject
  public ArtifactBuilder(TaskGenerator taskGenerator) {
    this.taskGenerator = taskGenerator;
  }

  public void addArtifact(Function function) {
    artifacts.add(taskGenerator.generateTask(new TaskableCall(function)));
  }

  public void runBuild() {
    try {
      for (Result result : artifacts) {
        result.result();
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
