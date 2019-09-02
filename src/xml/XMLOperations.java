package xml;

import Bean.GameListBean;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;


public class XMLOperations {


    public void gerarXML(List<GameListBean> gameList, String destino) {

        XStream xStream = new XStream(new DomDriver("UTF-8"));

        xStream.alias("gameList", List.class);
        xStream.alias("game", GameListBean.class);

        File arquivo = new File(destino + "/gamelist.xml");
        String version = "<?xml version=\"1.0\"?>\n";

        try {
            OutputStreamWriter bufferOut = new OutputStreamWriter(new FileOutputStream(arquivo), "UTF-8");
            bufferOut.write(version);
            bufferOut.write(xStream.toXML(gameList));
            bufferOut.close();
        } catch (IOException ex) {
            System.out.println("ERRO gerarXML: " + ex);
        }
    }
//
//    public static void main(String[] args) {
//        XMLOperations a = new XMLOperations();
//        a.lerXML("D:\\RODRIGO\\gamelist.xml");
//    }

}
