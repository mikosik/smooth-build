package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import java.util.Objects;
import org.smoothbuild.common.collect.List;

public final class PModule implements PScoped {
  private final String name;
  private final List<PStruct> structs;
  private final List<PNamedEvaluable> evaluables;
  private PScope scope;

  public PModule(String name, List<PStruct> structs, List<PNamedEvaluable> evaluables) {
    this(name, structs, evaluables, null);
  }

  public PModule(
      String name, List<PStruct> structs, List<PNamedEvaluable> evaluables, PScope scope) {
    this.name = name;
    this.structs = structs;
    this.evaluables = evaluables;
    this.scope = scope;
  }

  @Override
  public String name() {
    return name;
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
    var fields = list(
            "structs = [",
            indent(structs().toString("\n")),
            "]",
            "evaluables = [",
            indent(evaluables().toString("\n")),
            "]")
        .toString("\n");
    return "PModule(\n" + indent(fields) + "\n)";
  }
}
