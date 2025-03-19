package dataset;

public interface DataSetInterface {
    public void importFromCSV(String file) throws Exception;
    public void exportToCSV(String file) throws Exception;
}
