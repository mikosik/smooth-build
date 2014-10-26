package org.smoothbuild.parse;

import static org.smoothbuild.lang.base.Types.BLOB;
import static org.smoothbuild.lang.base.Types.FILE;
import static org.smoothbuild.lang.base.Types.NIL;
import static org.smoothbuild.lang.base.Types.NOTHING;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.base.Types.basicTypes;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.def.args.Arg.namedArg;
import static org.smoothbuild.lang.function.def.args.Arg.namelessArg;
import static org.smoothbuild.lang.function.def.args.Arg.pipedArg;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.FATAL;
import static org.smoothbuild.parse.LocationHelpers.locationOf;
import static org.smoothbuild.util.StringUnescaper.unescaped;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.SmoothParser.ArgContext;
import org.smoothbuild.antlr.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.SmoothParser.ArrayContext;
import org.smoothbuild.antlr.SmoothParser.ArrayElemContext;
import org.smoothbuild.antlr.SmoothParser.CallContext;
import org.smoothbuild.antlr.SmoothParser.ExpressionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.ParamNameContext;
import org.smoothbuild.antlr.SmoothParser.PipeContext;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayType;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Types;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.expr.ArrayExpr;
import org.smoothbuild.lang.expr.CallExpr;
import org.smoothbuild.lang.expr.ConstantExpr;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.ImplicitConverter;
import org.smoothbuild.lang.expr.InvalidExpr;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.lang.function.def.args.Arg;
import org.smoothbuild.lang.module.Module;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.CodeMessage;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.parse.err.ForbiddenArrayElemError;
import org.smoothbuild.parse.err.IncompatibleArrayElemsError;
import org.smoothbuild.util.Empty;
import org.smoothbuild.util.UnescapingFailedException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DefinedFunctionsCreator {
  private final ObjectsDb objectsDb;
  private final ArgExprsCreator argExprsCreator;
  private final ImplicitConverter implicitConverter;

  @Inject
  public DefinedFunctionsCreator(ObjectsDb objectsDb, ArgExprsCreator argExprsCreator,
      ImplicitConverter implicitConverter) {
    this.objectsDb = objectsDb;
    this.argExprsCreator = argExprsCreator;
    this.implicitConverter = implicitConverter;
  }

  public Map<Name, Function<?>> createDefinedFunctions(LoggedMessages messages,
      Module builtinModule, Map<Name, FunctionContext> functionContexts, List<Name> sorted) {
    Worker worker = new Worker(messages, builtinModule, functionContexts, sorted, objectsDb,
        argExprsCreator, implicitConverter);
    Map<Name, Function<?>> result = worker.run();
    messages.failIfContainsProblems();
    return result;
  }

  private static class Worker {
    private final LoggedMessages messages;
    private final Module builtinModule;
    private final Map<Name, FunctionContext> functionContexts;
    private final List<Name> sorted;
    private final ObjectsDb objectsDb;
    private final ArgExprsCreator argExprsCreator;
    private final ImplicitConverter implicitConverter;

    private final Map<Name, Function<?>> functions = Maps.newHashMap();

    public Worker(LoggedMessages messages, Module builtinModule,
        Map<Name, FunctionContext> functionContexts, List<Name> sorted, ObjectsDb objectsDb,
        ArgExprsCreator argExprsCreator, ImplicitConverter implicitConverter) {
      this.messages = messages;
      this.builtinModule = builtinModule;
      this.functionContexts = functionContexts;
      this.sorted = sorted;
      this.objectsDb = objectsDb;
      this.argExprsCreator = argExprsCreator;
      this.implicitConverter = implicitConverter;
    }

    public Map<Name, Function<?>> run() {
      for (Name name : sorted) {
        DefinedFunction<?> definedFunction = build(functionContexts.get(name));
        functions.put(name, definedFunction);
      }
      return functions;
    }

    public DefinedFunction<?> build(FunctionContext function) {
      Expression<?> expression = build(function.pipe());
      return buildDefinedFunction(function, expression);
    }

    private <T extends Value> DefinedFunction<T> buildDefinedFunction(FunctionContext function,
        Expression<T> expression) {
      Name name = name(function.functionName().getText());
      Signature<T> signature = new Signature<>(expression.type(), name, Empty.paramList());
      return new DefinedFunction<>(signature, expression);
    }

    private Expression<?> build(PipeContext pipe) {
      Expression<?> result = build(pipe.expression());
      List<CallContext> elements = pipe.call();
      for (int i = 0; i < elements.size(); i++) {
        CallContext call = elements.get(i);
        List<Arg> args = build(call.argList());
        // nameless piped argument's location is set to the pipe character '|'
        CodeLocation codeLocation = locationOf(pipe.p.get(i));
        args.add(pipedArg(result, codeLocation));
        result = build(call, args);
      }
      return result;
    }

    private Expression<?> build(ExpressionContext expression) {
      if (expression.array() != null) {
        return build(expression.array());
      }
      if (expression.call() != null) {
        return build(expression.call());
      }
      if (expression.STRING() != null) {
        return buildStringExpr(expression.STRING());
      }
      throw new Message(FATAL,
          "Illegal parse tree: " + ExpressionContext.class.getSimpleName() + " without children.");
    }

    private Expression<?> build(ArrayContext list) {
      List<ArrayElemContext> elems = list.arrayElem();
      ImmutableList<Expression<?>> elemExpressions = build(elems);

      CodeLocation location = locationOf(list);
      Type<?> elemType = commonSuperType(elems, elemExpressions, location);

      if (elemType != null) {
        return buildArray(elemType, elemExpressions, location);
      } else {
        return new InvalidExpr<>(NIL, location);
      }
    }

    private <T extends Value> Expression<Array<T>> buildArray(Type<T> elemType,
        ImmutableList<Expression<?>> elemExpressions, CodeLocation location) {
      ArrayType<T> arrayType = Types.arrayTypeContaining(elemType);
      ImmutableList<Expression<T>> convertedExpression = convertExprs(elemType, elemExpressions);
      return new ArrayExpr<>(arrayType, convertedExpression, location);
    }

    public <T extends Value> ImmutableList<Expression<T>> convertExprs(Type<T> type,
        Iterable<? extends Expression<?>> expressions) {
      ImmutableList.Builder<Expression<T>> builder = ImmutableList.builder();
      for (Expression<?> expression : expressions) {
        builder.add(implicitConverter.apply(type, expression));
      }
      return builder.build();
    }

    private ImmutableList<Expression<?>> build(List<ArrayElemContext> elems) {
      Builder<Expression<?>> builder = ImmutableList.builder();
      for (ArrayElemContext elem : elems) {
        Expression<?> expression = build(elem);
        if (!basicTypes().contains(expression.type())) {
          CodeLocation location = locationOf(elem);
          messages.log(new ForbiddenArrayElemError(location, expression.type()));
          builder.add(new InvalidExpr<>(NOTHING, location));
        } else {
          builder.add(expression);
        }
      }
      return builder.build();
    }

    private Expression<?> build(ArrayElemContext elem) {
      if (elem.STRING() != null) {
        return buildStringExpr(elem.STRING());
      }
      if (elem.call() != null) {
        return build(elem.call());
      }

      throw new Message(FATAL,
          "Illegal parse tree: " + ArrayElemContext.class.getSimpleName() + " without children.");
    }

    private Type<?> commonSuperType(List<ArrayElemContext> elems, ImmutableList<Expression<?>> elemExpressions,
        CodeLocation location) {
      if (elems.size() == 0) {
        return NOTHING;
      }
      Type<?> firstType = elemExpressions.get(0).type();
      Type<?> commonSuperType = firstType;

      for (int i = 1; i < elemExpressions.size(); i++) {
        Type<?> currentType = elemExpressions.get(i).type();
        commonSuperType = commonSuperType(commonSuperType, currentType);

        if (commonSuperType == null) {
          messages.log(new IncompatibleArrayElemsError(location, firstType, i, currentType));
          return null;
        }
      }
      return commonSuperType;
    }

    private static Type<?> commonSuperType(Type<?> type1, Type<?> type2) {
      if (type1 == STRING) {
        if (type2 == STRING) {
          return STRING;
        } else {
          return null;
        }
      } else if (type1 == BLOB) {
        if (type2 == BLOB || type2 == FILE) {
          return BLOB;
        } else {
          return null;
        }
      } else if (type1 == FILE) {
        if (type2 == FILE) {
          return FILE;
        } else if (type2 == BLOB) {
          return BLOB;
        } else {
          return null;
        }
      }
      return null;
    }

    private Expression<?> build(CallContext call) {
      List<Arg> args = build(call.argList());
      return build(call, args);
    }

    private Expression<?> build(CallContext call, List<Arg> args) {
      String functionName = call.functionName().getText();

      Function<?> function = getFunction(functionName);

      CodeLocation codeLocation = locationOf(call.functionName());
      ImmutableMap<String, ? extends Expression<?>> namedArgs = argExprsCreator.createArgExprs(
          codeLocation, messages, function, args);

      if (namedArgs == null) {
        return new InvalidExpr<>(function.type(), locationOf(call.functionName()));
      } else {
        return new CallExpr<>(function, false, codeLocation, namedArgs);
      }
    }

    private Function<?> getFunction(String functionName) {
      // UndefinedFunctionDetector has been run already so we can be sure at
      // this point that function with given name exists either among imported
      // functions or among already handled defined functions.

      Name name = Name.name(functionName);
      Function<?> function = builtinModule.getFunction(name);
      if (function == null) {
        return functions.get(name);
      } else {
        return function;
      }
    }

    private List<Arg> build(ArgListContext argList) {
      List<Arg> result = Lists.newArrayList();
      if (argList != null) {
        List<ArgContext> argContextList = argList.arg();
        for (int i = 0; i < argContextList.size(); i++) {
          result.add(build(i, argContextList.get(i)));
        }
      }
      return result;
    }

    private Arg build(int index, ArgContext arg) {
      Expression<?> expression = build(arg.expression());

      CodeLocation location = locationOf(arg);
      ParamNameContext paramName = arg.paramName();
      if (paramName == null) {
        return namelessArg(index + 1, expression, location);
      } else {
        return namedArg(index + 1, paramName.getText(), expression, location);
      }
    }

    private Expression<?> buildStringExpr(TerminalNode stringToken) {
      String quotedString = stringToken.getText();
      String string = quotedString.substring(1, quotedString.length() - 1);
      try {
        SString stringValue = objectsDb.string(unescaped(string));
        return new ConstantExpr<>(STRING, stringValue, locationOf(stringToken.getSymbol()));
      } catch (UnescapingFailedException e) {
        CodeLocation location = locationOf(stringToken.getSymbol());
        messages.log(new CodeMessage(ERROR, location, e.getMessage()));
        return new InvalidExpr<>(STRING, locationOf(stringToken.getSymbol()));
      }
    }
  }
}
