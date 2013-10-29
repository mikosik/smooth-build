package org.smoothbuild.task.base;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.nativ.NativeFunction;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.object.ResultDb;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.err.NullResultError;
import org.smoothbuild.task.base.err.ReflexiveInternalError;
import org.smoothbuild.task.base.err.UnexpectedError;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class NativeCallTask implements Task {
  // TODO
  @SuppressWarnings("unused")
  private final ResultDb resultDb;
  private final NativeFunction function;
  private final ImmutableMap<String, Result> dependencies;

  public NativeCallTask(ResultDb resultDb, NativeFunction function,
      Map<String, Result> dependencies) {
    this.resultDb = resultDb;
    this.function = function;
    this.dependencies = ImmutableMap.copyOf(dependencies);
  }

  @Override
  public Value execute(Sandbox sandbox) {
    try {
      Value result = function.invoke(sandbox, calculateArguments());
      if (result == null && !isNullResultAllowed()) {
        sandbox.report(new NullResultError());
      } else {
        return result;
      }
    } catch (IllegalAccessException e) {
      sandbox.report(new ReflexiveInternalError(e));
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof ErrorMessageException) {
        ErrorMessageException errorMessageException = (ErrorMessageException) cause;
        sandbox.report(errorMessageException.errorMessage());
      } else {
        sandbox.report(new UnexpectedError(cause));
      }
    }
    return null;
  }

  private boolean isNullResultAllowed() {
    return function.type() == Type.VOID;
  }

  private ImmutableMap<String, Value> calculateArguments() {
    Builder<String, Value> builder = ImmutableMap.builder();
    for (Map.Entry<String, Result> entry : dependencies.entrySet()) {
      String paramName = entry.getKey();
      Value result = entry.getValue().result();
      builder.put(paramName, result);
    }
    return builder.build();
  }
}
