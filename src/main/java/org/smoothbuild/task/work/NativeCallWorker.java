package org.smoothbuild.task.work;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.function.nativ.NativeFunctionLegacy;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.NativeApiImpl;
import org.smoothbuild.task.work.err.NullResultError;
import org.smoothbuild.task.work.err.ReflexiveInternalError;
import org.smoothbuild.task.work.err.UnexpectedError;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Ordering;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class NativeCallWorker extends TaskWorker {
  private final NativeFunctionLegacy function;
  private final ImmutableList<String> paramNames;

  public NativeCallWorker(NativeFunctionLegacy function, List<String> paramNames, boolean isInternal,
      CodeLocation codeLocation) {
    super(nativeCallWorkerHash(function, paramNames), function.type(), function.name().value(),
        isInternal, function.isCacheable(), codeLocation);
    this.function = function;
    this.paramNames = ImmutableList.copyOf(paramNames);
  }

  private static HashCode nativeCallWorkerHash(NativeFunctionLegacy function, List<String> paramNames) {
    Hasher hasher = Hash.newHasher();
    hasher.putBytes(function.hash().asBytes());
    for (String string : Ordering.natural().sortedCopy(paramNames)) {
      byte[] stringHash = Hash.string(string).asBytes();
      hasher.putBytes(stringHash);
    }
    HashCode hash = hasher.hash();

    return WorkerHashes.workerHash(NativeCallWorker.class, hash);
  }

  @Override
  public TaskOutput execute(TaskInput input, NativeApiImpl nativeApi) {
    Value result = null;
    try {
      result = function.invoke(nativeApi, calculateArguments(input));
      if (result == null && !nativeApi.loggedMessages().containsProblems()) {
        nativeApi.log(new NullResultError());
      }
    } catch (IllegalAccessException e) {
      nativeApi.log(new ReflexiveInternalError(e));
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof Message) {
        nativeApi.log((Message) cause);
      } else {
        nativeApi.log(new UnexpectedError(cause));
      }
    }
    if (result == null) {
      return new TaskOutput(nativeApi.loggedMessages());
    } else {
      return new TaskOutput(result, nativeApi.loggedMessages());
    }
  }

  private ImmutableMap<String, Value> calculateArguments(TaskInput input) {
    Builder<String, Value> builder = ImmutableMap.builder();
    Iterator<String> names = paramNames.iterator();
    Iterator<? extends Value> values = input.values().iterator();
    while (names.hasNext()) {
      String name = names.next();
      Value value = values.next();
      builder.put(name, value);
    }
    return builder.build();
  }
}
