//package main;
//
//import commands.Command;
//import exceptions.*;
//import io.ContainerWrapper;
//import io.XmlUtil;
//import net.Request;
//import net.RequestType;
//import net.UdpClient;
//import net.UdpServer;
//import organization.Organization;
//
//import java.io.*;
//import java.util.*;
//
//import static java.nio.charset.StandardCharsets.UTF_8;
//
//
//public class theory6.Main {
//    public static void main(String[] args) {
//        Container<Organization> container = new Container<>();
//        Invoker invoker = new Invoker(container);
//
//        String filePath = System.getenv("LAB5_8");
//        if (filePath == null) {
//            System.err.println("Ошибка, невозможно найти переменную окружения LAB5_8. Поставьте значение LAB5_8 = 'initial_collection.xml'");
//        } else {
//            try{
//            container.addList(XmlUtil.readListFromFile(filePath));
//            } catch (XmlUtilException e){
//                System.err.println("!! "+e.getMessage()+" !!");
//            }
//        }
//        programExecute(invoker,null,null);
//    }
//
//
//    public static void programExecute(Invoker invoker, UdpClient client, UdpServer server) {
//        programExecute(invoker, null, client,server);
//    }
//
//    public static void programExecute(Invoker invoker, String path, UdpClient client, UdpServer server) {
//        if (path == null) {
//            run(invoker, new BufferedReader(new InputStreamReader(System.in, UTF_8)), false, client,server);
//        } else {
//            try (FileInputStream file = new FileInputStream(path)) {
//                BufferedReader br = new BufferedReader(new InputStreamReader(file, UTF_8));
//                run(invoker, br, true, client,server);
//            } catch (IOException e) {
//                System.err.println("Файл не найден: "+e.getMessage());
//            }
//        }
//    }
//
//    public static void run(Invoker invoker, BufferedReader br, boolean isScript, UdpClient client, UdpServer server){
//        while (true) {
//            if (!isScript) {
//                System.out.print("$user: ");
//            }
//
//            try {
//                String input = br.readLine();
//
//                if (isScript) {
//                    if (input == null) {
//                        System.out.println("~~Файл обработан полностью~~");
//                        break;
//                    }
//
//                    if (input.trim().isEmpty()) {
//                        continue;
//                    }
//                    //showing what command was
//                    System.out.println(input);
//                }
//                // non-client-server mode
//                if (client == null && server == null) {
//                    invoker.defineCommand(input, isScript).execute();
//
//                // server mode
//                } else if (client == null) {
//                    //todo добавить проверку на requesttype
//                    Request request = server.receiveMessage();
//                    Command command = invoker.defineCommand(request.commandName() + " "+ request.argument() + " " + request.xmlArgument(), request.isScript());
//                    System.out.println("Запрос от клиента: $>"+ command);
//
//                    Request request1 = Request.build(request.runnerId())
//                            .setRequestType(RequestType.INFORMATION)
//                            .setErrorOrInformation(command.feedback());
//
//                    server.sendMessage(request1);
//                // client mode
//                } else if (server == null) {
//                    Request request;
//                    try {
//                        request = invoker.defineCommand(input,isScript).createRequest();
//                    } catch (InvalidInput e) {
//                        throw new RuntimeException(e);
//                    }
//                    client.sendMessage(request);
//
//                    Request request1 = client.receiveMessage();
//                    System.out.println("Ответ от сервера: "+request1.errorOrInformation());
//                }
//
//            } catch (NoSuchCommandException e) {
//                System.err.println("!! " + e.getMessage() + " !!");
//
//                invoker.getAllCommands().get("help").execute();
//
//            } catch (NoSuchElementException | RecursionLimitReached |
//                     EmptyContainerException | XmlUtilException | IOException e) {
//                System.err.println("!! " + e.getMessage() + " !!");
//
//            }
//        }
//    }
//}
//
