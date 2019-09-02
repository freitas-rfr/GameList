package gui;

import Bean.GameListBean;
import java.awt.Color;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import xml.XMLOperations;

public class Inicial extends javax.swing.JFrame {

    private String capas;
    private String rons;
    private String gamelist;
    private String dirXML;
    private String nomeCapaGenerica;
    private Boolean flagCapaGenerica;

    private List<String> listaNomeCapas;
    private List<String> listaNomeRons;
    private List<GameListBean> gameList;

    private Integer contador;
    private final JFileChooser fileChooser;

    private final Action exec;

    public Inicial() {
        initComponents();
        contador = 0;
        flagCapaGenerica = false;
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/joystick.png")));
        fileChooser = new JFileChooser();
        exec = new Action();
        UIManager.getDefaults().put("OptionPane.background", new Color(255, 255, 255));
        UIManager.put("Panel.background", new Color(255, 255, 255));
    }

    private void buscarCaminho(String acao) {
        String caminho;
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setApproveButtonText("Selecionar");
        fileChooser.setDialogTitle(acao.toUpperCase());
        int i = fileChooser.showOpenDialog(this);
        if (i != 1) {
            caminho = fileChooser.getSelectedFile().getAbsolutePath();
            //GAME LIST---------------------------------------
            if (acao.equals("capas")) {
                capas = caminho;
                cp_dirCapasGameList.setText(caminho);
            }
            if (acao.equals("rons")) {
                rons = caminho;
                cp_dirRonsGameList.setText(caminho);
            }
            if (acao.equals("gamelist")) {
                gamelist = caminho;
                cp_dirDestinoGameList.setText(caminho);
            }

            File tempDir = new File(caminho);
            fileChooser.setCurrentDirectory(tempDir);
        }

    }

    private void buscarArquivo(String acao) {
        String caminho;
        String arquivo;
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG (*.png)", "png");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setApproveButtonText("Selecionar");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setFileFilter(filter);
        fileChooser.setDialogTitle(acao.toUpperCase());
        int i = fileChooser.showOpenDialog(this);
        if (i != 1) {
            caminho = fileChooser.getSelectedFile().getAbsolutePath();
            arquivo = fileChooser.getSelectedFile().getName();
            //GAME LIST---------------------------------------
            if (acao.equals("Capa Genérica")) {
                cp_dirCapaGenerica.setText(caminho);
                nomeCapaGenerica = arquivo;
            }
            File tempDir = new File(caminho);
            fileChooser.setCurrentDirectory(tempDir);
        }
    }

    public void barraGameList() {
        new Thread() {

            @Override
            public void run() {
                do {
                    progressBarGameList.setValue(contador);
                } while (contador < progressBarGameList.getMaximum());
                JOptionPane.showMessageDialog(null, "O arquivo gamelist.xml foi gerado.", "Atenção!", JOptionPane.INFORMATION_MESSAGE);
            }
        }.start();
    }

    private void iniciarGameList() {
        if ("".equals(cp_dirCapaGameListXml.getText())) {
            JOptionPane.showMessageDialog(this, "Informe o diretório de capas do arquivo gamelist.xml.", "Atenção!", JOptionPane.INFORMATION_MESSAGE);
        } else if ("".equals(cp_dirRonsGameList.getText())) {
            JOptionPane.showMessageDialog(this, "Informe o diretório de origem das rons.", "Atenção!", JOptionPane.INFORMATION_MESSAGE);
        } else if (cp_dirCapasGameList.getText().equals(cp_dirRonsGameList.getText())) {
            JOptionPane.showMessageDialog(this, "O diretório de rons não pode ser igual ao de capas", "Atenção!", JOptionPane.INFORMATION_MESSAGE);
        } else if ("".equals(cp_dirDestinoGameList.getText())) {
            JOptionPane.showMessageDialog(this, "Informe onde o novo gamelist.xml sera salvo.", "Atenção!", JOptionPane.INFORMATION_MESSAGE);
        } else {

            flagCapaGenerica = !"".equals(cp_dirCapaGenerica.getText());
            dirXML = cp_dirCapaGameListXml.getText();
            listaNomeCapas = exec.listarCapas(capas);
            listaNomeRons = exec.listarRons(rons);

            Collections.reverse(listaNomeCapas);
            Collections.reverse(listaNomeRons);

            if (flagCapaGenerica) {
                copiarCapaGenerica();
            }

            if (!listaNomeCapas.isEmpty() && !listaNomeRons.isEmpty()) {
                progressBarGameList.setMaximum(listaNomeRons.size());
                gerarXML(true, flagCapaGenerica);
                barraGameList();
            } else if (!listaNomeCapas.isEmpty() && listaNomeRons.isEmpty()) {
                progressBarGameList.setMaximum(listaNomeRons.size());
                gerarXML(false, flagCapaGenerica);
                barraGameList();
            } else {
                JOptionPane.showMessageDialog(this, "Nenhuma rom foi encontrada, verifique o diretório informado", "Atenção!", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void copiarCapaGenerica() {
        File source = new File(cp_dirCapaGenerica.getText());
        File destination = new File(cp_dirCapasGameList.getText() + File.separator + nomeCapaGenerica);
        exec.copiarArquivo(source, destination);
    }

    public void gerarXML(boolean comCapa, boolean capaGenerica) {
        new Thread() {
            @Override
            public void run() {
                XMLOperations xml = new XMLOperations();
                Normalizar nor = new Normalizar();
                gameList = new ArrayList<>();
                GameListBean game;
                contador = 0;
                boolean flag;
                for (String nomeRom : listaNomeRons) {
                    contador++;
                    flag = false;
                    game = new GameListBean();

                    //SEM CAPA
                    if (!comCapa) {
                        game.setName(nomeRom);
                        game.setPath("./" + nomeRom + ".zip");
                        if (capaGenerica) {
                            game.setImage("./" + dirXML + "/" + nomeCapaGenerica);
                        }
                    }

                    //COM CAPA
                    if (comCapa) {
                        for (String nomeCapa : listaNomeCapas) {

                            String nomeCapaReplace = nomeCapa.replace(cp_prefixoGameList.getText(), "").replace(cp_sufixoGameList.getText(), "");
                            String nomeCapaLow = nor.normalizaNome(nomeCapaReplace, "").toUpperCase();
                            String nomeRomLow = nor.normalizaNome(nomeRom, "").toUpperCase();

                            if (nomeCapaLow.equals(nomeRomLow)) {
                                game.setImage("./" + dirXML + "/" + nomeCapa + ".png");
                                game.setName(nomeRom);
                                game.setPath("./" + nomeRom + ".zip");
                                flag = true;
                            }
                        }
                        if (!flag) {
                            game.setName(nomeRom);
                            game.setPath("./" + nomeRom + ".zip");
                            if (capaGenerica) {
                                game.setImage("./" + dirXML + "/" + nomeCapaGenerica);
                            }
                        }
                    }
                    gameList.add(game);
                }
                xml.gerarXML(gameList, gamelist);
            }
        }.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        cp_dirCapasGameList = new javax.swing.JTextField();
        bt_abrirCapasGameList = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        cp_dirRonsGameList = new javax.swing.JTextField();
        bt_abrirRonsGameList = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        progressBarGameList = new javax.swing.JProgressBar();
        bt_iniciarGameList = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        cp_sufixoGameList = new javax.swing.JTextField();
        cp_prefixoGameList = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        cp_dirCapaGameListXml = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        cp_dirCapaGenerica = new javax.swing.JTextField();
        bt_abrirCapasGenerica = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        cp_dirDestinoGameList = new javax.swing.JTextField();
        bt_selDestinoGameList = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GameList 1.0v");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(0, 0, 51));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Capas");

        cp_dirCapasGameList.setEditable(false);
        cp_dirCapasGameList.setBackground(new java.awt.Color(255, 255, 255));

        bt_abrirCapasGameList.setText("Selecionar");
        bt_abrirCapasGameList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_abrirCapasGameListActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel9.setText("Rons");

        cp_dirRonsGameList.setEditable(false);
        cp_dirRonsGameList.setBackground(new java.awt.Color(255, 255, 255));

        bt_abrirRonsGameList.setText("Selecionar");
        bt_abrirRonsGameList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_abrirRonsGameListActionPerformed(evt);
            }
        });

        progressBarGameList.setStringPainted(true);

        bt_iniciarGameList.setText("Iniciar");
        bt_iniciarGameList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_iniciarGameListActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel10.setText("Progresso");

        jLabel12.setText("Ignorar:");

        cp_sufixoGameList.setText("-image");

        jLabel13.setText("Sufixo");

        jLabel14.setText("Prefixo");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel15.setText("Diretório de capas do arquivo gamelist.xml");

        cp_dirCapaGameListXml.setText("downloaded_images");

        jLabel16.setText("Padrão: downloaded_images");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel17.setText("Capa genérica");

        cp_dirCapaGenerica.setEditable(false);
        cp_dirCapaGenerica.setBackground(new java.awt.Color(255, 255, 255));

        bt_abrirCapasGenerica.setText("Selecionar");
        bt_abrirCapasGenerica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_abrirCapasGenericaActionPerformed(evt);
            }
        });

        jLabel18.setText("(utilizado para rons que não possuem capas)");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Destino do arquivo gamelist.xml");

        cp_dirDestinoGameList.setEditable(false);
        cp_dirDestinoGameList.setBackground(new java.awt.Color(255, 255, 255));

        bt_selDestinoGameList.setText("Selecionar");
        bt_selDestinoGameList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_selDestinoGameListActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(363, 363, 363)
                        .addComponent(jLabel10)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(progressBarGameList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(bt_iniciarGameList)))))
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cp_dirCapasGameList, javax.swing.GroupLayout.PREFERRED_SIZE, 686, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_abrirCapasGameList))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cp_dirRonsGameList, javax.swing.GroupLayout.PREFERRED_SIZE, 686, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_abrirRonsGameList))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cp_prefixoGameList, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cp_sufixoGameList, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cp_dirCapaGameListXml))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cp_dirCapaGenerica, javax.swing.GroupLayout.PREFERRED_SIZE, 686, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_abrirCapasGenerica))
                    .addComponent(jLabel11)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cp_dirDestinoGameList, javax.swing.GroupLayout.PREFERRED_SIZE, 686, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_selDestinoGameList)))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cp_prefixoGameList, cp_sufixoGameList});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel15)
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cp_dirCapaGameListXml, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cp_dirCapaGenerica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bt_abrirCapasGenerica))
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cp_dirCapasGameList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bt_abrirCapasGameList))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel14)
                    .addComponent(cp_prefixoGameList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(cp_sufixoGameList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cp_dirRonsGameList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bt_abrirRonsGameList))
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cp_dirDestinoGameList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bt_selDestinoGameList))
                .addGap(27, 27, 27)
                .addComponent(jLabel10)
                .addGap(0, 0, 0)
                .addComponent(progressBarGameList, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bt_iniciarGameList)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Gamelist.xml", jPanel2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 799, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void bt_iniciarGameListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_iniciarGameListActionPerformed
        iniciarGameList();
    }//GEN-LAST:event_bt_iniciarGameListActionPerformed

    private void bt_abrirRonsGameListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_abrirRonsGameListActionPerformed
        buscarCaminho("rons");
    }//GEN-LAST:event_bt_abrirRonsGameListActionPerformed

    private void bt_abrirCapasGameListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_abrirCapasGameListActionPerformed
        buscarCaminho("capas");
    }//GEN-LAST:event_bt_abrirCapasGameListActionPerformed

    private void bt_abrirCapasGenericaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_abrirCapasGenericaActionPerformed
        buscarArquivo("Capa Genérica");
    }//GEN-LAST:event_bt_abrirCapasGenericaActionPerformed

    private void bt_selDestinoGameListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_selDestinoGameListActionPerformed
        buscarCaminho("gamelist");
    }//GEN-LAST:event_bt_selDestinoGameListActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Inicial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Inicial().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bt_abrirCapasGameList;
    private javax.swing.JButton bt_abrirCapasGenerica;
    private javax.swing.JButton bt_abrirRonsGameList;
    private javax.swing.JButton bt_iniciarGameList;
    private javax.swing.JButton bt_selDestinoGameList;
    private javax.swing.JTextField cp_dirCapaGameListXml;
    private javax.swing.JTextField cp_dirCapaGenerica;
    private javax.swing.JTextField cp_dirCapasGameList;
    private javax.swing.JTextField cp_dirDestinoGameList;
    private javax.swing.JTextField cp_dirRonsGameList;
    private javax.swing.JTextField cp_prefixoGameList;
    private javax.swing.JTextField cp_sufixoGameList;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JProgressBar progressBarGameList;
    // End of variables declaration//GEN-END:variables
}
