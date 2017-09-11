type val_type = 
	|None
	|Int of int
;;

let rec eval env x =  match x with
	Fun(dt_tp, str, ast1, ast2) -> eval env ast2
	|Define(dt_tp, Id(s)) -> if (Hashtbl.mem env s) then 
								raise (IllegalExpression "Double define")								
							else
								((Hashtbl.add env s None);
								env) 
	|Assign(Id(s), ast2) -> if (Hashtbl.mem env s) then
								(Hashtbl.replace env s (Int(eval_expr ast2 env));env)
							else
								raise (IllegalExpression "Use undeclared variable")
	|List(ast_lst) -> (match ast_lst with 
							h::t -> (let a = (eval env h) in
									eval a (List(t)))
							|[] -> env
	)
	|Print(ast) -> print_string (string_of_int (eval_expr ast env) ^ "\n");
					env
	|If(ast1, ast2, ast3) -> if eval_expr ast1 env == 1 then (eval env ast2) else (eval env ast3)
	|While(ast1, ast2) -> if eval_expr ast1 env == 1 then 
								(let a = (eval env ast2) in
								eval a x)
							else 
								env
	|Paren(ast) -> eval env ast
	|_ -> failwith "sb"
and eval_expr expr env= match expr with
	|Sum(ast1, ast2) -> (eval_expr ast1 env) + (eval_expr ast2 env)
	|Greater(ast1, ast2) -> if (eval_expr ast1 env) > (eval_expr ast2 env) then 1 else (-1)
	|Equal(ast1, ast2) -> if (eval_expr ast1 env) = (eval_expr ast2 env) then 1 else (-1)
	|Less(ast1, ast2) -> if (eval_expr ast1 env) < (eval_expr ast2 env) then 1 else (-1)
	|Mult(ast1, ast2) -> (eval_expr ast1 env) * (eval_expr ast2 env)
	|Pow(ast1, ast2) -> int_of_float ((float_of_int (eval_expr ast1 env))**(float_of_int (eval_expr ast2 env)))
	|Id(s) -> (match (Hashtbl.find env s) with Int(n) -> n |_ -> failwith "sb")
	|Num(n) -> n
	|Paren(ast) -> eval_expr ast env
	|_ -> failwith "sb"
;;