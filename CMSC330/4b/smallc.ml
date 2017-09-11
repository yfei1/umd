(*  
	This ocaml code reads a C code and properly indents it
	compile:
	ocamlc -g Str.cma indent.ml 
	@author: Anwar Mamat
	@date: 03/15/2016
	
*)

#load "str.cma"

type data_type =
	|Type_Int
;;

(* Use this as your abstract syntax tree *)

type ast =
  | Id of string
  | Num of int
  | Define of data_type * ast
  | Assign of ast * ast
  | List of ast list
  | Fun of data_type * string * ast * ast   (* return type * function name * argument list * statement list *)
  | Sum of ast * ast
  | Greater of ast * ast
  | Equal of ast * ast
  | Less of ast * ast
  | Mult of ast * ast
  | Pow of  ast * ast
  | Print of ast
  | If of ast * ast * ast	(* cond * if brach * else branch *)
  | While of ast * ast
  | Paren of ast
  
;;

type token =
 | Tok_Id of string
 | Tok_Num of int
 | Tok_String of string
 | Tok_Assign
 | Tok_Greater
 | Tok_Less
 | Tok_Equal
 | Tok_LParen
 | Tok_RParen
 | Tok_Semi
 | Tok_Main
 | Tok_LBrace
 | Tok_RBrace
 | Tok_Int 
 | Tok_Float
 | Tok_Sum
 | Tok_Mult
 | Tok_Pow
 | Tok_Print
 | Tok_If
 | Tok_Else
 | Tok_While
 | Tok_END
 
(* tokens *)
let re_lparen = Str.regexp "("
let re_rparen = Str.regexp ")"
let re_lbrace = Str.regexp "{"
let re_rbrace = Str.regexp "}"
let re_assign = Str.regexp "="
let re_greater = Str.regexp ">"
let re_less = Str.regexp "<"
let re_equal = Str.regexp "=="
let re_semi = Str.regexp ";"
let re_int = Str.regexp "int"
let re_float = Str.regexp "float"
let re_printf = Str.regexp "printf"
let re_main = Str.regexp "main"
let re_id = Str.regexp "[a-zA-Z][a-zA-Z0-9]*"
let re_num = Str.regexp "[-]?[0-9]+"
let re_string = Str.regexp "\"[^\"]*\""
let re_whitespace = Str.regexp "[ \t\n]"
let re_add = Str.regexp "+"
let re_mult = Str.regexp "*"
let re_pow = Str.regexp "\\^"
let re_if = Str.regexp "if"
let re_else = Str.regexp "else"
let re_while = Str.regexp "while"


exception Lex_error of int
exception Parse_error of int
exception IllegalExpression of string

let tokenize s =
 let rec tokenize' pos s =
   if pos >= String.length s then
     [Tok_END]
   else begin
     if (Str.string_match re_lparen s pos) then
       Tok_LParen::(tokenize' (pos+1) s)
     else if (Str.string_match re_rparen s pos) then
       Tok_RParen::(tokenize' (pos+1) s)
     else if (Str.string_match re_add s pos) then
       Tok_Sum::(tokenize' (pos+1) s)
     else if (Str.string_match re_mult s pos) then
       Tok_Mult::(tokenize' (pos+1) s)
     else if (Str.string_match re_equal s pos) then
       Tok_Equal::(tokenize' (pos+2) s)
     else if (Str.string_match re_if s pos) then
       Tok_If::(tokenize' (pos+2) s)
     else if (Str.string_match re_else s pos) then
       Tok_Else::(tokenize' (pos+4) s)    
     else if (Str.string_match re_while s pos) then
       Tok_While::(tokenize' (pos+5) s)       
	else if (Str.string_match re_pow s pos) then
       Tok_Pow::(tokenize' (pos+1) s)
    else if (Str.string_match re_printf s pos) then
       Tok_Print::tokenize' (pos+6) s
    else if (Str.string_match re_lbrace s pos) then
       Tok_LBrace::(tokenize' (pos+1) s)
    else if (Str.string_match re_rbrace s pos) then
       Tok_RBrace::(tokenize' (pos+1) s)
    else if (Str.string_match re_assign s pos) then
       Tok_Assign::(tokenize' (pos+1) s)
    else if (Str.string_match re_greater s pos) then
       Tok_Greater::(tokenize' (pos+1) s)
    else if (Str.string_match re_less s pos) then
       Tok_Less::(tokenize' (pos+1) s)
    else if (Str.string_match re_semi s pos) then
       Tok_Semi::(tokenize' (pos+1) s)
    else if (Str.string_match re_int s pos) then
       Tok_Int::(tokenize' (pos+3) s)
    else if (Str.string_match re_float s pos) then
       Tok_Float::(tokenize' (pos+5) s)
    else if (Str.string_match re_main s pos) then
       Tok_Main::(tokenize' (pos+4) s)
     else if (Str.string_match re_id s pos) then
       let token = Str.matched_string s in
       let new_pos = Str.match_end () in
       (Tok_Id token)::(tokenize' new_pos s)
     else if (Str.string_match re_string s pos) then
       let token = Str.matched_string s in
       let new_pos = Str.match_end () in
       let tok = Tok_String (String.sub token 1 ((String.length token)-2)) in
       tok::(tokenize' new_pos s)
     else if (Str.string_match re_num s pos) then
       let token = Str.matched_string s in
       let new_pos = Str.match_end () in
       (Tok_Num (int_of_string token))::(tokenize' new_pos s)
     else if (Str.string_match re_whitespace s pos) then
       tokenize' (Str.match_end ()) s
     else
       raise (Lex_error pos)
   end
 in
 tokenize' 0 s
 ;;
 
 (* C Grammar *)
 (* 
 
 basicType-> 'int'
  mainMethod-> basicType 'main' '(' ')' '{' methodBody '}'
  methodBody->(localDeclaration | statement)*
  localDeclaration->basicType ID ';'
  statement->
    whileStatement
    |ifStatement
    |assignStatement
    |printStatement
  
  assignStatement->ID '=' exp ';'
  ifStatement -> 'if' '(' exp ')'  '{' ( statement)* '}'  ( 'else' '{'( statement)* '}')?
  whileStatement -> 'while''(' exp ')' '{'(statement )*'}'
  printStatement->'printf' '(' exp ')' ';'
  exp -> additiveExp (('>'  | '<'  | '==' ) additiveExp )*
  additiveExp -> multiplicativeExp ('+' multiplicativeExp)*
  multiplicativeExp-> powerExp ( '*' powerExp  )*
  powerExp->primaryExp ( '^' primaryExp) *
  primaryExp->'(' exp ')' | ID 
  ID->( 'a'..'z' | 'A'..'Z') ( 'a'..'z' | 'A'..'Z' | '0'..'9')*
  WS-> (' '|'\r'|'\t'|'\n') 



*)

(*----------------------------------------------------------
  function lookahead : token list -> (token * token list)
	Returns tuple of head of token list & tail of token list
*)

let lookahead tok_list = match tok_list with
        [] -> raise (IllegalExpression "lookahead")
        | (h::t) -> (h,t)
;;        



(* -------------- Your Code Here ----------------------- *)
let rec lookahead_ntimes n tok_list = match tok_list with
	[] -> failwith "sb"
	|h::t -> 
		if n <> 1 then (lookahead_ntimes (n-1) t) else (h, t)
;;


let rec parse_Primexp lst = 
	let (h, hd_exp) = lookahead lst in
	match h with
		Tok_LParen  -> 
			let (ast_exp, end_exp) = parse_Exp hd_exp in
			let (_, end_primexp) = lookahead end_exp in
				(Paren(ast_exp), end_primexp)
		|Tok_Id(s)  ->
				(Id(s), hd_exp)
		|Tok_Num(n) ->
				(Num(n), hd_exp)
		|_ -> failwith "sb"

and parse_Powexp lst = 
	let (ast_primexp, end_primexp) = parse_Primexp lst in
	let (oprd, hd_exp2) = lookahead end_primexp in
    match oprd with 
        Tok_Pow -> 
        	let (ast_exp2, end_exp2) = parse_Powexp hd_exp2 in
        	(Pow(ast_primexp, ast_exp2), end_exp2)
        |_       -> (ast_primexp, end_primexp)

and parse_Multexp lst = 
	let (ast_powexp, end_powexp) = parse_Powexp lst in
	let (oprd, hd_exp2) = lookahead end_powexp in
    match oprd with 
        Tok_Mult -> 
        	let (ast_exp2, end_exp2) = parse_Multexp hd_exp2 in
        	(Mult(ast_powexp, ast_exp2), end_exp2)
        |_       -> (ast_powexp, end_powexp)

and parse_Addexp lst = 
	let (ast_multexp, end_multexp) = parse_Multexp lst in
	let (oprd, hd_exp2) = lookahead end_multexp in
    match oprd with 
        Tok_Sum -> 
        	let (ast_exp2, end_exp2) = parse_Addexp hd_exp2 in
        	(Sum(ast_multexp, ast_exp2), end_exp2)
        |_       -> (ast_multexp, end_multexp)
        
and parse_Exp lst = 
	let (ast_addexp, end_addexp) = parse_Addexp lst in
	let (oprd, hd_exp2) = lookahead end_addexp in
	match oprd with
	   	 Tok_Equal  -> 
	   	 	let (ast_exp2, end_exp2) = parse_Exp hd_exp2 in 
	   	 	(Equal(ast_addexp, ast_exp2), end_exp2)
		|Tok_Less    ->
	   	 	let (ast_exp2, end_exp2) = parse_Exp hd_exp2 in 
	   	 	(Less(ast_addexp, ast_exp2), end_exp2)
		|Tok_Greater ->
	   	 	let (ast_exp2, end_exp2) = parse_Exp hd_exp2 in 
	   	 	(Greater(ast_addexp, ast_exp2), end_exp2)
		|_           -> (ast_addexp, end_addexp)		
;;


let rec parse_Assign lst = 
	let (ids, b1) = lookahead lst in
		match ids with Tok_Id(s) ->										(*b1 is Tok_Assign*)
			let (_, b2) = lookahead b1 in							(*b2 is head of exp*)
			let (a2, b3) = parse_Exp b2 in							(*a2 is ast for the exp*)
			let (h, t) = lookahead b3 in
				(Assign(Id(s), a2), t)
		|_ -> failwith "sb"
;;

let rec parse_Print lst = 
	let (_, hd_exp) = lookahead_ntimes 2 lst in 
	let (ast_exp, end_exp) = parse_Exp hd_exp in
	let (_, end_print) = lookahead_ntimes 2 end_exp in 
	(Print(ast_exp), end_print)
;;


let rec parse_While lst = 
	let (_, exp) = lookahead_ntimes 2 lst in
	let (a1, b1) = parse_Exp exp in							(*a1 is cond*)
	let (a2, b2) = lookahead_ntimes 2 b1 in
	let (a3, b3) = parse_Stmlst b2 in						(*a3 is while body*)
	let (h, t) = lookahead b3 in
	(While(a1, a3), t)

and parse_If lst = 
	let (_, hd_exp) = lookahead_ntimes 2 lst in
	let (ast_exp, end_exp) = parse_Exp hd_exp in
	let (_, hd_stmt1) = lookahead_ntimes 2 end_exp in 
	let (ast_stmt1, end_stmt1) = parse_Stmlst hd_stmt1 in
	let (els, end_els) = lookahead_ntimes 2 end_stmt1 in
		match els with 
			Tok_Else ->
				let (_, hd_stmt2) = lookahead_ntimes 3 end_stmt1 in
				let (ast_stmt2, end_stmt2) = parse_Stmlst hd_stmt2 in
				let (_, end_els) = lookahead end_stmt2 in
					(If(ast_exp, ast_stmt1, ast_stmt2), end_els)
			|_ -> let (rbrace, endif) = lookahead end_stmt1 in
				(If(ast_exp, ast_stmt1, List([])), endif)

and parse_Stmlst lst = 	

	let (h1, t1) = lookahead lst in match h1 with
	Tok_While -> (
		let (a1, b1) = parse_While lst in 
		let (lst_a2, b2) = parse_Stmlst b1 in
		match lst_a2 with 
			List(a2) -> (List(a1::a2), b2)
			|_ -> failwith "sb"
	)
   |Tok_Id(s)   -> (
		let (a1, b1) = parse_Assign lst in 
		let (lst_a2, b2) = parse_Stmlst b1 in
		match lst_a2 with 
			List(a2) -> (List(a1::a2), b2)
			|_ -> failwith "sb"
	)
   |Tok_If    -> (
   		let (a1, b1) = parse_If lst in 
		let (lst_a2, b2) = parse_Stmlst b1 in
		match lst_a2 with 
			List(a2) -> (List(a1::a2), b2)
			|_ -> failwith "sb"
	)
   |Tok_Print -> (
   		let (a1, b1) = parse_Print lst in 
		let (lst_a2, b2) = parse_Stmlst b1 in
		match lst_a2 with 
			List(a2) -> (List(a1::a2), b2)
			|_ -> failwith "sb"
	)
   |_ ->(List([]), lst)
;;


let rec parse_Decla lst = 
	let (dt_type, t1) = lookahead lst in
	let (ids, t2) = lookahead t1 in
	match ids with Tok_Id(s) ->
	let (_, t3) = lookahead t2 in
		(Define(Type_Int, Id(s)), t3)
	|_ -> failwith "sb"
;;

let rec parse_Body lst = match lst with
	[] -> failwith "sb"
	|h::t -> if h = Tok_RBrace then (List([]), lst)
			else if h = Tok_Int then (* A -> BA *)
				let (a1, b1) = parse_Decla lst in
				let (lst_a2, b2) = parse_Body b1 in
					match lst_a2 with List(a2) -> (List(a1::a2), b2) 
					|_ -> failwith "sb"
			else 
				let (lst_a3, b3) = parse_Stmlst lst in
				let (lst_a4, b4) = parse_Body b3 in
				match lst_a3 with List(a3) -> (
					match lst_a4 with List(a4) -> (List(a3@a4), b4)
					|_ -> failwith "sb"
				)
				|_ -> failwith "sb"
					
;;

let rec parse_Function lst = 
	let (dt_type, t1) = lookahead lst in
	let (nm_func, t2) = lookahead t1  in
	let  (_, t3) = lookahead_ntimes 3 t2 in
	let (a1, b1) = parse_Body t3 in
	(Fun(Type_Int, "main", List([]), a1), b1)
;;

			
let rec parse_Exp lst = 
	let (ast_addexp, end_addexp) = parse_Addexp lst in
	let (oprd, hd_exp2) = lookahead end_addexp in
	match oprd with
	   	 Tok_Equal  -> 
	   	 	let (ast_exp2, end_exp2) = parse_Exp hd_exp2 in 
	   	 	(Equal(ast_addexp, ast_exp2), end_exp2)
		|Tok_Less    ->
	   	 	let (ast_exp2, end_exp2) = parse_Exp hd_exp2 in 
	   	 	(Less(ast_addexp, ast_exp2), end_exp2)
		|Tok_Greater ->
	   	 	let (ast_exp2, end_exp2) = parse_Exp hd_exp2 in 
	   	 	(Greater(ast_addexp, ast_exp2), end_exp2)
		|_           -> (ast_addexp, end_addexp)		
;;				

		
(* ------------------------------------------------------*)





exception Error of int ;;




let read_lines name : string list =
  let ic = open_in name in
  let try_read () =
    try Some (input_line ic) with End_of_file -> None in
  let rec loop acc = match try_read () with
    | Some s -> loop (s :: acc)
    | None -> close_in ic; List.rev acc in
  loop []


let tok_to_str t = ( match t with
          Tok_Num v -> string_of_int v
        | Tok_Sum -> "+"
        | Tok_Mult ->  "*"
        | Tok_LParen -> "("
        | Tok_RParen -> ")"
		| Tok_Pow->"^"
        | Tok_END -> "END"
        | Tok_Id id->id
		| Tok_String s->s
		| Tok_Assign->"="
		 | Tok_Greater->">"
		 | Tok_Less->"<"
		 | Tok_Equal->"=="
		 | Tok_Semi->";"
		 | Tok_Main->"main"
		 | Tok_LBrace->"{"
		 | Tok_RBrace->"}"
		 | Tok_Int->"int" 
		 | Tok_Float->"float"
		 | Tok_Print->"printf"
		 | Tok_If->"if"
		 | Tok_Else->"else"
		 | Tok_While-> "while"
    )

let print_token_list tokens =
	print_string "Input token list = " ;
	List.iter (fun x -> print_string (" " ^ (tok_to_str x))) tokens;
	print_endline ""
;;
	




(* -------------- Your Code Here ----------------------- *)

let rec print_ws n = if n = 0 then "" else " "^print_ws (n-1);;

let rec pretty_print pos x= let ws = print_ws pos in
	match x with
	Fun(dt_tp, str, ast1, ast2) ->print_string("int main(){\n");
								  pretty_print (pos+4) ast2;
								  print_string  ("}\n")
	|Define(dt_tp, Id(s)) -> print_string(ws ^ "int " ^ s ^ ";\n")
	|Assign(Id(s), ast2) -> print_string (ws ^ s ^ " = ");
							pretty_print pos ast2;
							print_string(";\n")
	|List(ast_lst) -> (
		match ast_lst with 
			[] -> print_string("")
			|h::t -> 
					 pretty_print pos h;
					 pretty_print pos (List(t));
					 ()
		)
	|Sum(ast1, ast2) -> pretty_print pos ast1;
						print_string " + ";
						pretty_print pos ast2
	|Greater(ast1, ast2) -> pretty_print pos ast1;
							print_string " > ";
							pretty_print pos ast2
	|Equal(ast1, ast2) -> pretty_print pos ast1;
						  print_string " == ";
						  pretty_print pos ast2
	|Less(ast1, ast2) -> pretty_print pos ast1;
						 print_string " < ";
						 pretty_print pos ast2
	|Mult(ast1, ast2) -> pretty_print pos ast1;
						 print_string " * ";
						 pretty_print pos ast2
	|Pow(ast1, ast2) -> pretty_print pos ast1;
						print_string " ^ ";
						pretty_print pos ast2
	|Print(ast) -> print_string ws;
				   print_string "printf(";
				   pretty_print pos ast;
				   print_string ");\n"
	|If(ast1, ast2, ast3) -> (
		match ast3 with List(lst) -> (
			match lst with
				[]-> print_string ws;
	    			 print_string "if(";
	    			 pretty_print pos ast1;
	    			 print_string "){\n";
	    			 pretty_print (pos+4) ast2;
	    			 print_string ws;
	    			 print_string "}\n"
				|_ -> print_string ws;
	    			  print_string "if(";
	    			  pretty_print pos ast1;
	    			  print_string "){\n";
	    			  pretty_print (pos+4) ast2;
	    			  print_string ws;
	    			  print_string "}";
				      print_string "else{\n";
				      pretty_print (pos+4) ast3;
				      print_string ws;
				      print_string "}\n"
		)
		|_ -> failwith "sb"
	)
	|While(ast1, ast2) -> print_string ws;
						  print_string "while(";
						  pretty_print pos ast1;
						  print_string "){\n";
						  pretty_print (pos+4) ast2;
						  print_string ws;
						  print_string "}\n"
	|Paren(ast) -> print_string "(";
				   pretty_print pos ast;
				   print_string ")"
	|Id(s) -> print_string s
	|Num(n) -> print_string(string_of_int n)
	|_ -> failwith "sb"
;;



(* ----------------------------------------------------- *)


(*
you can test your parser and pretty_print with following code 
*)

(*

let prg1 = read_lines "main.c";;
let code = List.fold_left (fun x y->x^y) "" prg1;;	
let t = tokenize code;;
let (a,b)=parse_Function t;;

*)