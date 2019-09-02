package Bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("game")
public class GameListBean {

    private String path;
    private String name;
    private String image;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
