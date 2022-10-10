package org.smoothbuild.compile.ps.ast;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.compile.lang.base.Loc.loc;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.sane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.lang.SmoothBaseVisitor;
import org.smoothbuild.antlr.lang.SmoothParser.AnnContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArgContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArgListContext;
import org.smoothbuild.antlr.lang.SmoothParser.ArrayTContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainCallContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainContext;
import org.smoothbuild.antlr.lang.SmoothParser.ChainPartContext;
import org.smoothbuild.antlr.lang.SmoothParser.ExprContext;
import org.smoothbuild.antlr.lang.SmoothParser.ExprHeadContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldContext;
import org.smoothbuild.antlr.lang.SmoothParser.FieldListContext;
import org.smoothbuild.antlr.lang.SmoothParser.FuncTContext;
import org.smoothbuild.antlr.lang.SmoothParser.LiteralContext;
import org.smoothbuild.antlr.lang.SmoothParser.ModContext;
import org.smoothbuild.antlr.lang.SmoothParser.ParamContext;
import org.smoothbuild.antlr.lang.SmoothParser.ParamListContext;
import org.smoothbuild.antlr.lang.SmoothParser.SelectContext;
import org.smoothbuild.antlr.lang.SmoothParser.StructContext;
import org.smoothbuild.antlr.lang.SmoothParser.TopContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeListContext;
import org.smoothbuild.antlr.lang.SmoothParser.TypeNameContext;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.ps.ast.expr.BlobP;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.IntP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.OrderP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.SelectP;
import org.smoothbuild.compile.ps.ast.expr.StringP;
import org.smoothbuild.compile.ps.ast.refable.FuncP;
import org.smoothbuild.compile.ps.ast.refable.ItemP;
import org.smoothbuild.compile.ps.ast.refable.PolyEvaluableP;
import org.smoothbuild.compile.ps.ast.refable.ValP;
import org.smoothbuild.compile.ps.ast.type.ArrayTP;
import org.smoothbuild.compile.ps.ast.type.FuncTP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
import org.smoothbuild.fs.space.FilePath;

import com.google.common.collect.ImmutableList;

public class AstCreator {
  public static Ast fromParseTree(FilePath filePath, ModContext module) {
    List<StructP> structs = new ArrayList<>();
    List<PolyEvaluableP> evaluables = new ArrayList<>();
    new SmoothBaseVisitor<Void>() {
      @Override
      public Void visitStruct(StructContext struct) {
        var name = struct.NAME().getText();
        var loc = locOf(filePath, struct.NAME().getSymbol());
        var fields = createFields(struct.fieldList());
        structs.add(new StructP(name, fields, loc));
        return null;
      }

      private List<ItemP> createFields(FieldListContext fieldList) {
        if (fieldList != null) {
          return sane(fieldList.field())
              .stream()
              .map(this::createField)
              .collect(toImmutableList());
        }
        return new ArrayList<>();
      }

      private ItemP createField(FieldContext field) {
        TypeP type = createT(field.type());
        TerminalNode nameNode = field.NAME();
        String name = nameNode.getText();
        Loc loc = locOf(filePath, nameNode);
        return new ItemP(type, name, Optional.empty(), loc);
      }

      @Override
      public Void visitTop(TopContext top) {
        TerminalNode nameNode = top.NAME();
        visitChildren(top);
        Optional<TypeP> type = createTypeSane(top.type());
        String name = nameNode.getText();
        Optional<ExprP> expr = createExprSane(top.expr());
        Optional<AnnP> annotation = createNativeSane(top.ann());
        Loc loc = locOf(filePath, nameNode);
        if (top.paramList() == null) {
          evaluables.add(new ValP(type, name, expr, annotation, loc));
        } else {
          List<ItemP> params = createParams(top.paramList());
          evaluables.add(new FuncP(type, name, params, expr, annotation, loc));
        }
        return null;
      }

      private Optional<AnnP> createNativeSane(AnnContext annotation) {
        if (annotation == null) {
          return Optional.empty();
        } else {
          var name = annotation.NAME().getText();
          return Optional.of(new AnnP(
              name,
              createStringNode(annotation, annotation.STRING()),
              locOf(filePath, annotation)));
        }
      }

      private List<ItemP> createParams(ParamListContext paramList) {
        ArrayList<ItemP> result = new ArrayList<>();
        if (paramList != null) {
          return sane(paramList.param())
              .stream().map(this::createParam)
              .collect(toImmutableList());
        }
        return result;
      }

      private ItemP createParam(ParamContext param) {
        var type = createT(param.type());
        var name = param.NAME().getText();
        var defaultVal = Optional.ofNullable(param.expr()).map(this::createExpr);
        var loc = locOf(filePath, param);
        return new ItemP(type, name, defaultVal, loc);
      }

      private Optional<ExprP> createExprSane(ExprContext expr) {
        return expr == null ? Optional.empty() : Optional.of(createExpr(expr));
      }

      private ExprP createExpr(ExprContext expr) {
        ExprP result = createChainHead(expr.exprHead());
        List<ChainCallContext> chainCallsInPipe = expr.chainCall();
        for (int i = 0; i < chainCallsInPipe.size(); i++) {
          ChainCallContext chain = chainCallsInPipe.get(i);
          result = createChainCallObj(result, chain);
        }
        return result;
      }

      private ExprP createChainHead(ExprHeadContext expr) {
        if (expr.chain() != null) {
          return createChainObj(expr.chain());
        }
        if (expr.literal() != null) {
          return createLiteral(expr.literal());
        }
        throw newRuntimeException(ExprHeadContext.class);
      }

      private ExprP createChainObj(ChainContext chain) {
        ExprP result = newRefNode(chain.NAME());
        return createChainParts(result, chain.chainPart());
      }

      private ExprP createLiteral(LiteralContext expr) {
        if (expr.array() != null) {
          List<ExprP> elems = map(expr.array().expr(), this::createExpr);
          return new OrderP(elems, locOf(filePath, expr));
        }
        if (expr.BLOB() != null) {
          return new BlobP(expr.BLOB().getText().substring(2), locOf(filePath, expr));
        }
        if (expr.INT() != null) {
          return new IntP(expr.INT().getText(), locOf(filePath, expr));
        }
        if (expr.STRING() != null) {
          return createStringNode(expr, expr.STRING());
        }
        throw newRuntimeException(LiteralContext.class);
      }

      private StringP createStringNode(ParserRuleContext expr, TerminalNode quotedString) {
        String unquoted = unquote(quotedString.getText());
        Loc loc = locOf(filePath, expr);
        return new StringP(unquoted, loc);
      }

      private ExprP createChainCallObj(ExprP expr, ChainCallContext chainCall) {
        ExprP result = newRefNode(chainCall.NAME());
        for (SelectContext fieldRead : chainCall.select()) {
          result = createSelect(result, fieldRead);
        }

        var args = createArgList(chainCall.argList());
        result = createCall(result, concat(expr, args), chainCall.argList());

        return createChainParts(result, chainCall.chainPart());
      }

      private RefP newRefNode(TerminalNode name) {
        return new RefP(name.getText(), locOf(filePath, name));
      }

      private SelectP createSelect(ExprP selectable, SelectContext fieldRead) {
        String name = fieldRead.NAME().getText();
        Loc loc = locOf(filePath, fieldRead);
        return new SelectP(selectable, name, loc);
      }

      private ExprP createChainParts(ExprP expr, List<ChainPartContext> chainParts) {
        ExprP result = expr;
        for (ChainPartContext chainPart : chainParts) {
          if (chainPart.argList() != null) {
            var args = createArgList(chainPart.argList());
            result = createCall(result, args, chainPart.argList());
          } else if (chainPart.select() != null) {
            result = createSelect(result, chainPart.select());
          } else {
            throw newRuntimeException(ChainContext.class);
          }
        }
        return result;
      }

      private List<ExprP> createArgList(ArgListContext argList) {
        List<ExprP> result = new ArrayList<>();
        for (ArgContext arg : argList.arg()) {
          ExprContext expr = arg.expr();
          TerminalNode nameNode = arg.NAME();
          ExprP exprP = createExpr(expr);
          if (nameNode == null) {
            result.add(exprP);
          } else {
            result.add(new NamedArgP(nameNode.getText(), exprP, locOf(filePath, arg)));
          }
        }
        return result;
      }

      private ExprP createCall(ExprP callable, List<ExprP> args, ArgListContext argListContext) {
        Loc loc = locOf(filePath, argListContext);
        return new CallP(callable, args, loc);
      }

      private Optional<TypeP> createTypeSane(TypeContext type) {
        return type == null ? Optional.empty() : Optional.of(createT(type));
      }

      private TypeP createT(TypeContext type) {
        return switch (type) {
          case TypeNameContext name -> createT(name);
          case ArrayTContext arrayT -> createArrayT(arrayT);
          case FuncTContext funcT -> createFuncT(funcT);
          default -> throw unexpectedCaseExc(type);
        };
      }

      private TypeP createT(TypeNameContext type) {
        return new TypeP(type.getText(), locOf(filePath, type.NAME()));
      }

      private TypeP createArrayT(ArrayTContext arrayT) {
        TypeP elemType = createT(arrayT.type());
        return new ArrayTP(elemType, locOf(filePath, arrayT));
      }

      private TypeP createFuncT(FuncTContext funcT) {
        TypeP resultType = createT(funcT.type());
        return new FuncTP(resultType, createTs(funcT.typeList()),
            locOf(filePath, funcT));
      }

      private ImmutableList<TypeP> createTs(TypeListContext typeList) {
        if (typeList != null) {
          return map(typeList.type(), this::createT);
        } else {
          return list();
        }
      }

      private RuntimeException newRuntimeException(Class<?> clazz) {
        return new RuntimeException("Illegal parse tree: " + clazz.getSimpleName()
            + " without children.");
      }
    }.visit(module);
    return new Ast(structs, evaluables);
  }

  private static String unquote(String quotedString) {
    return quotedString.substring(1, quotedString.length() - 1);
  }

  private static Loc locOf(FilePath filePath, ParserRuleContext parserRuleContext) {
    return locOf(filePath, parserRuleContext.getStart());
  }

  private static Loc locOf(FilePath filePath, TerminalNode node) {
    return locOf(filePath, node.getSymbol());
  }

  private static Loc locOf(FilePath filePath, Token token) {
    return loc(filePath, token.getLine());
  }
}
