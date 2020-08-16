parser grammar kdl;

@header {
  package com.xarql.kdl.antlr;
}

options {
    tokenVocab = kdlLexer;
}

arrayLength: VARNAME SIZE;

// literals
bool: TRUE | FALSE;
number: DIGIT+;
literal: bool | STRING_LIT | number;

statement: methodCall STATEMENT_END | variableDeclaration | variableAssignment | returnStatement | conditional;
block: BODY_OPEN statement* BODY_CLOSE;

// conditionals
conditional: r_if | assertion | r_while;
r_if: R_IF condition block r_else?;
r_else: R_ELSE block;
assertion: ASSERT condition STATEMENT_END;
r_while: WHILE condition block;

value: methodCall | arrayLength| literal | VARNAME | CONSTNAME | arrayAccess | R_NULL;
operator: PLUS | MINUS | DIVIDE | MULTIPLY | MODULUS;
expression: value (operator expression)?;

condition: singleCondition (appender singleCondition)?;
singleCondition: expression (comparator expression)?;
comparator: EQUAL | NOT_EQUAL | REF_EQUAL | REF_NOT_EQUAL | MORE_THAN | LESS_THAN | MORE_OR_EQUAL | LESS_OR_EQUAL;
appender: AND | OR;

variableDeclaration: typedVariable (SEPARATOR VARNAME)* (ASSIGN expression)? STATEMENT_END;
variableAssignment: VARNAME assignment STATEMENT_END;
assignment: (ASSIGN expression) | operatorAssign;
operatorAssign: operator ASSIGN value;
typedVariable: type VARNAME;
arrayAccess: VARNAME BRACE_OPEN expression BRACE_CLOSE;

// method calls
methodCall: VARNAME parameterSet;
parameterSet: PARAM_OPEN (expression (SEPARATOR expression)*)? PARAM_CLOSE;

// method definitions
methodDefinition: methodType typedVariable parameterDefinition block;
methodType: (METHOD | FUNCTION)?;
parameterDefinition: PARAM_OPEN typedVariable? (SEPARATOR typedVariable)* PARAM_CLOSE;

returnStatement: RETURN expression STATEMENT_END;

type: basetype | CLASSNAME;
basetype: BOOLEAN | INT | STRING;

source: pkg? use* clazz;
pkg: PKG PKG_NAME STATEMENT_END;
use: USE QUALIFIED_NAME STATEMENT_END;
clazz: TYPE CLASSNAME BODY_OPEN (constant | run | variableDeclaration | methodDefinition)* BODY_CLOSE;
constant: CONST CONSTNAME ASSIGN literal STATEMENT_END;
run: RUN block;
