package org.smoothbuild.common.dagger;

import static org.smoothbuild.common.filesystem.base.FullPath.fullPath;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.log.location.Locations.fileLocation;

import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.log.report.TraceLine;

public interface CommonTestApi {
  default Alias alias() {
    return alias("t-alias");
  }

  default Alias alias(String alias) {
    return Alias.alias(alias);
  }

  public default Trace trace() {
    return new Trace();
  }

  public default Trace trace(String name2, int line2, String name1, int line1) {
    var path = alias().append("path");
    return trace(name2, location(path, line2), name1, location(path, line1));
  }

  public default Trace trace(String name2, Location location2, String name1, Location location1) {
    var element1 = new TraceLine(name1, location1, null);
    var element2 = new TraceLine(name2, location2, element1);
    return new Trace(element2);
  }

  public default Trace trace(String name, int line) {
    var path = alias().append("path");
    return trace(name, location(path, line));
  }

  public default Trace trace(String name, Location location) {
    return new Trace(new TraceLine(name, location, null));
  }

  public default Location location(Alias alias) {
    return location(fullPath(alias, path("path")), 17);
  }

  public default Location location(FullPath fullPath, int line) {
    return fileLocation(fullPath, line);
  }
}
