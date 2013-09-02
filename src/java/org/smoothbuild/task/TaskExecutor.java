package org.smoothbuild.task;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.plugin.Path.path;

import java.util.Collection;

import javax.inject.Inject;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.plugin.SandboxImpl;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.problem.DetectingErrorsProblemsListener;
import org.smoothbuild.problem.ProblemsListener;

import com.google.common.collect.ImmutableMap;

public class TaskExecutor {
  private final FileSystem fileSystem;

  @Inject
  public TaskExecutor(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public void execute(ProblemsListener problemsListener, Task task) {
    new Worker(problemsListener).execute(task);
  }

  private class Worker {
    private final DetectingErrorsProblemsListener problems;
    private int temptDirCount = 0;

    public Worker(ProblemsListener problemsListener) {
      this.problems = new DetectingErrorsProblemsListener(problemsListener);
    }

    private void execute(Task task) {
      ImmutableMap<String, Task> dependencies = task.dependencies();
      calculateTasks(dependencies.values());

      if (problems.errorDetected()) {
        return;
      }

      Path tempPath = BUILD_DIR.append(path(Integer.toString(temptDirCount++)));
      task.calculateResult(problems, new SandboxImpl(fileSystem, tempPath, problems));
    }

    private void calculateTasks(Collection<Task> tasks) {
      for (Task task : tasks) {
        if (!task.isResultCalculated()) {
          execute(task);
        }
        if (problems.errorDetected()) {
          return;
        }
      }
    }
  }
}
