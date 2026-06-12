// Ariel Olea y Santiago González
public class BSTInventoryIndex implements InventoryIndex
{
    // Atributo interno que utiliza la clase de Princeton
    private BST<Integer, InventoryItem> st;

    // Constructor que inicializa el árbol vacío
    public BSTInventoryIndex() { this.st = new BST<>(); }

    @Override
    public void put(Integer key, InventoryItem value) { st.put(key, value); } // Delega la inserción en la clase de Princeton

    @Override
    public InventoryItem get(Integer key) { return st.get(key); } // Delega la búsqueda

    @Override
    public void delete(Integer key) { st.delete(key); } // Delega la eliminación

    @Override
    public boolean contains(Integer key) { return st.contains(key); } // Verifica si la clave ya existe

    @Override
    public Iterable<Integer> keys() { return st.keys(); } // Retorna un iterador con todas las claves presentes

    @Override
    public int size() { return st.size(); } // Retorna la cantidad de nodos actuales

    @Override
    public int height() { return st.height(); } // Retorna la altura del árbol para el análisis estadístico
}