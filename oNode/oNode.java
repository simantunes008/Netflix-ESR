public class oNode {

    public static void main(String[] args){
        private List<String> neighbors = new ArrayList<>();

        // oNode -[type of node] name

        if(args.length == 3 && args[1].equals("-bootstrapper")){
                String name = args[2];
                Bootstrapper = new Bootstrapper(name);
            
        } else if (args.length == 3 && args[2].equals("-client")){
                Client client = new Client();

        } else if (args.length == 3 && args[2].equals("-server")){
                Server server = new Server();
        }
    }
}
