#include "header08.h"

int power(int base, int exponent) {
  int i, result= base;

  for (i= 1; i < exponent; i++)
    result *= base;

  return result;
}
