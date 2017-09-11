package cmsc420.sortedmap;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cmsc420.meeshquest.part2.City;

// BinarySearchTree class
//
// CONSTRUCTION: with no initializer
//
// ******************PUBLIC OPERATIONS*********************
// void insert( x )       --> Insert x
// void remove( x )       --> Remove x (unimplemented)
// Comparable find( x )   --> Return item that matches x
// Comparable findMin( )  --> Return smallest item
// Comparable findMax( )  --> Return largest item
// boolean isEmpty( )     --> Return true if empty; else false
// void makeEmpty( )      --> Remove all items
// void printTree( )      --> Print tree in sorted order

/**
 * Implements an AVL tree.
 * Note that all "matching" is based on the compareTo method.
 * @author Mark Allen Weiss
 */
public class AvlGTree<K extends Comparable<K> ,V> implements SortedMap<K, V>
{
	private Comparator<? super K> comp ;
    private int cardinality;
    private int g;

    public int getG() {return this.g;}
    public int getHeight() {return getHeight(root);}
    
    private int getHeight(AvlNode n) {
    	if (n == null) return 0;
    	
    	return 1+ Math.max(height(root.left), height(root.right));
    }
    
	private class AvlNode
	 {    
	     AvlNode left, right;
	     K key;
	     V element;
	     int height;
	 
	     public AvlNode(K k, V v, AvlNode l, AvlNode r) {
	    	 this.key = k;
	    	 this.element = v;
	    	 this.left = l;
	    	 this.right = r;
	    	 height = 0;
	     }
	     
	     public String toString() {
	    	 return key.toString() + "=" + element.toString();
	     }
	     
	     @Override
	     public boolean equals(Object o) {
	    	 if (o instanceof AvlGTree.AvlNode) {
	    		 return key.equals(((AvlGTree.AvlNode) o).key) && element.equals(((AvlGTree.AvlNode) o).element)
	    				 && left.equals(((AvlGTree.AvlNode) o).left) && right.equals(((AvlGTree.AvlNode) o).left) &&
	    				 height == ((AvlGTree.AvlNode)o).height;
	    	 }
	    	 
	    	 return false;
	     }
	 }
	
    /**
     * Construct the tree.
     */
    public AvlGTree()
    {
        root = null;
        setCardinality(0);
        g = 1;
        comp = new Comparator<K>() {
			@Override
			public int compare(K k1, K k2) {
				if (k1 == null || k2 == null) throw new NullPointerException();
				
				return -k1.compareTo(k2);
			}
		};
    }
    
    public AvlGTree(final Comparator<? super K> c) {
    	this();
    	comp = c;
    }
    
    public AvlGTree(int g) {
    	this();
    	this.g = g;
    }

    public AvlGTree(final Comparator<? super K> comp, final int g) {
    	this();
    	this.comp = comp;
    	this.g = g;
    }

    

    /**
     * Remove from the tree. Nothing is done if x is not found.
     * @param x the item to remove.
     */
    public void remove(K x )
    {
        System.out.println( "Sorry, remove unimplemented" );
    }

    /**
     * Find the smallest item in the tree.
     * @return smallest item or null if empty.
     */
    public AvlNode findMin( )
    {
        return findMin(root);
    }

    /**
     * Find the largest item in the tree.
     * @return the largest item of null if empty.
     */
    public AvlNode findMax( )
    {
        return findMax(root);
    }

    /**
     * Test if the tree is logically empty.
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty( )
    {
        return root == null;
    }

    /**
     * Print the tree contents in sorted order.
     */
    public void printTree( )
    {
        if( isEmpty( ) )
            System.out.println( "Empty tree" );
        else
            printTree( root );
    }

    /**
     * Internal method to find the smallest item in a subtree.
     * @param t the node that roots the tree.
     * @return node containing the smallest item.
     */
    private AvlNode findMin( AvlNode t )
    {
        if( t == null )
            return t;

        while( t.left != null )
            t = t.left;
        return t;
    }

    /**
     * Internal method to find the largest item in a subtree.
     * @param t the node that roots the tree.
     * @return node containing the largest item.
     */
    private AvlNode findMax( AvlNode t )
    {
        if( t == null )
            return t;

        while( t.right != null )
            t = t.right;
        return t;
    }

    /**
     * Internal method to print a subtree in sorted order.
     * @param t the node that roots the tree.
     */
    private void printTree( AvlNode t )
    {
        if( t != null )
        {
            printTree( t.left );
            System.out.println( t.key );
            printTree( t.right );
        }
    }

    /**
     * Return the height of node t, or -1, if null.
     */
    private int height( AvlNode t )
    {
        return t == null ? -1 : t.height;
    }

    /**
     * Return maximum of lhs and rhs.
     */
    private static int max( int lhs, int rhs )
    {
        return lhs > rhs ? lhs : rhs;
    }

    /**
     * Rotate binary tree node with left child.
     * For AVL trees, this is a single rotation for case 1.
     * Update heights, then return new root.
     */
    private AvlNode rotateWithLeftChild( AvlNode k2 )
    {
        AvlNode k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        k2.height = max( height( k2.left ), height( k2.right ) ) + 1;
        k1.height = max( height( k1.left ), k2.height ) + 1;
        return k1;
    }

    /**
     * Rotate binary tree node with right child.
     * For AVL trees, this is a single rotation for case 4.
     * Update heights, then return new root.
     */
    private AvlNode rotateWithRightChild( AvlNode k1 )
    {
        AvlNode k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        k1.height = max( height( k1.left ), height( k1.right ) ) + 1;
        k2.height = max( height( k2.right ), k1.height ) + 1;
        return k2;
    }

    /**
     * Double rotate binary tree node: first left child
     * with its right child; then node k3 with new left child.
     * For AVL trees, this is a double rotation for case 2.
     * Update heights, then return new root.
     */
    private AvlNode doubleWithLeftChild( AvlNode k3 )
    {
        k3.left = rotateWithRightChild( k3.left );
        return rotateWithLeftChild( k3 );
    }

    /**
     * Double rotate binary tree node: first right child
     * with its left child; then node k1 with new right child.
     * For AVL trees, this is a double rotation for case 3.
     * Update heights, then return new root.
     */
    private AvlNode doubleWithRightChild( AvlNode k1 )
    {
        k1.right = rotateWithLeftChild( k1.right );
        return rotateWithRightChild( k1 );
    }

      /** The tree root. */
    private AvlNode root;

    public Element printAvlGTree(Document d, Element e) {
    	return printAvlGTree(root, d, e, 0);
    }
    
    private Element printAvlGTree(AvlNode a, Document res, Element e, int i) {
		if (a == null) {
			Element empty = res.createElement("emptyChild");
			return empty;
		} else {
			Element node = res.createElement("node");
			City c = (City)a.element;
			StringBuilder sb = new StringBuilder("(");
			sb.append(c.getX());
			sb.append(",");
			sb.append(c.getY());
			sb.append(")");
			
			node.setAttribute("key", c.getName());
			node.setAttribute("value", sb.toString());
			//node.setAttribute("level", i+"");
			node.appendChild(printAvlGTree(a.left, res, node, i+1));
			node.appendChild(printAvlGTree(a.right, res, node, i+1));
			return node;
		}
	}

	@Override
	public void clear() {
		root = null;
        setCardinality(0);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		if (key == null) throw new NullPointerException();
		
		AvlNode find = find((K)key, root);
		return find == null ? null: find.element;
	}
	 
    /**
     * Internal method to find an item in a subtree.
     * @param x is item to search for.
     * @param t the node that roots the tree.
     * @return node containing the matched item.
     */
    private AvlNode find(K x, AvlNode t )
    {
        while( t != null )
            if( comp.compare(x, t.key) < 0 )
                t = t.left;
            else if(comp.compare(x, t.key) > 0 )
                t = t.right;
            else
                return t;    // Match

        return null;   // No match
    }
    
	@SuppressWarnings("unchecked")
	@Override
	public boolean containsKey(Object key) {
		if (key == null) throw new NullPointerException();
		
		return find((K)key, root) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		if (getCardinality() > 0) {
			for (Entry<K,V> e :this.entrySet()) {
				if (value == null && e.getValue() == null) return true;
				else if (value != null && value.equals(e.getValue())) return true;
			}
		}
		
		return false;
	}


	@Override
	public V put(K key, V value) {
		if (key == null) throw new NullPointerException();
		AvlNode temp; 
		if ((temp = this.find(key, root)) != null) {
			insert(key, value);
			return temp.element;
		}
		insert(key, value); 
		return null;
	}

	/**	
     * Insert into the tree; duplicates are ignored.
     * @param x the item to insert.
     */
    public void insert(K x, V v)
    {
        root = insert(x, v, root );
    }
    
    /**
     * Internal method to insert into a subtree.
     * @param x the item to insert.
     * @param t the node that roots the tree.
     * @return the new root.
     */
    private AvlNode insert(K key, V x, AvlNode t )
    {
        if( t == null ) {
            t = new AvlNode(key, x, null, null );
            setCardinality(getCardinality() + 1);
        } else if(comp.compare(key, t.key) < 0 )
        {
            t.left = insert(key, x, t.left );
            if( height( t.left ) - height( t.right ) == g+1 )
                if(comp.compare(key, t.left.key) < 0 )
                    t = rotateWithLeftChild( t );
                else
                    t = doubleWithLeftChild( t );
        }
        else if(comp.compare(key, t.key) > 0 )
        {
            t.right = insert(key, x, t.right );
            if( height( t.right ) - height( t.left ) == g+1 )
                if( comp.compare(key, t.right.key) > 0 )
                    t = rotateWithRightChild( t );
                else
                    t = doubleWithRightChild( t );
        }
        else t.element = x;  // Duplicate;
        t.height = max(height( t.left ), height( t.right ) ) + 1;
        return t;
    }
    
	@Override
	public void putAll(Map m) {
		if (m == null) throw new NullPointerException();
		
		Set<Entry<K,V>> s = (Set<Entry<K, V>>)m.entrySet();
		
		for(Entry<K,V> e: s) {
			K k = e.getKey();
			V v = e.getValue();
			
			this.put(k, v);
		}
	}

	@Override
	public V remove(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		return getCardinality();
	}

	@Override
	public Comparator<? super K> comparator() {
		return comp;
	}

	
	
	@Override
	public Set<Entry<K,V>> entrySet() {
		return new Set<Entry<K, V>>() {
			
			public boolean equals(Object o) {
				if (this == null || o == null) return false;
				if (o instanceof Set && this.size() == ((Set) o).size()) {
					Iterator<Entry<K, V>> i = this.iterator();
					
					while(i.hasNext()) {
						Entry<K,V> e = i.next();
						if (!((Set)o).contains(e)) return false; 
					}
					
					
					return true;
				}
				
				return false;
			}
			
			/*@Override
			public String toString() {
				Iterator<Entry<K,V>> i = this.iterator();
				StringBuilder sb = new StringBuilder("[");
				
				while(i.hasNext()) {
					Entry<K,V> entry = i.next();
					K key = entry.getKey();
					V val = entry.getValue();
					sb.append(key.toString());
					sb.append("=");
					sb.append(val.toString());
					
					if (i.hasNext()) sb.append(", ");
				}
				sb.append("]");
				
				return sb.toString();
			}*/
			
			@Override
			public boolean add(Entry<K, V> e) {
				K key = e.getKey();
				V value = e.getValue();
				boolean add = AvlGTree.this.containsKey(key);
				AvlGTree.this.put(key, value);
				return add;
			}

			@Override
			public boolean addAll(Collection<? extends Entry<K, V>> c) {
				Iterator<? extends Entry<K, V>> iter = c.iterator();
				boolean add = false;
				
				while (iter.hasNext()) {
					add = add || this.add((Entry<K,V>)iter.next());
				}
				return add;
			}

			@Override
			public void clear() {
				AvlGTree.this.clear();
			}

			@SuppressWarnings("unchecked")
			@Override
			public boolean contains(Object o) {
				K key = ((Entry<K,V>)o).getKey();
				
				return AvlGTree.this.get(key).equals(((Entry<K,V>)o).getValue());
			}

			@SuppressWarnings("unchecked")
			@Override
			public boolean containsAll(Collection<?> c) {
				Iterator<?> iter = c.iterator();
				
				while (iter.hasNext()) {
					Entry<K,V> e = (Entry<K,V>)iter.next();
					if (!contains(e)) return false;
				}
				
				return true;
			}

			@Override
			public boolean isEmpty() {
				return AvlGTree.this.size() == 0;
			}

			@Override
			public Iterator<Entry<K, V>> iterator() {
				return AvlGTree.this.entryIterator();
			}

			@Override
			public boolean remove(Object o) {
				if (AvlGTree.this.containsKey(o)){
					AvlGTree.this.remove(o);
					return true;
				}
				
				return false;
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				Iterator<?> iter = c.iterator();
				boolean res = true;
				
				while (iter.hasNext()) {
					Object o = iter.next();
					
					if (this.contains(iter.next()))
						this.remove(o);
					else
						res = false;
				}
				
				return res;
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				int org_size = AvlGTree.this.size();
				
				Iterator<Entry<K, V>> iter = this.iterator();
				while(iter.hasNext()) {
					Object key = iter.next().getKey();
					
					if (!c.contains(key))
						AvlGTree.this.remove(key);
				}
				
				return org_size != AvlGTree.this.size();
			}

			@Override
			public int size() {
				return 0;
			}

			@Override
			public Object[] toArray() {
				Object[] o = new Object[getCardinality()];
				Iterator<Entry<K, V>> i = this.iterator();
				int count = 0;
				
				while(i.hasNext()) o[count++] = i.next();
				
				return o;
			}

			@SuppressWarnings("unchecked")
			@Override
			public <T> T[] toArray(T[] a) {
				T[] t = (T[]) new Object[getCardinality()];
				Iterator<Entry<K, V>> i = this.iterator();
				int count = 0;
				
				while(i.hasNext()) t[count++] = (T) i.next();
				
				return t;
			}
			
		};
	}

	protected Iterator<Entry<K, V>> entryIterator() {
		return new EntryIterator();
	}

	protected class EntryIterator implements Iterator<Entry<K,V>> {
		Queue<Entry<K,V>> q = new LinkedList<Entry<K,V>>();
		
		private void inst(AvlNode n) {
			if (n == null) return;
			
			inst(n.left);
			q.offer(new AbstractMap.SimpleEntry<K,V>(n.key,n.element));
			inst(n.right);
		}
		
		EntryIterator() {
			inst(root);
		}
		
		@Override
		public boolean hasNext() {
			return !q.isEmpty();
		}

		@Override
		public java.util.Map.Entry<K, V> next() {
			if (!this.hasNext()) throw new NoSuchElementException();
			
			return q.poll();
		}
		
	}
	
	@Override
	public K firstKey() {
		if (this.isEmpty()) throw new NoSuchElementException();
		
		return findMin().key;
	}

	@Override
	public K lastKey() {
		if (this.isEmpty()) throw new NoSuchElementException();

		return findMax().key;
	}

	public boolean equals(Object o) {
		/*if (!(o instanceof Map)) return false;
		else {
			@SuppressWarnings("unchecked")
			Iterator<Entry<K, V>> i = ((Map<K,V>)o).entrySet().iterator();
			while(i.hasNext()) {
				Entry<K,V> e = (Entry<K, V>) i.next();
				if (!AvlGTree.this.containsKey(e.getKey()) || !AvlGTree.this.get(e.getKey()).equals(e.getValue())) return false;
			}
			
			return true;
		}*/
		if (!(o instanceof Map)) return false;
		return this.entrySet().equals(((Map)o).entrySet());
	}
	
	public int hashCode() {
		Set<Entry<K,V>> e = AvlGTree.this.entrySet();
		Iterator<Entry<K, V>> i = e.iterator();
		int hashcode = 0;
		
		while(i.hasNext()) {
			hashcode += i.next().hashCode();
		}
		
		return hashcode;
	}
	
	/*public String toString() {
		return this.entrySet().toString();
	}*/
	
	//Need to Modify
	@Override
	public SortedMap<K, V> subMap(K fromKey, K toKey) {
		// TODO Auto-generated method stub
		AvlGTree a = new AvlGTree(this.comp, g);
		
		a.put("TestKey10000", "TestKey10000");
		
		return a;
	}
	
	@Override
	public Set<K> keySet() {
		//No need to implement
		return null;
	}

	@Override
	public Collection<V> values() {
		//No need to implement
		return null;
	}

	@Override
	public SortedMap<K, V> headMap(K toKey) {
		//No need to implement
		return null;
	}
	
	@Override
	public SortedMap<K, V> tailMap(K fromKey) {
		//No need to implement
		return null;
	}
	
	public int getCardinality() {
		return cardinality;
	}

	public void setCardinality(int cardinality) {
		this.cardinality = cardinality;
	}
	

    // Test program
	public static void main( String [ ] args )
	{
		AvlGTree<Integer, City> avl = new AvlGTree<Integer, City>();
		
		City c1 = new City("A", 100, 100, 0, "black");
		City c2 = new City("B", 100, 1000, 0, "black");
		City c3 = new City("C", 1000, 1000, 0, "black");
		City c4 = new City("D", 1000, 100, 0, "black");
		City c5 = new City("E", 512, 512, 0, "black");
		City c6 = new City("M", 0, 0, 0, "black");
		City c7 = new City("N", 0, 1024, 0, "black");
		City c8 = new City("P", 1024, 0, 0, "black");
		City c9 = new City("O", 1024,1024,0,"black");
		City c10 = new City("K", 800,-300,0,"black");

		avl.insert(1, c1);
		avl.insert(2, c2);
		avl.insert(100, c9);
		avl.insert(3, c3);
		avl.insert(45, c5);
		avl.insert(23, c4);
		avl.insert(589, c8);
		avl.insert(111, c7);
		avl.insert(399, c6);
		avl.insert(41, c10);
		avl.insert(41, c1);

		avl.printTree();
		
		Set<Entry<Integer, City>> s = avl.entrySet();
		Iterator i = s.iterator();
		
		while(i.hasNext()) {
			System.out.println(i.next());
		}
		s.add(new AbstractMap.SimpleEntry<Integer, City>(1000, new City("largest", 1,1,0,"black")));
		System.out.println(s);
		System.out.println(avl);
	}
	

}
