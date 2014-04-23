package org.smoothbuild.task.base;

import static org.smoothbuild.SmoothContants.CHARSET;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.taskoutputs.TaskOutput;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.base.err.NullResultError;
import org.smoothbuild.task.base.err.ReflexiveInternalError;
import org.smoothbuild.task.base.err.UnexpectedError;
import org.smoothbuild.task.exec.NativeApiImpl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Ordering;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class NativeCallWorker<T extends SValue> extends TaskWorker<T> {
  private final NativeFunction<T> function;
  private final ImmutableList<String> paramNames;

  public NativeCallWorker(NativeFunction<T> function, ImmutableList<String> paramNames,
      CodeLocation codeLocation) {
    super(workerHash(function, paramNames), function.type(), function.name().value(), false,
        function.isCacheable(), codeLocation);
    this.function = function;
    this.paramNames = paramNames;
  }

  private static HashCode workerHash(NativeFunction<?> function, ImmutableList<String> paramNames) {
    Hasher hasher = Hash.function().newHasher();
    hasher.putBytes(function.name().value().getBytes(CHARSET));
    for (String string : Ordering.natural().sortedCopy(paramNames)) {
      byte[] stringHash = Hash.string(string).asBytes();
      hasher.putBytes(stringHash);
    }
    HashCode hash = hasher.hash();

    return WorkerHashes.workerHash(NativeCallWorker.class, hash);
  }

  @Override
  public TaskOutput<T> execute(Iterable<? extends SValue> input, NativeApiImpl nativeApi) {
    T result = null;
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
      return new TaskOutput<>(nativeApi.loggedMessages());
    } else {
      return new TaskOutput<>(result, nativeApi.loggedMessages());
    }
  }

  private ImmutableMap<String, SValue> calculateArguments(Iterable<? extends SValue> dependencies) {
    Builder<String, SValue> builder = ImmutableMap.builder();
    Iterator<String> names = paramNames.iterator();
    Iterator<? extends SValue> values = dependencies.iterator();
    while (names.hasNext()) {
      String name = names.next();
      SValue value = values.next();
      builder.put(name, value);
    }
    return builder.build();
  }
}
