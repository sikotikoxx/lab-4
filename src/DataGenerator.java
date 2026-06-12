// Ariel Olea y Santiago González
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DataGenerator
{
    // Arreglos fijos sugeridos por la guia
    private static final String[] CATEGORIES = { "Sensor", "Motor", "Microcontrolador", "Cable", "Bateria", "Herramienta", "Modulo", "Kit" };

    private static final String[] LOCATIONS = { "Estante_A", "Estante_B", "Caja_1", "Caja_2", "Laboratorio", "Bodega" };

    // Metodo para generar un componente individual
    public static InventoryItem generateItem(int id)
    {
        String name = "Componente " + id;
        // StdRandom.uniform(n) retorna un entero entre 0 (inclusivo) y n (exclusivo)
        String category = CATEGORIES[StdRandom.uniform(CATEGORIES.length)];
        String location = LOCATIONS[StdRandom.uniform(LOCATIONS.length)];
        // StdRandom.uniform(a, b) retorna un entero entre a (inclusivo) y b (exclusivo)
        int stockTotal = StdRandom.uniform(1, 21); // Rango de 1 a 20
        return new InventoryItem(id, name, category, location, stockTotal, stockTotal, 0); // El disponible inicia igual al total, y el prestado en 0
    }
    // Metodo para generar la lista de operaciones
    public static ArrayList<InventoryOperation> generateOperations(int m, int keyUniverse, long seed)
    {
        StdRandom.setSeed(seed); // Fijar la semilla
        ArrayList<InventoryOperation> operations = new ArrayList<>(m);
        Set<Integer> currentInventoryIds = new HashSet<>(); // Estructura interna para controlar si un ID ya esta en el inventario o no
        for (int i = 0; i < m; i++)
        {
            int key = StdRandom.uniform(1, keyUniverse + 1); // Clave aleatoria entre 1 y keyUniverse (ambos inclusivos)
            double p = StdRandom.uniform(0.0, 1.0); // Probabilidad para seleccionar el tipo de operación
            OperationType type;
            int quantity = 0;
            InventoryItem item = null;
            if (p < 0.35)
            { // 35% de probabilidad
                type = OperationType.PURCHASE;
                quantity = StdRandom.uniform(1, 6); // Rango de 1 a 5
                // Si la clave no esta en el inventario, creamos el ítem
                if (!currentInventoryIds.contains(key))
                {
                    item = generateItem(key);
                    currentInventoryIds.add(key); // Lo registramos como existente
                }
            } else if (p < 0.65) { // 30% de probabilidad (0.35 + 0.30)
                type = OperationType.QUERY;
                // quantity se queda en 0 y el item en null
            } else if (p < 0.80) { // 15% de probabilidad (0.65 + 0.15)
                type = OperationType.LEND;
                quantity = StdRandom.uniform(1, 6);
            } else if (p < 0.90) { // 10% de probabilidad (0.80 + 0.10)
                type = OperationType.RECEIVE;
                quantity = StdRandom.uniform(1, 6);
            } else { // 10% de probabilidad restante
                type = OperationType.DISPOSE;
                currentInventoryIds.remove(key);
            }
            // Agregamos la operación a la lista
            operations.add(new InventoryOperation(type, key, quantity, item));
        }
        return operations;
    }
}