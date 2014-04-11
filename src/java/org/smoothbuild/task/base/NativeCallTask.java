package org.smoothbuild.task.base;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.base.err.NullResultError;
import org.smoothbuild.task.base.err.ReflexiveInternalError;
import org.smoothbuild.task.base.err.UnexpectedError;
import org.smoothbuild.task.exec.PluginApiImpl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class NativeCallTask<T extends SValue> extends Task<T> {
  private final NativeFunction<T> function;
  private final ImmutableMap<String, Result<?>> dependencies;

  public NativeCallTask(NativeFunction<T> function, Map<String, ? extends Result<?>> dependencies,
      CodeLocation codeLocation) {
    super(function.type(), function.name().value(), false, codeLocation);
    this.function = function;
    this.dependencies = ImmutableMap.copyOf(dependencies);
  }

  @Override
  public T execute(PluginApiImpl pluginApi) {
    try {
      T result = function.invoke(pluginApi, calculateArguments());
      if (result == null && !pluginApi.loggedMessages().containsProblems()) {
        pluginApi.log(new NullResultError());
      } else {
        return result;
      }
    } catch (IllegalAccessException e) {
      pluginApi.log(new ReflexiveInternalError(e));
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof Message) {
        pluginApi.log((Message) cause);
      } else {
        pluginApi.log(new UnexpectedError(cause));
      }
    }
    return null;
  }

  private ImmutableMap<String, SValue> calculateArguments() {
    Builder<String, SValue> builder = ImmutableMap.builder();
    for (Map.Entry<String, Result<?>> entry : dependencies.entrySet()) {
      String paramName = entry.getKey();
      SValue result = entry.getValue().value();
      builder.put(paramName, result);
    }
    return builder.build();
  }
}
