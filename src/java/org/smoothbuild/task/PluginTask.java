package org.smoothbuild.task;

import java.util.Map;

import org.smoothbuild.function.plugin.PluginInvoker;
import org.smoothbuild.function.plugin.exc.FunctionReflectionException;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.problem.ProblemsListener;
import org.smoothbuild.run.err.FunctionError;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class PluginTask extends AbstractTask {
  private final PluginInvoker pluginInvoker;

  public PluginTask(PluginInvoker pluginInvoker, Map<String, Task> dependencies) {
    super(dependencies);
    this.pluginInvoker = pluginInvoker;
  }

  @Override
  public void calculateResult(ProblemsListener problems, Path tempDir) {
    try {
      setResult(pluginInvoker.invoke(tempDir, calculateArguments(dependencies())));
      // TODO handle also FileSystemException and others RuntimeException and
      // even Errors/Throwable (?)
    } catch (FunctionReflectionException e) {
      problems.report(new FunctionError(e));
      return;
    }
  }

  private static ImmutableMap<String, Object> calculateArguments(
      ImmutableMap<String, Task> dependencies) {
    Builder<String, Object> builder = ImmutableMap.builder();
    for (Map.Entry<String, Task> entry : dependencies.entrySet()) {
      builder.put(entry.getKey(), entry.getValue().result());
    }
    return builder.build();
  }
}
