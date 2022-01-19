package org.smoothbuild.vm;

import static org.smoothbuild.util.collect.Maps.map;
import static org.smoothbuild.util.collect.Maps.mapValues;

import java.util.Optional;

import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.vm.job.JobCreator;
import org.smoothbuild.vm.parallel.ParallelJobExecutor;

import com.google.common.collect.ImmutableMap;

public class Vm {
  private final JobCreator jobCreator;
  private final ParallelJobExecutor parallelExecutor;

  public Vm(JobCreator jobCreator, ParallelJobExecutor parallelExecutor) {
    this.jobCreator = jobCreator;
    this.parallelExecutor = parallelExecutor;
  }

  public <K> Optional<ImmutableMap<K, ValB>> evaluate(ImmutableMap<K, ObjB> objs)
      throws InterruptedException {
    var jobs = mapValues(objs, jobCreator::eagerJobFor);
    var result = parallelExecutor.executeAll(jobs);
    if (result.values().stream().anyMatch(Optional::isEmpty)) {
      return Optional.empty();
    } else {
      return Optional.of(map(result, k -> k, Optional::get));
    }
  }
}
