package org.smoothbuild.task;

import static org.smoothbuild.message.message.CallLocation.callLocation;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.nativ.Invoker;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.ErrorMessageException;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.task.err.NullResultError;
import org.smoothbuild.task.err.ReflexiveInternalError;
import org.smoothbuild.task.err.UnexpectedError;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class NativeCallTask extends AbstractTask {
  private final Signature signature;
  private final Invoker invoker;
  private final ImmutableMap<String, Task> dependencies;

  public NativeCallTask(Signature signature, CodeLocation codeLocation, Invoker invoker,
      Map<String, Task> dependencies) {
    super(callLocation(signature.name(), codeLocation));
    this.dependencies = ImmutableMap.copyOf(dependencies);
    this.signature = signature;
    this.invoker = invoker;
  }

  @Override
  public void execute(Sandbox sandbox) {
    try {
      Object result = invoker.invoke(sandbox, calculateArguments());
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
    return signature.type() == Type.VOID;
  }

  private ImmutableMap<String, Object> calculateArguments() {
    Builder<String, Object> builder = ImmutableMap.builder();
    for (Map.Entry<String, Task> entry : dependencies.entrySet()) {
      builder.put(entry.getKey(), entry.getValue().result());
    }
    return builder.build();
  }

  @Override
  public ImmutableCollection<Task> dependencies() {
    return dependencies.values();
  }
}
