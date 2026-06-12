// Ariel Olea y Santiago González
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Experimento
{
    // Clase interna para almacenar las metricas
    private class Metrics
    {
        int purchaseTotal = 0, queryTotal = 0, lendTotal = 0, receiveTotal = 0, disposeTotal = 0;
        int querySucc = 0, queryFail = 0, lendSucc = 0, lendFail = 0, receiveSucc = 0, receiveFail = 0;
        double elapsed = 0.0;
        int finalSize = 0;
        int finalHeight = 0;
    }

    public void run() {
        // Tamaños m = 2^t, con t desde 12 hasta 19
        int[] tValues = { 12, 13, 14, 15, 16, 17, 18, 19 };
        File dataDir = new File("data");
        if (!dataDir.exists()) { dataDir.mkdirs(); }
        for (int t : tValues)
        {
            int m = (int) Math.pow(2, t);
            int keyUniverse = 4 * m;
            String filename = "data/inventory_experiment_" + m + ".csv";
            try (FileWriter writer = new FileWriter(filename))
            {
                // 1. Escribir el encabezado del CSV
                writer.write("instancia,estructura,m,purchase_total,query_total,lend_total,receive_total," + "dispose_total,query_successful,query_failed,lend_successful,lend_failed," + "receive_successful,receive_failed,final_size,final_height,elapsed_seconds\n");
                for (int i = 1; i <= 30; i++)
                {
                    long seed = m + i;
                    ArrayList<InventoryOperation> opsBST = DataGenerator.generateOperations(m, keyUniverse, seed);
                    ArrayList<InventoryOperation> opsRB = DataGenerator.generateOperations(m, keyUniverse, seed);
                    BSTInventoryIndex bst = new BSTInventoryIndex();
                    RedBlackBSTInventoryIndex rb = new RedBlackBSTInventoryIndex();
                    // 2. Ejecutar y medir
                    Metrics mBST = executeAndMeasure(bst, opsBST);
                    Metrics mRB = executeAndMeasure(rb, opsRB);
                    // 3. Validar correctitud
                    validateCorrectness(bst, rb, m);
                    // 4. Escribir resultados en el CSV
                    writeMetrics(writer, i, "BST", m, mBST);
                    writeMetrics(writer, i, "RedBlackBST", m, mRB);
                }
            }
            catch (IOException e) { System.err.println("Error al escribir el archivo: " + e.getMessage()); }
        }
    }
    // Metodo que ejecuta las operaciones y retorna las metricas
    private Metrics executeAndMeasure(InventoryIndex index, ArrayList<InventoryOperation> ops)
    {
        Metrics metrics = new Metrics();
        StopwatchCPU timer = new StopwatchCPU();
        for (InventoryOperation op : ops)
        {
            int key = op.getKey();
            if (op.getType() == OperationType.PURCHASE)
            {
                metrics.purchaseTotal++;
                InventoryItem existing = index.get(key);
                if (existing == null) { index.put(key, op.getItem()); } // Componente nuevo
                else {
                    existing.addStock(op.getQuantity()); // Actualizar stock de existente
                    index.put(key, existing);
                }
            } else if (op.getType() == OperationType.QUERY) {
                metrics.queryTotal++;
                InventoryItem item = index.get(key);
                if (item != null) metrics.querySucc++;
                else metrics.queryFail++;
            } else if (op.getType() == OperationType.LEND) {
                metrics.lendTotal++;
                InventoryItem item = index.get(key);
                if (item != null && item.lend(op.getQuantity())) metrics.lendSucc++;
                else metrics.lendFail++;
            } else if (op.getType() == OperationType.RECEIVE) {
                metrics.receiveTotal++;
                InventoryItem item = index.get(key);
                if (item != null && item.receive(op.getQuantity())) metrics.receiveSucc++;
                else metrics.receiveFail++;
            } else if (op.getType() == OperationType.DISPOSE) {
                metrics.disposeTotal++;
                index.delete(key);
            }
        }
        metrics.elapsed = timer.elapsedTime();
        metrics.finalSize = index.size();
        metrics.finalHeight = index.height();
        return metrics;
    }
    // Metodo de validación post-experimento
    private void validateCorrectness(InventoryIndex bst, InventoryIndex rb, int m)
    {
        // El tamaño final debe ser exactamente el mismo
        if (bst.size() != rb.size()) { throw new RuntimeException("Los tamaños finales no coinciden."); }
        // Validamos hasta 100 claves
        int count = 0;
        for (Integer key : bst.keys())
        {
            InventoryItem itemB = bst.get(key);
            InventoryItem itemR = rb.get(key);
            // Verificamos nulos y consistencia en ambos arboles
            if (itemR == null || !itemB.getName().equals(itemR.getName()) || itemB.getStockAvailable() != itemR.getStockAvailable()) { throw new RuntimeException("Inconsistencia entre BST y RedBlackBST en clave " + key); }
            // No stocks negativos
            if (itemB.getStockAvailable() < 0 || itemB.getStockOnLoan() < 0) { throw new RuntimeException("Stocks negativos detectados en " + key); }
            // La suma de disponibles y prestados equivale al total
            if ((itemB.getStockAvailable() + itemB.getStockOnLoan()) != itemB.getStockTotal()) { throw new RuntimeException("La suma de stocks no cuadra en " + key); }
            count++;
            if (count >= 100) break;
        }
    }
    // Metodo auxiliar para imprimir de forma limpia las líneas del CSV
    private void writeMetrics(FileWriter writer, int instancia, String estructura, int m, Metrics met) throws IOException
    { writer.write(String.format("%d,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%.6f\n", instancia, estructura, m, met.purchaseTotal, met.queryTotal, met.lendTotal, met.receiveTotal, met.disposeTotal, met.querySucc, met.queryFail, met.lendSucc, met.lendFail, met.receiveSucc, met.receiveFail, met.finalSize, met.finalHeight, met.elapsed)); }
    // Punto de entrada del programa
    public static void main(String[] args)
    {
        Experimento exp = new Experimento();
        exp.run();
    }
}