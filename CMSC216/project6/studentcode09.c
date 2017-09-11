#include "header09.h"

int max(int x, int y, int z) {
  x= (x > y) ? x : y;  /* x has the max of x and y */

  return (x > z) ? x : z;
}
