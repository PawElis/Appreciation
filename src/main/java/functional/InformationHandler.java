package functional;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import bean.ConnectorBean;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
import com.vk.api.sdk.queries.friends.FriendsGetOrder;
import com.vk.api.sdk.queries.users.UserField;
import org.apache.log4j.Logger;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import structures.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import static oracle.jrockit.jfr.events.Bits.intValue;

/**
 * Created by Pavel on 13.11.2017.
 * Class for get information from VK and set it to DataBase
 */

public class InformationHandler {
    private static String jsonString = "";
    private static JSONParser parser = new JSONParser();
    private LuceneMorphology luceneMorph = new RussianLuceneMorphology();
    private static ArrayList<Double> latitude = new ArrayList<>();
    private static ArrayList<Double> longitude = new ArrayList<>();
    private static ArrayList<Integer> idUser = new ArrayList<>();
    private static ArrayList<Integer> idFriends = new ArrayList<>();
    private static ArrayList<String> name = new ArrayList<>();
    private static ArrayList<String> photo = new ArrayList<>();
    private static ArrayList<Integer> idPlace = new ArrayList<>();
    private static ArrayList<ArrayList<String>> vocabularies = new ArrayList<>();
    private static ArrayList<String> text = new ArrayList<>();
    private static final Logger log = Logger.getLogger(InformationHandler.class);
    private static final String res = "response";
    private final String it = "items";
    private static final String ph = "photo_100";
    private static final String fn = "first_name";

    public InformationHandler() throws IOException {
    }

    // Getting information from VK
    private static JSONObject getResponseFromVk(VkApiClient vk, UserActor actor, int id) {
        try {
            log.info("Getting CheckIns.");
            jsonString = vk.places().getCheckins(actor)
                    .userId(id)
                    .count(100)
                    .executeAsString();
        } catch (ClientException e) {
            log.warn("CheckIn is disable.", e);
        }
        return parserString();
    }

    private static JSONObject getResponseIdFriendsFromVk(VkApiClient vk, UserActor actor, int idUser) {
        try {
            log.info("Getting id friends.");
            jsonString = vk.friends().get(actor).userId(idUser).order(FriendsGetOrder.HINTS).count(50)
                    .executeAsString();
        } catch (ClientException e) {
            log.warn("Friends is disable.", e);
        }
        return parserString();
    }

    private static JSONObject getResponseInfUsersFromVk(VkApiClient vk, UserActor actor, ArrayList<Integer> idUsers) {
        ArrayList<String> idUsersStr = new ArrayList<>();
        for (int idUs : idUsers) {
            idUsersStr.add(String.valueOf(idUs));
        }
        try {
            log.info("Getting photo by id.");
            jsonString = vk.users().get(actor).userIds(idUsersStr).fields(UserField.PHOTO_100)
                    .executeAsString();
        } catch (ClientException e) {
            log.warn("Photo is disable", e);
        }
        return parserString();
    }

    private static JSONObject parserString() {
        Object obj = null;
        try {
            log.info("Parsing JSON.");
            obj = parser.parse(jsonString);
        } catch (ParseException e) {
            log.warn("Parsing JSON failed.", e);
        }
        return (JSONObject) obj;
    }

    // Data processing
    private ArrayList<Integer> getIdUser() {
        return idUser;
    }

    /* Getting name*/
    private static String getNameFromJSON(JSONObject jsonObject, int index) {
        JSONArray jsonArray = (JSONArray) jsonObject.get(res);
        jsonObject = (JSONObject) jsonArray.get(index);
        return jsonObject.get(fn).toString();
    }

    /* Getting photo*/
    private static String getPhotoFromJSON(JSONObject jsonObject, int index) {
        JSONArray jsonArray = (JSONArray) jsonObject.get(res);
        jsonObject = (JSONObject) jsonArray.get(index);
        return jsonObject.get(ph).toString();
    }

    private static boolean isNamePhotoNotNull(JSONObject jsonObject, int index) {
        return getNameFromJSON(jsonObject, index) != null && getPhotoFromJSON(jsonObject, index) != null;
    }

    private byte getAppreciationForString(String s, int index) {
        byte number = 0;
        for (String s1 : vocabularies.get(index)) {
            if (s.contains(s1)) {
                number = (byte) ((s + "\0").split(s1).length - 1);
            }
        }
        return number;
    }

    private ArrayList<Byte> getAppreciationForText(String s) throws IOException {
        ArrayList<Byte> weight = new ArrayList<>();
        byte number = 0;
        ArrayList<String> stringArrayList = getNormalForm(s);
        for (int i = 0; i < vocabularies.size(); i++) {
            for (int j = 1; j < stringArrayList.size(); j++) {
                if (vocabularies.get(i).indexOf(stringArrayList.get(j)) != -1) {
                    number = getNumberOfRepetition(vocabularies.get(i), stringArrayList.get(j));
                }
            }
            number += getAppreciationForString(s, i);
            weight.add(i, number);
            number = 0;
        }
        return weight;
    }

    private byte getNumberOfRepetition(ArrayList<String> arr, String s) throws IOException {
        byte number = 0;
        for (String s1 : arr) {
            if (getNormalFormOneWord(s1).equals(s)) {
                number += 1;
            }
        }
        return number;
    }

    /* Getting the dictionary form*/
    private ArrayList<String> getNormalForm(String text) throws IOException {
        ArrayList<String> s = new ArrayList<>(Arrays.asList(text.toLowerCase().replaceAll(",", " ")
                .replaceAll("[^а-я ]", "").replaceAll("\\s+", " ").trim().split(" ")));
        if (s.size() > 0) {
            s.remove("");
        }
        for (int i = 0; i < s.size(); i++) {
            List<String> str = luceneMorph.getNormalForms(s.get(i));
            if (str.size() > 0) {
                s.set(i, luceneMorph.getNormalForms(s.get(i)).get(0));
            } else {
                s.remove(i);
            }
        }
        return s;
    }

    private String getNormalFormOneWord(String text) throws IOException {
        text = getNormalForm(text).get(0);
        return text;
    }

    private void allAdd(JSONObject jsonObject, int id) throws IOException {
        jsonObject = (JSONObject) jsonObject.get(res);
        JSONArray jsonArray = (JSONArray) jsonObject.get(it);
        int idPl;
        for (Object aJsonArray : jsonArray) {
            String id_p = "place_id";
            idPl = intValue(((JSONObject) aJsonArray).get(id_p));
            if (idPl != 0) {
                String lat = "latitude";
                latitude.add((double) ((JSONObject) aJsonArray).get(lat));
                String lon = "longitude";
                longitude.add((double) ((JSONObject) aJsonArray).get(lon));
                String txt = "text";
                text.add((String) ((JSONObject) aJsonArray).get(txt));
                idPlace.add(idPl);
                idUser.add(id);
            }
        }
    }

    private void addFriends(JSONObject jsonObject) throws IOException {
        try {
            log.info("Getting friends from VK.");
            jsonObject = (JSONObject) jsonObject.get(res);
            JSONArray jsonArray = (JSONArray) jsonObject.get(it);
            if (jsonArray.size() > 0) {
                for (Object aJsonArray : jsonArray) {
                    idFriends.add((int) Double.parseDouble(aJsonArray.toString()));
                }
            }
        } catch (Exception e) {
            log.warn("Friends don't get from VK.", e);
        }
    }

    private void addInf(JSONObject jsonObject) {
        JSONArray jsonArray = (JSONArray) jsonObject.get(res);
        for (int i = 0; i < jsonArray.size(); i++) {
            if (isNamePhotoNotNull(jsonObject, i)) {
                name.add(((JSONObject) jsonArray.get(i)).get(fn).toString());
                photo.add(((JSONObject) jsonArray.get(i)).get(ph).toString());
            } else {
                name.add("Друг");
                photo.add("https://s00.yaplakal.com/pics/userpic/0/3/7/av-122730.jpg");
            }
        }
    }

    private static void getVocabularies(ConnectorBean connectorBean) {
        int numOfVoc = connectorBean.numOfVocabulary();
        for (int j = 0; j < numOfVoc; j++) {
            vocabularies.add(connectorBean.getVocabulary(connectorBean.getAllTypeOfAppreciation().get(j).getAppreciationTypeId()));
        }
    }

    // Organization of information collection
    public static UserActor getUserActor(String code, VkApiClient vk, String redirectUrl) {
        UserAuthResponse authResponse = null;
        try {
            authResponse = vk.oauth()
                    .userAuthorizationCodeFlow(6246163, "HxYcSdZnXGTSPjNbGZL8", redirectUrl, code)
                    .execute();
        } catch (ApiException | ClientException e) {
            log.warn("Auth failed.", e);
        }
        assert authResponse != null;
        return new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
    }

    private static InformationHandler getNPlaces(int idUser, InformationHandler informationHandler, UserActor actor, VkApiClient vk) throws IOException {
        JSONObject jsonObject = getResponseFromVk(vk, actor, idUser);
        informationHandler.allAdd(jsonObject, idUser);
        return informationHandler;
    }

    private static void getInformAboutUsers(UserActor actor, VkApiClient vk, InformationHandler informationHandler) {
        JSONObject jsonObject = getResponseInfUsersFromVk(vk, actor, informationHandler.getIdUser());
        informationHandler.addInf(jsonObject);
    }

    static ArrayList<String> getInformAboutUser(UserActor actor, VkApiClient vk, ArrayList<Integer> idUser) {
        JSONObject jsonObject = getResponseInfUsersFromVk(vk, actor, idUser);
        return getInf(jsonObject);
    }

    private static ArrayList<String> getInf(JSONObject jsonObject) {
        ArrayList<String> s = new ArrayList<>();
        if (isNamePhotoNotNull(jsonObject, 0)) {
            s.add(0, getNameFromJSON(jsonObject, 0));
            s.add(1, getPhotoFromJSON(jsonObject, 0));
        } else {
            s.add(0, "Друг");
            s.add(1, "https://s00.yaplakal.com/pics/userpic/0/3/7/av-122730.jpg");
        }
        return s;
    }

    private void InsertNewRecords(ConnectorBean connectorBean) throws IOException {
        getVocabularies(connectorBean);
        ArrayList<Places> listPlaces = new ArrayList<>();
        ArrayList<Users> listUsers = new ArrayList<>();
        ArrayList<Appreciation> listAppreciation = new ArrayList<>();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        long milliSeconds;
        Calendar calendar;
        Date dateNow = new Date();
        //Users
        int idUserSize = idUser.size();

        for (int i = 0; i < idUserSize; i++) {
            int anIdUser = idUser.get(i);
            try {
                if (!userIsHave(connectorBean, anIdUser) && !userIsHaveInArr(anIdUser, listUsers)) {
                    listUsers.add(new Users(anIdUser, name.get(i), photo.get(i)));
                }
            } catch (IndexOutOfBoundsException e) {
                if (!userIsHave(connectorBean, anIdUser) && !userIsHaveInArr(anIdUser, listUsers)) {
                    listUsers.add(new Users(anIdUser, "Друг", "https://s00.yaplakal.com/pics/userpic/0/3/7/av-122730.jpg"));
                }
            }
        }

        try {
            connectorBean.updateUsers(listUsers);
        } catch (SQLException e) {
            log.warn("Empty record.", e);
        }
        //Places
        for (int i = 0; i < idPlace.size(); i++) {
            if (!placeIsHave(connectorBean, idPlace.get(i)) && !placeIsHaveInArr(idPlace.get(i), listPlaces)) {
                milliSeconds = dateNow.getTime();
                calendar = Calendar.getInstance();
                calendar.setTimeInMillis(milliSeconds);
                listPlaces.add(new Places(idPlace.get(i), longitude.get(i).floatValue(),
                        latitude.get(i).floatValue(), formatter.format(calendar.getTime()), idUser.get(i)));
            }
        }
        try {
            connectorBean.updatePlaces(listPlaces);
        } catch (SQLException e) {
            log.warn("Empty record.", e);
        }
        //Appreciation
        int idPl;
        int idUs;
        for (int i = 0; i < listPlaces.size(); i++) {
            int numOfVoc = connectorBean.numOfVocabulary();
            idPl = idPlace.get(i);
            idUs = idUser.get(i);
            String usersText = text.get(i);
            for (int j = 0; j < numOfVoc; j++) {
                listAppreciation.add(new Appreciation(idPl,
                        connectorBean.getAllTypeOfAppreciation().get(j).getAppreciationTypeId(),
                        getAppreciationForText(usersText).get(j),
                        1, idUs));
            }
        }
        try {
            connectorBean.updateAppreciation(listAppreciation);
        } catch (SQLException e) {
            log.warn("Empty record.", e);
        }
    }

    private static boolean userIsHave(ConnectorBean connectorBean, int id) {
        for (Users user : connectorBean.getUsers()) {
            if (user.getIdUser() == id) {
                return true;
            }
        }
        return false;
    }

    private static boolean userIsHaveInArr(int id, ArrayList<Users> usersArrayList) {
        for (Users user : usersArrayList) {
            if (user.getIdUser() == id) {
                return true;
            }
        }
        return false;
    }

    private static boolean placeIsHave(ConnectorBean connectorBean, int id) {
        for (Places place : connectorBean.getPlaces()) {
            if (place.getIdPlace() == id) {
                return true;
            }
        }
        return false;
    }

    private static boolean placeIsHaveInArr(int id, ArrayList<Places> placesArrayList) {
        for (Places place : placesArrayList) {
            if (place.getIdPlace() == id) {
                return true;
            }
        }
        return false;
    }

    public void getFriends(ConnectorBean connectorBean, InformationHandler informationHandler, VkApiClient vk, UserActor actor) throws SQLException, IOException, NullPointerException {
        int idUs = connectorBean.getIdUserForInformation();
        JSONObject jsonObject = getResponseIdFriendsFromVk(vk, actor, idUs);
        try {
            informationHandler.addFriends(jsonObject);
        } catch (IOException | NullPointerException e) {
            log.warn("Friends adding failed", e);
        }
        ConnectorBean.addFriendsToInformation(idFriends);
        ConnectorBean.deleteIdUserForInformation(idUs);
        try {
            InformationHandler.getNPlaces(idUs, informationHandler, actor, vk);
            InformationHandler.getInformAboutUsers(actor, vk, informationHandler);
            informationHandler.InsertNewRecords(connectorBean);
            clearAll();
        } catch (Exception e) {
            clearAll();
            log.warn("Error access to vk. Access to checkIns denied.", e);
        }
    }

    static ArrayList<ArrayList<Integer>> getAppreciationForAllVoc(ConnectorBean connectorBean) {
        int numOfVoc = connectorBean.numOfVocabulary();
        ArrayList<Appreciation> appreciationArrayList = connectorBean.getAllAppreciation();
        ArrayList<ArrayList<Integer>> appToVoc = new ArrayList<>(numOfVoc);
        for (int i = 0; i < numOfVoc; i++) {
            appToVoc.add(new ArrayList<>(numOfVoc));
        }
        for (int j = 0; j < appreciationArrayList.size(); j++) {
            appToVoc.get(j % numOfVoc)
                    .add(appreciationArrayList.get(j)
                            .getWeight() *
                            appreciationArrayList.get(j)
                                    .getNumberOfAppreciation());
        }
        return appToVoc;
    }

    static ArrayList<ArrayList<MapsPreparation.Point>> getPlaces(ConnectorBean connectorBean) {
        ArrayList<ArrayList<Integer>> appForVoc = getAppreciationForAllVoc(connectorBean);
        ArrayList<Appreciation> appreciationArrayList = connectorBean.getAllAppreciation();
        ArrayList<Places> places = connectorBean.getPlaces();
        ArrayList<Integer> idPlaces = new ArrayList<>();
        ArrayList<ArrayList<MapsPreparation.Point>> points = new ArrayList<>();
        for (Appreciation app : appreciationArrayList) {
            if (!idPlaces.contains(app.getIdPlace())) {
                idPlaces.add(app.getIdPlace());
            }
        }
        for (int i = 0; i < appForVoc.size(); i++) {
            points.add(new ArrayList<>());
        }
        for (int i = 0; i < appForVoc.size(); i++) {
            ArrayList<MapsPreparation.Point> point = points.get(i);
            for (int j = 0; j < appForVoc.get(i).size(); j++) {
                Places place = places.get(j);
                if (appForVoc.get(i).get(j) > 0) {
                    point.add(new MapsPreparation.Point(place.getLatitude(), place.getLongitude()));
                }
            }
        }
        return points;
    }

    static ArrayList<String> getAllNameVoc(ConnectorBean connectorBean) {
        ArrayList<TypeOfAppreciation> vocabularies = connectorBean.getAllTypeOfAppreciation();
        ArrayList<String> nameOfVoc = new ArrayList<>();
        for (TypeOfAppreciation voc : vocabularies) {
            nameOfVoc.add(voc.getNameVocabularyEng());
        }
        return nameOfVoc;
    }

    static ArrayList<String> getColorsVoc(ConnectorBean connectorBean) {
        ArrayList<TypeOfAppreciation> typeOfAppreciations = connectorBean.getAllTypeOfAppreciation();
        ArrayList<String> colorsOfVoc = new ArrayList<>();
        for (TypeOfAppreciation voc : typeOfAppreciations) {
            colorsOfVoc.add(voc.getColor());
        }
        return colorsOfVoc;
    }

    static ArrayList<MapsPreparation.Point> getAdvised(ConnectorBean connectorBean, UserActor actor) {
        ArrayList<MapsPreparation.Point> advised = new ArrayList<>();
        int numOfVoc = connectorBean.numOfVocabulary();
        try {
            ArrayList<Appreciation> app = connectorBean.getAllAppreciationToUser(actor.getId());
            ArrayList<Integer> arrayList = new ArrayList<>(numOfVoc);
            for (int i = 0; i < numOfVoc; i++) {
                arrayList.set(i, app.get(i).getWeight() * app.get(i).getNumberOfAppreciation());
            }
            //get places nearest
            ArrayList<Places> pl = connectorBean.getPlacesToAdvised(arrayList);
            for (Places places : pl) {
                advised.set(pl.indexOf(places), new MapsPreparation.Point(places.getLatitude(), places.getLongitude()));
            }
        } catch (Exception e) {
            advised = getPlaces(connectorBean).get(actor.getId() % numOfVoc);
            log.warn("Haven't places.", e);
        }
        return advised;
    }

    private static void clearAll() {
        idUser.clear();
        idPlace.clear();
        longitude.clear();
        latitude.clear();
        name.clear();
        photo.clear();
        text.clear();
        idFriends.clear();
    }
}
