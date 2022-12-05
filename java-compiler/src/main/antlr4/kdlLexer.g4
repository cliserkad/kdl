lexer grammar kdlLexer;

@header {
  package com.xarql.kdl.antlr;
}

// skip over comments in lexer
COMMENT: '//' .*? '\n' -> channel(HIDDEN);
BLOCK_COMMENT: '/*' .*? '*/' -> channel(HIDDEN);

// skip over whitespace
WS : [ \t\r\n]+ -> skip;

QUOTE: '"';
STRING_LIT: QUOTE (~["\\] | '\\' .)* QUOTE;

// keywords
ASSERT: 'assert';
CONST: 'const';
FOR: 'for';
MAIN: 'main';
PATH: 'path';
RETURN: 'return';
R_ELSE: 'else';
R_IF: 'if';
R_NULL: 'null';
SIZE: 'size';
TYPE: 'type';
USE: 'use';
WHILE: 'while';

// base types
BOOLEAN: 'boolean';
BYTE: 'byte';
CHAR: 'char';
DOUBLE: 'double';
FLOAT: 'float';
INT: 'int';
LONG: 'long';
SHORT: 'short';
STRING: 'string';

// boolean values
FALSE: 'false';
TRUE: 'true';

// syntax
SET: ':';
BODY_CLOSE: '}'; // closing bracket
BODY_OPEN: '{'; // opening bracket
BRACE_CLOSE: ']';
BRACE_OPEN: '[';
DOT: '.';
MUTABLE: '~';
PARAM_CLOSE: ')'; // closing paren
PARAM_OPEN: '('; // opening paren
SEMICOLON: ';';
SEPARATOR: ',';

// comparator
EQUAL: '=';
LESS_OR_EQUAL: '<=';
LESS_THAN: '<';
MORE_OR_EQUAL: '>=';
MORE_THAN: '>';
NOT_EQUAL: '!=';
REF_EQUAL: '@';
REF_NOT_EQUAL: '!@';

/*
// operator assignments
ADD_SET: ADD SET;
SUB_SET: SUB SET;
DIV_SET: DIV SET;
MUL_SET: MUL SET;
MOD_SET: MOD SET;
*/

// operators
ADD: '+';
SUB: '-';
DIV: '/';
MUL: '*';
MOD: '%';

// logical combinations
AND: '&&';
OR: '||';

// bitwise combinations
BIT_AND: '&';
BIT_OR: '|';

DIGIT               : '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9';
fragment UPLETTER   : [A-Z];
fragment DNLETTER   : [a-z];
fragment LETTER     : UPLETTER | DNLETTER;
fragment ALPHANUM   : LETTER | DIGIT;
UNDERSCORE : '_';
fragment DNTEXT     : DNLETTER+;

// identifiers can be any combination of alphanumeric characters
ID: LETTER (ALPHANUM | UNDERSCORE)*;

// any single character wrapped in single quotes
CHAR_LIT: '\'' . '\'';
