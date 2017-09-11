/*---------------------------------------------------------------

   CMSC 330 Project 6 - Maze Solver and SAT in Prolog

   NAME: Yufan Fei

*/


%%%%%%%%%%%%%%%%%%%%%%
% Part 1 - Recursion %
%%%%%%%%%%%%%%%%%%%%%%

% ackermann - M and N are the two arguments, and R is the result. Cf http://mathworld.wolfram.com/AckermannFunction.html for the definition of the ackermann function

ackermann(0, Y, R):- R is Y+1,!.
ackermann(X, 0, R):- 
    X1 is X - 1,
    ackermann(X1, 1, R),!.
ackermann(X,Y,R) :-
    X1 is X - 1,
    Y1 is Y - 1,
    ackermann(X,Y1,Yprime),
    ackermann(X1, Yprime, R).

% prod - R is product of entries in list L

prod([],1).
prod([H|L], R) :-
    prod(L, R1),
    R is H*R1.

% fill - R is list of N copies of X

fill(0,_,[]):- !.
fill(N,X,[X|R]) :-
    N1 is N-1,
    fill(N1,X,R).

% genN - R is value between 0 and N-1, in order

genN(N,R):-
    N1 is N-1,
    N1 > 0,
    genN(N1,R1),
    R is R1.
genN(N,R):- R is N-1.

% genXY - R is pair of values [X,Y] between 0 and N-1, in lexicographic order

genXY(N,[H,T]) :- 
    genN(N, H),
    genN(N, T).

% flat(L,R) - R is elements of L concatentated together, in order

flat([],[]).
flat([[]],[]).
flat([[]|TL], R):- flat(TL, R).
flat([[H|T]|TL],[H|R]) :- 
    flat([T|TL], R),!.
flat([H|TL], [H|R]) :-
    flat(TL, R).

% is_prime(P) - P is an integer; predicate is true if P is prime.

is_prime(2):-!.
is_prime(3):-!.
is_prime(P) :- 
    sqrt(P,R),
    prime(2,P,R).
prime(N,P,R):-
    R1 is R-N,
    R1 < 1,
    A is P/N,
    float(A),!.
prime(N,P,R):-
    A is P/N,
    float(A),
    N1 is N+1,
    R >= N1,
    prime(N1,P,R).

% in_lang(L) - L is a list of atoms a and b; predicate is true L is in the language accepted by the following CFG:
/*    
CFG 
S -> T | V
T -> UU
U -> aUb | ab
V -> aVb | aWb
W -> bWa | ba
*/

in_lang(L) :- t(L),!.
in_lang(L) :- v(L).
t(L):- append(X,Y,L),u(X),u(Y).
u([a,b]):-!.
u([a|L]):- append(X,[b],L), u(X).
v([a|L]):- append(X,[b],L), v(X),!.
v([a|L]):- append(X,[b],L), w(X).
w([b,a]):-!.
w([b|L]):- append(X,[a],L), w(X).
 
%%%%%%%%%%%%%%%%%%%%%%%%
% Part 2 - Maze Solver %
%%%%%%%%%%%%%%%%%%%%%%%%

% stats(U,D,L,R) - number of cells w/ openings up, down, left, right

mem(A,X,LST):- member(A, LST), X is 1,!.
mem(A,X,LST):- not(member(A,LST)), X is 0.
stats(U,D,L,R) :- 
    findall(X, cell(_,_,X,_),A),
    flat(A,LST),
    cum(LST,U,D,L,R),!.
cum([u|T],U,D,L,R):- cum(T,U1,D,L,R),U is U1+1.
cum([d|T],U,D,L,R):- cum(T,U,D1,L,R),D is D1+1.
cum([l|T],U,D,L,R):- cum(T,U,D,L1,R),L is L1+1.
cum([r|T],U,D,L,R):- cum(T,U,D,L,R1),R is R1+1.    
cum([],0,0,0,0).    
    
% validPath(N,W) - W is weight of valid path N rounded to 4 decimal places

validPath(N,W) :- 
    path(N,SX,SY,LST),
    validPath(SX,SY,LST,TW),round4(TW,W).
validPath(_,_,[],0):-!.
validPath(X,Y,[H|T],W):- validCell(X,Y,NX,NY,H,SW),validPath(NX,NY,T,NW),W is NW+SW.
validCell(X,Y,NX,NY,u,W):-cell(X,Y,DL,WL),mem(u,W,DL,WL), NX is X, NY is Y-1,!. 
validCell(X,Y,NX,NY,d,W):-cell(X,Y,DL,WL),mem(d,W,DL,WL), NX is X, NY is Y+1,!.
validCell(X,Y,NX,NY,l,W):-cell(X,Y,DL,WL),mem(l,W,DL,WL), NX is X-1, NY is Y,!.
validCell(X,Y,NX,NY,r,W):-cell(X,Y,DL,WL),mem(r,W,DL,WL), NX is X+1, NY is Y,!.
mem(C,W,[DH|_],[WH|_]):- C = DH, W is WH,!.
mem(C,W,[DH|DT],[_|WT]):- C \=DH, mem(C,W,DT,WT).
mem(_,_,[],[]):- fail.

round4(X,Y) :- T1 is X*10000, T2 is round(T1), Y is T2/10000.

% findDistance(L) - L is list of coordinates of cells at distance D from start

findDistance(L) :- maze(_,SX,SY,_,_),addN(0,[[SX,SY]],L,[[SX,SY]]),!.
span(_,_,[],[],_):-!.
span(X,Y,[H|T],[[NX,NY]|L],CL):- validCell(X,Y,NX,NY,H,_),not(member([NX,NY],CL)),span(X,Y,T,L,[[NX,NY]|CL]),!. 
span(X,Y,[H|T],L,CL):-validCell(X,Y,NX,NY,H,_),member([NX,NY],CL),span(X,Y,T,L,CL).
add([_,[]],[],_):-!.
add([N,[[X,Y]|T]],R, Rep):- cell(X,Y,D,_),!,span(X,Y,D,E,Rep),add([N,T],L,Rep),append(E,L,R).
addN(_,[],[],_):-!.
addN(N,CL,[[N,CL]|T],Rep):- add([N,CL],Lprime,Rep),set(Lprime,LS),sort(LS,L),N1 is N+1, append(Rep,L,P),addN(N1,L,T,P).
set([],[]):-!.
set([H|T],S):- member(H,T), set(T,S),!.
set([H|T],[H|S]):- not(member(H,T)),set(T,S).

% solve - True if maze is solvable, fails otherwise.

solve :- 
    maze(_,_,_,EX,EY),
    findDistance(L),
    flat(L,R),
    flat(R,R1),
    member([EX,EY],R1),!.



%%%%%%%%%%%%%%%%
% Part 3 - SAT %
%%%%%%%%%%%%%%%%



% eval(F,A,R) - R is t if formula F evaluated with list of 
%                 true variables A is true, false otherwise

noV(_,[],[]):-!.
noV(V,[H|T],[H|L]):- H \= V,noV(V,T,L),!.
noV(V,[H|T],L):- H = V, noV(V,T,L).
whV(V,A,[V|A]).
islist([_|Tail]):- islist(Tail).
islist([]).
eval(t,_,t):-!.
eval(f,_,f):-!.

eval(X,A,t):- not(islist(X)),X \= t, X \= f, member(X,A),!.
eval(X,A,f):- not(islist(X)),X \= t, X \= f, not(member(X,A)),!.

eval([every,V,F],A,t):-whV(V,A,VA),eval(F,VA,t), noV(V,A,NA),eval(F,NA,t),!.
eval([every,V,F],A,f):-not(eval([every,V,F],A,t)),!.
eval([exists,V,F],A,f):- whV(V,A,VA),eval(F,VA,f), noV(V,A,NA),eval(F,NA,f),!.
eval([exists,V,F],A,t):- not(eval([exists,V,F],A,f)),!.
eval([and|T],A,R):- and(T,A,R),!.
eval([or|T],A,R):- or(T,A,R),!.
eval([no,T],A,R):- no(T,A,R),!.

and([X,Y],A,t):- eval(X,A,t),eval(Y,A,t),!.
and([X,Y],A,f):- not(and([X,Y],A,t)).
or([X,Y],A,f):-eval(X,A,f),eval(Y,A,f),!.
or([X,Y],A,t):- not(or([X,Y],A,f)).
no(T,A,t):- eval(T,A,f),!.
no(T,A,f):- eval(T,A,t).

% varsOf(F,R) - R is list of free variables in formula F

varsOf(F,R):- varsOf(F,R1,[]),set(R1, R2), sort(R2,R),!.
varsOf([every,V,F],R,NR):- varsOf(F,R,[V|NR]),!.
varsOf([exists,V,F],R,NR):- varsOf(F,R,[V|NR]),!.
varsOf([no,X],R,NR):- varsOf(X,R,NR),!.
varsOf([and,X,Y],R,NR):- varsOf(X,R1,NR),varsOf(Y,R2,NR),append(R1,R2,R),!.
varsOf([or,X,Y],R,NR):- varsOf(X,R1,NR),varsOf(Y,R2,NR),append(R1,R2,R),!.

varsOf(t,[],_):-!.
varsOf(f,[],_):-!.
varsOf(X,[],NR):- member(X,NR),!.
varsOf(X,[X],NR):- not(member(X,NR)).

% sat(F,R) - R is a list of true variables that satisfies F

sat(F,R) :- 
    varsOf(F,S),
    subset(S,R),
    eval(F,R,B),
    B = t.

% Helper Function
% subset(L, R) - R is a subset of list L, with relative order preserved

subset([], []).
subset([H|T], [H|NewT]) :- subset(T, NewT).
subset([_|T], NewT) :- subset(T, NewT).

