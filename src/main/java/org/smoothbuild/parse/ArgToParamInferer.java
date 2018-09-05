package org.smoothbuild.parse;

import static java.util.Collections.nCopies;
import static org.smoothbuild.util.Lists.filter;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.lang.base.ParameterInfo;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.ast.NamedNode;

public class ArgToParamInferer<T extends NamedNode> {
  private final List<T> arguments;
  private final List<ParameterInfo> parameters;
  private final boolean[] used;
  private final List<T> current;
  private List<T> found;
  private int unusedCount;

  public static <T extends NamedNode> List<T> findAssignment(List<T> arguments,
      List<ParameterInfo> parameters) throws ArgToParamInfererException {
    int requiredParamCount = filter(parameters, ParameterInfo::isRequired).size();
    if (arguments.size() < requiredParamCount) {
      throw new ArgToParamInfererException("Function has " + requiredParamCount +
          " parameters that haven't been assigned explicitly in that call and have no default value"
          + " but only " + arguments.size()
          + " implicit arguments have been provided in this call.");
    }

    return new ArgToParamInferer<T>(arguments, parameters).infer();
  }

  private ArgToParamInferer(List<T> arguments, List<ParameterInfo> parameters) {
    this.arguments = arguments;
    this.parameters = parameters;
    this.used = new boolean[arguments.size()];
    this.unusedCount = arguments.size();
    this.current = new ArrayList<T>(nCopies(parameters.size(), (T) null));
  }

  private List<T> infer() throws ArgToParamInfererException {
    infer(0);
    if (found == null) {
      throw new ArgToParamInfererException(
          "Cannot find any valid assignment between implicit arguments and parameters.");
    }
    return found;
  }

  private void infer(int i) throws ArgToParamInfererException {
    if (i < current.size()) {
      ParameterInfo parameter = parameters.get(i);
      Type parameterType = parameter.type();
      for (int j = 0; j < used.length; j++) {
        if (!used[j] && parameterType.isParamAssignableFrom(arguments.get(j).get(Type.class))) {
          used[j] = true;
          unusedCount--;
          current.set(i, arguments.get(j));
          infer(i + 1);
          used[j] = false;
          unusedCount++;
        }
      }
      if (!parameter.isRequired() && (unusedCount < current.size() - i)) {
        current.set(i, null);
        infer(i + 1);
      }
    } else {
      if (found != null) {
        throw new ArgToParamInfererException(
            "Found more than one valid assignment between implicit arguments and parameters.");
      } else {
        found = new ArrayList<>(current);
      }
    }
  }

  public static class ArgToParamInfererException extends Exception {
    public ArgToParamInfererException(String message) {
      super(message);
    }
  }
}
