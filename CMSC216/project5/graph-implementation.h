typedef struct vertex {
  char *name;
  int numedg;
  struct edge *edge_list;
} Vertex;

typedef struct edge {
  struct vertex *dest;
  int cost;
  struct edge *next_edge;
} Edge;

typedef struct graph{
  int numvtx;
  struct vertex **vtx_list;
} Graph;
