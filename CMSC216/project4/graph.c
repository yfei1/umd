#include "graph.h"
#include <string.h>
#include <stdlib.h>

static int vertex_no(Graph graph, const char vertex[]) {
  int i = 0, num_vtx = num_vertices(graph);

  while(i < num_vtx) {
    if(strcmp((*(graph.vtx_list+i))->name, vertex) == 0)
      return i;
    i++;
  }

  return -1;
}


/* done */
void init_graph(Graph *graph) {
  memset(graph, 0, sizeof(Graph));
}


/* Graph itself is unsafe!!!?? */
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
      graph->vtx_list = realloc(graph->vtx_list, (num_vtx+1)*sizeof(Vertex *));


    graph->vtx_list[num_vtx] = malloc(sizeof(Vertex));
    /* allocate memory for name of the new vertex */
    graph->vtx_list[num_vtx]->name = malloc(sizeof(char)*(strlen(new_vertex)+1));
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

  while(graph.vtx_list != NULL && i < num_vtx) {
    if(strcmp((*(graph.vtx_list+i))->name, name) == 0)
      return 1;
    i++;
  }

  return 0;
}

int add_edge(Graph *graph, const char source[], const char dest[], int cost) {
  int source_id = vertex_no(*graph, source);
  int dest_id = vertex_no(*graph, dest);
  Edge **temp = &graph->vtx_list[source_id]->edge_list;

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

    while(temp != NULL) {
      temp = temp->next_edge;
      count++;
    }

    return count;
  }
}
