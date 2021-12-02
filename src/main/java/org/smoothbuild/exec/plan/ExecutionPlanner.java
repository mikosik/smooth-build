package org.smoothbuild.exec.plan;

import static org.smoothbuild.util.collect.Maps.mapValues;
import static org.smoothbuild.util.collect.Maps.toMap;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.exec.job.Job;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.lang.expr.RefS;

import com.google.common.collect.ImmutableMap;

public class ExecutionPlanner {
  private final JobCreatorProvider jobCreatorProvider;
  private final ShConvProv shConvProv;

  @Inject
  public ExecutionPlanner(JobCreatorProvider jobCreatorProvider,
      ShConvProv shConvProv) {
    this.jobCreatorProvider = jobCreatorProvider;
    this.shConvProv = shConvProv;
  }

  public ImmutableMap<RefS, Job> createPlans(DefsS defs, List<RefS> values) {
    var shConverter = shConvProv.get(defs);
    var shMapping = toMap(values, shConverter::convertExpr);
    var jobCreator = jobCreatorProvider.get(shConverter.nals());
    return mapValues(shMapping, jobCreator::commandLineExprEagerJob);
  }
}
