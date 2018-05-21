import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;


public class main {
    private static ArrayList<String> arquivosTemporarios_A;
    private static ArrayList<String> arquivosTemporarios_B;
    private static int memoriaDisponivel = 16; //Valor arbitrário
    private static String arquivoFinal;

    public static void main(String[] args) {
        String string = "texto.txt";
        arquivoFinal = "output.txt";
        int numeroArqTemp = 0;

        //Arquivos temporários são armazenados em vetores para facilitar a sua chamada posterior
        arquivosTemporarios_A = new ArrayList<String>();
        arquivosTemporarios_B = new ArrayList<String>();

        if (!status(string, numeroArqTemp)) {
            //"rodada" determinará em qual arquivo temporário será escrita a sequência de palavras 
            int rodada = 1;
            String arquivoImpar = null;

            
            //Loop principal do programa.
            while (true) {
                boolean arquivoPar = false;
                numeroArqTemp = 0;
                if (rodada % 2 == 1) {
                    //Se for o primeiro item do vetor, não há com o que comparar
                    if (arquivosTemporarios_A.size() == 1){
                        break;
                    }
                    if (arquivosTemporarios_A.size() % 2 == 0){
                        arquivoPar = true;
                    }
                    int a = 0, b = 1;
                    for (int i = 0; i < arquivosTemporarios_A.size() / 2; i++) {
                        merge(arquivosTemporarios_A.get(a), arquivosTemporarios_A.get(b), rodada, numeroArqTemp, false);
                        numeroArqTemp++;
                        a = a + 2;
                        b = b + 2;
                    }
                    if (!arquivoPar && arquivoImpar == null) {
                        arquivoImpar = arquivosTemporarios_A.get(arquivosTemporarios_A.size() - 1);
                    } else if (!arquivoPar && arquivoImpar != null) {
                        merge(arquivoImpar, arquivosTemporarios_A.get(arquivosTemporarios_A.size() - 1), rodada, numeroArqTemp, false);
                        arquivoImpar = null;
                    }
                    arquivosTemporarios_A.clear();
                } else {
                    if (arquivosTemporarios_B.size() == 1)
                        break;
                    if (arquivosTemporarios_B.size() % 2 == 0)
                        arquivoPar = true;
                    int a = 0, b = 1;
                    for (int i = 0; i < arquivosTemporarios_B.size() / 2; i++) {
                        merge(arquivosTemporarios_B.get(a), arquivosTemporarios_B.get(b), rodada, numeroArqTemp, false);
                        numeroArqTemp++;
                        a = a + 2;
                        b = b + 2;
                    }
                    if (!arquivoPar && arquivoImpar == null) {
                        arquivoImpar = arquivosTemporarios_B.get(arquivosTemporarios_B.size() - 1);
                    } else if (!arquivoPar && arquivoImpar != null) {
                        merge(arquivoImpar, arquivosTemporarios_B.get(arquivosTemporarios_B.size() - 1), rodada, numeroArqTemp, false);
                        arquivoImpar = null;
                    }
                    arquivosTemporarios_B.clear();
                }
                rodada++;
            }
            //Dá merge nos arquivos temporários e escreve no arquivo final
            if (arquivoImpar != null) {

                if (arquivosTemporarios_A.size() == 1){
                    merge(arquivoImpar, arquivosTemporarios_A.get(arquivosTemporarios_A.size() - 1), rodada - 1, 0, true);
                }
                if (arquivosTemporarios_B.size() == 1){
                    merge(arquivoImpar, arquivosTemporarios_B.get(arquivosTemporarios_B.size() - 1), rodada - 1, 0, true);
                }

            } else if (arquivosTemporarios_A.size() == 1) {

                File f = new File(arquivosTemporarios_A.get(0));
                File o = new File(arquivoFinal);
                f.renameTo(o);

            } else if (arquivosTemporarios_B.size() == 1) {

                File f = new File(arquivosTemporarios_B.get(0));
                File o = new File(arquivoFinal);
                f.renameTo(o);

            }


        }
    }

    private static boolean status(String string, int numeroArqTemp) {
        File arqAux = new File(string);
        //Carrega o arquivo auxiliar no Scanner
        try {
            Scanner input = new Scanner(arqAux);
            //int numeroArqTemp = 0;
            int rodada = 0;
            int contadorStrings = 0;
            while (input.hasNext()) {
                ArrayList<String> temp = new ArrayList<String>();
                int i = 0;
                while ( input.hasNext() && i < memoriaDisponivel) {
                    temp.add(input.next());
                    contadorStrings++;
                    i++;
                }
                if (!input.hasNext() && contadorStrings < memoriaDisponivel) {
                    temp = ordena(temp);
                    grava(temp, rodada, numeroArqTemp, true);
                    input.close();
                    return true;
                }
                //Adiciona o número de arquivos temporários para a montagem dos mesmos
                temp = ordena(temp);
                grava(temp, rodada, numeroArqTemp, false);
                numeroArqTemp++;
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.exit(1);
        }

        for (int z = 0; z < numeroArqTemp; z++){

            String delete = "output" + z + ".txt";                
            File file = new File(delete);
            file.delete();
        }
        return false;
    }

    //Função que une os arquivos temporários
    private static void merge(String temp1, String temp2, int rodada, int numeroArqTemp, boolean acabou) {
        ArrayList<String> auxiliar = new ArrayList<String>();
        FileReader leitor1;
        FileReader leitor2;
        String palavra1 = ""; 
        String palavra2 = "";
        try {
            leitor1 = new FileReader(temp1);
            leitor2 = new FileReader(temp2);
            boolean cont1 = true;
            boolean cont2 = true;
            BufferedReader buff1 = new BufferedReader(leitor1);
            BufferedReader buff2 = new BufferedReader(leitor2);

            try {
                //Loop que realiza as comparações necessárias antes de gravar os arquivos
                while (true) {
                    if (cont1)
                        if ((palavra1 = buff1.readLine()) == null)
                            break;
                    if (cont2)
                        if ((palavra2 = buff2.readLine()) == null)
                            break;
                    if (palavra1.compareTo(palavra2) <= 0) {
                        auxiliar.add(palavra1);
                        cont2 = false;
                        cont1 = true;
                    } else {
                        auxiliar.add(palavra2);
                        cont1 = false;
                        cont2 = true;
                    }
                }
                if (palavra1 != null) {
                    auxiliar.add(palavra1);
                    while ((palavra1 = buff1.readLine()) != null) {
                        auxiliar.add(palavra1);
                    }
                }
                if (palavra2 != null) {
                    auxiliar.add(palavra2);
                    while ((palavra2 = buff2.readLine()) != null) {
                        auxiliar.add(palavra2);
                    }
                }
                grava(auxiliar, rodada, numeroArqTemp, acabou);
                buff1.close();
                buff2.close();
            } catch (IOException e) {
                System.exit(1);
            }

        } catch (FileNotFoundException e) {
            System.exit(1);
        }
    }
    //Função que grava as palavras nos arquivos temporários e depois no arquivo final, quando possível
    private static void grava(ArrayList<String> auxPalavras, int rodada, int numeroArqTemp, boolean acabou) {
        try {
            if (!acabou) {
                //Se a ordenação ainda não acabou, grava nos temporários
                Path file = Paths.get("output" + String.valueOf(numeroArqTemp) + ".txt");
                if (rodada % 2 == 0) {
                    arquivosTemporarios_A.add("output" + String.valueOf(numeroArqTemp) + ".txt");
                } else {
                    arquivosTemporarios_B.add("output" + String.valueOf(numeroArqTemp) + ".txt");
                }
                Files.write(file, auxPalavras);
            //Se a ordenação acabou, grava no arquivo final    
            } else {
                Path file = Paths.get(arquivoFinal);
                Files.write(file, auxPalavras);
            }
        } catch (IOException e) {
            System.exit(1);
        }
    }

    //Função auxiliar que organiza o vetor, necessária para a verificação do status
    private static ArrayList<String> ordena(ArrayList<String> auxPalavras) {
        for (int i = 0; i < auxPalavras.size(); i++) {
            for (int j = 0; j <= i; j++) {
                if (auxPalavras.get(i).compareTo(auxPalavras.get(j)) <= 0) {
                    String temp = auxPalavras.get(j);
                    auxPalavras.set(j, auxPalavras.get(i));
                    auxPalavras.set(i, temp);
                }
            }
        }
        return auxPalavras;
    }

}
