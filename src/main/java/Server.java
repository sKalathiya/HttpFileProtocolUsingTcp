import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.tools.ant.types.Commandline;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Server {
    public static void main(String args[]) throws IOException {

        Scanner sc = new Scanner(System.in);
        String cmd = sc.nextLine();
        parse(cmd);
    }


    public static void connection(int port,String directory,boolean verbose) throws IOException {
        ServerSocket server = new ServerSocket(8080);

        System.out.println("Listening for connection on port 8080 ....");
        while (true) {
            try (Socket socket = server.accept()) {
                if(verbose){
                    System.out.println("Server accepted a new connection");
                }
                String request = "";
                InputStreamReader isr
                        = new InputStreamReader(socket.getInputStream());
                BufferedReader reader = new BufferedReader(isr);
                String line = reader.readLine();
                String[] w = line.split(" ");
                if(!w[0].equals("GET")) {

                    int empty = -1;
                    while (!(line.isEmpty() && empty > 0)) {
                        if (line.isEmpty()) {
                            empty++;
                        }
                        request = request + line + "\n";
                        line = reader.readLine();
                    }
                }
                else{
                    request = request + line;
                }
                if(verbose){
                    System.out.println("request received :   " + request + "\n");
                }

                String httpResponse = "";
                String[] words = request.split(" ");
                if (words[0].equals("GET")) {

                    httpResponse = HttpcLib.getResponse(request, directory,verbose);
                } else {
                    httpResponse = HttpcLib.postResponse(request,directory,verbose);
                }

                if(verbose){
                    System.out.println("response Created :\n   " + httpResponse +"\n");
                }
                socket.getOutputStream()
                        .write(httpResponse.getBytes("UTF-8"));

                if(verbose){
                    System.out.println("response Sent \nConnection closed \n");
                }

            }
        }
    }
        public static void parse(String cmd) throws IOException {
            String mysrgs[] = Commandline.translateCommandline(cmd);
            String r;
            if (mysrgs.length <= 1) {

                r = "Wrong Command";

            }
            if (!mysrgs[0].equalsIgnoreCase("httpfs")) {

                r = "Wrong command";

            }

            OptionParser parser = new OptionParser();
            parser.acceptsAll(Arrays.asList("verbose", "v"), "TimeServer hostname");
            parser.acceptsAll(Arrays.asList("port", "p"), "TimeServer hostname")
                    .withOptionalArg()
                    .defaultsTo("8080");
            parser.acceptsAll(Arrays.asList("directory", "d"), "TimeServer data")
                    .withOptionalArg()
                    .defaultsTo(".");

            OptionSet options = parser.parse(mysrgs);
            int port = Integer.parseInt((String) options.valueOf("p"));
            boolean verbose = options.has("v");
            String directory = (String) options.valueOf("d");
            connection(port,directory,verbose);
        }

}
