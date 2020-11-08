package Negocio;

import java.io.Serializable;
import java.util.*;

/**
 * Clase para emular la funcionalidad de la clase java.util.Hashtable, pero implementada
 * en base al modelo de Resolución de Colisiones por Direccionamiento Abierto. Modelo para
 * aplicar de base para el desarrollo del TPU.
 *
 * @author Ing. Valerio Frittelli.
 * @version Octubre de 2019.
 * @param <K> el tipo de los objetos que serán usados como clave en la tabla.
 * @param <V> el tipo de los objetos que serán los valores de la tabla.
 * */
public class TSBHashtableDA<K,V> implements Map<K,V>, Cloneable, Serializable
{

    public static final int OPEN = 0;
    public static final int CLOSED = 1;
    public static final int TOMBSTONE = 2;

    private Object table[];
    
    private int initial_capacity;
    
    private int count;
    
    private float load_factor;

    private transient Set<K> keySet = null;
    private transient Set<Map.Entry<K,V>> entrySet = null;
    private transient Collection<V> values = null;

    protected transient int modCount;

    /**
     * Crea una tabla vacía, con la capacidad inicial igual a 11 y con factor 
     * de carga igual a 0.5f (que equivale a un nivel de carga del 50%).
     */

    public TSBHashtableDA()
    {
        this(11, 0.5f);
    }
    
    /**
     * Crea una tabla vacía, con la capacidad inicial indicada y con factor 
     * de carga igual a 0.5f (que equivale a un nivel de carga del 50%).
     * @param initial_capacity la capacidad inicial de la tabla.
     */

    public TSBHashtableDA(int initial_capacity)
    {
        this(initial_capacity, 0.5f);
    }

    /**
     * Crea una tabla vacía, con la capacidad inicial indicada y con el factor 
     * de carga indicado. Si la capacidad inicial indicada por initial_capacity 
     * es menor o igual a 0, la tabla será creada de tamaño 11. Si el factor de
     * carga indicado es negativo, cero o mayor a 0.5, se ajustará a 0.5f. Si el
     * valor de initial_capacity no es primo, el tamaño se ajustará al primer
     * primo que sea mayor a initial_capacity.
     * @param initial_capacity la capacidad inicial de la tabla.
     * @param load_factor el factor de carga de la tabla.
     */

    public TSBHashtableDA(int initial_capacity, float load_factor)
    {
        if(load_factor <= 0 || load_factor > 0.5) { load_factor = 0.5f; }
        if(initial_capacity <= 0) { initial_capacity = 11; }
        else
        {
            if(!isPrime(initial_capacity))
            {
                initial_capacity = nextPrime(initial_capacity);
            }
        }
        
        this.table = new Object[initial_capacity];
        for(int i=0; i<table.length; i++)
        {
            table[i] = new Entry<K, V>(null, null);
        }
        
        this.initial_capacity = initial_capacity;
        this.load_factor = load_factor;
        this.count = 0;
        this.modCount = 0;
    }
    
    /**
     * Crea una tabla a partir del contenido del Map especificado.
     * @param t el Map a partir del cual se creará la tabla.
     */

    public TSBHashtableDA(Map<? extends K,? extends V> t)
    {
        this(11, 0.5f);
        this.putAll(t);
    }

    /**
     * Retorna la cantidad de elementos contenidos en la tabla.
     * @return la cantidad de elementos de la tabla.
     */

    @Override
    public int size() 
    {
        return this.count;
    }

    /**
     * Determina si la tabla está vacía (no contiene ningún elemento).
     * @return true si la tabla está vacía.
     */

    @Override
    public boolean isEmpty() 
    {
        return (this.count == 0);
    }

    /**
     * Determina si la clave key está en la tabla. 
     * @param key la clave a verificar.
     * @return true si la clave está en la tabla.
     * @throws NullPointerException si la clave es null.
     */

    @Override
    public boolean containsKey(Object key) 
    {
        return (this.get((K)key) != null);
    }

    /**
     * Determina si alguna clave de la tabla está asociada al objeto value que
     * entra como parámetro. Equivale a contains().
     * @param value el objeto a buscar en la tabla.
     * @return true si alguna clave está asociada efectivamente a ese value.
     */

    @Override
    public boolean containsValue(Object value)
    {
        return this.contains(value);
    }

    /**
     * Retorna el objeto al cual está asociada la clave key en la tabla, o null 
     * si la tabla no contiene ningún objeto asociado a esa clave.
     * @param key la clave que será buscada en la tabla.
     * @return el objeto asociado a la clave especificada (si existe la clave) o 
     *         null (si no existe la clave en esta tabla).
     * @throws NullPointerException si key es null.
     * @throws ClassCastException si la clase de key no es compatible con la 
     *         tabla.
     */

    @Override
    public V get(Object key)
    {
        if(key == null) throw new NullPointerException("get(): parámetro null");
        try {
            int pos= search_for_index((K) key, h( (K) key));
            if (pos!=-1){
                return (V) ((Entry<K,V>)table[pos]).getValue();}
            return null;
        }
        catch (ClassCastException c){
            throw new ClassCastException("No se puede castear");
        }

    }

    /**
     * Asocia el valor (value) especificado, con la clave (key) especificada en
     * esta tabla. Si la tabla contenía previamente un valor asociado para la 
     * clave, entonces el valor anterior es reemplazado por el nuevo (y en este 
     * caso el tamaño de la tabla no cambia). 
     * @param key la clave del objeto que se quiere agregar a la tabla.
     * @param value el objeto que se quiere agregar a la tabla.
     * @return el objeto anteriormente asociado a la clave si la clave ya 
     *         estaba asociada con alguno, o null si la clave no estaba antes 
     *         asociada a ningún objeto.
     * @throws NullPointerException si key es null o value es null.
     */

    @Override
    public V put(K key, V value) 
    {
       if(key == null || value == null) throw new NullPointerException("put(): parámetro null");
       
       int ik = this.h(key);

       V old = null;
       Map.Entry<K, V> x = this.search_for_entry((K)key, ik);
       if(x != null) 
       {
           old = x.getValue();
           x.setValue(value);
       }
       else
       {
           if(this.load_level() >= this.load_factor) { this.rehash(); }
           int pos = search_for_OPEN(this.table, this.h(key));
           Map.Entry<K, V> entry = new Entry<>(key, value, CLOSED);
           table[pos] = entry;

           this.count++;
           this.modCount++;
       }
       
       return old;
    }

    /**
     * Elimina de la tabla la clave key (y su correspondiente valor asociado).  
     * El método no hace nada si la clave no está en la tabla. 
     * @param key la clave a eliminar.
     * @return El objeto al cual la clave estaba asociada, o null si la clave no
     *         estaba en la tabla.
     * @throws NullPointerException - if the key is null.
     */

    @Override
    public V remove(Object key) {
        if (key == null) throw new NullPointerException ("remove(): parámetro null");
        if (!this.containsKey (key))
        {
            return null;
        }

        Object old = get(key);
        int ik = this.h ((K) key);
        int ib = search_for_index ((K) key, ik);
        Entry<K, V> en  = (Entry<K, V>)  table[ib];
        en.setState (TOMBSTONE);
        en.setValue(null);
        table[ib] = en;
        count --;
        modCount ++;
        return (V) old;
    }

    /**
     * Copia en esta tabla, todos los objetos contenidos en el map especificado.
     * Los nuevos objetos reemplazarán a los que ya existan en la tabla 
     * asociados a las mismas claves (si se repitiese alguna).
     * @param m el map cuyos objetos serán copiados en esta tabla. 
     * @throws NullPointerException si m es null.
     */

    @Override
    public void putAll(Map<? extends K, ? extends V> m) 
    {
        for(Map.Entry<? extends K, ? extends V> e : m.entrySet())
        {
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * Elimina el contenido de la tabla, de forma de dejarla vacía. En esta
     * implementación además, el arreglo de soporte vuelve a tener el tamaño que
     * inicialmente tuvo al ser creado el objeto.
     */

    @Override
    public void clear()
    {
        Object aux[] = new Object[initial_capacity];
        for(int i=0; i<aux.length; i++)
        {
            aux[i] = new Entry<K, V>(null, null);
        }

        this.table = aux;
        this.count = 0;
        this.modCount = 0;
    }

    /**
     * Retorna un Set (conjunto) a modo de vista de todas las claves (key)
     * contenidas en la tabla. El conjunto está respaldado por la tabla, por lo 
     * que los cambios realizados en la tabla serán reflejados en el conjunto, y
     * viceversa. Si la tabla es modificada mientras un iterador está actuando 
     * sobre el conjunto vista, el resultado de la iteración será indefinido 
     * (salvo que la modificación sea realizada por la operación remove() propia
     * del iterador, o por la operación setValue() realizada sobre una entrada 
     * de la tabla que haya sido retornada por el iterador). El conjunto vista 
     * provee métodos para eliminar elementos, y esos métodos a su vez 
     * eliminan el correspondiente par (key, value) de la tabla (a través de las
     * operaciones Iterator.remove(), Set.remove(), removeAll(), retainAll() 
     * y clear()). El conjunto vista no soporta las operaciones add() y 
     * addAll() (si se las invoca, se lanzará una UnsuportedOperationException).
     * @return un conjunto (un Set) a modo de vista de todas las claves
     *         mapeadas en la tabla.
     */

    @Override
    public Set<K> keySet() 
    {
        if(keySet == null) 
        { 
            keySet = new KeySet();
        }
        return keySet;  
    }
        
    /**
     * Retorna una Collection (colección) a modo de vista de todos los valores
     * (values) contenidos en la tabla. La colección está respaldada por la 
     * tabla, por lo que los cambios realizados en la tabla serán reflejados en 
     * la colección, y viceversa. Si la tabla es modificada mientras un iterador 
     * está actuando sobre la colección vista, el resultado de la iteración será 
     * indefinido (salvo que la modificación sea realizada por la operación 
     * remove() propia del iterador, o por la operación setValue() realizada 
     * sobre una entrada de la tabla que haya sido retornada por el iterador). 
     * La colección vista provee métodos para eliminar elementos, y esos métodos 
     * a su vez eliminan el correspondiente par (key, value) de la tabla (a 
     * través de las operaciones Iterator.remove(), Collection.remove(), 
     * removeAll(), removeAll(), retainAll() y clear()). La colección vista no 
     * soporta las operaciones add() y addAll() (si se las invoca, se lanzará 
     * una UnsuportedOperationException).
     * @return una colección (un Collection) a modo de vista de todas los 
     *         valores mapeados en la tabla.
     */

    @Override
    public Collection<V> values() 
    {
        if(values==null)
        {
            values = new ValueCollection();
        }
        return values;    
    }

    /**
     * Retorna un Set (conjunto) a modo de vista de todos los pares (key, value)
     * contenidos en la tabla. El conjunto está respaldado por la tabla, por lo 
     * que los cambios realizados en la tabla serán reflejados en el conjunto, y
     * viceversa. Si la tabla es modificada mientras un iterador está actuando 
     * sobre el conjunto vista, el resultado de la iteración será indefinido 
     * (salvo que la modificación sea realizada por la operación remove() propia
     * del iterador, o por la operación setValue() realizada sobre una entrada 
     * de la tabla que haya sido retornada por el iterador). El conjunto vista 
     * provee métodos para eliminar elementos, y esos métodos a su vez 
     * eliminan el correspondiente par (key, value) de la tabla (a través de las
     * operaciones Iterator.remove(), Set.remove(), removeAll(), retainAll() 
     * and clear()). El conjunto vista no soporta las operaciones add() y 
     * addAll() (si se las invoca, se lanzará una UnsuportedOperationException).
     * @return un conjunto (un Set) a modo de vista de todos los objetos 
     *         mapeados en la tabla.
     */

    @Override
    public Set<Map.Entry<K, V>> entrySet() 
    {
        if(entrySet == null) 
        { 
            entrySet = new EntrySet();
        }
        return entrySet;
    }

    /**
     * Retorna una copia superficial de la tabla. Las listas de desborde o 
     * buckets que conforman la tabla se clonan ellas mismas, pero no se clonan 
     * los objetos que esas listas contienen: en cada bucket de la tabla se 
     * almacenan las direcciones de los mismos objetos que contiene la original. 
     * @return una copia superficial de la tabla.
     * @throws java.lang.CloneNotSupportedException si la clase no implementa la
     *         interface Cloneable.    
     */

    @Override
    protected Object clone() throws CloneNotSupportedException 
    {
        TSBHashtableDA<K, V> t = new TSBHashtableDA<>();
        t.putAll(this);
        return t;
    }

    /**
     * Determina si esta tabla es igual al objeto especificado.
     * @param obj el objeto a comparar con esta tabla.
     * @return true si los objetos son iguales.
     */

    @Override
    public boolean equals(Object obj) 
    {
        if(!(obj instanceof Map)) { return false; }
        
        Map<K, V> t = (Map<K, V>) obj;
        if(t.size() != this.size()) { return false; }

        try 
        {
            Iterator<Map.Entry<K,V>> i = this.entrySet().iterator();
            while(i.hasNext()) 
            {
                Map.Entry<K, V> e = i.next();
                K key = e.getKey();
                V value = e.getValue();
                if(t.get(key) == null) { return false; }
                else 
                {
                    if(!value.equals(t.get(key))) { return false; }
                }
            }
        } 
        
        catch (ClassCastException | NullPointerException e) 
        {
            return false;
        }

        return true;    
    }

    /**
     * Retorna un hash code para la tabla completa.
     * @return un hash code para la tabla.
     */

    @Override
    public int hashCode() 
    {
        if(this.isEmpty()) {return 0;}
        
        int hc = 0;
        for(Map.Entry<K, V> entry : this.entrySet())
        {
            hc += entry.hashCode();
        }
        
        return hc;
    }
    
    /**
     * Devuelve el contenido de la tabla en forma de String.
     * @return una cadena con el contenido completo de la tabla.
     */

    @Override
    public String toString() 
    {
        StringBuilder cad = new StringBuilder("[");
        for(int i = 0; i < this.table.length; i++)
        {
            Entry<K, V> entry = (Entry<K, V>) table[i];
            if(entry.getState() == CLOSED)
            {
                cad.append(entry.toString());
                cad.append(" ");
            }
        }
        cad.append("]");
        return cad.toString();
    }
    
    

    /**
     * Determina si alguna clave de la tabla está asociada al objeto value que
     * entra como parámetro. Equivale a containsValue().
     * @param value el objeto a buscar en la tabla.
     * @return true si alguna clave está asociada efectivamente a ese value.
     */

    public boolean contains(Object value)
    {
        if(value == null) return false;
        for (int i = 0; i < table.length; i++) {
            Map.Entry<K, V> ent = (Map.Entry<K, V>) table[i];
            K key = ent.getKey();
            Object v = this.get(key);
            if(Objects.equals (value, v)){ return true;}
        }
        return false;
    }
    
    /**
     * Incrementa el tamaño de la tabla y reorganiza su contenido. Se invoca 
     * automaticamente cuando se detecta que la cantidad promedio de nodos por 
     * lista supera a cierto el valor critico dado por (10 * load_factor). Si el
     * valor de load_factor es 0.8, esto implica que el límite antes de invocar 
     * rehash es de 8 nodos por lista en promedio, aunque seria aceptable hasta 
     * unos 10 nodos por lista.
     */

    protected void rehash()
    {
        int old_length = this.table.length;
        
        int new_length = nextPrime((int)(old_length * 1.5f));
        
        Object temp[] = new Object[new_length];
        for(int j=0; j<temp.length; j++) { temp[j] = new Entry<>(null, null); }
        
        this.modCount++;
       
        for(int i=0; i<this.table.length; i++)
        {
           Entry<K, V> x = (Entry<K, V>) table[i];

           if(x.getState() == CLOSED)
           {
               K key = x.getKey();
               int ik = this.h(key, temp.length);
               int y = search_for_OPEN(temp, ik);

               temp[y] = x;
           }
        }
       
        this.table = temp;
    }

    private int h(int k)
    {
        return h(k, this.table.length);
    }
    

    private int h(K key)
    {
        return h(key.hashCode(), this.table.length);
    }
    

    private int h(K key, int t)
    {
        return h(key.hashCode(), t);
    }
    

    private int h(int k, int t)
    {
        if(k < 0) k *= -1;
        return k % t;        
    }

    private boolean isPrime(int n)
    {
        if(n < 0) return false;

        if(n == 1) return false;
        if(n == 2) return true;
        if(n % 2 == 0) return false;

        int raiz = (int) Math.pow(n, 0.5);
        for(int div = 3;  div <= raiz; div += 2)
        {
            if(n % div == 0) return false;
        }

        return true;
    }

    private int nextPrime (int n)
    {
        if(n % 2 == 0) n++;
        for(; !isPrime(n); n+=2);
        return n;
    }

    private float load_level()
    {
        return (float) this.count / this.table.length;
    } 

    private Map.Entry<K, V> search_for_entry(K key, int ik)
    {
        int pos = search_for_index(key, ik);
        return pos != -1 ? (Map.Entry<K, V>) table[pos] : null;
    }
    

    private int search_for_index(K key, int ik)
    {
        for(int j=0; ;j++)
        {
            ik += (int)Math.pow(j, 2);
            ik %= table.length;

            Entry<K, V> entry = (Entry<K, V>) table[ik];
            if(entry.getState() == OPEN) { return -1; }
            if(key.equals(entry.getKey())) { return ik; }
        }
    }


    private int search_for_OPEN(Object t[], int ik)
    {
        for(int j=0; ;j++)
        {
            ik += (int)Math.pow(j, 2);
            ik %= t.length;

            Entry<K, V> entry = (Entry<K, V>) t[ik];
            if(entry.getState() == OPEN) { return ik; }
        }
    }


    private class Entry<K, V> implements Map.Entry<K, V>
    {
        private K key;
        private V value;
        private int state;
        
        public Entry(K key, V value) 
        {
            this(key, value, OPEN);
        }

        public Entry(K key, V value, int state)
        {
            this.key = key;
            this.value = value;
            this.state = state;
        }

        @Override
        public K getKey() 
        {
            return key;
        }

        @Override
        public V getValue() 
        {
            return value;
        }

        public int getState() { return state; }

        @Override
        public V setValue(V value) 
        {
            if(value == null) 
            {
                throw new IllegalArgumentException("setValue(): parámetro null...");
            }
                
            V old = this.value;
            this.value = value;
            return old;
        }

        public void setState(int ns)
        {
            if(ns >= 0 && ns < 3)
            {
                state = ns;
            }
        }
       
        @Override
        public int hashCode() 
        {
            int hash = 7;
            hash = 61 * hash + Objects.hashCode(this.key);
            hash = 61 * hash + Objects.hashCode(this.value);            
            return hash;
        }

        @Override
        public boolean equals(Object obj) 
        {
            if (this == obj) { return true; }
            if (obj == null) { return false; }
            if (this.getClass() != obj.getClass()) { return false; }
            
            final Entry other = (Entry) obj;
            if (!Objects.equals(this.key, other.key)) { return false; }
            if (!Objects.equals(this.value, other.value)) { return false; }            
            return true;
        }       
        
        @Override
        public String toString()
        {
            return "(" + key.toString() + ", " + value.toString() + ")";
        }
    }

    private class KeySet extends AbstractSet<K> 
    {
        @Override
        public Iterator<K> iterator() 
        {
            return new KeySetIterator();
        }
        
        @Override
        public int size() 
        {
            return TSBHashtableDA.this.count;
        }
        
        @Override
        public boolean contains(Object o) 
        {
            return TSBHashtableDA.this.containsKey(o);
        }
        
        @Override
        public boolean remove(Object o) 
        {
            return (TSBHashtableDA.this.remove(o) != null);
        }
        
        @Override
        public void clear() 
        {
            TSBHashtableDA.this.clear();
        }
        
        private class KeySetIterator implements Iterator<K>
        {
            private int last;

            private  int current;

            private int next;
            private boolean next_ok;

            private int expected_modCount;

            public KeySetIterator()
            {
                next_ok = false;
                expected_modCount = TSBHashtableDA.this.modCount;

                last = 0;
                current = -1;
                next = current + 1;
            }


            @Override
            public boolean hasNext() 
            {
                if(current >= table.length) { return false; }
                next = current + 1;
                if(next >= table.length)
                { return false; }
                for ( int i = next ; i < table.length; i++)
                {
                    Entry<K, V> entry = (Entry<K, V>) TSBHashtableDA.this.table[i];
                    if (entry.getState() == CLOSED)
                    {
                        next = i;
                        return true;
                    }
                }
                next_ok = false;
                return false;
            }

            @Override
            public K next() 
            {
                if(TSBHashtableDA.this.modCount != expected_modCount)
                {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }
                if(!hasNext())
                {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }
                Entry<K, V> entry = (Entry<K, V>) TSBHashtableDA.this.table[next];
                last = current;
                current = next;
                next_ok = true;
                K key = entry.getKey();
                return key;
            }
            

            @Override
            public void remove() 
            {
                if (TSBHashtableDA.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("remove(): modificación inesperada de tabla...");
                }

                if (!next_ok) {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }

                Entry<K, V> entry = (Entry<K, V>) TSBHashtableDA.this.table[current];
                entry.setState(TOMBSTONE);

                current = last;

                next_ok = false;

                TSBHashtableDA.this.count--;

                TSBHashtableDA.this.modCount++;
                expected_modCount++;
            }     
        }
    }


    private class EntrySet extends AbstractSet<Map.Entry<K, V>> 
    {

        @Override
        public Iterator<Map.Entry<K, V>> iterator() 
        {
            return new EntrySetIterator();
        }


        @Override
        public boolean contains(Object o) 
        {
            if (o == null) {
                return false;
            }
            if (!(o instanceof Entry)) {
                return false;
            }

            Entry<K, V> entry = (Entry<K, V>) o;

            int ik = TSBHashtableDA.this.h(entry.getKey());

            int indice = ik;
            int j = 1;

            Entry<K, V> x = (Entry<K, V>) TSBHashtableDA.this.table[ik];

            while (x.getState() != OPEN) {
                if (x.getState() == CLOSED) {
                    Entry<K, V> entryTable = x;
                    if(entryTable.equals(entry)) return true;
                }

                indice += j * j;
                j++;

                if (indice >= table.length) {
                    indice %= table.length;
                }

                x = (Entry<K, V>) TSBHashtableDA.this.table[indice];
            }

            return false;
        }


        @Override
        public boolean remove(Object o) 
        {
            if (o == null) {
                throw new NullPointerException("remove(): parámetro null");
            }
            if (!(o instanceof Entry)) {
                return false;
            }

            Entry<K, V> entry = (Entry<K, V>) o;

            int indice = TSBHashtableDA.this.h(entry.getKey());
            int ic = indice;
            int j = 1;

            Entry<K, V> entryTabla = (Entry<K, V>) TSBHashtableDA.this.table[indice];

            while (entryTabla.getState() != OPEN) {

                if (entryTabla.getState() == CLOSED) {
                    Entry<K, V> entryTable = entryTabla;

                    if(entryTable.equals(entry)){
                        entryTabla.setState(TOMBSTONE);

                        TSBHashtableDA.this.count--;
                        TSBHashtableDA.this.modCount++;

                        return true;
                    }
                }

                ic += j * j;
                j++;
                if (ic >= table.length) {
                    ic %= table.length;
                }
            }

            return false;
        }

        @Override
        public int size() 
        {
            return TSBHashtableDA.this.count;
        }

        @Override
        public void clear() 
        {
            TSBHashtableDA.this.clear();
        }
        
        private class EntrySetIterator implements Iterator<Map.Entry<K, V>>
        {
            private int current;
            private int next;

            private boolean next_ok;

            private int expected_modCount;


            public EntrySetIterator()
            {

                current= -1;
                next_ok = false;
                expected_modCount = TSBHashtableDA.this.modCount;
                next = current + 1;
            }


            @Override
            public boolean hasNext()
            {
                if(current >= table.length) { return false; }

                next = current + 1;

                if(next >= table.length)
                {
                    return false;
                }
                for (int i = next ; i < table.length; i++) {
                    Entry<K, V> entry = (Entry<K, V>) TSBHashtableDA.this.table[i];
                    if (entry.getState() == CLOSED)
                    {
                        next = i;
                        return true;
                    }
                }

                return false;
            }


            @Override
            public Map.Entry<K, V> next()
            {
                if(TSBHashtableDA.this.modCount != expected_modCount)
                {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }

                if(!hasNext())
                {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }

                Entry<K, V> t = (Entry<K, V>) TSBHashtableDA.this.table[next];

                current = next;

                next_ok = true;

                return t;
            }


            @Override
            public void remove()
            {
                if(!next_ok)
                {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }

                TSBHashtableDA.this.remove(current);
                next_ok = false;

                TSBHashtableDA.this.count--;

                TSBHashtableDA.this.modCount++;
                expected_modCount++;
            }     
        }
    }    

    private class ValueCollection extends AbstractCollection<V> 
    {
        @Override
        public Iterator<V> iterator() 
        {
            return new ValueCollectionIterator();
        }
        
        @Override
        public int size() 
        {
            return TSBHashtableDA.this.count;
        }
        
        @Override
        public boolean contains(Object o) 
        {
            return TSBHashtableDA.this.containsValue(o);
        }
        
        @Override
        public void clear() 
        {
            TSBHashtableDA.this.clear();
        }
        
        private class ValueCollectionIterator implements Iterator<V>
        {
            private int last_entry;

            private int current_entry;

            private int next_entry;
            private boolean next_ok;

            private int expected_modCount;

            public ValueCollectionIterator()
            {
                last_entry = 0;
                current_entry = -1;
                next_entry = current_entry +1;
                next_ok = false;
                expected_modCount = TSBHashtableDA.this.modCount;
            }


            @Override
            public boolean hasNext()
            {
                if(current_entry >= table.length) { return false; }

                next_entry = current_entry + 1;

                if(next_entry >= table.length)
                {
                    return false;
                }

                for (int i = next_entry ; i < table.length; i++) {
                    Entry<K, V> t = (Entry<K, V>) TSBHashtableDA.this.table[i];
                    if (t.getState() == CLOSED)
                    {
                        next_entry = i;
                        return true;
                    }
                }

                return false;
            }


            @Override
            public V next()
            {
                if (TSBHashtableDA.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }

                if (!hasNext()) {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }

                Entry<K, V> entry = (Entry<K, V>) TSBHashtableDA.this.table[next_entry];

                last_entry = current_entry;

                current_entry = next_entry;

                next_ok = true;

                V value = entry.getValue();

                return value;
            }


            @Override
            public void remove()
            {
                if (TSBHashtableDA.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("remove(): modificación inesperada de tabla...");
                }

                if (!next_ok) {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }


                Entry<K, V> entry = (Entry<K, V>) TSBHashtableDA.this.table[current_entry];

                entry.setState(TOMBSTONE) ;

                current_entry = last_entry;

                next_ok = false;

                TSBHashtableDA.this.count--;

                TSBHashtableDA.this.modCount++;
                expected_modCount++;
            }     
        }
    }
}
