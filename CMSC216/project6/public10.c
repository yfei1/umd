#include <stdio.h>
#include <assert.h>
#include "sss.h"

/* CMSC 216, Fall 2015, Project #6
 * Public test 10 (public10.c)
 *
 * Tests calling read_commands() with an invalid (missing) file as one of
 * the filename arguments.
 */

int main(void) {
  Commands commands;

  printf("Win!\n");  /* this should be the ONLY output produced by this test */

  commands= read_commands("a-file-that-does-not-exist", "test-commands10");
  printf("sucks");
  /* this should never execute, but it's needed to make the compiler happy,
   * otherwise commands is a variable that is set but never used
   */
  compile_program(commands);

  /* if we get here and print this the test should FAIL, because
   * read_commands() should have caused the entire program to exit without
   * doing anything when trying to open the missing file
   */
  printf("Lose!!!\n");
  
  return 0;
}
