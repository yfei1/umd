#include <stdio.h>
#include <assert.h>
#include "sss.h"

/* CMSC 216, Fall 2015, Project #6
 * Public test 5 (public05.c)
 *
 * Tests executing multiple compilation commands, then executing several
 * test commands (running several resulting executable programs).
 */

int main(void) {
  Commands commands;

  commands= read_commands("compile-commands05+06", "test-commands05+06");

  assert(compile_program(commands) == 1);
  /* three tests in test-commands5 succeeded */
  assert(test_program(commands) == 3);

  printf("Win!\n");  /* the assertions succeeded */
  
  return 0;
}
