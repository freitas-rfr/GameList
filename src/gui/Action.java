package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class Action {

    public List<String> listarCapas(String dir) {
        Normalizar nor = new Normalizar();
        List<String> listaNomesCapas = new ArrayList<>();
        File file = new File(dir);
        File afile[] = file.listFiles(filterPng);
        for (File dados : afile) {
            String normalizaNome = nor.normalizaNome(dados.getName(), "rom");
            String extensao = normalizaNome.substring(normalizaNome.lastIndexOf("."), normalizaNome.length());
            normalizaNome = normalizaNome.replace(extensao, "");
            listaNomesCapas.add(normalizaNome);
        }
        return listaNomesCapas;
    }

    public List<String> listarRons(String dir) {
        Normalizar nor = new Normalizar();
        List<String> listaCapas = new ArrayList<>();
        File file = new File(dir);
        File afile[] = file.listFiles(filterZip);
        for (File dados : afile) {
            String normalizaNome = nor.normalizaNome(dados.getName(), "rom");
            String extensao = normalizaNome.substring(normalizaNome.lastIndexOf("."), normalizaNome.length());
            normalizaNome = normalizaNome.replace(extensao, "");
            listaCapas.add(normalizaNome);
        }
        return listaCapas;
    }

    // create new filename filter
    FilenameFilter filterZip = (File dir, String name) -> {
        if (name.lastIndexOf('.') > 0) {
            // get last index for '.' char
            int lastIndex = name.lastIndexOf('.');
            // get extension
            String str = name.substring(lastIndex);
            // match path name extension
            if (str.equals(".zip")) {
                return true;
            }
        }
        return false;
    };

    // create new filename filter
    FilenameFilter filterPng = (File dir, String name) -> {
        if (name.lastIndexOf('.') > 0) {
            // get last index for '.' char
            int lastIndex = name.lastIndexOf('.');
            // get extension
            String str = name.substring(lastIndex);
            // match path name extension
            if (str.equals(".png")) {
                return true;
            }
        }
        return false;
    };

    public void copiarArquivo(File source, File destination) {
        try {
            if (destination.exists()) {
                destination.delete();
            }

            FileChannel sourceChannel;
            FileChannel destinationChannel;

            sourceChannel = new FileInputStream(source).getChannel();
            destinationChannel = new FileOutputStream(destination).getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);

            sourceChannel.close();
            destinationChannel.close();

        } catch (IOException ex) {
            System.out.println("Erro ao copiar arquivo. " + ex);
        }
    }

//    public List<String> listarNomeCapasCompleto(String dir) {
//        List<String> listaNomesCapas = new ArrayList<>();
//        File file = new File(dir);
//        File afile[] = file.listFiles();
//        for (File dados : afile) {
//            listaNomesCapas.add(dados.getName());
//        }
//        return listaNomesCapas;
//    }
//    public void renomearArquivo(String nomeAntigo, String novoNome, String origem) {
//        File file = new File(origem + File.separator + nomeAntigo);
//        file.renameTo(new File(origem, novoNome));
//
//    }
//
//    public void moverRom(String arquivo, String origem, String destino) {
//        File file = new File(origem + File.separator + arquivo + ".zip");
//        file.renameTo(new File(destino, file.getName()));
//    }
//
//    public void moverCapa(String arquivo, String origem, String destino) {
//        File file = new File(origem + File.separator + arquivo);
//        file.renameTo(new File(destino, file.getName()));
//    }
}
