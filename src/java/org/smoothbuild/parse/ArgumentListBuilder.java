package org.smoothbuild.parse;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.parse.err.DuplicateArgNameProblem;
import org.smoothbuild.parse.err.ManyAmbigiousParamsAssignableFromImplicitArgProblem;
import org.smoothbuild.parse.err.NoParamAssignableFromImplicitArgProblem;
import org.smoothbuild.parse.err.TypeMismatchProblem;
import org.smoothbuild.parse.err.UnknownParamNameProblem;
import org.smoothbuild.problem.Problem;
import org.smoothbuild.problem.ProblemsListener;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class ArgumentListBuilder {
  private final ProblemsListener problemsListener;

  @Inject
  public ArgumentListBuilder(ProblemsListener problemsListener) {
    this.problemsListener = problemsListener;
  }

  public Map<String, Expression> convert(Collection<Argument> arguments,
      ImmutableMap<String, Param> params) {
    Map<String, Expression> explicitArgs = processExplicitArguments(arguments, params);
    if (problemsListener.hasAnyProblem()) {
      return null;
    }

    convertImplicitToExplicit(params, implicitArgs(arguments), explicitArgs);
    if (problemsListener.hasAnyProblem()) {
      return null;
    }

    return explicitArgs;
  }

  private Map<String, Expression> processExplicitArguments(Collection<Argument> arguments,
      ImmutableMap<String, Param> params) {
    Map<String, Expression> explicitArgs = Maps.newHashMap();
    boolean success = true;

    for (Argument argument : arguments) {
      if (argument.isExplicit()) {
        String argName = argument.name();
        Expression argExpression = argument.expression();
        Param param = params.get(argName);
        if (param == null) {
          problemsListener.report(new UnknownParamNameProblem(argument));
          success = false;
        } else if (explicitArgs.containsKey(argName)) {
          problemsListener.report(new DuplicateArgNameProblem(argument));
          success = false;
        } else if (!param.type().isAssignableFrom(argExpression.type())) {
          problemsListener.report(new TypeMismatchProblem(argument, param.type()));
          success = false;
        } else {
          explicitArgs.put(argName, argExpression);
        }
      }
    }
    if (success) {
      return explicitArgs;
    } else {
      return null;
    }
  }

  private ImmutableList<Argument> implicitArgs(Collection<Argument> arguments) {
    Builder<Argument> builder = ImmutableList.builder();
    for (Argument argument : arguments) {
      if (!argument.isExplicit()) {
        builder.add(argument);
      }
    }
    return builder.build();
  }

  private void convertImplicitToExplicit(Map<String, Param> params, Collection<Argument> arguments,
      Map<String, Expression> explicitArgs) {

    // TODO Implicit arguments are allowed only in pipes so there can be at most
    // one implicit argument
    checkArgument(arguments.size() <= 1);

    if (arguments.size() == 1) {
      // TODO Once implicit arguments are allowed, error messages have to
      // be more detailed. Ideally they should contain info about all successful
      // assignments of implicit arguments to param names and the failed one.

      Argument onlyImplicit = arguments.iterator().next();
      Type type = onlyImplicit.expression().type();
      boolean found = false;
      for (Param param : params.values()) {
        if (param.type() == type && !explicitArgs.containsKey(param.name())) {
          if (found) {
            Problem problem = new ManyAmbigiousParamsAssignableFromImplicitArgProblem(onlyImplicit);
            problemsListener.report(problem);
            return;
          } else {
            explicitArgs.put(param.name(), onlyImplicit.expression());
            found = true;
          }
        }
      }
      if (!found) {
        Problem problem = new NoParamAssignableFromImplicitArgProblem(onlyImplicit);
        problemsListener.report(problem);
      }
    }
  }
}
