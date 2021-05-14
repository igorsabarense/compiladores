import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Igor Sabarense
 * @author Frederico Terrinha
 * @author Brenon Henrique
 * Disciplina de Compiladores - PUC-MG
 */

public class LC {
    public static void main(String[] args) throws IOException {
        //Redirecionamento do arquivo L
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        Parser parser = new Parser(new Lexer(br));
        parser.S();
        System.out.printf("%d linhas compiladas.", parser.getLexer().getLines());

    }
}

/**
 * Enum Type com os tipos de escalares ( INT | CHAR | BOOLEAN )
 */
enum Type {
    INT, CHAR, BOOLEAN
}

/**
 * Enum Token com as palavras reservadas que vão ser adicionadas na tabela de simbolos
 */
enum Token {
    INT("int"),
    CHAR("char"),
    BOOLEAN("boolean"),
    FINAL("final"),
    FOR("for"),
    IF("if"),
    ELSE("else"),
    TRUE("TRUE"),
    FALSE("FALSE"),
    AND("and"),
    OR("or"),
    NOT("not"),
    ATTRIBUTION(":="),
    EQUAL("="),
    OPENING_PARENTHESIS("("),
    CLOSING_PARENTHESIS(")"),
    LT("<"),
    GT(">"),
    NOT_EQUAL("<>"),
    GTOE(">="),
    LTOE("<="),
    COMMA(","),
    PLUS_SIGN("+"),
    MINUS_SIGN("-"),
    ASTERISK("*"),
    SLASH("/"),
    SEMICOLON(";"),
    OPENING_BRACES("{"),
    CLOSING_BRACES("}"),
    THEN("then"),
    READLN("readln"),
    WRITE("write"),
    WRITELN("writeln"),
    PERCENTAGE("%"),
    OPENING_BRACKETS("["),
    CLOSING_BRACKETS("]"),
    MAIN("main"),
    CONST("const"),
    IDENTIFIER("identifier"),
    END_OF_FILE("end of file");

    private String description;

    Token(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}

enum Classe {
    CLASSE_VAR, CLASSE_CONST
}

/**
 * Classe Symbol , contem : token , lexema e tipo
 **/
class Symbol {

    private Token token;
    private Type type;
    private Classe classe;
    private String lexeme;
    private int size;
    private int memoryAdress;


    public Symbol() {
        this.size = 0;
        this.memoryAdress = 0;
    }

    public Symbol(String lexeme, Token token) {
        this.token = token;
        this.lexeme = lexeme;
        this.size = 0;
    }

    public Symbol(Token token, Type type, String lexeme) {
        this.token = token;
        this.lexeme = lexeme;
        this.type = type;
        this.size = 0;
        this.memoryAdress = 0;
    }

    public Symbol(String lexeme, Token token, int size) {
        this.token = token;
        this.lexeme = lexeme;
        this.size = size;
        this.type = null;
        this.memoryAdress = 0;

    }

    public Symbol(Token token, Type type, int size , String lexeme) {
        this.token = token;
        this.lexeme = lexeme;
        this.type = type;
        this.size = size;
        this.memoryAdress = 0;
    }

    public Symbol(Token token, Type type, int size , String lexeme, int memoryAdress) {
        this.token = token;
        this.lexeme = lexeme;
        this.type = type;
        this.size = size;
        this.memoryAdress = memoryAdress;
    }

    public int getMemoryAdress() {
        return memoryAdress;
    }

    public void setMemoryAdress(int memoryAdress) {
        this.memoryAdress = memoryAdress;
    }


    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }


    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public Classe getClasse() {
        return classe;
    }

    public void setClasse(Classe classe) {
        this.classe = classe;
    }

    @Override
    public String toString() {
        return type != null ? "Symbol{" +
                "token=" + token +
                ", type=" + type +
                ", lexeme='" + lexeme + '\'' +
                '}' :
                "Symbol{" +
                        "token=" + token +
                        " lexeme='" + lexeme + '\'' +
                        '}';
    }
}


/**
 * Tabela de simbolos.
 */
class SymbolTable extends HashSet<Symbol> {

    public SymbolTable() {
        this.add(new Symbol(Token.FINAL, null, 0, "final"));
        this.add(new Symbol(Token.INT, Type.INT, 0, "int"));
        this.add(new Symbol(Token.CHAR, Type.CHAR, 0, "char"));
        this.add(new Symbol(Token.BOOLEAN, Type.BOOLEAN, 0, "boolean"));
        this.add(new Symbol("for", Token.FOR,0));
        this.add(new Symbol("if", Token.IF,0));
        this.add(new Symbol("else", Token.ELSE,0));
        this.add(new Symbol(Token.TRUE, Type.BOOLEAN, 0, "TRUE"));
        this.add(new Symbol(Token.FALSE, Type.BOOLEAN, 0, "FALSE"));
        this.add(new Symbol("and", Token.AND,0));
        this.add(new Symbol("or", Token.OR,0));
        this.add(new Symbol("not", Token.NOT,0));
        this.add(new Symbol(":=", Token.ATTRIBUTION,0));
        this.add(new Symbol("=", Token.EQUAL,0));
        this.add(new Symbol("(", Token.OPENING_PARENTHESIS,0));
        this.add(new Symbol(")", Token.CLOSING_PARENTHESIS,0));
        this.add(new Symbol("<", Token.LT,0));
        this.add(new Symbol(">", Token.GT,0));
        this.add(new Symbol("<>", Token.NOT_EQUAL,0));
        this.add(new Symbol(">=", Token.GTOE,0));
        this.add(new Symbol("<=", Token.LTOE,0));
        this.add(new Symbol(",", Token.COMMA,0));
        this.add(new Symbol("+", Token.PLUS_SIGN,0));
        this.add(new Symbol("-", Token.MINUS_SIGN,0));
        this.add(new Symbol("*", Token.ASTERISK,0));
        this.add(new Symbol("/", Token.SLASH,0));
        this.add(new Symbol(";", Token.SEMICOLON,0));
        this.add(new Symbol("{", Token.OPENING_BRACES,0));
        this.add(new Symbol("}", Token.CLOSING_BRACES,0));
        this.add(new Symbol("then", Token.THEN,0));
        this.add(new Symbol("readln", Token.READLN,0));
        this.add(new Symbol("write", Token.WRITE,0));
        this.add(new Symbol("writeln", Token.WRITELN,0));
        this.add(new Symbol("%", Token.PERCENTAGE,0));
        this.add(new Symbol("[", Token.OPENING_BRACKETS,0));
        this.add(new Symbol("]", Token.CLOSING_BRACKETS,0));
        this.add(new Symbol("main", Token.MAIN,0));
    }

    public Symbol searchByLexeme(String lexeme) {
        AtomicReference<Symbol> symbolToBeFound = new AtomicReference<>();

        this.forEach(symbol -> {
            if (lexeme.equals(symbol.getLexeme())) {
                symbolToBeFound.set(symbol);
            }
        });

        return symbolToBeFound.get() != null ? symbolToBeFound.get() : null;
    }
}

/**
 * Analisador Sintatico, realiza a análise sintática do programa L.
 */
class Parser {
    private static final int MAX_ARRAY_SIZE = 8*1024;

    private Lexer lexer;
    private Symbol symbol;
    private BufferedWriter buffer;
    private List<String> asmList;
    private int memoryAdressParser;
    private StringBuilder asmLine = new StringBuilder();

    public Parser(Lexer lexer) throws IOException {

        this.lexer = lexer;
        this.memoryAdressParser = 0x4000;
        this.symbol = this.lexer.lexicalAnalysis();
        this.buffer =  new BufferedWriter(new FileWriter("saida.L"));
        asmList = new ArrayList<>();
    }

    public Lexer getLexer() {
        return lexer;
    }

    /**
     * matchToken ( Casa Token )
     * verifica se o token atual é o esperado, chamando a proxima analise léxica caso true
     */
    public void matchToken(Token token) {

        if (Objects.isNull(symbol) && token.equals(Token.END_OF_FILE)) {
            return;
        } else if (Objects.nonNull(symbol) && Objects.nonNull(symbol.getToken()) && compareToken(token)) {
            symbol = lexer.lexicalAnalysis();
        } else if (Objects.nonNull(symbol)) {
            AssertType.unexpectedToken(symbol.getLexeme(), lexer.getLines());
        } else if (Objects.isNull(symbol) && !token.equals(Token.END_OF_FILE)) {
            AssertType.unexpectedEOF(lexer.getLines());
        }
    }

    /**
     * Verifica se o token do simbolo está contido dentro da lista de tokens
     *
     * @param tokens<Token> tokens
     * @return true | false
     **/
    private boolean hasToken(List<Token> tokens) {
        if (Objects.nonNull(symbol)) {
            return tokens.stream().anyMatch(token -> token.equals(symbol.getToken()));
        }
        return false;
    }

    /**
     * Compara se o token passado por parametro é igual ao token lido pelo analisador sintatico
     *
     * @param token
     * @return true | false
     */
    private boolean compareToken(Token token) {
        if (symbol != null && symbol.getToken() != null) return symbol.getToken().equals(token);
        else AssertType.unexpectedEOF(lexer.getLines());
        return false;
    }

    private boolean compareType(Type type) {
        return symbol.getType().equals(type);
    }

    // S -> {D} main { "{" C "}" }
    public void S() {

        asmList.add("sseg SEGMENT STACK ;início seg. pilha");
        asmList.add("  byte 4000h DUP(?) ;dimensiona pilha");
        asmList.add("sseg ENDS ;fim seg. pilha");
        asmList.add("dseg SEGMENT PUBLIC ;início seg. dados");
        asmList.add("  byte 4000h DUP(?) ;temporários");


         while (hasToken(Arrays.asList(Token.FINAL, Token.INT, Token.CHAR, Token.BOOLEAN))) {
            D();
         }

        asmList.add("dseg ENDS ;fim seg. dados");
        asmList.add("cseg SEGMENT PUBLIC ;início seg. código");
        asmList.add("  ASSUME CS:cseg, DS:dseg");
        asmList.add("strt: ;início do programa");
        asmList.add("  mov ax, dseg");
        asmList.add("  mov ds, ax");

        matchToken(Token.MAIN);

        matchToken(Token.OPENING_BRACES);
        while (hasToken(Arrays.asList(Token.SEMICOLON, Token.FOR, Token.IDENTIFIER, Token.IF, Token.READLN, Token.WRITE, Token.WRITELN))) {
            C();
        }

        asmList.add("  mov ah, 4Ch");
        asmList.add("  int 21h");
        asmList.add("cseg ENDS ;fim seg. código");
        asmList.add("END strt; fim programa");

        matchToken(Token.CLOSING_BRACES);

        matchToken(Token.END_OF_FILE);

    }
    //D -> T id({,id} | = V | ["[V]"]); | final id = V;
    private void D() {
        Symbol auxSymbol = new Symbol();
        Symbol symbolFromTable = null;


        switch (symbol.getToken()) {

            case FINAL:
                matchToken(Token.FINAL);
                auxSymbol.setClasse(Classe.CLASSE_CONST);
                checkIfInUse(symbol, auxSymbol); // verifica se o identificador está em uso

                auxSymbol = symbol;

                matchToken(Token.IDENTIFIER);

                matchToken(Token.EQUAL);

                auxSymbol.setType(symbol.getType()); // tipo do valor


                V();

                symbolFromTable = getLexer().getSymbolTable().searchByLexeme(auxSymbol.getLexeme());
                symbolFromTable.setType(auxSymbol.getType());


                matchToken(Token.SEMICOLON);
                asmLine.append("\n");
                break;

            case BOOLEAN:
            case CHAR:
            case INT:
                // Match Token BOOLEAN | CHAR | INT
                auxSymbol.setClasse(Classe.CLASSE_VAR);
                auxSymbol.setType(symbol.getType());
                matchToken(symbol.getToken());
                symbol.setType(auxSymbol.getType());
                checkIfInUse(symbol, auxSymbol); // verifica se o identificador está em uso
                auxSymbol = symbol; // salva o identificador
                matchToken(Token.IDENTIFIER);
                do {
                    if (compareToken(Token.COMMA)) {
                        matchToken(Token.COMMA);
                        symbol.setType(auxSymbol.getType());
                        checkIfInUse(symbol, auxSymbol);
                        auxSymbol = symbol; // novo identificador apos a virgula
                        matchToken(Token.IDENTIFIER);
                    }

                    if (compareToken(Token.OPENING_BRACKETS)) {

                        matchToken(Token.OPENING_BRACKETS);
                        if (symbol.getType() == Type.INT) {
                            symbolFromTable = lexer.getSymbolTable().searchByLexeme(auxSymbol.getLexeme());


                            String lexeme = symbol.getLexeme();
                            int size = Integer.parseInt(lexeme);

                            //caso vetor seja declarado com 0


                            // excecao caso tamanho seja maior que 8192 ( 8kb )
                            if(size <= (MAX_ARRAY_SIZE) && size > 0){
                                symbolFromTable.setSize(size);
                            }else if(size == 0){
                                AssertType.incompatibleTypes(lexer.getLines());
                            }else{
                                AssertType.sizeExceedsLimitsOfArray(lexer.getLines());
                            }
                            V();
                        }else{
                            AssertType.incompatibleTypes(lexer.getLines());
                        }

                        matchToken(Token.CLOSING_BRACKETS);
                    } else if (compareToken(Token.EQUAL)) {
                        matchToken(Token.EQUAL);
                        checkIfCompatibleType(symbol,auxSymbol);
                        V();
                    }
                    else if (compareToken(Token.ATTRIBUTION)) {
                        matchToken(Token.ATTRIBUTION);
                        checkIfCompatibleType(auxSymbol,symbol);
                        if (compareToken(Token.PLUS_SIGN)) {
                            matchToken(Token.PLUS_SIGN);
                        }
                        V();
                    }

                } while (compareToken(Token.COMMA));
                matchToken(Token.SEMICOLON);
        }
    }

    /**
     * Checa se o identificador já tem classe, caso true, lança erro
     * @param symbol
     * @param auxSymbol
     */
    private void checkIfInUse(Symbol symbol, Symbol auxSymbol) {
        if(Objects.isNull(symbol.getClasse())){
            symbol.setClasse(auxSymbol.getClasse());
        }else{
            AssertType.identifierAlreadyInUse(lexer.getLines(),  symbol.getLexeme());
        }
    }


    //T-> int | boolean | char
    private void T(Token token) {
        matchToken(token);
    }

    private void V() {
        if (compareToken(Token.MINUS_SIGN)) {
            matchToken(Token.MINUS_SIGN);

            // ASSEMBLY LINE
            asmLine.append("    sword ");
            //ASSEMBLY LINE
            asmLine.append(" - ").append(symbol.getLexeme());

            matchToken(Token.CONST);

        } else if (compareToken(Token.CONST)) {
            // ASSEMBLY LINE
            if(symbol.getType().equals(Type.CHAR)){
                asmLine.append("    byte ");
            }else{
                asmLine.append("    sword ");
            }
            //ASSEMBLY LINE
            asmLine.append(symbol.getLexeme());
            matchToken(Token.CONST);
        } else if (compareToken(Token.TRUE) || compareToken(Token.FALSE)) {
            // ASSEMBLY LINE
            asmLine.append("    sword ");
            //ASSEMBLY LINE
            asmLine.append(symbol.getLexeme().equals("TRUE") ? 1 : 0);
            matchToken(symbol.getToken());
        } else {
            AssertType.unexpectedToken(symbol.getLexeme(), lexer.getLines());
        }
    }
    private int inFor = 0;
    private void C() {
        if (compareToken(Token.SEMICOLON)) {
            matchToken(Token.SEMICOLON);
        } else if (compareToken(Token.IDENTIFIER)) {
            ATTR();
        } else if (compareToken(Token.FOR)) {
            FOR();
        } else if (compareToken(Token.IF)) {
            IF();
        } else if (compareToken(Token.READLN)) {
            READLN();
        } else if (compareToken(Token.WRITE) || compareToken(Token.WRITELN)) {
            WRITE();
        } else {
            AssertType.unexpectedToken(symbol.getLexeme(), lexer.getLines());
        }


    }

    private void WRITE() {
        matchToken(symbol.getToken());
        matchToken(Token.OPENING_PARENTHESIS);
        EXP();
        while (compareToken(Token.COMMA)) {
            matchToken(Token.COMMA);

            if(compareToken(Token.IDENTIFIER)){
                checkIfHasBeenDeclared(symbol);
            }

            EXP();
        }
        matchToken(Token.CLOSING_PARENTHESIS);
        if (inFor == 0 ) matchToken(Token.SEMICOLON);

    }

    private void READLN() {
        matchToken(Token.READLN);
        matchToken(Token.OPENING_PARENTHESIS);
        checkIfHasBeenDeclared(symbol);
        //matchToken(Token.IDENTIFIER);
        if(compareToken(Token.IDENTIFIER)){
            FS();
        }
        /*if (compareToken(Token.OPENING_BRACKETS)) {
            matchToken(Token.OPENING_BRACKETS);

            matchToken(Token.CLOSING_BRACKETS);
        }*/
        matchToken(Token.CLOSING_PARENTHESIS);
        if (inFor == 0) matchToken(Token.SEMICOLON);
//        else if(hasToken(Arrays.asList(Token.CONST, Token.MINUS_SIGN))){
//            if(compareToken(Token.MINUS_SIGN)){
//                matchToken(Token.MINUS_SIGN);
//            }
//            matchToken(Token.CONST);
//            matchToken(Token.CLOSING_PARENTHESIS);
//            if (inFor == 0 ) matchToken(Token.SEMICOLON);
//
//        }

    }

    private void IF() {
        matchToken(Token.IF);
        matchToken(Token.OPENING_PARENTHESIS);
        EXP();
        matchToken(Token.CLOSING_PARENTHESIS);
        matchToken(Token.THEN);
        if (compareToken(Token.OPENING_BRACES)) {
            matchToken(Token.OPENING_BRACES);
            do{
                C();
            }while (hasToken(Arrays.asList(Token.SEMICOLON, Token.FOR, Token.IDENTIFIER, Token.IF, Token.READLN, Token.WRITE, Token.WRITELN)));
            matchToken(Token.CLOSING_BRACES);
        } else {
            C();

        }

        if (compareToken(Token.ELSE)) {
            matchToken(Token.ELSE);
            if (compareToken(Token.OPENING_BRACES)) {
                matchToken(Token.OPENING_BRACES);

                do {
                    C();
                    if (compareToken(Token.SEMICOLON)) matchToken(Token.SEMICOLON);
                } while (hasToken(Arrays.asList(Token.SEMICOLON, Token.FOR, Token.IDENTIFIER, Token.IF, Token.READLN, Token.WRITE, Token.WRITELN)));

                matchToken(Token.CLOSING_BRACES);

            } else {
                C();

            }
        }
    }


    private boolean firstTime = false;
    private void FOR() {

        inFor++;
        matchToken(Token.FOR);
        matchToken(Token.OPENING_PARENTHESIS);

        F();

        if((firstTime && compareToken(Token.SEMICOLON)) || compareToken(Token.SEMICOLON))  matchToken(Token.SEMICOLON);

        EXP();

        matchToken(Token.SEMICOLON);

        if(!compareToken(Token.CLOSING_PARENTHESIS)) F();

        matchToken(Token.CLOSING_PARENTHESIS);
        if (compareToken(Token.OPENING_BRACES)) {
            matchToken(Token.OPENING_BRACES);
            if (compareToken(Token.CLOSING_BRACES)){
                matchToken(Token.CLOSING_BRACES);
            }else {
                do {
                    C();
                    if (compareToken(Token.SEMICOLON)) matchToken(Token.SEMICOLON);
                } while (hasToken(Arrays.asList(Token.SEMICOLON, Token.FOR, Token.IDENTIFIER, Token.IF, Token.READLN, Token.WRITE, Token.WRITELN)));
                matchToken(Token.CLOSING_BRACES);
            }

        } else {
            C();
        }
        inFor--;
    }

    private void ATTR() {
        Symbol auxSymbol = new Symbol();
        Symbol auxSecSymbol = new Symbol();
        boolean arrayElement = false;

        if(inFor == 0) checkIfHasBeenDeclared(symbol);

        checkIfIsNotFinal(symbol); // não é possível atribuir valor a symbols de classe final

        auxSymbol = symbol;

        matchToken(Token.IDENTIFIER);


        if (compareToken(Token.OPENING_BRACKETS)) {
            matchToken(Token.OPENING_BRACKETS);
            if((auxSymbol.getType().equals(Type.INT) && auxSymbol.getSize() > 0) && (symbol.getType() != null && !symbol.getType().equals(Type.INT))){
                AssertType.incompatibleTypes(getLexer().getLines());
            }
            if(auxSymbol.getSize() > 0){
                arrayElement = true;
            }
            EXP();
            matchToken(Token.CLOSING_BRACKETS);

        }else if(auxSymbol.getSize() > 0 && !auxSymbol.getType().equals(Type.CHAR)){
            AssertType.incompatibleTypes(getLexer().getLines());
        }

        matchToken(Token.ATTRIBUTION);

        specialCaseArrayCharIncompatibleType(auxSymbol, arrayElement);

        checkIfCompatibleType( auxSymbol ,symbol );

        if(auxSymbol.getSize() > 0 ) checkIfArrayAndLimitIsNotExceeded(symbol, auxSymbol);

        EXP();

        if (inFor == 0 ) matchToken(Token.SEMICOLON);

    }

    private void specialCaseArrayCharIncompatibleType(Symbol auxSymbol, boolean arrayElement) {

        if(auxSymbol.getSize() == 0 && symbol.getSize() > 0 && !arrayElement && !symbol.getLexeme().contains("\"")){
            AssertType.incompatibleTypes(lexer.getLines());
        }

        if(!symbol.getLexeme().contains("\"")){
            if((Objects.nonNull(symbol.getType()) && Objects.nonNull(auxSymbol.getType())
                    && (auxSymbol.getSize() > 0 && auxSymbol.getType().equals(Type.CHAR)))){
                if(symbol.getSize() == 0 && !arrayElement){
                    AssertType.incompatibleTypes(lexer.getLines());
                }
            }
        }
    }

    private void checkIfArrayAndLimitIsNotExceeded(Symbol symbol, Symbol auxSymbol) {
        int lexemeSize = symbol.getLexeme().replaceAll("\'|\"","").length();
        int symbolSize = symbol.getSize();

        if(symbol.getLexeme().contains("\"")){
           lexemeSize +=  1; // adiciona $ ao final da string
        }


            if(auxSymbol.getType().equals(Type.CHAR)){
                if(symbolSize > 0 && symbolSize > auxSymbol.getSize()){
                    AssertType.incompatibleTypes(getLexer().getLines());
                }else if(symbolSize == 0 && lexemeSize > auxSymbol.getSize()){
                    AssertType.incompatibleTypes(getLexer().getLines());
                }
            }

    }


    /**
     *
     * @param symbol -> no caso de char/string, symbol deve ser o identificador
     * @param auxSymbol ->  no caso de char/string , auxSymbol deve ser a constante string
     */
    private void checkIfCompatibleType(Symbol symbol, Symbol auxSymbol) {



        if((Objects.nonNull(symbol.getType()) && Objects.nonNull(auxSymbol.getType())
           && (symbol.getSize() == 0 && symbol.getType().equals(Type.CHAR)))){
           if(auxSymbol.getLexeme().contains("\"")){
               AssertType.incompatibleTypes(lexer.getLines());
           }
        }

        if((Objects.nonNull(symbol.getType()) && Objects.nonNull(auxSymbol.getType())
                && (symbol.getSize() == 0 && symbol.getType().equals(Type.CHAR)))){
            if(auxSymbol.getLexeme().contains("\"")){
                AssertType.incompatibleTypes(lexer.getLines());
            }
        }

        if((Objects.nonNull(symbol.getType()) && Objects.nonNull(auxSymbol.getType()))
            && !symbol.getType().equals(auxSymbol.getType())){
            AssertType.incompatibleTypes(lexer.getLines());
        }

    }

    private void checkIfIsNotFinal(Symbol symbol) {
        if(Objects.nonNull(symbol.getClasse()) && symbol.getClasse().equals(Classe.CLASSE_CONST)){
            AssertType.incompatibleIdentifierClass(lexer.getLines(), symbol.getLexeme());
        }
    }

    private void checkIfHasBeenDeclared(Symbol symbol) {
        if(Objects.isNull(symbol.getClasse())){
            AssertType.identifierNotDeclared(lexer.getLines(), symbol.getLexeme());
        }
    }

    private void EXP() {
         Symbol auxSymbol = symbol;
         Symbol auxSecSymbol = new Symbol();
         Token operation = null;

         EXPS();

         if (hasToken(Arrays.asList(Token.EQUAL,Token.LT,Token.NOT_EQUAL,Token.LTOE,Token.GT,Token.GTOE))){
            operation = symbol.getToken();

            matchToken(symbol.getToken());

            auxSecSymbol = symbol;

             if(auxSymbol.getType().equals(Type.CHAR)){
                 checkStringArrayOperator(auxSymbol, auxSecSymbol, operation);
             }


            EXPS();
         }


    }

    //Retornar aqui, string = charVector
    private void checkStringArrayOperator(Symbol auxSymbol, Symbol auxSecSymbol, Token operation) {

           if(!operation.equals(Token.EQUAL)){
               AssertType.incompatibleTypes(lexer.getLines());
           }else{
               if(!auxSecSymbol.getType().equals(auxSymbol.getType())){
                   AssertType.incompatibleTypes(lexer.getLines());
               }
           }

    }

    private void EXPS() {
        Symbol firstTerm = new Symbol();
        Symbol secondTerm = new Symbol();
        boolean orOperation = false;

        if (compareToken(Token.MINUS_SIGN) || compareToken(Token.PLUS_SIGN)) {
            matchToken(symbol.getToken());
        }
        firstTerm = symbol;

        TS();
        while (hasToken(Arrays.asList(Token.PLUS_SIGN, Token.MINUS_SIGN, Token.OR))) {
            if(symbol.getToken().equals(Token.OR)){
               orOperation = true;
            }
            matchToken(symbol.getToken());
            if(orOperation){
                secondTerm = symbol;
                checkIfTermsAreLogicType(firstTerm, secondTerm );
            }
            TS();
        }

    }

    private void checkIfTermsAreLogicType(Symbol firstTerm, Symbol secondTerm) {
        if(Objects.nonNull(firstTerm.getType()) && Objects.nonNull(secondTerm.getType())){
            if(!firstTerm.getType().equals(Type.BOOLEAN) && !secondTerm.getType().equals(Type.BOOLEAN)){
                AssertType.incompatibleTypes(lexer.getLines());
            }
        }
    }

    private void F() {
            if(compareToken(Token.SEMICOLON)){
                firstTime = true;
            }else {
                firstTime = false;
            }
        C();
        while (compareToken(Token.COMMA)) {
            matchToken(Token.COMMA);
            C();
        }


    }

    private void TS() {
        FS();
        while (hasToken(Arrays.asList(Token.ASTERISK, Token.SLASH, Token.PERCENTAGE, Token.AND))) {
            matchToken(symbol.getToken());
            FS();
        }

    }

    private void FS() {
        if (compareToken(Token.NOT)) {
            matchToken(Token.NOT);
            if(Objects.nonNull(symbol.getType())  &&  !symbol.getType().equals(Type.BOOLEAN)){
                AssertType.incompatibleTypes(getLexer().getLines());
            }
            FS();

        } else if (compareToken(Token.OPENING_PARENTHESIS)) {
            matchToken(Token.OPENING_PARENTHESIS);
            EXP();
            matchToken(Token.CLOSING_PARENTHESIS);
        } else if (hasToken(Arrays.asList(Token.CONST, Token.TRUE, Token.FALSE))) {
            V();
        } else if (compareToken(Token.IDENTIFIER)) {
            if (symbol.getSize() > 0 && !symbol.getType().equals(Type.CHAR)){
                matchToken(Token.IDENTIFIER);
                matchToken(Token.OPENING_BRACKETS);
                if(symbol.getType() == Type.INT){
                    V();
                }
                else{
                    AssertType.incompatibleTypes(lexer.getLines());
                }
                matchToken(Token.CLOSING_BRACKETS);
            }
            else{
                matchToken(Token.IDENTIFIER);
            }

            if (compareToken(Token.OPENING_BRACKETS)) {
                matchToken(Token.OPENING_BRACKETS);
                EXP();
                matchToken(Token.CLOSING_BRACKETS);
            }
        } else {
            AssertType.unexpectedToken(symbol.getLexeme(), lexer.getLines());
        }

    }
}


/**
 * Analisador Lexico , recebe o programa fonte L e analisa se está dentro dos parâmetros do alfabeto
 */
class Lexer {

    private static final char EOF = (char) -1;

    private SymbolTable symbolTable = new SymbolTable();

    private String symbols = "=(),+-*;{}%[]";
    private BufferedReader bufferedReader;
    //Symbol creation
    private String lexeme = "";
    private Type type;
    private Token token;
    private char currentChar = ' ';
    private Character previousChar = null;
    private Byte INITIAL_STATE = 0;
    private Byte CURRENT_STATE = 0;
    private Byte FINAL_STATE = 127;
    private int lines = 1;

    public Lexer(BufferedReader br) {
        this.bufferedReader = br;
    }

    public int getLines() {
        return lines;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }




    /**
     * Analisa o programa fonte recebido e retorna um Symbol ( type , token , lexema )
     *
     * @return Symbol ( token )
     */
    public Symbol lexicalAnalysis() {
        Symbol symbol = null;


        CURRENT_STATE = INITIAL_STATE;
        lexeme = "";
        token = null;
        type = null;

        while (CURRENT_STATE != FINAL_STATE && currentChar != EOF) {

            readCharacter();

            if (!AssertType.isValidChar(currentChar) && currentChar != EOF) {
                AssertType.printInvalidChar(currentChar, lines);
            }

            switch (CURRENT_STATE) {
                case 0:
                    initialState();
                    break;
                case 1:
                    state1();
                    break;
                case 2:
                    state2();
                    break;
                case 3:
                    state3();
                    break;
                case 4:
                    state4();
                    break;
                case 5:
                    state5();
                    break;
                case 6:
                    state6();
                    break;
                case 7:
                    state7();
                    break;
                case 8:
                    state8();
                    break;
                case 9:
                    state9();
                    break;
                case 10:
                    state10();
                    break;
                case 11:
                    state11();
                    break;
                case 12:
                    state12();
                    break;
                case 13:
                    state13();
                    break;
                case 14:
                    state14();
                    break;
                case 15:
                    state15();
                    break;
                case 16:
                    state16();
                    break;
                case 17:
                    state17();
                    break;
                case 18:
                    state18();
                    break;
                case 19:
                    state19();
                    break;
                case 20:
                    state20();
                    break;
            }


            if (previousChar == null && (currentChar != '\n' && currentChar != '\r') && currentChar != ' ' && currentChar != EOF) {
                lexeme = lexeme.concat(String.valueOf(currentChar));
            }
        }


        Symbol symbolFromTable = symbolTable.searchByLexeme(lexeme);

        if (symbolFromTable == null && !lexeme.equals("")) {
            if (type != null) {
                symbol = new Symbol(token, type, lexeme);
            } else {
                symbol = new Symbol(lexeme, token);
            }

            symbolTable.add(symbol);
        } else {
            symbol = symbolFromTable;
        }

        return symbol;
    }

    private void initialState() {
        if (checkSymbols(currentChar)) {
            CURRENT_STATE = FINAL_STATE;
        } else if (currentChar == ' ' || (currentChar == '\n' || currentChar == '\r') || (currentChar == EOF)) {
            CURRENT_STATE = INITIAL_STATE;
        } else if (AssertType.isNumericNotZero(currentChar)) {
            CURRENT_STATE = 1;
        } else if (currentChar == '0') {
            CURRENT_STATE = 2;
        } else if (AssertType.isCharacter(currentChar)) {
            CURRENT_STATE = 3;
        } else if (currentChar == '_') {
            CURRENT_STATE = 4;
        } else if (currentChar == '>') {
            CURRENT_STATE = 5;
        } else if (currentChar == ':') {
            CURRENT_STATE = 6;
        } else if (currentChar == '<') {
            CURRENT_STATE = 7;
        } else if (currentChar == '\'') {
            CURRENT_STATE = 8;
        } else if (currentChar == '/') {
            CURRENT_STATE = 9;
        } else if (currentChar == '"') {
            CURRENT_STATE = 10;
        } else {
            AssertType.lexemeNotIdentified(String.valueOf(currentChar), lines);
        }
    }

    private void state1() {
        if (!AssertType.isNumeric(currentChar)) {
            returnChar();
            token = Token.CONST;
            type = Type.INT;
        } else {
            CURRENT_STATE = 1;
        }
    }

    private void state2() {
        if (AssertType.isNumeric(currentChar)) {
            CURRENT_STATE = 12;
        } else if (AssertType.isHexa(currentChar)) {
            CURRENT_STATE = 13;
        } else if (currentChar == 'h') {
            AssertType.lexemeNotIdentified("" + currentChar, lines);
        } else {
            type = Type.INT;
            token = Token.CONST;
            returnChar();
        }
    }

    private void state3() {
        if (AssertType.isCharacter(currentChar) || AssertType.isNumeric(currentChar) || currentChar == '_') {
            CURRENT_STATE = 3;
        } else {
            token = Token.IDENTIFIER;

            returnChar();
        }
    }

    private void state4() {
        if (currentChar == '_') {
            CURRENT_STATE = 4;
        } else if (AssertType.isCharacter(currentChar) || AssertType.isNumeric(currentChar)) {
            CURRENT_STATE = 15;
        } else {
            AssertType.lexemeNotIdentified(lexeme, lines);
        }
    }

    private void state5() {
        if (currentChar == '=') {
            CURRENT_STATE = FINAL_STATE;
        } else {
            returnChar();
        }
    }

    private void state6() {
        if (currentChar == '=') {
            CURRENT_STATE = FINAL_STATE;
        } else {
            AssertType.lexemeNotIdentified(lexeme, lines);

        }
    }

    private void state7() {
        if (currentChar == '=' || currentChar == '>') {
            CURRENT_STATE = FINAL_STATE;
        } else {
            returnChar();
        }
    }

    private void state8() {
        if (currentChar != '\n' && currentChar != '\r' && currentChar != '$') {
            CURRENT_STATE = 11;
        } else {
            AssertType.lexemeNotIdentified(lexeme, getLines());
        }
    }

    private void state9() {
        if (currentChar == '*') {
            CURRENT_STATE = 16;
        } else {
            returnChar();
        }
    }

    private void state10() {

        if (currentChar != '$' && (currentChar != '\n' && currentChar != '\r') && currentChar != '\"') {
            CURRENT_STATE = 10;
        } else if (currentChar == '"') {
            CURRENT_STATE = FINAL_STATE;
            token = Token.CONST;
            type = Type.CHAR;

        } else {
            AssertType.lexemeNotIdentified(lexeme, lines);
        }
    }

    private void state11() {
        if (currentChar == '\'') {
            CURRENT_STATE = FINAL_STATE;
            token = Token.CONST;
            type = Type.CHAR;
        } else {
            AssertType.lexemeNotIdentified(lexeme, lines);
        }
    }

    private void state12() {
        if (AssertType.isNumeric(currentChar)) {
            CURRENT_STATE = 14;
        } else if (AssertType.isHexa(currentChar)) {
            CURRENT_STATE = 18;
        } else {
            token = Token.CONST;
            type = Type.INT;
           returnChar();
        }
    }

    private void state13() {
        if (AssertType.isNumeric(currentChar)) {
            CURRENT_STATE = 19;
        } else if (AssertType.isHexa(currentChar)) {
            CURRENT_STATE = 20;
        } else {
            AssertType.lexemeNotIdentified(lexeme, lines);
        }
    }

    private void state14() {
        if (AssertType.isNumeric(currentChar)) {
            CURRENT_STATE = 1;
        } else if (currentChar == 'h' || !AssertType.isNumeric(currentChar)) {
            if(currentChar == 'h') {
                CURRENT_STATE = FINAL_STATE;
                token = Token.CONST;
                type = Type.CHAR;
            }else{
                returnChar();
            }


        }
        else {
            AssertType.lexemeNotIdentified(lexeme, lines);
        }
    }

    private void state15() {
        if (AssertType.isNumeric(currentChar) || AssertType.isCharacter(currentChar) || currentChar == '_') {
            CURRENT_STATE = 15;

        } else {
            token = Token.IDENTIFIER;
            returnChar();
        }
    }

    private void state16() {

        if (currentChar == EOF) {
            AssertType.unexpectedEOF(lines);
        } else if (currentChar == '*') {
            CURRENT_STATE = 17;
        } else {
            CURRENT_STATE = 16;
        }
    }

    private void state17() {
        if (currentChar == '/') {
            CURRENT_STATE = INITIAL_STATE;
            lexeme = "";
            readCharacter();
        } else if (currentChar == '*') {
          CURRENT_STATE = 17;
        }else{
            CURRENT_STATE = 16;
        }
    }

    private void state18() {
        if (currentChar == 'h') {
            CURRENT_STATE = FINAL_STATE;
            token = Token.CONST;
            type = Type.CHAR;
        } else {
            AssertType.lexemeNotIdentified(lexeme, lines);
        }
    }

    private void state19() {
        if (currentChar == 'h') {
            CURRENT_STATE = FINAL_STATE;
            token = Token.CONST;
            type = Type.CHAR;
        } else {
            AssertType.lexemeNotIdentified(lexeme, lines);
        }
    }

    private void state20() {
        if (currentChar == 'h') {
            CURRENT_STATE = FINAL_STATE;
            token = Token.CONST;
            type = Type.CHAR;
        } else {
            AssertType.lexemeNotIdentified(lexeme, lines);
        }
    }

    private boolean checkSymbols(char currentChar) {
        String str = String.valueOf(currentChar);
        if (symbols.contains(str.toString())) {
            return true;
        }
        return false;
    }

    private void readCharacter() {
        try {
            if (previousChar == null) {
                currentChar = (char) bufferedReader.read();
                if (currentChar == '\n') {
                    lines++;
                }
            } else {
                currentChar = previousChar;
                previousChar = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void returnChar() {
        previousChar = currentChar;
        CURRENT_STATE = FINAL_STATE;
    }
}


/**
 * A classe recebe o caractere C e valida seu tipo, além disso é utilizada para gerar errors de compilação
 *
 * @param "C"
 * @return ;
 */
class AssertType {
    private static final Pattern validCharRegex = Pattern.compile("[\\s\\r\\n!?.,;{}=*()><\\[\\]:+=\"\\-\\/'%@\\d\\w]");
    public static final char EOF = (char) -1;

    public static boolean isCharacter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public static boolean isNumericNotZero(char c) {
        return (c >= '1' && c <= '9');
    }

    public static boolean isNumeric(char c) {
        return (c >= '0' && c <= '9');
    }

    public static boolean isHexa(char c) {
        return ((c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'));
    }

    public static boolean isValidChar(char c) {
        String character = String.valueOf(c);
        Matcher matcher = validCharRegex.matcher(character);
        return matcher.matches();
    }

    public static void printInvalidChar(char c, int lines) {
        String errorMessage = c == EOF ? "%d\nfim de arquivo nao esperado." : "%d\ncaractere invalido.";
        System.out.printf(errorMessage, lines);
        System.exit(1);
    }

    public static void lexemeNotIdentified(String lexeme, int lines) {
        System.out.printf("%d\nlexema nao identificado [%s].", lines, lexeme);
        System.exit(1);
    }

    public static void unexpectedEOF(int lines) {
        System.out.printf("%d\nfim de arquivo nao esperado.", lines);
        System.exit(1);
    }

    public static void unexpectedToken(String lexeme, int lines) {
        System.out.printf("%d\ntoken nao esperado [%s].", lines, lexeme);
        System.exit(1);
    }

    public static void identifierAlreadyInUse(int lines, String lexeme) {
        System.out.printf("%d\nidentificador ja declarado [%s].", lines, lexeme);
        System.exit(1);
    }

    public static void sizeExceedsLimitsOfArray(int lines) {
        System.out.printf("%d\ntamanho do vetor excede o maximo permitido.", lines);
        System.exit(1);
    }

    public static void identifierNotDeclared(int lines, String lexeme) {
        System.out.printf("%d\nidentificador nao declarado [%s].", lines, lexeme);
        System.exit(1);
    }

    public static void incompatibleIdentifierClass(int lines, String lexeme) {
        System.out.printf("%d\nclasse de identificador incompatível [%s].", lines, lexeme);
        System.exit(1);
    }

    public static void incompatibleTypes(int lines) {
        System.out.printf("%d\ntipos incompativeis.", lines);
        System.exit(1);
    }
}
