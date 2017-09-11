#
# Macro for this makefile
#
CC = gcc -g
CFLAGS = -ansi -pedantic-errors -Wall -Werror -Wshadow -Wwrite-strings

#This makefile test if the function can pass all of the public test
all: public01.x public02.x public03.x public04.x public05.x \
	public06.x public07.x public08.x public09.x public10.x

sss.o: sss.c sss.h split.h
	$(CC) $(CFLAGS) -c sss.c

sss.h: sss-implementation.h
	touch sss-implementation.h

public01.o: public01.c sss.h
	$(CC) $(CFLAGS) -c public01.c

public02.o: public02.c sss.h
	$(CC) $(CFLAGS) -c public02.c

public03.o: public03.c sss.h memory-checking.h
	$(CC) $(CFLAGS) -c public03.c

public04.o: public04.c sss.h
	$(CC) $(CFLAGS) -c public04.c

public05.o: public05.c sss.h
	$(CC) $(CFLAGS) -c public05.c

public06.o: public06.c sss.h memory-checking.h
	$(CC) $(CFLAGS) -c public06.c

public07.o: public07.c sss.h
	$(CC) $(CFLAGS) -c public07.c

public08.o: public08.c sss.h
	$(CC) $(CFLAGS) -c public08.c

public09.o: public09.c sss.h
	$(CC) $(CFLAGS) -c public09.c

public10.o: public10.c sss.h
	$(CC) $(CFLAGS) -c public10.c

public01.x: public01.o sss.o split.o
	$(CC) public01.o sss.o split.o -o public01.x

public02.x: public02.o sss.o split.o
	$(CC) public02.o sss.o split.o -o public02.x

public03.x: public03.o sss.o split.o memory-checking.o
	$(CC) public03.o sss.o split.o memory-checking.o -o public03.x

public04.x: public04.o sss.o split.o
	$(CC) public04.o sss.o split.o -o public04.x

public05.x: public05.o sss.o split.o
	$(CC) public05.o sss.o split.o -o public05.x

public06.x: public06.o sss.o split.o memory-checking.o
	$(CC) public06.o sss.o split.o memory-checking.o -o public06.x

public07.x: public07.o sss.o split.o
	$(CC) public07.o sss.o split.o -o public07.x

public08.x: public08.o sss.o split.o
	$(CC) public08.o sss.o split.o -o public08.x

public09.x: public09.o sss.o split.o
	$(CC) public09.o sss.o split.o -o public09.x

public10.x: public10.o sss.o split.o
	$(CC) public10.o sss.o split.o -o public10.x

clean:
	rm -f a.out public*.o  public*.x sss.o

