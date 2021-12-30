package org.smoothbuild.vm;

import static org.smoothbuild.util.collect.Maps.mapValues;

import java.util.Map;
import java.util.Optional;

import org.smoothbuild.bytecode.obj.base.ObjB;
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

  public <K> Map<K, Optional<ObjB>> evaluate(ImmutableMap<K, ObjB> objs)
      throws InterruptedException {
    var jobs = mapValues(objs, jobCreator::eagerJobFor);
    var artifacts = parallelExecutor.executeAll(jobs);
    return artifacts;
  }
}
