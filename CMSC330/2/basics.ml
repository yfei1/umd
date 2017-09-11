(* CMSC 330 / Project 2a *)
(* Student: MY NAME *)

(* Fill in the implementation and submit basics.ml *)


(****************************)
(* Part A: Simple functions *)
(****************************)

(* Implement a function mult_of_five: int -> bool that returns
   whether the argument given is a multiple of five. *)
let mult_of_five x = 
 x mod 5 == 0
;;
(* Implement a function sum_upto_three : int list -> int that sums its
   prefix, up to three elements. That is, if the list is three or
   more elements, this function should return the sum of the first
   three; if it has two or fewer, it should sum all elements in the
   list, returning 0 for the empty list. *)
let sum_upto_three ls = 
 match ls with
 []-> 0
 | f::s::t::rest -> f+s+t
 | f::s::rest -> f+s
 | f::rest -> f
;;
(* Implement a caddr_int : int list -> int that returns the second element
   of the list, if the list has two or more elements, and returns -1 if
   the list has zero or one elements. *)
let caddr_int l = match l with
 f::s::rest -> s
 | _ -> -1
;;
      

(*************************************)
(* Part B: Simple curried functions. *)
(*************************************)

  
(* A curried function is one that takes multiple arguments "one at a
   time". For example, the following function sub takes two arguments and
   computes their difference:

   let sub x y = x - y

   The type of this function is int -> int -> int. Technically, this
   says that sub is a function that takes an int and returns a
   function that takes another int and finally returns the answer,
   also an int. In other words, we could write

   sub 2 1

   and this will produce the answer 1. But we could also do something
   like this:

   let f = sub 2 in
   f 1

   and this will also produce 1. Notice how we call sub with only one
   argument, so it returns a function f that takes the second
   argument. In general, you can think of a function f of the type 

   t1 -> t2 -> t3 -> ... -> tn

   as a function that takes n-1 arguments of types t1, t2, t3, ...,
   tn-1 and produces a result of type tn. Such functions are written
   with OCaml syntax

   let f a1 a2 a3 ... = body

   where a1 has type t1, a2 has type t2, etc.
*)

(* Implement a function mult_of_n: int -> int -> bool. Calling
   mutl_of_n x y returns true if x is a multiple of y, and false
   otherwise. For example, mult_of_n 5 5 = true and mult_of_n 21 5 =
   false. Note that mult_of_n x 0 = false for all x. *)
let mult_of_n x n = 
 if n == 0 then false
 else x mod n == 0
;;

(* Implement a function triple_it: 'a -> 'b -> 'c -> 'a*'b*'c. Calling
   triple_it on arguments x, y and z, should return a tuple with those
   three arguments, e.g., triple_it 1 2 3 = (1,2,3) *)
let triple_it x y z = 
 (x,y,z)
;;
      
(* Write a function maxpair : int*int -> int*int -> int*int that takes
   two pairs of integers, and returns the pair that is larger,
   according to lexicographic ordering. For example, maxpair (1,2)
   (3,4) = (3,4), and maxpair (1,2) (1,3) = (1,3).
*)    
let maxpair (x,y) (m,n) = 
 if x < m then (m, n)
 else if x > m then (x,y)
  else if y < n then (m, n)
   else (x,y)  
;;


(*******************************)
(* Part C: Recursive functions *)
(*******************************)

      
(* Write a function prod : int list -> int. Calling prod l returns the
   product of the elements of l. The function prod should return 1 if
   the list is empty. *)
let rec prod l = match l with
 [] -> 1
 | h::t -> h*prod t
;;
(* Write a function unzip : ('a*'b) list -> ('a list)*('b
   list). Calling unzip l, where l is a list of pairs, returns a pair
   of lists with the elements in the same order. For example, unzip
   [(1, 2); (3, 4)] = ([1; 3], [2;4]) and unzip [] = ([],[]).
*)
let rec decomp l a b = match l with
  [] -> (a,b)
  |(f,s)::t -> (
   let a = a@[f] in
   let b = b@[s] in
   decomp t a b
  );; 

let rec unzip l=
 let a = [] and b = [] in
  decomp l a b
;;


(* Write a function maxpairall : int*int list -> int*int. Calling
   maxpairall l returns the largest pair in the list l, according to
   lexicographic ordering. If the list is empty, it should return
   (0,0).  For example, maxpairall [(1,2);(3,4)] = (3,4) and
   maxpairall [(1,2);(2,1);(3,1)] = (3,1).
*)
let rec maxpairall l = match l with
 [] -> (0,0)
 | [(a,b)] -> (a,b)
 | (a,b)::(c,d)::t -> 
     maxpairall ((maxpair (a,b) (c,d))::t)
;;

(* Write a function addTail : 'a list -> 'a -> 'a list. Calling
   addTail l e returns a new list where e is appended to the back of
   l. For example, addTail [1;2] 3 = [1;2;3]. *)
let rec addTail l x = match l with
 [] -> [x]
 | h::t -> h::addTail t x
;;

(* 
get_val x n 	
int list -> int -> int 	
element of list x at index n, or -1 if not found
  (indexes start at 0)
Example: get_val [5;6;7;3] 1 => 6
*)
let rec get_val x n =
 match x with
 |[]-> -1
 |h::t -> if n= 0 then h else get_val t (n-1)
;;


(* 
get_vals x y 	
int list -> int list -> int list 	
list of elements of list x at indexes in list y, 
-1 for any indexes outside the bounds of x
elements must be returned in order listed in y 
Example: get_vals [5;6;7;3] [2;0] => [7;5]
Example: get_vals [5;6;7;3] [2;4] => [7;-1]
*)
let rec get_vals b n = match n with
 |[] ->[]
 | h::t-> (get_val b h)::get_vals b t
;;

(* 
list_swap_val b u v
'a list -> 'a -> 'a -> 'a list 	
list b with values u,v swapped 	
(change value of multiple occurrences of u and/or v, if found, and
change value for u even if v not found in list, and vice versa )
Example: list_swap_val [5;6;7;3] 7 5 => [7;6;5;3]
*)
let rec list_swap_val b u v = match b with
 [] -> []
 | h::t-> if h== u then v::list_swap_val t u v
          else if h ==v then u::list_swap_val t u v
          else h::list_swap_val t u v
;;


(* Write a function index : 'a list -> 'a -> int. Calling index l e
   returns the index in l of the rightmost occurrence of e, or -1 if e
   is not present. The first element has index 0. For example, index
   [1;2;2] 1 = 0 and index [1;2;2;3] 2 = 2 and index [1;2;3] 4 = -1.

   Hint: it's easiest to write a helper function, but you can also do
   it without one.
*)
let rec index l e =
 match l with
   |[] -> -1
   |h::t -> let num = index t e in
     if num = -1 && h = e then 0
     else if num = -1 && h <> e then -1
     else 1+num
;;      

(* 
distinct x
'a list -> 'a list 
return list of distinct members of list x 
*)
let rec distinct_helper e dsls = match dsls with
 []-> []
 |h::t -> if h = e then distinct_helper e t else h::distinct_helper e t
;;

let rec distinct l = match l with
 [] -> []
 |h::t -> h::distinct_helper h (distinct t)
;; 
	
(* 
find_new x y
'a list -> 'a list -> 'a list 	
list of members of list x not found in list y 	
maintain relative order of elements in result
Example: find_new [4;3;7] [5;6;5;3] => [4;7]

Hint: You might want to use a helper function for this.
*)
let rec find_new x y = match x with
 []-> []
 |h::t-> let rec find_new_one h y = match y with
       [] -> [h]
       |hs::ts-> if hs <> h then find_new_one h ts else[]
   in
   find_new_one h y @find_new t y
;;  

(* 
is_sorted x 	
'a list -> bool 	
true if elements in x are in sorted order, false otherwise 	
  (return true for [])
Example: is_sorted [5;5;7;9] => true 
*)
let rec is_sorted x = match x with
 [] -> true
 | [x] -> true
 | a::b::t -> if a<=b then (is_sorted (b::t)) else false
;;
