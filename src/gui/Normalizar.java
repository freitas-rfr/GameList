package gui;

import java.text.Normalizer;

public class Normalizar {

    public String normalizaNome(String nome, String tipo) {
        String normalizado;
        int contador = 0;
        String extensao = "";
        StringBuilder novoNome = new StringBuilder();
        try {

            if (tipo.equals("rom")) {
                extensao = nome.substring(nome.lastIndexOf("."), nome.length());
                nome = nome.replace(extensao, "");
            }

            normalizado = padronizarNome(nome);

            String[] split = normalizado.split(" ");
            for (int i = 0; i < split.length; i++) {
                contador++;
                String capitalize = capitalize(split[i]);
                if (contador == split.length) {
                    novoNome.append(capitalize);
                    if (tipo.equals("rom")) {
                        novoNome.append(extensao);
                    }
                } else {
                    novoNome.append(capitalize);
                    novoNome.append(" ");
                }
            }

        } catch (Exception ex) {
            System.out.println(" - Erro ao normalizar o nome do nome\n" + ex);
        }

        return novoNome.toString();
    }


    private String padronizarNome(String nomes) {
        String normalizado;
        normalizado = Normalizer.normalize(nomes, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toLowerCase();
        return normalizado;
    }

    public static String capitalize(String name) {
        if ((name == null) || (name.length() == 0)) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

//    public static void main(String args[]) {
//        Normalizar exe = new Normalizar();
//        String normalizaNome = exe.normalizaNome("A.S.P. - Air Strike Patrol (USA).zip", "rom");
//        System.out.println(normalizaNome);
//    }
}
