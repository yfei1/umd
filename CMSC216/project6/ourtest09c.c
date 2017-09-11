#include <stdio.h>
#include <assert.h>
#include "header09.h"

/* this represents one test of the functions in studentcode09.c */

int main(void) {
  int a, b, c;

  scanf("%d %d %d", &a, &b, &c);

  assert(max(a, b, c) == 111);

  printf("The student code in studentcode09.c works on its third test!\n");

  return 0;
}
