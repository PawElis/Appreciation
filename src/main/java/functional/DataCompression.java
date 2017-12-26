package functional;

import bean.ConnectorBean;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Contract;
import structures.Appreciation;
import structures.Places;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static functional.MapsPreparation.doKMeans;


/**
 * Created by Pavel on 19.11.2017.
 * Database maintenance.
 */

public class DataCompression {
    private static final Logger log = Logger.getLogger(ConnectorBean.class);

    private static Places oneFromTwoPlaces(Places places1, Places places2) {
        int idUser;
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        long milliSeconds;
        Calendar calendar;
        Date dateNow = new Date();
        milliSeconds = dateNow.getTime();
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        if (places1.getIdUser() == places2.getIdUser()) {
            idUser = places1.getIdUser();
        } else {
            // If the places have another users
            idUser = -1;
        }
        return new Places(places1.getLongitude(), places1.getLatitude(), formatter.format(calendar.getTime()), idUser);
    }

    @Contract("_, _, _ -> !null")
    private static Appreciation oneFromTwoAppreciation(Appreciation appreciation1, Appreciation appreciation2, int idPlace) {
        int idUser = -1;
        return new Appreciation(idPlace, appreciation1.getAppreciationTypeId(), (byte) Math.round((appreciation1.getWeight() + appreciation2.getWeight()) / 2),
                appreciation1.getNumberOfAppreciation() + appreciation2.getNumberOfAppreciation(), idUser);
    }

    public static void mergePlacesAppreciation(ConnectorBean connectorBean, int num) {
        log.info("Data compression in the process.");
        if (connectorBean.sizePlaces() > num) {
            log.info("Data compression .");
            long time = getDateNow();
            //collection places and appreciations for this places
            ArrayList<Places> places = connectorBean.getPlaces(Long.toString(time));
            ArrayList<ArrayList<Appreciation>> appreciationList = new ArrayList<>();
            for (Places place : places) {
                appreciationList.add(connectorBean.getAllAppreciationToUser(connectorBean.getIdAppreciationByIdPlace(place.getIdPlace())));
            }
            double percentOfPlaces = 0.1;
            int numbOfClusters = (int) Math.round(places.size() * percentOfPlaces);
            ArrayList<MapsPreparation.Point> centers = (ArrayList<MapsPreparation.Point>) doKMeans(places, numbOfClusters);
            ArrayList<Integer> indexesOfCenters = new ArrayList<>(places.size());

            for (Places place : places) {
                indexesOfCenters.set(places.indexOf(place), getIdNearestCenter(place, centers));
            }
            for (int i = 0; i < places.size(); i++) {
                int indexCenter = getIdNearestCenter(places.get(i), centers);
                for (int j = i; j < places.size(); j++) {
                    if (indexCenter == getIdNearestCenter(places.get(j), centers)) {
                        places.set(i, oneFromTwoPlaces(places.get(i), places.get(j)));
                        places.remove(j);
                    }
                }
            }
            try {
                connectorBean.updatePlaces(places);
            } catch (SQLException e) {
                log.warn("Data compression error.", e);
            }
            //Get id place from bd and work with Appreciations
            for (int i = 0; i < indexesOfCenters.size(); i++) {
                for (int j = i; j < indexesOfCenters.size(); j++) {
                    ArrayList<Appreciation> appList = appreciationList.get(i);
                    if (Objects.equals(indexesOfCenters.get(i), indexesOfCenters.get(j))) {

                        for (int k = 0; k < appreciationList.get(i).size(); k++) {
                            appreciationList.get(i).set(k, oneFromTwoAppreciation(appList.get(k), appreciationList.get(j).get(k),
                                    connectorBean.getIdPlaceByLongitude(places.get(i).getLongitude()).getIdPlace()));
                        }
                        appreciationList.remove(j);
                    }
                }
            }
            for (ArrayList<Appreciation> app : appreciationList) {
                try {
                    connectorBean.updateAppreciation(app);
                } catch (SQLException e) {
                    log.warn("Update Appreciation error.", e);
                }
            }
        }
        log.info("Data compression is successfully completed.");
    }

    private static long getDateNow() {
        Date dateNow = new Date();
        return dateNow.getTime();
    }

    private static int getIdNearestCenter(Places place, ArrayList<MapsPreparation.Point> centers) {
        double min = 1000;
        int index = -1;
        for (MapsPreparation.Point center : centers) {
            double distance = (new MapsPreparation.Point(place.getLongitude(), place.getLatitude())).getDistance(center);
            if (distance < min) {
                index = centers.indexOf(center);
                min = distance;
            }
        }
        return index;
    }
}
