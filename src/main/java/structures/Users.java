package structures;

/**
 * Created by Pavel on 12.11.2017.
 * Entity Users
 */
public class Users {
    private int idUser;
    private String name;
    private String photo;

    public Users(int idUser, String name, String photo) {
        this.idUser = idUser;
        this.name = name;
        this.photo = photo;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getName() {
        return name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
