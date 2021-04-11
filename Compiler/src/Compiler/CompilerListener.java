package Compiler;

import gen.MoolaListener;
import gen.MoolaParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

public class CompilerListener implements MoolaListener {
  private String SpaceBuilder(int depth) {
    StringBuilder builder = new StringBuilder();
    for (int i = 1; i < depth; i++) {
      builder.append("    ");
    }
    return builder.toString();
  }

  @Override
  public void enterProgram(MoolaParser.ProgramContext ctx) {
    System.out.println("start program{");
  }

  @Override
  public void exitProgram(MoolaParser.ProgramContext ctx) {
    System.out.println(SpaceBuilder(ctx.depth()) + "}");
  }

  @Override
  public void enterClassDeclaration(MoolaParser.ClassDeclarationContext ctx) {
    if (ctx.parent != null && ctx.parent.getChild(0).getText().equalsIgnoreCase("entry")) {
      System.out.println(
          SpaceBuilder(ctx.depth() - 1) + "main Class: " + ctx.className.getText() + "{");
    }
    System.out.println(SpaceBuilder(ctx.depth()) + "Class: " + ctx.className.getText() + "{");
  }

  @Override
  public void exitClassDeclaration(MoolaParser.ClassDeclarationContext ctx) {
    System.out.println(SpaceBuilder(ctx.depth()) + "}");
  }

  @Override
  public void enterEntryClassDeclaration(MoolaParser.EntryClassDeclarationContext ctx) {
    // never implement it :)
  }

  @Override
  public void exitEntryClassDeclaration(MoolaParser.EntryClassDeclarationContext ctx) {
    // never implement it :)
  }

  @Override
  public void enterFieldDeclaration(MoolaParser.FieldDeclarationContext ctx) {
    StringBuilder builder = new StringBuilder();
    builder.append("field: ");
    builder.append(ctx.fieldName.getText());
    if (ctx.fieldType != null) {
      builder.append("/ type=" + ctx.fieldType.getText() + " ");
    }
    if (ctx.fieldAccessModifier != null) {
      builder.append("/ access modifier=" + ctx.fieldAccessModifier.getText());
    }
    System.out.println(SpaceBuilder(ctx.depth()) + builder.toString());
  }

  @Override
  public void exitFieldDeclaration(MoolaParser.FieldDeclarationContext ctx) {}

  @Override
  public void enterAccess_modifier(MoolaParser.Access_modifierContext ctx) {}

  @Override
  public void exitAccess_modifier(MoolaParser.Access_modifierContext ctx) {}

  @Override
  public void enterMethodDeclaration(MoolaParser.MethodDeclarationContext ctx) {
    StringBuilder builder = new StringBuilder();
    builder.append("class method: ");
    builder.append(ctx.methodName.getText());
    if (ctx.t != null) {
      builder.append("/ return type=" + ctx.t.getText() + " ");
    }
    if (ctx.methodAccessModifier != null) {
      builder.append("/ access modifier=" + ctx.methodAccessModifier.getText());
    }
    builder.append("{");
    System.out.println(SpaceBuilder(ctx.depth()) + builder.toString());
  }

  @Override
  public void exitMethodDeclaration(MoolaParser.MethodDeclarationContext ctx) {
    System.out.println(SpaceBuilder(ctx.depth()) + "}");
  }

  @Override
  public void enterClosedStatement(MoolaParser.ClosedStatementContext ctx) {}

  @Override
  public void exitClosedStatement(MoolaParser.ClosedStatementContext ctx) {}

  @Override
  public void enterClosedConditional(MoolaParser.ClosedConditionalContext ctx) {}

  @Override
  public void exitClosedConditional(MoolaParser.ClosedConditionalContext ctx) {}

  @Override
  public void enterOpenConditional(MoolaParser.OpenConditionalContext ctx) {}

  @Override
  public void exitOpenConditional(MoolaParser.OpenConditionalContext ctx) {}

  @Override
  public void enterOpenStatement(MoolaParser.OpenStatementContext ctx) {}

  @Override
  public void exitOpenStatement(MoolaParser.OpenStatementContext ctx) {}

  @Override
  public void enterStatement(MoolaParser.StatementContext ctx) {}

  @Override
  public void exitStatement(MoolaParser.StatementContext ctx) {}

  @Override
  public void enterStatementVarDef(MoolaParser.StatementVarDefContext ctx) {
    int t = 0;
    RuleContext x = ctx;
    while (t==0) {
      if (x.getParent().getClass().getTypeName() == "gen.MoolaParser$MethodDeclarationContext") {
        t = 1;
      } else if (x.getParent().getClass().getTypeName() != "gen.MoolaParser$MethodDeclarationContext") {
        x = x.getParent();
      }
    }

    System.out.println(SpaceBuilder(x.depth()) + "var: " + ctx.i1.getText() + "");
  }
  @Override
  public void exitStatementVarDef(MoolaParser.StatementVarDefContext ctx) {}

  @Override
  public void enterStatementBlock(MoolaParser.StatementBlockContext ctx) {}

  @Override
  public void exitStatementBlock(MoolaParser.StatementBlockContext ctx) {}

  @Override
  public void enterStatementContinue(MoolaParser.StatementContinueContext ctx) {}

  @Override
  public void exitStatementContinue(MoolaParser.StatementContinueContext ctx) {}

  @Override
  public void enterStatementBreak(MoolaParser.StatementBreakContext ctx) {}

  @Override
  public void exitStatementBreak(MoolaParser.StatementBreakContext ctx) {}

  @Override
  public void enterStatementReturn(MoolaParser.StatementReturnContext ctx) {}

  @Override
  public void exitStatementReturn(MoolaParser.StatementReturnContext ctx) {}

  @Override
  public void enterStatementClosedLoop(MoolaParser.StatementClosedLoopContext ctx) {}

  @Override
  public void exitStatementClosedLoop(MoolaParser.StatementClosedLoopContext ctx) {}

  @Override
  public void enterStatementOpenLoop(MoolaParser.StatementOpenLoopContext ctx) {}

  @Override
  public void exitStatementOpenLoop(MoolaParser.StatementOpenLoopContext ctx) {}

  @Override
  public void enterStatementWrite(MoolaParser.StatementWriteContext ctx) {}

  @Override
  public void exitStatementWrite(MoolaParser.StatementWriteContext ctx) {}

  @Override
  public void enterStatementAssignment(MoolaParser.StatementAssignmentContext ctx) {}

  @Override
  public void exitStatementAssignment(MoolaParser.StatementAssignmentContext ctx) {}

  @Override
  public void enterStatementInc(MoolaParser.StatementIncContext ctx) {}

  @Override
  public void exitStatementInc(MoolaParser.StatementIncContext ctx) {}

  @Override
  public void enterStatementDec(MoolaParser.StatementDecContext ctx) {}

  @Override
  public void exitStatementDec(MoolaParser.StatementDecContext ctx) {}

  @Override
  public void enterExpression(MoolaParser.ExpressionContext ctx) {}

  @Override
  public void exitExpression(MoolaParser.ExpressionContext ctx) {}

  @Override
  public void enterExpressionOr(MoolaParser.ExpressionOrContext ctx) {}

  @Override
  public void exitExpressionOr(MoolaParser.ExpressionOrContext ctx) {}

  @Override
  public void enterExpressionOrTemp(MoolaParser.ExpressionOrTempContext ctx) {}

  @Override
  public void exitExpressionOrTemp(MoolaParser.ExpressionOrTempContext ctx) {}

  @Override
  public void enterExpressionAnd(MoolaParser.ExpressionAndContext ctx) {}

  @Override
  public void exitExpressionAnd(MoolaParser.ExpressionAndContext ctx) {}

  @Override
  public void enterExpressionAndTemp(MoolaParser.ExpressionAndTempContext ctx) {}

  @Override
  public void exitExpressionAndTemp(MoolaParser.ExpressionAndTempContext ctx) {}

  @Override
  public void enterExpressionEq(MoolaParser.ExpressionEqContext ctx) {}

  @Override
  public void exitExpressionEq(MoolaParser.ExpressionEqContext ctx) {}

  @Override
  public void enterExpressionEqTemp(MoolaParser.ExpressionEqTempContext ctx) {}

  @Override
  public void exitExpressionEqTemp(MoolaParser.ExpressionEqTempContext ctx) {}

  @Override
  public void enterExpressionCmp(MoolaParser.ExpressionCmpContext ctx) {}

  @Override
  public void exitExpressionCmp(MoolaParser.ExpressionCmpContext ctx) {}

  @Override
  public void enterExpressionCmpTemp(MoolaParser.ExpressionCmpTempContext ctx) {}

  @Override
  public void exitExpressionCmpTemp(MoolaParser.ExpressionCmpTempContext ctx) {}

  @Override
  public void enterExpressionAdd(MoolaParser.ExpressionAddContext ctx) {}

  @Override
  public void exitExpressionAdd(MoolaParser.ExpressionAddContext ctx) {}

  @Override
  public void enterExpressionAddTemp(MoolaParser.ExpressionAddTempContext ctx) {}

  @Override
  public void exitExpressionAddTemp(MoolaParser.ExpressionAddTempContext ctx) {}

  @Override
  public void enterExpressionMultMod(MoolaParser.ExpressionMultModContext ctx) {}

  @Override
  public void exitExpressionMultMod(MoolaParser.ExpressionMultModContext ctx) {}

  @Override
  public void enterExpressionMultModTemp(MoolaParser.ExpressionMultModTempContext ctx) {}

  @Override
  public void exitExpressionMultModTemp(MoolaParser.ExpressionMultModTempContext ctx) {}

  @Override
  public void enterExpressionUnary(MoolaParser.ExpressionUnaryContext ctx) {}

  @Override
  public void exitExpressionUnary(MoolaParser.ExpressionUnaryContext ctx) {}

  @Override
  public void enterExpressionMethods(MoolaParser.ExpressionMethodsContext ctx) {}

  @Override
  public void exitExpressionMethods(MoolaParser.ExpressionMethodsContext ctx) {}

  @Override
  public void enterExpressionMethodsTemp(MoolaParser.ExpressionMethodsTempContext ctx) {}

  @Override
  public void exitExpressionMethodsTemp(MoolaParser.ExpressionMethodsTempContext ctx) {}

  @Override
  public void enterExpressionOther(MoolaParser.ExpressionOtherContext ctx) {}

  @Override
  public void exitExpressionOther(MoolaParser.ExpressionOtherContext ctx) {}

  @Override
  public void enterMoolaType(MoolaParser.MoolaTypeContext ctx) {}

  @Override
  public void exitMoolaType(MoolaParser.MoolaTypeContext ctx) {}

  @Override
  public void enterSingleType(MoolaParser.SingleTypeContext ctx) {}

  @Override
  public void exitSingleType(MoolaParser.SingleTypeContext ctx) {}

  @Override
  public void visitTerminal(TerminalNode terminalNode) {}

  @Override
  public void visitErrorNode(ErrorNode errorNode) {}

  @Override
  public void enterEveryRule(ParserRuleContext parserRuleContext) {}

  @Override
  public void exitEveryRule(ParserRuleContext parserRuleContext) {}
}
