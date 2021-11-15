package org.smoothbuild.exec.plan;

import static org.smoothbuild.util.collect.Maps.toMap;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.exec.job.Job;
import org.smoothbuild.lang.base.define.DefinitionsS;
import org.smoothbuild.lang.base.define.ValueS;

import com.google.common.collect.ImmutableMap;

public class ExecutionPlanner {
  private final JobCreatorProvider jobCreatorProvider;

  @Inject
  public ExecutionPlanner(JobCreatorProvider jobCreatorProvider) {
    this.jobCreatorProvider = jobCreatorProvider;
  }

  public ImmutableMap<ValueS, Job> createPlans(DefinitionsS definitions, List<ValueS> values) {
    JobCreator jobCreator = jobCreatorProvider.get(definitions);
    return toMap(values, jobCreator::commandLineValueEagerJob);
  }
}
