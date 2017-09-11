#include "graph.h"
#include <string.h>
#include <stdlib.h>

/*
  Helper function which returns index of a given vertex 
  in the graph's array
*/
static int vertex_no(Graph graph, const char vertex[]) {
  int i = 0, num_vtx = num_vertices(graph);

  while(i < num_vtx) {
    if(strcmp((*(graph.vtx_list+i))->name, vertex) == 0)
      return i;
    i++;
  }

  return -1;
}


/* Initialize the graph by setting its memory as all 0 */
void init_graph(Graph *graph) {
  memset(graph, 0, sizeof(Graph));
}

int add_vertex(Graph *graph, const char new_vertex[]) {
  int return_val = 1;
  int num_vtx = num_vertices(*graph);

  if(graph == NULL || new_vertex == NULL || has_vertex(*graph, new_vertex))
    return_val = 0;
  else {
    if(graph->numvtx == 0)
      graph->vtx_list = malloc(sizeof(Vertex *));
    else
      /* reallocate memory such that it can holds one more vertex */
      graph->vtx_list =
	realloc(graph->vtx_list, (num_vtx+1)*sizeof(Vertex *));


    graph->vtx_list[num_vtx] = malloc(sizeof(Vertex));

    graph->vtx_list[num_vtx]->numedg = 0;

    /* allocate memory for name of the new vertex */
    graph->vtx_list[num_vtx]->name =
      malloc(sizeof(char)*(strlen(new_vertex)+1));

    /* assign corresponding values to this vertex */
    strcpy(graph->vtx_list[num_vtx]->name, new_vertex);
    graph->vtx_list[num_vtx]->edge_list = NULL;

    graph->numvtx++;
  }

  return return_val;
}

int num_vertices(Graph graph) {
  return graph.numvtx;
}

int has_vertex(Graph graph, const char name[]) {
  int i = 0, num_vtx = num_vertices(graph);

  if(name == NULL) return 0;
  /*
    Whenever there is a vertex such that its name
    is the same as the given char array, return 1
  */
  while(graph.vtx_list != NULL && i < num_vtx) {
    if(strcmp((*(graph.vtx_list+i))->name, name) == 0)
      return 1;
    i++;
  }

  /*If not, return 0*/
  return 0;
}

int add_edge(Graph *graph, const char source[], const char dest[], int cost) {
  int source_id = vertex_no(*graph, source);
  int dest_id = vertex_no(*graph, dest);
  Edge **temp = &graph->vtx_list[source_id]->edge_list;

  /*
    By using double pointer, we can skip the case when edge list is null
  */
  if (graph == NULL || source == NULL || dest == NULL || cost < 0 ||
      !has_vertex(*graph, source) || !has_vertex(*graph, dest))
    return 0;

  while(*temp != NULL) {
    if (strcmp((*temp)->dest->name, dest) == 0)
      return 0;
    else
      temp = &((*temp)->next_edge);
  }

  *temp = malloc(sizeof(Edge));
  (*temp)->dest = graph->vtx_list[dest_id];
  (*temp)->cost = cost;
  (*temp)->next_edge = NULL;
  graph->vtx_list[source_id]->numedg++;

  return 1;
}


int get_edge_cost(Graph graph, const char source[], const char dest[]) {
  int return_val = -1;

  if (source == NULL || dest == NULL ||
      !has_vertex(graph, source) || !has_vertex(graph, dest))
    return_val = -1;
  else {

    int source_id = vertex_no(graph, source);
    Edge *temp = graph.vtx_list[source_id]->edge_list;

    /*
      Find corresponding vertex and assign its edge cost 
      to return_val
    */
    while(temp != NULL) {
      if(strcmp(temp->dest->name, dest) == 0)
	return_val = temp->cost;
      temp = temp->next_edge;
    }

  }

  return return_val;
}

int change_edge_cost(Graph *graph, const char source[], const char dest[],
		     int new_cost) {
  int return_val = 0;


  if (graph == NULL || source == NULL || dest == NULL ||
      new_cost < 0 ||
      !has_vertex(*graph, source) || !has_vertex(*graph, dest))
    return_val = 0;
  else {

    int source_id = vertex_no(*graph, source);
    Edge *temp = graph->vtx_list[source_id]->edge_list;

    /*
      Find corresponding edge and change its cost to 
      the passed-in value
    */
    while(temp != NULL) {
      if(strcmp(temp->dest->name, dest) == 0) {
	temp->cost = new_cost;
	return_val = 1;
      }
      temp = temp->next_edge;
    }
  }

  return return_val;
}


int num_neighbors(Graph graph, const char vertex[]) {

  if (vertex == NULL || !has_vertex(graph, vertex)) return -1;
  else {
    int vertex_id = vertex_no(graph, vertex);
    int count = 0;
    Edge *temp = graph.vtx_list[vertex_id]->edge_list;

    /*
      Simply counts number of edges of a given vertex
    */
    while(temp != NULL) {
      temp = temp->next_edge;
      count++;
    }

    return count;
  }
}

void clear_graph(Graph *graph) {
  if (graph != NULL) {

    int i = 0;

    /*
      1. Step into each vertex and free their edges
      2. Step back to free each vertice's name
      3. Free each vertice's pointer
      4. Free the vertex array of our given graph
    */
    while (i < graph->numvtx) {
      if(graph->vtx_list[i]->edge_list != NULL) {
	Edge *curr = graph->vtx_list[i]->edge_list;
	Edge *next = graph->vtx_list[i]->edge_list->next_edge;

	while (next != NULL) {
	  free(curr);
	  curr = next;
	  next = next->next_edge;
	}

	free(curr);
      }

      free(graph->vtx_list[i]->name);
      free(graph->vtx_list[i]);
      i++;
    }

    free(graph->vtx_list);
  }
}

char **get_vertices(Graph graph) {
  char **arr = calloc(graph.numvtx + 1, sizeof(char *));
  int i = 0, j =0;

  /*
    Creating an unsorted array
  */
  while(i < graph.numvtx) {
    char *element = malloc(strlen(graph.vtx_list[i]->name)+1);
    strcpy(element, graph.vtx_list[i]->name);
    arr[i] = element;

    i++;
  }

  /*Sorting this array by using simple insertion sort
   */
  for (i = 1; i < graph.numvtx; i++) {
    j = i;

    while (j > 0 && strcmp(arr[j-1], arr[j]) > 0) {
      char *temp = arr[j-1];
      arr[j-1] = arr[j];
      arr[j] = temp;

      j--;
    }
  }

  return arr;
}

char **get_neighbors(Graph graph, const char vertex[]) {
  if (vertex == NULL) return NULL;
  else {
  int i, j, size;
  char **arr;
  Edge *temp;

  if (!has_vertex(graph, vertex)) return NULL;

  /* Loop through the vertex array to find number of edges in it */
  for (i = 0; i < graph.numvtx; i++) {
    if (strcmp(graph.vtx_list[i]->name, vertex) == 0) {
      temp = graph.vtx_list[i]->edge_list;
      size = graph.vtx_list[i]->numedg;
      break;
    }
  }


  arr = calloc(size + 1, sizeof(char *));
  i = 0;

  /* Create an unsorted array of edges*/
  while(i < size) {
    char *element = malloc(strlen(temp->dest->name)+1);
    strcpy(element, temp->dest->name);
    arr[i] = element;
    temp = temp->next_edge;

    i++;
  }

  /* Then sort this array by using insertion sort */
  for (i = 1; i < size; i++) {
    j = i;

    while (j > 0 && strcmp(arr[j-1], arr[j]) > 0) {
      char *tempstr = arr[j-1];
      arr[j-1] = arr[j];
      arr[j] = tempstr;

      j--;
    }
  }

  return arr;
  }
}

void free_vertex_name_list(char **vertex_names) {
  if (vertex_names != NULL) {
  
    int i = 0;

    while(vertex_names[i] != NULL) {
      free(vertex_names[i]);
      i++;
    }

    free(vertex_names);
  }
}

int remove_edge(Graph *graph, const char source[], const char dest[]) {

  if(graph != NULL && source != NULL && dest != NULL &&
     has_vertex(*graph, source) && has_vertex(*graph, dest)) {
    int source_id = vertex_no(*graph, source);
    Edge *prev = graph->vtx_list[source_id]->edge_list;
    Edge *temp = NULL;

    /* Enter the edge list given by source */
    if (prev != NULL) {
      if(strcmp(prev->dest->name, dest) == 0) {
	graph->vtx_list[source_id]->edge_list =
	  graph->vtx_list[source_id]->edge_list->next_edge;
	free(prev);

	graph->vtx_list[source_id]->numedg--;

	return 1;
      }

      temp = prev->next_edge;

      /* Delete this edge from the edge list  */
      while(temp != NULL) {
	if(strcmp(temp->dest->name, dest) == 0) {
	  prev->next_edge = temp->next_edge;
	  free(temp);
	  graph->vtx_list[source_id]->numedg--;
	  return 1;
	}

	prev = temp;
	temp = temp->next_edge;
      }

    }
  }

  return 0;
}

int remove_vertex(Graph *graph, const char vertex[]) {

  if (graph == NULL || vertex == NULL ||
      !has_vertex(*graph, vertex)) 
    return 0;
  else {
    int i = 0;
    int vertex_id = vertex_no(*graph, vertex);
    char *temp_name;
    Vertex *temp_vtx;

    /*
      Delete edges in other vertices which contains "vertex"
      as their dest vertex
    */
    while (i < graph->numvtx) {
      if(i != vertex_id) {
	temp_name = graph->vtx_list[i]->name;
	remove_edge(graph, temp_name, vertex);
      }

      i++;
    }

    /* 
       If "vertex" is the source vertex, then delete every edge 
       in this vertex
    */
    if(graph->vtx_list[vertex_id]->edge_list != NULL) {
      Edge *curr = graph->vtx_list[vertex_id]->edge_list;
      Edge *next = graph->vtx_list[vertex_id]->edge_list->next_edge;

      while (next != NULL) {
	free(curr);
	curr = next;
	next = next->next_edge;
      }

      free(curr);
      graph->vtx_list[vertex_id]->edge_list = NULL;
      graph->vtx_list[vertex_id]->numedg = 0;
    }

    free(graph->vtx_list[vertex_id]->name);
    free(graph->vtx_list[vertex_id]);

    /*
      After deleting the vertice, rearrage the vertex list(array) to ensure
      our graph dont have access to this vertice anymore
    */
    for (i = vertex_id; i < graph->numvtx-1; i++) {
      temp_vtx = graph->vtx_list[i];
      graph->vtx_list[i] = graph->vtx_list[i+1];
      graph->vtx_list[i+1] = temp_vtx;
    }

    graph->vtx_list =
      realloc(graph->vtx_list, (graph->numvtx-1)*sizeof(Vertex *));

    graph->numvtx--;

    return 1;
  }
}

