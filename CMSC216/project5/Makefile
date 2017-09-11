#
# Macro for this makefile
#
CC = gcc -g
CFLAGS = -ansi -pedantic-errors -Wall -Werror -Wshadow -Wwrite-strings

#This makefile test if the function can pass all of the public test
all: public01.x public02.x public03.x public04.x public05.x \
	public06.x public07.x public08.x public09.x public10.x

graph.o: graph.c graph.h
	$(CC) $(CFLAGS) -c graph.c

graph.h: graph-implementation.h
	touch graph-implementation.h

name-list-to-string.o: name-list-to-string.h
	$(CC) $(CFLAGS) -c name-list-to-string.c

public01.o: public01.c graph.h memory-checking.h
	$(CC) $(CFLAGS) -c public01.c

public02.o: public02.c graph.h memory-checking.h
	$(CC) $(CFLAGS) -c public02.c

public03.o: public03.c graph.h memory-checking.h
	$(CC) $(CFLAGS) -c public03.c

public04.o: public04.c graph.h memory-checking.h
	$(CC) $(CFLAGS) -c public04.c

public05.o: public05.c graph.h memory-checking.h
	$(CC) $(CFLAGS) -c public05.c

public06.o: public06.c graph.h memory-checking.h
	$(CC) $(CFLAGS) -c public06.c

public07.o: public07.c graph.h memory-checking.h
	$(CC) $(CFLAGS) -c public07.c

public08.o: public08.c graph.h memory-checking.h
	$(CC) $(CFLAGS) -c public08.c

public09.o: public09.c graph.h memory-checking.h
	$(CC) $(CFLAGS) -c public09.c

public10.o: public10.c graph.h memory-checking.h name-list-to-string.h
	$(CC) $(CFLAGS) -c public10.c

public01.x: public01.o graph.o memory-checking.o
	$(CC) public01.o graph.o memory-checking.o -o public01.x

public02.x: public02.o graph.o memory-checking.o
	$(CC) public02.o graph.o memory-checking.o -o public02.x

public03.x: public03.o graph.o memory-checking.o
	$(CC) public03.o graph.o memory-checking.o -o public03.x

public04.x: public04.o graph.o memory-checking.o
	$(CC) public04.o graph.o memory-checking.o -o public04.x

public05.x: public05.o graph.o memory-checking.o
	$(CC) public05.o graph.o memory-checking.o -o public05.x

public06.x: public06.o graph.o memory-checking.o
	$(CC) public06.o graph.o memory-checking.o -o public06.x

public07.x: public07.o graph.o memory-checking.o
	$(CC) public07.o graph.o memory-checking.o -o public07.x

public08.x: public08.o graph.o memory-checking.o
	$(CC) public08.o graph.o memory-checking.o -o public08.x

public09.x: public09.o graph.o memory-checking.o
	$(CC) public09.o graph.o memory-checking.o -o public09.x

public10.x: public10.o graph.o memory-checking.o name-list-to-string.o
	$(CC) public10.o graph.o memory-checking.o name-list-to-string.o -o public10.x

clean:
		rm -f public*.o public*.x graph.o name-list-to-string.o
