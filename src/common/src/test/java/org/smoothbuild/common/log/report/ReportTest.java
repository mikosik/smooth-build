package org.smoothbuild.common.log.report;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.Origin.DISK;
import static org.smoothbuild.common.log.report.Report.report;

import org.junit.jupiter.api.Test;

public class ReportTest {
  @Test
  void with_label() {
    var report = report(label("name"), new Trace(), DISK, list(error("error")));
    assertThat(report.withLabel(label("other")))
        .isEqualTo(report(label("other"), new Trace(), DISK, list(error("error"))));
  }

  @Test
  void with_logs() {
    var report = report(label("name"), new Trace(), DISK, list(error("error")));
    assertThat(report.withLogs(list(info("info"))))
        .isEqualTo(report(label("name"), new Trace(), DISK, list(info("info"))));
  }

  @Test
  void map_label() {
    var report = report(label("name"), new Trace(), DISK, list(error("error")));
    assertThat(report.mapLabel(label -> label.append("suffix")))
        .isEqualTo(report(label("name:suffix"), new Trace(), DISK, list(error("error"))));
  }

  @Test
  void map_logs() {
    var report = report(label("name"), new Trace(), DISK, list(error("error")));
    assertThat(report.mapLogs(logs -> logs.append(info("info"))))
        .isEqualTo(report(label("name"), new Trace(), DISK, list(error("error"), info("info"))));
  }
}
