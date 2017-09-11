/*Yufan Fei / yfei1 / 113169434 / Section #0403*/
/*I pledge on my honor that I have not given or received any unauthorized
assistance on this assignment.*/

/*The function below accepts a standard input flow and is able to print
out number of characters in each line. While reading the input, this 
function would store each line in an character array and print them out 
one by one. */
#include<stdio.h>

/*The chracter array (large enough) that is used to store a line of input*/
static char arr[100000];


int main() {
/*  i  : the index of the for loop below
    end: represents number of characters printed out in each line of code
    num: represents actual number of characters in each line of code
    (except tab and EOL)
*/
  int i, end, num = 0;

  /*read the first character of the input file, if it actives the EOF 
    flag, then the while loop below won't be executed. */
  scanf("%c", &arr[num]);

  
  while(!feof(stdin)) {
    /*If the chracter is neither a tab nor a new line character, 
     increment num and end by one */
    if(arr[num] != '\n' && arr[num] != '\t'){
      num++;
      end++;
    } else if (arr[num] == '\t') {
      /*Else if it is a tab character, let end round up the nearest 8*a,
       where a is an integer.*/
      end += (num/8+1)*8- num;
      num++;
    }else {
      /*If the program reach to a new line, prints out # of chracters 
       in this row and prints out its context. */
      printf("%4d: ", end);
      
      for (i = 0; i <= num; i++) {
	printf("%c", arr[i]);
      }

      /*reset end and num to 0 in order to start a new line*/
     end = num = 0;
    }

    scanf("%c", &arr[num]);
  }

  return 0;
}  
