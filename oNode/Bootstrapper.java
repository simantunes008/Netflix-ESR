public class Bootstrapper implemens Runnable{

    private String name;
    private List<InetAdress> my_neighbors;

    public BootstrapperServer(String name){
        this.name = name;
        this.my_neighbors = new ArrayList<>();
    }

    public void parserFile(String name) {
        String path = "../config_files/" + this.name;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    break; 
                }
                InetAddress ip_n = InetAddress.getByName(line);
                this.my_neighbors.add(ip_n);
                System.out.printls("Neighbor: " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}