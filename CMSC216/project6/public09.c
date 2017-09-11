#include <stdio.h>
#include <assert.h>
#include "sss.h"

/* CMSC 216, Fall 2015, Project #6
 * Public test 9 (public09.c)
 *
 * Tests executing some test commands where input redirection is used.
 */

int main(void) {
  Commands commands;

  commands= read_commands("compile-commands09", "test-commands09");

  assert(compile_program(commands) == 1);
  assert(test_program(commands) == 3);

  printf("Win!\n");  /* the assertions succeeded */
  
  return 0;
}
