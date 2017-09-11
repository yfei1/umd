#include <string.h>
#include "name-list-to-string.h"

/* Takes an array of strings (pointers to characters), which must end in
 * NULL, and stores the strings, in order, into a result string, separated
 * by spaces.  This just makes it easier to compare the results of some
 * tests against the expected results (tests that would be calling
 * get_vertices() and get_neighbors()).
 */
void name_list_to_string(char **names, char result[]) {
  int i= 0;

  strcpy(result, "");  /* clear out any existing contents */

  while (names[i] != NULL) {
    /* add the current string to the result, if there's room */
    if (strlen(result) + strlen(names[i]) + 1 < MAX_LEN) {
      strcat(result, names[i]);

      /* add a space after it if it's not the last element */
      if (names[i + 1] != NULL)
        strcat(result, " ");
    }

    i++;
  }
}
