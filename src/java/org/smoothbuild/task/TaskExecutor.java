package org.smoothbuild.task;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.plugin.Path.path;

import java.util.Collection;

import org.smoothbuild.plugin.Path;
import org.smoothbuild.problem.DetectingErrorsProblemsListener;
import org.smoothbuild.problem.ProblemsListener;

import com.google.common.collect.ImmutableMap;

public class TaskExecutor {

  public static void execute(ProblemsListener problemsListener, Task task) {
    new Worker(problemsListener).execute(task);
  }

  private static class Worker {
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
      task.calculateResult(problems, tempPath);
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
