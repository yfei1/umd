(* CMSC 330 / Spring 2015 / Project 4 *)
(* Name: ?? *)

#load "str.cma"

(* ------------------------------------------------- *)
(* MODULE SIGNATURE *)
(* ------------------------------------------------- *)

module type NFA =
  sig
    (* You may NOT change this signature *)

    (* ------------------------------------------------- *)
    (* PART 1: NFA IMPLEMENTATION *)
    (* ------------------------------------------------- *)

    (* ------------------------------------------------- *)
    (* Abstract type for NFAs *)
    type nfa

    (* Type of an NFA transition.

       (s0, Some c, s1) represents a transition from state s0 to state s1
       on character c

       (s0, None, s1) represents an epsilon transition from s0 to s1
     *)
    type transition = int * char option * int

    (* ------------------------------------------------- *)
    (* Returns a new NFA.  make_nfa s fs ts returns an NFA with start
       state s, final states fs, and transitions ts.
     *)
    val make_nfa : int -> int list -> transition list -> nfa

    (* ------------------------------------------------- *)
    (*  Calculates epsilon closure in an NFA.

	e_closure m ss returns a list of states that m could
	be in, starting from any state in ss and making 0 or
	more epsilon transitions.

       There should be no duplicates in the output list of states.
     *)

    val e_closure : nfa -> int list -> int list

    (* ------------------------------------------------- *)
    (*  Calculates move in an NFA.

	move m ss c returns a list of states that m could
	be in, starting from any state in ss and making 1
	transition on c.

       There should be no duplicates in the output list of states.
     *)

    val move : nfa -> int list -> char -> int list

    (* ------------------------------------------------- *)
    (* Returns true if the NFA accepts the string, and false otherwise *)
    val accept : nfa -> string -> bool

    (* ------------------------------------------------- *)
    (* Gives the stats of the NFA

      the first integer representing the number of states
      the second integer representing the number of final states
      the (int * int) list represents the number of states with a particular number of transitions
      e.g. (0,1) means there is 1 state with 0 transitions, (1,2) means there is 2 states with 1 transition
      the list would look something like: [(0,1);(1,2);(2,3);(3,1)]

    *)

    val stats : nfa -> int * int * (int * int) list

    (* ------------------------------------------------- *)
    (* PART 2: REGULAR EXPRESSION IMPLEMENTATION *)
    (* ------------------------------------------------- *)

    (* ------------------------------------------------- *)
    type regexp =
	Empty_String
      | Char of char
      | Union of regexp * regexp
      | Concat of regexp * regexp
      | Star of regexp

    (* ------------------------------------------------- *)
    (* Given a regular expression, print it as a regular expression in
       postfix notation (as in project 2).  Always print the first regexp
       operand first, so output string will always be same for each regexp.
     *)
    val regexp_to_string : regexp -> string

    (* ------------------------------------------------- *)
    (* Given a regular expression, return an nfa that accepts the same
       language as the regexp
     *)
    val regexp_to_nfa : regexp -> nfa

    (* ------------------------------------------------- *)
    (* PART 3: REGULAR EXPRESSION PARSER *)
    (* ------------------------------------------------- *)

    (* ------------------------------------------------- *)
    (* Given a regular expression as string, parses it and returns the
       equivalent regular expression represented as the type regexp.
     *)
    val string_to_regexp : string -> regexp

    (* ------------------------------------------------- *)
    (* Given a regular expression as string, parses it and returns
       the equivalent nfa
     *)
    val string_to_nfa: string -> nfa

    (* ------------------------------------------------- *)
    (* Throw IllegalExpression expression when regular
       expression syntax is illegal
     *)
    exception IllegalExpression of string

end

(* ------------------------------------------------- *)
(* MODULE IMPLEMENTATION *)
(* ------------------------------------------------- *)

    (* Make all your code changes past this point *)
    (* You may add/delete/reorder code as you wish
       (but note that it still must match the signature above) *)

module NfaImpl =
struct

type transition = int * char option * int

type nfa = int * int list * transition list

let make_nfa ss fs ts = (ss, fs, ts)

let rec e_single const_lst lst hd acc= match lst with
	[] -> []
	|(h, p ,d)::t -> if hd = h && p = None && (List.mem d acc = false) then [d]@(e_single const_lst const_lst d (d::acc))@e_single const_lst t hd (d::acc)
					 else e_single const_lst t hd acc

let rec rm_dup lst acc = match lst with
	[] -> []
	|h::t -> if List.mem h acc then rm_dup t acc 
			 else let facc = h::acc in h::rm_dup t facc

let rec e_closure m ss = match ss with
	[] -> []
	|h1::t1 ->
		match m with (_, _, tlst) ->
			let temp = [h1]@(e_single tlst tlst h1 [])@e_closure m t1 in rm_dup temp []


let move m ss c = match m with (_,_,tlst) -> let ch = Some c in
	let rec move_helper const_tlst tlst ss = match ss with
		[] -> []
		|sh::st -> match tlst with
			[] -> move_helper const_tlst const_tlst st
			|(th, tp, td)::tt -> if th = sh && ch = tp then td::move_helper const_tlst tt ss
								 else move_helper const_tlst tt ss
	in rm_dup (move_helper tlst tlst ss) []


let rec accept_node fs ls = match ls with
	[] -> false
	|h::t -> if List.mem h fs then true else accept_node fs t
 
let accept m s = match m with (ss, fs, ts) ->
	let rec accept_helper new_ss s =
		if String.length s > 0 then (
			let sub = String.sub s 1 ((String.length s) - 1) in 
			let e = e_closure m new_ss in 
			let rst = move m e s.[0] in
			accept_helper rst sub
		) else match new_ss with 
			[] -> false
			|h::t -> let h_span = e_closure m [h] in
			if accept_node fs h_span then true else accept_helper t s
	in accept_helper (e_closure m [ss]) s



let rec num_total ts acc = match ts with
	[] -> []
	|(h, p, d)::t -> if List.mem h acc && List.mem d acc then num_total t acc
					 else if List.mem h acc then d::(num_total t (d::acc))
					 else if List.mem d acc then h::(num_total t (h::acc))
					 else let total = [h;d]@acc in 
					 	let acct = num_total t total in [h;d]@acct

let rec rel ele_l ts_l acc const_ls = match ele_l with
	[] -> []
	|hl::tl -> match ts_l with 
		[] -> (acc,hl)::rel tl const_ls 0 const_ls
		|(h,p,d)::t -> if h = hl then rel ele_l t (acc+1) const_ls else rel ele_l t acc const_ls

let rec comb_map const_lst lst acc num max_val	= if num < (max_val+1) then (
	match lst with 
	[] -> let nxt = comb_map const_lst const_lst 0 (num+1) max_val in
		if acc  = 0 then nxt else (num,acc)::nxt
	|(h,idx)::t -> if h = num then comb_map const_lst t (acc+1) num max_val
				   else comb_map const_lst t acc num max_val
	) else []

let stats n = match n with (ss, fs, ts) ->
	let total = num_total ts [] in
	let rel_map = rel total ts 0 ts in
	let max_val = match (List.nth rel_map (List.length rel_map -1)) with (a,b) -> b in
	let map = comb_map rel_map rel_map 0 0 max_val in
	(List.length total, List.length fs, map)

type regexp =
	  Empty_String
	| Char of char
	| Union of regexp * regexp
	| Concat of regexp * regexp
	| Star of regexp

let rec regexp_to_string r = match r with
	Empty_String -> " E"
	|Char c -> String.make 1 c
	|Union(r1, r2) -> regexp_to_string r1 ^ " " ^ regexp_to_string r2 ^ " |"
	|Concat(r1, r2) -> regexp_to_string r1 ^ " " ^ regexp_to_string r2 ^ " ."
	|Star r -> regexp_to_string r ^ " *"

let next =
    let count = ref 0 in
        function () ->
            let temp = !count in
                count := (!count) + 1;
                temp

let rec lst_none_fs lst fs = match lst with
	[] -> []
	|h::t -> (h, None, fs)::lst_none_fs t fs

let rec regexp_to_nfa r = 		
	let t1 = next() in 
	let t2 = next() in
	match r with
		Empty_String -> make_nfa t1 [t2] [(t1,None,t2)]
		|Char c -> make_nfa t1 [t2] [(t1, Some c, t2)]
		|Union(r1, r2) -> 
			(let u1 = regexp_to_nfa r1 in 
			let u2 = regexp_to_nfa r2 in
			match u1 with (a1,b1,c1) -> 
			match u2 with (a2,b2,c2) ->
				let tl = lst_none_fs (b1@b2) t2 in 
				let total_ts = [(t1, None, a1);(t1, None, a2)]@tl@c1@c2 in
				make_nfa t1 [t2] total_ts
			)
		|Concat(r1, r2) -> 
			(let u1 = regexp_to_nfa r1 in
			let u2 = regexp_to_nfa r2 in
			match u1 with (a1,b1,c1) -> 
			match u2 with (a2,b2,c2) ->
			let total_ts = (lst_none_fs b1 a2)@c1@c2 in
				make_nfa a1 b2 total_ts
			)
		|Star r ->
			(let u = regexp_to_nfa r in
			match u with (a,b,c) ->
			let total_ss = (t1,None,a)::(t1,None,t2)::[(t2,None,t1)] in
			let total_ts = lst_none_fs b t2 in
			let total = total_ss@c@total_ts in
				make_nfa t1 [t2] total
			)
exception IllegalExpression of string

(************************************************************************)
(* PARSER. You shouldn't have to change anything below this point *)
(************************************************************************)

(* Scanner code provided to turn string into a list of tokens *)

type token =
   Tok_Char of char
 | Tok_Epsilon
 | Tok_Union
 | Tok_Star
 | Tok_LParen
 | Tok_RParen
 | Tok_END

let re_var = Str.regexp "[a-z]"
let re_epsilon = Str.regexp "E"
let re_union = Str.regexp "|"
let re_star = Str.regexp "*"
let re_lparen = Str.regexp "("
let re_rparen = Str.regexp ")"

let tokenize str =
 let rec tok pos s =
   if pos >= String.length s then
     [Tok_END]
   else begin
     if (Str.string_match re_var s pos) then
       let token = Str.matched_string s in
       (Tok_Char token.[0])::(tok (pos+1) s)
	 else if (Str.string_match re_epsilon s pos) then
       Tok_Epsilon::(tok (pos+1) s)
	 else if (Str.string_match re_union s pos) then
       Tok_Union::(tok (pos+1) s)
	 else if (Str.string_match re_star s pos) then
       Tok_Star::(tok (pos+1) s)
     else if (Str.string_match re_lparen s pos) then
       Tok_LParen::(tok (pos+1) s)
     else if (Str.string_match re_rparen s pos) then
       Tok_RParen::(tok (pos+1) s)
     else
       raise (IllegalExpression "tokenize")
   end
 in
 tok 0 str

(*
  A regular expression parser. It parses strings matching the
  context free grammar below.

   S -> A Tok_Union S | A
   A -> B A | B
   B -> C Tok_Star | C
   C -> Tok_Char | Tok_Epsilon | Tok_LParen S Tok_RParen

   FIRST(S) = Tok_Char | Tok_Epsilon | Tok_LParen
   FIRST(A) = Tok_Char | Tok_Epsilon | Tok_LParen
   FIRST(B) = Tok_Char | Tok_Epsilon | Tok_LParen
   FIRST(C) = Tok_Char | Tok_Epsilon | Tok_LParen
 *)

let lookahead tok_list = match tok_list with
	[] -> raise (IllegalExpression "lookahead")
	| (h::t) -> (h,t)

let rec parse_S l =
	let (a1,l1) = parse_A l in
	let (t,n) = lookahead l1 in
	match t with
		Tok_Union -> (
		let (a2,l2) = (parse_S n) in
		(Union (a1,a2),l2)
		)
		| _ -> (a1,l1)

and parse_A l =
	let (a1,l1) = parse_B l in
	let (t,n) = lookahead l1 in
	match t with
	Tok_Char c ->
		let (a2,l2) = (parse_A l1) in (Concat (a1,a2),l2)
	| Tok_Epsilon ->
		let (a2,l2) = (parse_A l1) in (Concat (a1,a2),l2)
	| Tok_LParen ->
		let (a2,l2) = (parse_A l1) in (Concat (a1,a2),l2)
	| _ -> (a1,l1)

and parse_B l =
	let (a1,l1) = parse_C l in
	let (t,n) = lookahead l1 in
	match t with
	Tok_Star -> (Star a1,n)
	| _ -> (a1,l1)

and parse_C l =
	let (t,n) = lookahead l in
	match t with
   	  Tok_Char c -> (Char c, n)
	| Tok_Epsilon -> (Empty_String, n)
	| Tok_LParen ->
		let (a1,l1) = parse_S n in
		let (t2,n2) = lookahead l1 in
		if (t2 = Tok_RParen) then
			(a1,n2)
		else
			raise (IllegalExpression "parse_C 1")
	| _ -> raise (IllegalExpression "parse_C 2")

let string_to_regexp str =
	let tok_list = tokenize str in
	let (a,t) = (parse_S tok_list) in
	match t with
	[Tok_END] -> a
	| _ -> raise (IllegalExpression "string_to_regexp")

let string_to_nfa s = regexp_to_nfa (string_to_regexp s)

end

module Nfa : NFA = NfaImpl;;
