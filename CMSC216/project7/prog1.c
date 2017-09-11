#include <stdio.h>

int m, n, mod_value, count= 0;

int main() {
  scanf("%d %d %d", &m, &n, &mod_value);

  while (m <= n) {
    if (m % mod_value == 0)
      count++;
    m++;
  }

  printf("%d\n", count);

  return 0;
}
