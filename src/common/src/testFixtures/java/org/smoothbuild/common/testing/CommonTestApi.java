package org.smoothbuild.common.testing;

import static org.smoothbuild.common.filesystem.base.FullPath.fullPath;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.log.location.Locations.fileLocation;

import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.common.log.report.STrace;
import org.smoothbuild.common.schedule.Scheduler;

public interface CommonTestApi {
  public Scheduler scheduler();

  public TestReporter reporter();

  default Alias alias() {
    return alias("t-alias");
  }

  default Alias alias(String alias) {
    return Alias.alias(alias);
  }

  public default STrace sTrace() {
    return new STrace();
  }

  public default STrace sTrace(String name2, int line2, String name1, int line1) {
    var path = alias().append("path");
    return sTrace(name2, location(path, line2), name1, location(path, line1));
  }

  public default STrace sTrace(String name2, Location location2, String name1, Location location1) {
    var element1 = new STrace.Line(name1, location1, null);
    var element2 = new STrace.Line(name2, location2, element1);
    return new STrace(element2);
  }

  public default STrace sTrace(String name, int line) {
    var path = alias().append("path");
    return sTrace(name, location(path, line));
  }

  public default STrace sTrace(String name, Location location) {
    return new STrace(new STrace.Line(name, location, null));
  }

  public default Location location(Alias alias) {
    return location(fullPath(alias, path("path")), 17);
  }

  public default Location location(FullPath fullPath, int line) {
    return fileLocation(fullPath, line);
  }

}
