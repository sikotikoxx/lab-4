// Ariel Olea y Santiago González
public class RedBlackBSTInventoryIndex implements InventoryIndex
{
    // Atributo interno que utiliza la clase balanceada de Princeton
    private RedBlackBST<Integer, InventoryItem> st;

    // Constructor que inicializa el árbol balanceado vacío
    public RedBlackBSTInventoryIndex() { this.st = new RedBlackBST<>(); }

    @Override
    public void put(Integer key, InventoryItem value) { st.put(key, value); } // Delega la inserción balanceada

    @Override
    public InventoryItem get(Integer key) { return st.get(key); }

    @Override
    public void delete(Integer key) { st.delete(key); }

    @Override
    public boolean contains(Integer key) { return st.contains(key); }

    @Override
    public Iterable<Integer> keys() { return st.keys(); }

    @Override
    public int size() { return st.size(); }

    @Override
    public int height() { return st.height(); } // Clave para contrastar el balanceo contra el BST normal
}