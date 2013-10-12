package org.smoothbuild.task;

import static org.smoothbuild.message.message.CallLocation.callLocation;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.nativ.NativeFunction;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.ErrorMessageException;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.task.err.NullResultError;
import org.smoothbuild.task.err.ReflexiveInternalError;
import org.smoothbuild.task.err.UnexpectedError;
import org.smoothbuild.util.Hash;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.hash.HashCode;

public class NativeCallTask extends AbstractTask {
  private final NativeFunction function;
  private final ImmutableMap<String, HashCode> dependencies;

  public NativeCallTask(NativeFunction function, CodeLocation codeLocation,
      Map<String, HashCode> dependencies) {
    super(callLocation(function.signature().name(), codeLocation), Hash
        .call(function, dependencies));
    this.function = function;
    this.dependencies = ImmutableMap.copyOf(dependencies);
  }

  @Override
  public void execute(Sandbox sandbox, HashedTasks hashedTasks) {
    try {
      Object result = function.invoke(sandbox, calculateArguments(hashedTasks));
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

  private ImmutableMap<String, Object> calculateArguments(HashedTasks hashedTasks) {
    Builder<String, Object> builder = ImmutableMap.builder();
    for (Map.Entry<String, HashCode> entry : dependencies.entrySet()) {
      String paramName = entry.getKey();
      Object value = hashedTasks.get(entry.getValue()).result();
      builder.put(paramName, value);
    }
    return builder.build();
  }

  @Override
  public ImmutableCollection<HashCode> dependencies() {
    return dependencies.values();
  }
}
