package org.smoothbuild.function.plugin;

import org.smoothbuild.function.base.AbstractFunction;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.task.PluginTask;
import org.smoothbuild.task.Task;

import com.google.common.collect.ImmutableMap;

/**
 * Function that is implemented completely in java (as opposed to
 * {@link org.smoothbuild.function.plugin.PluginFunction} which is defined in
 * Smooth script using Smooth language).
 */
public class PluginFunction extends AbstractFunction {
  private final PluginInvoker pluginInvoker;

  public PluginFunction(Signature signature, PluginInvoker pluginInvoker) {
    super(signature);
    this.pluginInvoker = pluginInvoker;
  }

  @Override
  public Task generateTask(ImmutableMap<String, Task> dependencies) {
    return new PluginTask(pluginInvoker, dependencies);
  }
}
