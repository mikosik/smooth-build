package org.smoothbuild.function.plugin;

import java.lang.reflect.Method;

import javax.inject.Inject;

import org.smoothbuild.function.plugin.exc.PluginImplementationException;

public class PluginInvokerFactory {
  private final ReflexiveInvoker reflexiveInvoker;

  @Inject
  public PluginInvokerFactory(ReflexiveInvoker reflexiveInvoker) {
    this.reflexiveInvoker = reflexiveInvoker;
  }

  public PluginInvoker create(Method method, Class<?> paramsInterface)
      throws PluginImplementationException {
    ArgumentsCreator argumentsCreator = new ArgumentsCreator(paramsInterface);
    return new PluginInvoker(reflexiveInvoker, method, argumentsCreator);
  }
}
