package org.smoothbuild.task.base;

import static org.smoothbuild.message.message.CallLocation.callLocation;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.nativ.NativeFunction;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.task.base.err.NullResultError;
import org.smoothbuild.task.base.err.ReflexiveInternalError;
import org.smoothbuild.task.base.err.UnexpectedError;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class NativeCallTask extends AbstractTask {
  private final NativeFunction function;
  private final ImmutableMap<String, Task> dependencies;

  public NativeCallTask(NativeFunction function, CodeLocation codeLocation,
      Map<String, Task> dependencies) {
    super(callLocation(function.signature().name(), codeLocation));
    this.function = function;
    this.dependencies = ImmutableMap.copyOf(dependencies);
  }

  @Override
  public void execute(Sandbox sandbox) {
    try {
      Object result = function.invoke(sandbox, calculateArguments());
      if (result == null && !isNullResultAllowed()) {
        sandbox.report(new NullResultError(location()));
      } else {
        setResult(result);
      }
    } catch (IllegalAccessException e) {
      sandbox.report(new ReflexiveInternalError(location(), e));
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof ErrorMessageException) {
        ErrorMessageException errorMessageException = (ErrorMessageException) cause;
        sandbox.report(errorMessageException.errorMessage());
      } else {
        sandbox.report(new UnexpectedError(location(), cause));
      }
    }
  }

  private boolean isNullResultAllowed() {
    return function.type() == Type.VOID;
  }

  private ImmutableMap<String, Object> calculateArguments() {
    Builder<String, Object> builder = ImmutableMap.builder();
    for (Map.Entry<String, Task> entry : dependencies.entrySet()) {
      String paramName = entry.getKey();
      Object result = entry.getValue().result();
      builder.put(paramName, result);
    }
    return builder.build();
  }

  @Override
  public ImmutableCollection<Task> dependencies() {
    return dependencies.values();
  }
}
