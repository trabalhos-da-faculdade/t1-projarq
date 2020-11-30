package ecommerce2.servidor;

import java.io.*;
import java.net.*;

import ecommerce2.cliente.Client;
import ecommerce2.servidor.*;

public class Server {
    public static void main(String args[]){
        String clientData[] = new String[3];
        // Cliente clientesOnline[] = new Cliente[4];
        int ecoAux = 1;
        int userAux = 1;
        int prodAux = 1;
        Cliente clientesDatabase = Cliente.getInstance();
        Ecommerce ecommerceDatabase = new Ecommerce();
        
        try(ServerSocket serverSocket = new ServerSocket(8184);){
            System.out.println("Servidor rodando na Porta 8184!");
            while(true){
                Socket socket = serverSocket.accept();

                // writer envia dados ao Cliente
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output,true);

                // reader recebe dados do cliente
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String text;
                
                do{
                    text = reader.readLine();
                    System.out.println("Entrada: " + text);

                    if(text.equals("vincular")){
                        writer.println("vincular");
                        continue;
                    }

                    if(text.equals("produtos")){
                        writer.println("produtos");
                        continue;
                    }

                    if(text.equals("pedidos")){
                        writer.println("pedidos");
                        continue;
                    }

                    if(text.equals("sair")){
                        writer.println("exit");
                        continue;
                    }

                    if(text.substring(0,4).equals("data")){
                        clientData = text.split(":");
                        int cpf = Integer.parseInt(clientData[2]);
                        //Cliente addNew = new Cliente(userAux,clientData[1],cpf);
                        Cliente addNew = Cliente.getInstance();
                        addNew.setId(userAux);
                        addNew.setNome(clientData[1]);
                        addNew.setCpf(cpf);
                        clientesDatabase.clienteDatabase(addNew);
                        System.out.println("\nCliente " + clientData[1] + " foi Criado com Sucesso!\n");
                        writer.println("created");
                        userAux++;
                        clientesDatabase.getDatabase();
                        continue;
                    }

                    if(text.substring(0,4).equals("user")){
                        clientData = text.split(":");
                        int cpf = Integer.parseInt(clientData[2]);
                        boolean check = clientesDatabase.checkLogin(clientData[1], cpf);
                        if(check == true){
                            writer.println("granted");
                            System.out.println("\nCliente " + clientData[1] + " Logado com Sucesso!\n");
                        }else{
                            writer.println("error");
                        }
                        continue;
                    }

                    if(text.substring(0,3).equals("ped")){
                        String ped[] = new String[7];
                        ped = text.split(":");
                        int id = Integer.parseInt(ped[2]);
                        int tempo = Integer.parseInt(ped[5]);
                        Pedido pedido = new Pedido(id,ped[1],ped[3],ped[4],tempo,ped[6]);
                        ecommerceDatabase.addPedidosToDatabase(pedido,ped[1]);
                        writer.println("reset");
                        ecommerceDatabase.visualizePedidosDatabase();
                        continue;
                    }

                    if(text.substring(0,3).equals("eco")){
                        String eco[] = new String[2];
                        eco = text.split(":");
                        Ecommerce newEco = new Ecommerce(ecoAux,eco[1]);
                        ecommerceDatabase.addEcommerceToDatabase(newEco);
                        writer.println("eco created");
                        ecommerceDatabase.visualizeEcommerceDatabase();
                        ecoAux++;
                        continue;
                    }

                    if(text.substring(0,4).equals("prod")){
                        String prod[] = new String[5];
                        prod = text.split(":");
                        double preco = Double.parseDouble(prod[4]);
                        Produto produto = new Produto(prodAux,prod[1],prod[2],prod[3],preco);
                        ecommerceDatabase.addProdutosToDatabase(produto,prod[1]);
                        ecommerceDatabase.visualizeProdutosDatabase();
                        prodAux++;
                        writer.println("reset");
                        continue;
                    }

                }while(!text.equals("sair"));
            }
        }catch(IOException e){
           System.err.println(e);
        }catch(NullPointerException e){
            System.err.println("Null info");
        }
    }
}