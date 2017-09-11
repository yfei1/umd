#include <stdio.h>
#include <assert.h>
#include "sss.h"

/* CMSC 216, Fall 2015, Project #6
 * Public test 8 (public08.c)
 *
 * Tests executing some test commands where some do not succeed (some
 * commands have a nonzero exit status).
 */

int main(void) {
  Commands commands;

  commands= read_commands("compile-commands08", "test-commands08");

  assert(compile_program(commands) == 1);
  /* only two tests in test-commands8 succeeded */
  assert(test_program(commands) == 2);

  printf("Win!\n");  /* the assertions succeeded */
  
  return 0;
}
