package org.smoothbuild.exec.plan;

import static org.smoothbuild.util.collect.Maps.mapValues;
import static org.smoothbuild.util.collect.Maps.toMap;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.exec.job.Job;
import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.lang.expr.TopRefS;

import com.google.common.collect.ImmutableMap;

public class ExecutionPlanner {
  private final JobCreatorProvider jobCreatorProvider;
  private final CompilerProv compilerProv;

  @Inject
  public ExecutionPlanner(JobCreatorProvider jobCreatorProvider,
      CompilerProv compilerProv) {
    this.jobCreatorProvider = jobCreatorProvider;
    this.compilerProv = compilerProv;
  }

  public ImmutableMap<TopRefS, Job> createPlans(DefsS defs, List<TopRefS> values) {
    var shConverter = compilerProv.get(defs);
    var shMapping = toMap(values, shConverter::convertExpr);
    var jobCreator = jobCreatorProvider.get(shConverter.nals());
    return mapValues(shMapping, jobCreator::eagerJobFor);
  }
}
