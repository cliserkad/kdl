parser grammar kdl;

@header {
  package com.xarql.kdl.antlr;
}

options {
    tokenVocab = kdlLexer;
}

// literals
bool: TRUE | FALSE;
decimalNumber: DIGIT? (DIGIT | SEPARATOR | UNDERSCORE)* DOT DIGIT (DIGIT | SEPARATOR | UNDERSCORE)*;
integer: DIGIT (DIGIT | SEPARATOR | UNDERSCORE)*;
literal: bool | CHAR_LIT | STRING_LIT | integer | decimalNumber;

arrayLength: ID DOT SIZE;

statement: methodCall | variableDeclaration | assignment | returnStatement | conditional;
block: BODY_OPEN statement* BODY_CLOSE;

// for loop
for_loop: FOR ID SET range block;
for_each_loop: FOR ID SET expression block;
range: expression? DOT DOT expression;

// conditionals
conditional: r_if | assertion | r_while | for_loop | for_each_loop;
r_if: R_IF condition block r_else?;
r_else: R_ELSE (block | statement);
assertion: ASSERT condition;
r_while: WHILE condition block;

addressable: ID (DOT ID)*;
value: addressable | methodCall | arrayLength | literal | indexAccess | subSequence | R_NULL;
operator: ADD | SUB | DIV | MUL | MOD | AND | OR | BIT_AND | BIT_OR;
expression: value (operator expression)?;

condition: expression (comparator expression)?;
comparator: EQUAL | NOT_EQUAL | REF_EQUAL | REF_NOT_EQUAL | MORE_THAN | LESS_THAN | MORE_OR_EQUAL | LESS_OR_EQUAL;

variableDeclaration: details (SEPARATOR ID)* (SET expression)?;
assignment: (addressable) ((SET expression) | operatorAssign);
operatorAssign: operator SET value;
details: type MUTABLE? ID;
indexAccess: ID BRACE_OPEN expression BRACE_CLOSE;
subSequence: ID BRACE_OPEN range BRACE_CLOSE;

// method calls
methodCall: addressable parameterSet;
parameterSet: PARAM_OPEN (expression (SEPARATOR expression)*)? PARAM_CLOSE;

// method definitions
methodDefinition: (details | ID MUTABLE?) paramSet block;
paramSet: PARAM_OPEN ((ID | param) (SEPARATOR param)*)? PARAM_CLOSE;
param: details (SET value)?;

returnStatement: RETURN expression?;

type: (basetype | ID) (BRACE_OPEN BRACE_CLOSE)*;
basetype: BOOLEAN | BYTE | SHORT | CHAR | INT | FLOAT | LONG | DOUBLE | STRING;

source: path? use* clazz EOF;
path: PATH addressable;
use: USE addressable;
clazz: TYPE ID BODY_OPEN (constantDef | fieldDef | main | methodDefinition)* BODY_CLOSE;
constantDef: CONST ID SET value;
fieldDef: variableDeclaration;
main: MAIN block;
