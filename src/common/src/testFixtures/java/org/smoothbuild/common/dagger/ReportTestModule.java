package org.smoothbuild.common.dagger;

import dagger.Binds;
import dagger.Module;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.testing.TestReporter;

@Module
public interface ReportTestModule {
  @Binds
  Reporter bindReporter(TestReporter reporter);
}
