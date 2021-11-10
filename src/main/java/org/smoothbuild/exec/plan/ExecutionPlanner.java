package org.smoothbuild.exec.plan;

import static org.smoothbuild.util.collect.Maps.toMap;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.exec.job.Job;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.ValueS;

import com.google.common.collect.ImmutableMap;

public class ExecutionPlanner {
  private final TaskCreatorProvider taskCreatorProvider;

  @Inject
  public ExecutionPlanner(TaskCreatorProvider taskCreatorProvider) {
    this.taskCreatorProvider = taskCreatorProvider;
  }

  public ImmutableMap<ValueS, Job> createPlans(Definitions definitions, List<ValueS> values) {
    JobCreator jobCreator = taskCreatorProvider.get(definitions);
    return toMap(values, jobCreator::commandLineValueEagerJob);
  }
}
