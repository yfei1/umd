#include <stdio.h>

int arr[100], n, max, element, idx, pos;

int main() {
  scanf("%d", &n);
  while (n != 0) {
    if (max < 100) {
      arr[max]= n;
      max++;
    }
    scanf("%d", &n);
  }

  scanf("%d", &element);
  while (element != 0) {
    idx= 0;

    while (idx < max && arr[idx] != element)
      idx++;

    if (idx < max)
      printf("%d", idx);
    else printf("%d", -1);
    printf("\n");

    scanf("%d", &element);
  }

  return 0;
}
