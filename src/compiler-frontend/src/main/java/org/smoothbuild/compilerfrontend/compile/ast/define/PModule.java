package org.smoothbuild.compilerfrontend.compile.ast.define;

import java.util.Objects;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.log.location.FileLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public final class PModule implements PContainer {
  private final FullPath fullPath;
  private final List<PStruct> structs;
  private final List<PNamedEvaluable> evaluables;
  private PScope scope;

  public PModule(FullPath fullPath, List<PStruct> structs, List<PNamedEvaluable> evaluables) {
    this(fullPath, structs, evaluables, null);
  }

  public PModule(
      FullPath fullPath, List<PStruct> structs, List<PNamedEvaluable> evaluables, PScope scope) {
    this.fullPath = fullPath;
    this.structs = structs;
    this.evaluables = evaluables;
    this.scope = scope;
  }

  public FullPath fullPath() {
    return fullPath;
  }

  @Override
  public Fqn fqn() {
    return null;
  }

  @Override
  public Location location() {
    return new FileLocation(fullPath, 0);
  }

  public List<PNamedEvaluable> evaluables() {
    return evaluables;
  }

  public List<PStruct> structs() {
    return structs;
  }

  @Override
  public PScope scope() {
    return scope;
  }

  @Override
  public void setScope(PScope scope) {
    this.scope = scope;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof PModule that
        && Objects.equals(this.structs, that.structs)
        && Objects.equals(this.evaluables, that.evaluables);
  }

  @Override
  public int hashCode() {
    return Objects.hash(structs, evaluables);
  }

  @Override
  public String toString() {
    return new ToStringBuilder("PModule")
        .addField("fullPath", fullPath)
        .addListField("structs", structs)
        .addListField("evaluables", evaluables)
        .addField("scope", scope)
        .toString();
  }
}
