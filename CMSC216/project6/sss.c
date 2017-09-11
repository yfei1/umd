#include "sss.h"
#include "split.h"
#include "memory-checking.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <err.h>
#include <sysexits.h>

static void free_split(char **words, int op, int old_end);

Commands read_commands(const char *compile_cmds, const char *test_cmds) {
  if (compile_cmds == NULL || test_cmds == NULL) exit(0);
  else {
    Commands cmd = {0};
    /* Use a fixed size array to temporarily hold the buffer*/
    char *alloc = NULL, line[257] = {0};
    FILE *fp = NULL;
    int i = 0, count = 0;
    int devNull = 0;
    
    /* Suppress all the error msg*/
    devNull = open("/dev/null", 0);
    dup2(devNull, STDERR_FILENO);
    close(devNull);
    
    /* Use a loop to load two files
       when count equals to 0 it represents compile_cmds
       otherwise represents test_cmds
    */
    while (count < 2) {
      i = 0;
      
      if (count == 0) fp = fopen(compile_cmds, "r");
      else fp = fopen(test_cmds, "r");
      
      if (fp == NULL) {
	/*err(EX_OSERR, "Oops, something wrong here");*/
	exit(0);
      } else {
	/* In each iteration, allocate corresponding size to commands*/
	while(fgets(line, 257, fp) != NULL) {
	  if (count == 0) {
	    if (i == 0) cmd.compile_arr = malloc(sizeof(char *));
	    else cmd.compile_arr =
		   realloc(cmd.compile_arr, (i+1)*sizeof(char *));
	  } else {
	    if (i == 0) cmd.test_arr = malloc(sizeof(char *));
	    else cmd.test_arr = realloc(cmd.test_arr, (i+1)*sizeof(char *));
	  }
	 
	  alloc = malloc((strlen(line)+1)*sizeof(char));
	  strncpy(alloc, line, strlen(line));
	 
	  /* There are two cases when allocating memory*/
	  if (count == 0)
	    cmd.compile_arr[i] = alloc;
	  else
	    cmd.test_arr[i] = alloc;
	 
	  i++;
	}
      }
    
      if (count == 0) cmd.comp_num = i;
      else cmd.test_num = i;
      
      fclose(fp);
      
      count++;
    }
    
    return cmd;
  }
}


void clear_commands(Commands *const commands) {
  if (commands->comp_num != 0 && commands->test_num != 0) {
    /* In below iterations, free each element in this commands arr */
    while (commands->comp_num > 0) {
      free(commands->compile_arr[commands->comp_num-1]);

      commands->comp_num--;
    }

    while (commands->test_num > 0) {
      free(commands->test_arr[commands->test_num-1]);

      commands->test_num--;
    }

    /* Sequence does matter!*/
    free(commands->compile_arr);
    free(commands->test_arr);
  }
}

int compile_program(Commands commands) {
  pid_t pid;
  int status = 0, i = 0, j = 0;
  int devNull;

  
  while (i < commands.comp_num) {
    pid = fork();

    if (pid < 0)
      perror("Fork Error...");
    else if (pid > 0) {
      /* Return 0 if any of the command exit with exitcode != 0*/
      waitpid(pid, &status, 0);
      if (!WIFEXITED(status) || WEXITSTATUS(status)) return 0;
    } else {
      char **words = split(commands.compile_arr[i]);
      j = 0;

      /* throw away all of the error msg*/
      devNull = open("/dev/null", 0);

      dup2(devNull, STDERR_FILENO);
      close(devNull);
      execvp(words[0], words);

      /*free the array if execution failed*/
      while(words[j] != NULL) {
	free(words[j]);
	j++;
      }

      free(words[j]);
      free(words);
    }

    i++;
  }

  return 1;
}

int test_program(Commands commands) {
  pid_t pid;
  int status = 0, i = 0, count = 0;

  if(!compile_program(commands)) return 0;
  
  while (i < commands.test_num) {
    if ((pid = fork()) < 0)
      err(EX_OSERR ,"Fork Error...");
    else if (pid > 0) {
      wait(&status);
    } else {
      char **words = split(commands.test_arr[i]);
      int num = 0;
      int in, out, devNull;

      /* 
	 there are four cases, so we have four different 
	 redirection situation here
       */
      while(words[num] != NULL) num++;

      if (num > 4 &&
	  strcmp(words[num-2], ">") == 0 &&
	  strcmp(words[num-4], "<") == 0)
	/*when both are used*/ {
	in = open(words[num-3], O_RDONLY);
	out = open(words[num-1], O_WRONLY | O_CREAT);

	dup2(in, STDIN_FILENO);
	dup2(out, STDOUT_FILENO);

	close(in);
	close(out);

	free_split(words, 2, num);

      } else if (num >= 2) {
	if (strcmp(words[num-2], "<") == 0) {
	  /*infile is used*/
	  in = open(words[num-1], O_RDONLY);

	  dup2(in, STDIN_FILENO);

	  close(in);

	  free_split(words, 1, num);

	} else if (strcmp(words[num-2], ">") == 0) {
	  /* if output file is used*/
	  out = open(words[num-1], O_WRONLY | O_CREAT);

	  dup2(out, STDOUT_FILENO);

	  close(out);

	  free_split(words, 1, num);
	}
      }

      devNull = open("/dev/null", 0);
      dup2(devNull, STDERR_FILENO);
      close(devNull);
      execvp(words[0], words);

      /*free the whole array if execution failed */
      free_split(words, 0, num);
      free(words[0]);

    }

    if(WIFEXITED(status) && !WEXITSTATUS(status)) count++;

    i++;

  }

  return count;
}

static void free_split(char **words, int op, int old_end) {
  int new_end = 0, temp;
  /* 
     do the dirty word to free the splited array
     including the work to set the newend location element 
     as null
  */
  if (op == 1) new_end = old_end - 2;
  else if(op == 2) new_end = old_end - 4;

  temp = new_end;

  while(new_end+1 <= old_end) {
    free(words[new_end+1]);
    new_end++;
  }

  words[temp] = NULL;
} 
