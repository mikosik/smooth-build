package org.smoothbuild.common.schedule;

import org.smoothbuild.common.log.report.Report;

public record Output<V>(V result, Report report) {}
