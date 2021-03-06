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
TYPE: 'type';
CONST: 'const';
MAIN: 'main';
RETURN: 'return';
USE: 'use';
PATH: 'path';
R_IF: 'if';
R_ELSE: 'else';
R_NULL: 'null';
SIZE: 'size';
ASSERT: 'assert';
WHILE: 'while';
FOR: 'for';

// base types
BOOLEAN: 'boolean';
BYTE: 'byte';
SHORT: 'short';
CHAR: 'char';
INT: 'int';
FLOAT: 'float';
LONG: 'long';
DOUBLE: 'double';
STRING: 'string';

// boolean values
TRUE: 'true';
FALSE: 'false';

// syntax
BODY_OPEN: '{'; // opening bracket
BODY_CLOSE: '}'; // closing bracket
PARAM_OPEN: '('; // opening paren
PARAM_CLOSE: ')'; // closing paren
BRACE_OPEN: '[';
BRACE_CLOSE: ']';
DOT: '.';
SEPARATOR: ',';
SEMICOLON: ';';
ASSIGN: ':';
MUTABLE: '~';

// comparator
NOT_EQUAL: '!=';
EQUAL: '=';
REF_NOT_EQUAL: '!?';
REF_EQUAL: '?=';
LESS_THAN: '<';
MORE_THAN: '>';
LESS_OR_EQUAL: '<=';
MORE_OR_EQUAL: '>=';

// operators
PLUS: '+';
MINUS: '-';
DIVIDE: '/';
MULTIPLY: '*';
MODULUS: '%';

// appenders
AND: '&';
OR: '|';

DIGIT               : '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9';
fragment UPLETTER   : [A-Z];
fragment DNLETTER   : [a-z];
fragment LETTER     : UPLETTER | DNLETTER;
fragment ALPHANUM   : LETTER | DIGIT;
UNDERSCORE : '_';
fragment DNTEXT     : DNLETTER+;

CONSTNAME : UPLETTER (UPLETTER | DIGIT | UNDERSCORE)+;
CLASSNAME : UPLETTER+ DNLETTER (LETTER | DIGIT)+;
VARNAME   : DNLETTER (LETTER | DIGIT)*;

QUALIFIED_NAME: (DNTEXT '.')+ CLASSNAME;
PATH_LIT: DNTEXT ('.' DNTEXT)* SEMICOLON;

CHAR_LIT: '\'' . '\'';
