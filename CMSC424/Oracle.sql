/*P1*/
select player.name
from player
where player.name like 'Nicolas%';

/*P2*/
select A.NAME, A.LOCATION, A.SURFACE, (A.ENDDATE-A.STARTDATE) Duration
from tournament A
where A.TTYPE = 'Doubles';

/*P3*/
with u(playerid) as (
  select pid
  from player
  where player.name = 'Jim Thomas'
)
select distinct name
from REGISTRATION natural join PLAYER
where REGISTRNUM in (
  select REGISTRATION.REGISTRNUM
  from REGISTRATION, u
  where pid = playerid
) and name != 'Jim Thomas';

/*P4*/
with u(playerid) as (
  select distinct pid
  from registration
  where REGISTRNUM in (
    select REGISTRNUM
    from PLAYEDIN
    where seed is not null
  )
)
select name
from player, u
where player.pid = u.playerid;

/*P5*/
with u (regis, pid, name) as (select registrnum, pid, name
  from (PLAYEDIN join REGISTRATION using (REGISTRNUM)) join player using (pid)
  where tid = (
    select tid
    from tournament
    where ttype = 'Doubles' and name = 'Wimbledon' and extract(year from startdate) = '2007'
  )
)
select A.name PlayerA, B.name PlayerB
from u A join u B using (regis)
where A.pid > B.pid;

/*P6*/
select tournament.name, extract(year from startdate), 
  (
    select name 
    from registration join player using (pid) 
    where registration.REGISTRNUM = matchresults.WINNER
  )
from tournament, match join matchresults using (mid)
where TTYPE = 'Singles' and match.round = tournament.NUMROUNDS and match.tid = tournament.tid;

/*P7*/
create view TournamentRounds as (
  select name, tournament.numrounds-2 quarter, tournament.numrounds-1 semi, tournament.numrounds final
  from tournament
);

/*P8*/
with u(name, type, tid) as (
  select tournament.NAME, tournament.TTYPE, tid
  from (tournament join match using (tid))
)
select name, tid,count(name)
from u
group by tid, name, tid
having count(name) > 1;

/*P9*/
select name/*match.registrnum1, match.registrnum2*/
from tournament join match using (tid)
where match.round = tournament.numrounds-1 and tournament.TTYPE = 'Singles'
  and match.REGISTRNUM1 in (select playedin.REGISTRNUM from playedin where seed is not null)
  and match.REGISTRNUM2 in (select playedin.REGISTRNUM from playedin where seed is not null)
group by TID, NAME
having count(tid) = 2;

/*P10*/
with u(ccode, tid) as (
  select distinct ccode, tid 
  from ((tournament join playedin using (tid)) join registration using (registrnum)) join player using (pid)
  group by tid, ccode
)
select country
from u, countrycodes
where u.ccode = countrycodes.CODE
group by ccode, country
having count(*) >= (select count(*) from tournament);

/*P11*/
with u(pid) as (
  select matchresults.WINNER
  from (matchresults join match using (mid)) join tournament using (tid)
  where numsets = 5 and tournament.ttype = 'Singles'
)
select name
from u join player using (pid);

/*P12*/
with numofseeded(name, countseed) as (
  select name, count(seed)
  from (playedin join REGISTRATION using (REGISTRNUM)) join player using (pid)
  where seed is not null
  group by name, seed
  order by count(seed) desc
)
select countseed, name
from ( select name, countseed, Row_number() over (order by countseed desc) R from numofseeded)
where R between 1 and 3