package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.NativeApiImpl;
import org.smoothbuild.task.work.TaskWorker;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class Task<T extends Value> {
  private final TaskWorker<T> worker;
  private final ImmutableList<Task<?>> dependencies;
  private TaskOutput<T> output;

  public Task(TaskWorker<T> worker, ImmutableList<Task<?>> dependencies) {
    this.worker = worker;
    this.dependencies = dependencies;
    this.output = null;
  }

  public TaskWorker<T> worker() {
    return worker;
  }

  public ImmutableList<Task<?>> dependencies() {
    return dependencies;
  }

  public String name() {
    return worker.name();
  }

  public SType<T> resultType() {
    return worker.resultType();
  }

  public boolean isInternal() {
    return worker.isInternal();
  }

  public boolean isCacheable() {
    return worker.isCacheable();
  }

  public CodeLocation codeLocation() {
    return worker.codeLocation();
  }

  public void execute(NativeApiImpl nativeApi) {
    output = worker.execute(input(), nativeApi);
  }

  public TaskOutput<T> output() {
    checkState(output != null);
    return output;
  }

  public void setOutput(TaskOutput<T> output) {
    this.output = output;
  }

  public boolean hasOutput() {
    return output != null;
  }

  public HashCode hash() {
    Hasher hasher = Hash.newHasher();
    hasher.putBytes(worker.hash().asBytes());
    hasher.putBytes(input().hash().asBytes());
    return hasher.hash();
  }

  private TaskInput input() {
    return TaskInput.fromTaskReturnValues(dependencies);
  }
}
