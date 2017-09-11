#
# Macro for this makefile
#
CC = gcc
CFLAGS = -ansi -pedantic-errors -Wall -Werror -Wshadow -Wwrite-strings


#This makefile test if the function can pass all of the public test
all: public01.x public02.x public03.x public04.x public05.x \
	public06.x public07.x public08.x public09.x public10.x

interpreter.o: interpreter.c interpreter.h
	$(CC) $(CFLAGS) -c interpreter.c

machine.o: machine.c machine.h
	$(CC) $(CFLAGS) -c machine.c

public01.o: public01.c interpreter.h machine.h
	$(CC) $(CFLAGS) -c public01.c

public02.o: public02.c interpreter.h machine.h
	$(CC) $(CFLAGS) -c public02.c

public03.o: public03.c interpreter.h machine.h
	$(CC) $(CFLAGS) -c public03.c

public04.o: public04.c interpreter.h machine.h
	$(CC) $(CFLAGS) -c public04.c

public05.o: public05.c interpreter.h machine.h
	$(CC) $(CFLAGS) -c public05.c

public06.o: public06.c interpreter.h machine.h
	$(CC) $(CFLAGS) -c public06.c

public07.o: public07.c interpreter.h machine.h
	$(CC) $(CFLAGS) -c public07.c

public08.o: public08.c interpreter.h machine.h
	$(CC) $(CFLAGS) -c public08.c

public09.o: public09.c interpreter.h machine.h
	$(CC) $(CFLAGS) -c public09.c

public10.o: public10.c interpreter.h machine.h
	$(CC) $(CFLAGS) -c public10.c

public01.x: public01.o interpreter.o machine.o
	$(CC) public01.o machine.o interpreter.o -o public01.x

public02.x: public02.o interpreter.o machine.o
	$(CC) public02.o machine.o interpreter.o -o public02.x

public03.x: public03.o interpreter.o machine.o
	$(CC) public03.o machine.o interpreter.o -o public03.x

public04.x: public04.o interpreter.o machine.o
	$(CC) public04.o machine.o interpreter.o -o public04.x

public05.x: public05.o interpreter.o machine.o
	$(CC) public05.o machine.o interpreter.o -o public05.x

public06.x: public06.o interpreter.o machine.o
	$(CC) public06.o machine.o interpreter.o -o public06.x

public07.x: public07.o interpreter.o machine.o
	$(CC) public07.o machine.o interpreter.o -o public07.x

public08.x: public08.o interpreter.o machine.o
	$(CC) public08.o machine.o interpreter.o -o public08.x

public09.x: public09.o interpreter.o machine.o
	$(CC) public09.o machine.o interpreter.o -o public09.x

public10.x: public10.o interpreter.o machine.o
	$(CC) public10.o machine.o interpreter.o -o public10.x

test1:
	public01.x | diff -ub - public01.output

test2:
	public02.x | diff -ub - public02.output

test3:
	public03.x | diff -ub - public03.output

test4:
	public04.x | diff -ub - public04.output

test5:
	public05.x | diff -ub - public05.output

test6:
	public06.x | diff -ub - public06.output

test7:
	public07.x | diff -ub - public07.output

test8:
	public08.x | diff -ub - public08.output

test9:
	public09.x | diff -ub - public09.output

test10:
	public10.x | diff -ub - public10.output

clean:
	rm public*.o public*.x machine.o interpreter.o
