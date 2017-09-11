#include "graph-implementation.h"

void init_graph(Graph *graph);
int add_vertex(Graph *graph, const char new_vertex[]);
int num_vertices(Graph graph);
int has_vertex(Graph graph, const char name[]);
int add_edge(Graph *graph, const char source[], const char dest[], int cost);
int get_edge_cost(Graph graph, const char source[], const char dest[]);
int change_edge_cost(Graph *graph, const char source[], const char dest[],
                     int new_cost);
int num_neighbors(Graph graph, const char vertex[]);
void clear_graph(Graph *graph);
char **get_vertices(Graph graph);
char **get_neighbors(Graph graph, const char vertex[]);
void free_vertex_name_list(char **vertex_names);
int remove_edge(Graph *graph, const char source[], const char dest[]);
int remove_vertex(Graph *graph, const char vertex[]);
