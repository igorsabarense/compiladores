    import java.io.*;
    import java.util.*;
    import java.util.concurrent.atomic.AtomicReference;
    import java.util.regex.*;

    public class LC {
        public static void main(String [] args) throws IOException {
            String sourceCode = readLineByLine();
            Parser parser = new Parser(new Lexer(sourceCode));
            parser.S();
            System.out.printf("%d linhas compiladas.", parser.getLexer().getLines());

        }

        public static String readLineByLine() throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "US-ASCII"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        }
    }

    enum Type {
        INT, CHAR,BOOLEAN;
    }

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
        EQUAL ("="),
        OPENING_PARENTHESIS ("("),
        CLOSING_PARENTHESIS (")"),
        LT ("<"),
        GT (">"),
        NOT_EQUAL("<>"),
        GTOE (">="),
        LTOE ("<="),
        COMMA (","),
        PLUS_SIGN ("+"),
        MINUS_SIGN ("-"),
        ASTERISK ("*"),
        SLASH ("/"),
        SEMICOLON (";"),
        OPENING_BRACES ("{"),
        CLOSING_BRACES ("}"),
        THEN ("then"),
        READLN ("readln"),
        WRITE ("write"),
        WRITELN ("writeln"),
        PERCENTAGE ("%"),
        OPENING_BRACKETS ("["),
        CLOSING_BRACKETS ("]"),
        MAIN("main"),
        CONST("const"),
        IDENTIFIER("identifier");

        private String description;

        Token(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

    }

    class Symbol  {

        private Token token;
        private Type type;
        private String lexeme;
        public Symbol(){

        }

        public Symbol( String lexeme, Token token ) {
            this.token = token;
            this.lexeme = lexeme;
        }

        public Symbol(Token token , Type type ,String lexeme) {
            this.token = token;
            this.lexeme = lexeme;
            this.type = type;
        }

        public Token getToken() { return token; }

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
                            '}'  ;
        }
    }

    class SymbolTable extends HashSet<Symbol>{

        public SymbolTable(){
            this.add(new Symbol("final",Token.FINAL));
            this.add(new Symbol("int",Token.INT));
            this.add(new Symbol("char",Token.CHAR));
            this.add(new Symbol("boolean",Token.BOOLEAN));
            this.add(new Symbol("for",Token.FOR));
            this.add(new Symbol("if",Token.IF));
            this.add(new Symbol("else",Token.ELSE));
            this.add(new Symbol(Token.TRUE, Type.BOOLEAN, "TRUE"));
            this.add(new Symbol(Token.FALSE, Type.BOOLEAN, "FALSE"));
            this.add(new Symbol("and",Token.AND));
            this.add(new Symbol("or",Token.OR));
            this.add(new Symbol("not",Token.NOT));
            this.add(new Symbol(":=",Token.ATTRIBUTION));
            this.add(new Symbol("=",Token.EQUAL));
            this.add(new Symbol("(",Token.OPENING_PARENTHESIS));
            this.add(new Symbol(")",Token.CLOSING_PARENTHESIS));
            this.add(new Symbol("<",Token.LT));
            this.add(new Symbol(">",Token.GT));
            this.add(new Symbol("<>",Token.NOT_EQUAL));
            this.add(new Symbol(">=",Token.GTOE));
            this.add(new Symbol("<=",Token.LTOE));
            this.add(new Symbol(",",Token.COMMA));
            this.add(new Symbol("+",Token.PLUS_SIGN));
            this.add(new Symbol("-",Token.MINUS_SIGN));
            this.add(new Symbol("*",Token.ASTERISK));
            this.add(new Symbol("/",Token.SLASH));
            this.add(new Symbol(";",Token.SEMICOLON));
            this.add(new Symbol("{",Token.OPENING_BRACES));
            this.add(new Symbol("}",Token.CLOSING_BRACES));
            this.add(new Symbol("then",Token.THEN));
            this.add(new Symbol("readln",Token.READLN));
            this.add(new Symbol("write",Token.WRITE));
            this.add(new Symbol("writeln",Token.WRITELN));
            this.add(new Symbol("%",Token.PERCENTAGE));
            this.add(new Symbol("[",Token.OPENING_BRACKETS));
            this.add(new Symbol("]", Token.CLOSING_BRACKETS));
            this.add(new Symbol("main",Token.MAIN));
        }

        public Symbol searchByLexeme(String lexeme){
            AtomicReference<Symbol> symbolToBeFound = new AtomicReference<>();

            this.forEach( symbol -> {
                if(lexeme.equals(symbol.getLexeme())){
                    symbolToBeFound.set(symbol);
                }
            });

            return symbolToBeFound.get() != null ? symbolToBeFound.get() : null;
        }
    }

    class Parser {

        private Lexer lexer;
        private Symbol symbol;

        public Parser(Lexer lexer) {

            this.lexer = lexer;
            this.symbol = this.lexer.lexicalAnalysis();

        }

        public Lexer getLexer() {
            return lexer;
        }

        public void matchToken(Token token){
            if(Objects.nonNull(symbol) && compareToken(token)){
                symbol = lexer.lexicalAnalysis();
            }else{
                AssertType.unexpectedToken(symbol.getLexeme(), lexer.getLines());
            }
        }

        private boolean hasToken(List<Token> tokens){
            return tokens.stream().anyMatch(token -> token.equals(symbol.getToken()));
        }

        private boolean compareToken (Token token){
            if(Objects.nonNull(symbol) && Objects.nonNull(symbol.getToken())){
                return symbol.getToken().equals(token);
            }
            return false;
        }

        private boolean compareType (Type type){
            return symbol.getType().equals(type);
        }

        // S -> {D} main B
        public void S (){

            while ( hasToken(Arrays.asList(Token.FINAL,Token.INT, Token.CHAR, Token.BOOLEAN))){
                D();
            }

            matchToken(Token.MAIN);

            B();

        }

        //D -> T id({,id} | = V | ["[V]"]); | final id = V;
        private void D (){
            if(compareToken(Token.FINAL)){
                matchToken(Token.FINAL);
                matchToken(Token.IDENTIFIER);
                matchToken(Token.EQUAL);
                V();
                matchToken(Token.SEMICOLON);
            }else if(compareToken(Token.INT) || compareToken(Token.CHAR) || compareToken(Token.BOOLEAN)){
                // when inside if , we know the symbol's token is the INT|CHAR|BOOLEAN , so we pass to the next state the symbol's token
                T(symbol.getToken());
                matchToken(Token.IDENTIFIER);

                if(compareToken(Token.OPENING_BRACKETS)){
                    matchToken(Token.OPENING_BRACKETS);
                    matchToken(Token.CONST);
                    matchToken(Token.CLOSING_BRACKETS);
                }else if(compareToken(Token.COMMA)){
                    matchToken(Token.COMMA);
                    matchToken(Token.IDENTIFIER);
                }else if(compareToken(Token.EQUAL)){
                    matchToken(Token.EQUAL);
                    V();
                }

                matchToken(Token.SEMICOLON);
            }
        }

        //B ->  "{" {C} "}"
        private void B (){
            matchToken(Token.OPENING_BRACES);
            while ( hasToken(Arrays.asList(Token.SEMICOLON,Token.FOR, Token.IDENTIFIER, Token.IF,Token.READLN, Token.WRITE, Token.WRITELN))) {
                C();
            }
            matchToken(Token.CLOSING_BRACES);
        }
        //
        private void T (Token token){
            matchToken(token);
        }

        private void V (){
            if(compareToken(Token.MINUS_SIGN)){
                matchToken(Token.MINUS_SIGN);
                matchToken(Token.CONST);
            }else if(compareToken(Token.CONST)){
                matchToken(Token.CONST);
            }else if(compareToken(Token.TRUE) || compareToken(Token.FALSE)){
                matchToken(symbol.getToken());
            }
        }

        private void C (){
            if(compareToken(Token.SEMICOLON)){
                matchToken(Token.SEMICOLON);
            }else if(compareToken(Token.IDENTIFIER)){
                matchToken(Token.IDENTIFIER);
                if(compareToken(Token.OPENING_BRACKETS)){
                    matchToken(Token.OPENING_BRACKETS);
                    EXP();
                    matchToken(Token.CLOSING_BRACKETS);
                }
                matchToken(Token.ATTRIBUTION);
                EXP();

            } else if (compareToken(Token.FOR)) {
                matchToken(Token.FOR);
                matchToken(Token.OPENING_PARENTHESIS);

                while(compareToken(Token.IDENTIFIER) || compareToken(Token.COMMA)){
                    F();
                }
                matchToken(Token.SEMICOLON);
                EXP();
                matchToken(Token.SEMICOLON);

                while(compareToken(Token.IDENTIFIER) || compareToken(Token.COMMA)){
                    F();
                }
                matchToken(Token.CLOSING_PARENTHESIS);

                if(compareToken(Token.OPENING_BRACES)){
                    B();
                }else{
                    C();
                }

            }else if(compareToken(Token.IF)){
                matchToken(Token.IF);
                matchToken(Token.OPENING_PARENTHESIS);
                EXP();
                matchToken(Token.CLOSING_PARENTHESIS);
                matchToken(Token.THEN);
                if(compareToken(Token.OPENING_BRACES)){
                    B();
                }else{
                    C();
                }

                if(compareToken(Token.ELSE)){
                    matchToken(Token.ELSE);
                    if(compareToken(Token.OPENING_BRACES)){
                        B();
                    }else{
                        C();
                    }
                }
            }else if(compareToken(Token.READLN)){
                matchToken(Token.READLN);
                matchToken(Token.OPENING_PARENTHESIS);
                matchToken(Token.IDENTIFIER);
                if(compareToken(Token.OPENING_BRACKETS)){
                    matchToken(Token.OPENING_BRACKETS);
                    EXP();
                    matchToken(Token.CLOSING_BRACKETS);
                }
                matchToken(Token.CLOSING_PARENTHESIS);
            }else if(compareToken(Token.WRITE) || compareToken(Token.WRITELN)){
                matchToken(symbol.getToken());
                matchToken(Token.OPENING_PARENTHESIS);
                EXP();

                while(compareToken(Token.COMMA)){

                    matchToken(Token.COMMA);
                    EXP();
                }
                matchToken(Token.CLOSING_PARENTHESIS);

            }
        }
        private void EXP (){
            EXPS();
            if(compareToken(Token.EQUAL) ||
                    compareToken( Token.LT) ||
                    compareToken(Token.NOT_EQUAL) ||
                    compareToken(Token.LTOE) ||
                    compareToken(Token.GT) ||
                    compareToken(Token.GTOE)){

                matchToken(symbol.getToken());
                EXPS();

            }
        }
        private void EXPS (){
            if(compareToken(Token.MINUS_SIGN) || compareToken(Token.PLUS_SIGN)){
                matchToken(symbol.getToken());
            }
            TS();
            while (hasToken(Arrays.asList(Token.PLUS_SIGN, Token.MINUS_SIGN,Token.OR))){

                matchToken(symbol.getToken());
                TS();
            }
        }

        private void F (){
            C();
            if(compareToken(Token.COMMA)){
                matchToken(Token.COMMA);
                C();
            }
        }

        private void TS (){
            FS();
            while(hasToken(Arrays.asList(Token.ASTERISK,Token.SLASH,Token.PERCENTAGE,Token.AND))){
                matchToken(symbol.getToken());
                FS();
            }
        }

        private void FS(){
            if(compareToken(Token.NOT)){
                matchToken(Token.NOT);
                FS();
            }else if ( compareToken(Token.OPENING_PARENTHESIS)) {
                matchToken(Token.OPENING_PARENTHESIS);
                EXP();
                matchToken(Token.CLOSING_PARENTHESIS);
            }else if(hasToken(Arrays.asList(Token.CONST, Token.TRUE , Token.FALSE))){
                V();
            }else if(compareToken(Token.IDENTIFIER)){
                matchToken(Token.IDENTIFIER);

                if(compareToken(Token.OPENING_BRACKETS)){
                    matchToken(Token.OPENING_BRACKETS);
                    EXP();
                    matchToken(Token.CLOSING_BRACES);
                }
            }
        }
    }

    class Lexer {

        private SymbolTable symbolTable = new SymbolTable();
        private String symbols = "=.(),+-*;{}%[]";


        //Symbol creation;
        private String lexeme = "";
        private Type type;
        private Token token;

        private char[] sourceCode;
        private char currentChar = ' ';
        private Character previousChar = null;

        private Byte INITIAL_STATE = 0;
        private Byte CURRENT_STATE = 0;
        private Byte FINAL_STATE = 127;

        private int lines = 1;
        private int index;
        private final char EOF = (char) -1;

        public SymbolTable getSymbolTable() {
            return symbolTable;
        }

        public Lexer (String sourceCode){
             this.sourceCode = sourceCode.replaceAll("\r\n","\n").toCharArray();
             this.index = 0 ;
        }

        public void printSymbolTableLexer(){
            this.symbolTable.forEach(e-> System.out.println(e.toString()));
        }

        public int getLines() {
            return lines;
        }

        public Symbol lexicalAnalysis(){
            Symbol symbol = new Symbol();


            CURRENT_STATE = INITIAL_STATE;
            lexeme = "";
            token = null;
            type = null;

            while (CURRENT_STATE != FINAL_STATE && index <= this.sourceCode.length - 1 ){

                readCharacter();

                if(!AssertType.isValidChar(currentChar)){
                    AssertType.printInvalidChar(currentChar,lines);
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


                if ( previousChar == null && currentChar != '\n' && currentChar != ' ') {
                    lexeme = lexeme.concat(String.valueOf(currentChar));
                }
            }

            
            Symbol symbolFromTable = symbolTable.searchByLexeme(lexeme);

            if(symbolFromTable == null && !lexeme.equals("")){

                symbol.setLexeme(lexeme);
                if(type != null) symbol.setType(type);
                symbol.setToken(token);
                symbolTable.add(symbol);
            }
            else {
                symbol = symbolFromTable;
            }

            return  symbol;
        }

        private void initialState(){
            if(checkSymbols(currentChar)){
                CURRENT_STATE = FINAL_STATE;
            }
            else if (currentChar == ' ' || currentChar == '\n'){
                CURRENT_STATE = INITIAL_STATE;
            }
            else if (AssertType.isNumericNotZero(currentChar)){
                CURRENT_STATE = 1;
            }
            else if (currentChar == '0'){
                CURRENT_STATE = 2;
            }
            else if (AssertType.isCharacter(currentChar)){
                CURRENT_STATE = 3;
            }
            else if (currentChar =='_'){
                CURRENT_STATE = 4;
            }
            else if (currentChar == '>'){
                CURRENT_STATE = 5;
            }
            else if (currentChar == ':'){
                CURRENT_STATE = 6;
            }
            else if (currentChar == '<'){
                CURRENT_STATE = 7;
            }
            else if (currentChar == '\''){
                CURRENT_STATE = 8;
            }
            else if (currentChar == '/'){
                CURRENT_STATE = 9;
            }
            else if (currentChar == '"'){
                CURRENT_STATE = 10;
            }
        }

        private void state1() {
            if(!AssertType.isNumeric(currentChar)){
                returnChar();
                token = Token.CONST;
                type = Type.INT;
            }
            else{
                CURRENT_STATE = 1;
            }
        }

        private void state2() {
            if(AssertType.isNumeric(currentChar)){
                CURRENT_STATE = 12;
            }
            else if(AssertType.isHexa(currentChar)){
                CURRENT_STATE = 13;
            }
            else if(!AssertType.isHexa(currentChar) || !AssertType.isNumeric(currentChar)){
                type = Type.INT;
                token = Token.CONST;
                returnChar();
            }
        }

        private void state3() {
            if(AssertType.isCharacter(currentChar) || AssertType.isNumeric(currentChar) || currentChar == '_' ){
                CURRENT_STATE = 3;
            }
            else{
               token = Token.IDENTIFIER;
               returnChar();
            }
        }

        private void state4() {
            if (currentChar == '_'){
                CURRENT_STATE = 4;
            }
            else if(AssertType.isCharacter(currentChar) || AssertType.isNumeric(currentChar)){
                CURRENT_STATE = 15;
            }
            else{
                AssertType.lexemeNotIdentified(lexeme,lines);
            }
        }

        private void state5() {
            if(currentChar == '='){
                CURRENT_STATE = FINAL_STATE;
            }
            else {
                returnChar();
            }
        }

        private void state6() {
            if(currentChar == '='){
                CURRENT_STATE = FINAL_STATE;
            }
            else{
                AssertType.lexemeNotIdentified(lexeme,lines);

            }
        }

        private void state7() {
            if(currentChar == '=' || currentChar == '>'){
                CURRENT_STATE = FINAL_STATE;
            }
            else{
                returnChar();
            }
        }

        private void state8() {
            if(AssertType.isCharacter(currentChar) || AssertType.isNumeric(currentChar)){
                CURRENT_STATE = 11;
            }
        }

        private void state9() {
            if(currentChar == '*'){
                CURRENT_STATE = 16;
            }
            else{
                returnChar();
            }
        }

        private void state10() {

            if(currentChar != '$' && currentChar != '\n' && currentChar != '"' ){
                CURRENT_STATE = 10;
            }
            else if(currentChar == '"'){
                CURRENT_STATE = FINAL_STATE;
                token = Token.CONST;

            }
            else{
                AssertType.lexemeNotIdentified(lexeme,lines);
            }
        }

        private void state11() {
            if(currentChar == '\''){
                CURRENT_STATE = FINAL_STATE;
                token = Token.CONST;
                type = Type.CHAR;
            }
            else{
                AssertType.lexemeNotIdentified(lexeme,lines);
            }
        }

        private void state12(){
            if(AssertType.isNumeric(currentChar)){
                CURRENT_STATE = 14;
            }
            else if(AssertType.isHexa(currentChar)){
                CURRENT_STATE = 18;
            }
            else{
                AssertType.lexemeNotIdentified(lexeme,lines);
            }
        }

        private void state13() {
            if(AssertType.isNumeric(currentChar)){
                CURRENT_STATE = 19;
            }
            else if(AssertType.isHexa(currentChar)){
                CURRENT_STATE = 20;
            }
            else{
                AssertType.lexemeNotIdentified(lexeme,lines);
            }
        }

        private void state14() {
            if(AssertType.isNumeric(currentChar)){
                CURRENT_STATE = 1;
            }
            else if(currentChar == 'h'){
                CURRENT_STATE = FINAL_STATE;
                token = Token.CONST;
                type = Type.INT;
            }
            else{
                AssertType.lexemeNotIdentified(lexeme,lines);
            }
        }

        private void state15() {
            if(AssertType.isNumeric(currentChar) || AssertType.isCharacter(currentChar)|| currentChar == '_'){
                CURRENT_STATE = 15;

            }
            else{
                token = Token.IDENTIFIER;
                returnChar();
            }
        }

        private void state16() {
            if(currentChar == '*'){
                CURRENT_STATE = 17;
            }
            else{
                CURRENT_STATE = 16;
            }
        }

        private void state17() {
            if(currentChar != '/'){
                CURRENT_STATE = 16;
            }
            else{
                CURRENT_STATE = INITIAL_STATE;
                lexeme = "";
                readCharacter();
            }
        }

        private void state18(){
            if(currentChar == 'h'){
                CURRENT_STATE = FINAL_STATE;
                token = Token.CONST;
                type = Type.INT;
            }
            else{
                AssertType.lexemeNotIdentified(lexeme,lines);
            }
        }

        private void state19() {
            if(currentChar == 'h'){
                CURRENT_STATE = FINAL_STATE;
                token = Token.CONST;
                type = Type.INT;
            }
            else{
                AssertType.lexemeNotIdentified(lexeme,lines);
            }
        }

        private void state20() {
            if(currentChar == 'h'){
                CURRENT_STATE = FINAL_STATE;
                token = Token.CONST;
                type = Type.INT;
            }
            else{
                AssertType.lexemeNotIdentified(lexeme,lines);
            }
        }

        private boolean checkSymbols(char currentChar) {
            String str = String.valueOf(currentChar);
            if(symbols.contains(str.toString())){
                return true;
            }
            return false;
        }

        private void readCharacter() {

            if (previousChar == null) {
                currentChar = sourceCode[index++];
                if (currentChar == '\n') {
                    lines++;
                }
            }
            else {
                currentChar = previousChar;
                previousChar = null;
            }
        }

        private void returnChar () {
            previousChar = currentChar;
            CURRENT_STATE = FINAL_STATE;
        }
    }

    class AssertType {
        private static final Pattern validCharRegex = Pattern.compile("^[\\s\\n!?,;{}=*()><\\[\\]:+-/\"\'@a-zA-Z0-9%_.-]*$");
        public static final char EOF = (char)-1;

        public static boolean isCharacter(char c) {
            return (c >= 'a' && c <= 'z') || (c >='A' && c <='Z');
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

        public static boolean isValidChar(char c){
            String character = String.valueOf(c);
            Matcher matcher = validCharRegex.matcher(character);
            return matcher.matches();
        }

        public static void printInvalidChar(char c, int lines){
            String errorMessage = c == EOF ? "%d\nfim de arquivo nao esperado." : "%d\ncaractere invalido.";
            System.out.printf(errorMessage,lines);
            System.exit(1);
        }

        public static void lexemeNotIdentified(String  lexeme, int lines){
            System.out.printf("%d\nlexema nao identificado [%s].",lines,lexeme);
            System.exit(1);
        }


        public static void unexpectedToken(String lexeme, int lines) {
            System.out.printf("%d\ntoken nao esperado [%s].",lines,lexeme);
            System.exit(1);
        }
    }


