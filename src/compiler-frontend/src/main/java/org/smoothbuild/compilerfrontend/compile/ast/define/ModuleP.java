package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.collect.List;

public final class ModuleP implements ScopedP {
  private final String name;
  private final List<StructP> structs;
  private final List<NamedEvaluableP> evaluables;
  private ScopeP scope;

  public ModuleP(String name, List<StructP> structs, List<NamedEvaluableP> evaluables) {
    this(name, structs, evaluables, null);
  }

  public ModuleP(
      String name, List<StructP> structs, List<NamedEvaluableP> evaluables, ScopeP scope) {
    this.name = name;
    this.structs = structs;
    this.evaluables = evaluables;
    this.scope = scope;
  }

  @Override
  public String name() {
    return name;
  }

  public List<NamedEvaluableP> evaluables() {
    return evaluables;
  }

  public List<StructP> structs() {
    return structs;
  }

  @Override
  public ScopeP scope() {
    return scope;
  }

  @Override
  public void setScope(ScopeP scope) {
    this.scope = scope;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof ModuleP that
        && Objects.equals(this.structs, that.structs)
        && Objects.equals(this.evaluables, that.evaluables);
  }

  @Override
  public int hashCode() {
    return Objects.hash(structs, evaluables);
  }

  @Override
  public String toString() {
    var fields = list(
            "structs = [",
            indent(structs().toString("\n")),
            "]",
            "evaluables = [",
            indent(evaluables().toString("\n")),
            "]")
        .toString("\n");
    return "ModuleP(\n" + indent(fields) + "\n)";
  }
}
