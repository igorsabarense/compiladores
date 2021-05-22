# Compiladores # 

MicroCompilador - Semestre 01/2021

### Autores ###
 ```   
 Brenon Henrique 
 Frederico Terrinha
 Igor Sabarense
```



## Automato #

![automatofinalizado](https://user-images.githubusercontent.com/36987853/114118883-f7e3a980-98bf-11eb-9aaa-2df747cffc13.png)


## Gramática #
```
D -> DECLARACAO
B -> BLOCO DE COMANDOS
C -> COMANDO
MAIN -> MAIN
T -> TIPO
V -> CONSTANTE
EXP -> EXPRESSAO

S -> {D} main "{" {C} "}" fim_de_arquivo


D -> T id({,id} | = V | ["[const]"]); | final id = V  ;
T -> int | char | boolean
V -> [-] const | const_hexa | (TRUE | FALSE)
C -> ; | ATTR | FOR | IF | WRITE | READLN
F -> C{,C}

ATTR ->  id["["EXP"]"] := EXP
FOR ->  for "(" [F] ; EXP ; [F] ")" ( C | "{" {C;} "}" )
IF ->   if "(" EXP ")" then (C| "{" {C} "}") [else (C| "{" {C;} "}")]
READLN ->  readln"("AB")"
WRITE -> write"("EXP{,EXP}")" |  writeln"("EXP{,EXP}")"



EXP -> EXPS [(= | <> | < | > | <= | >= ) EXPS]
EXPS -> [+|-] TS { + | - | or ) TS}
TS -> FS {(* | / | % | and ) FS}
FS -> not FS | "("EXP")" | V | AB
AB -> id["["EXP"]"]






```



