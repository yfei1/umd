#include <stdio.h>

int n;

int count_digits(int value);

int main() {
  scanf("%d", &n);

  printf("%d\n", count_digits(n));

  return 0;
}

int count_digits(int value) {
  int num_digits= 0;

  if (value < 0)
    value= -value;

  do {
    value /= 10;
    num_digits++;
  } while (value > 0);

  return num_digits;
}
