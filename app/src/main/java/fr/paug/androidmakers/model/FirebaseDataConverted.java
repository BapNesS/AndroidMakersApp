package fr.paug.androidmakers.model;

import android.util.SparseArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static fr.paug.androidmakers.util.MapUtil.*;

/**
 * Created by stan on 18/03/2017.
 */

public class FirebaseDataConverted {
    private static final SimpleDateFormat ISO_8601_DATEFORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);


    private final SparseArray<Room> mRooms = new SparseArray<>();
    private final SparseArray<Session> mSessions = new SparseArray<>();
    private final SparseArray<Speaker> mSpeakers = new SparseArray<>();
    private final List<ScheduleSlot> mScheduleSlots = new ArrayList<>();

    public SparseArray<Room> getRooms() {
        return mRooms;
    }

    public SparseArray<Session> getSessions() {
        return mSessions;
    }

    public SparseArray<Speaker> getSpeakers() {
        return mSpeakers;
    }

    public List<ScheduleSlot> getScheduleSlots() {
        return mScheduleSlots;
    }

    public void loadAllFromFirebase(Object value) {
        mScheduleSlots.clear();
        if (!(value instanceof Map)) {
            return;
        }
        Map root = (Map) value;
        loadRoomsFromFirebase(root.get("rooms"));
        loadSpeakersFromFirebase(root.get("speakers"));
        loadSessionsFromFirebase(root.get("sessions"));
        loadScheduleFromFirebase(root.get("schedule"));
    }

    private void loadRoomsFromFirebase(Object object) {
        if (!(object instanceof List)) {
            return;
        }
        List values = (List) object;
        for (Object value : values) {
            if (value instanceof Map) {
                Map map = (Map) value;
                int id = getId(map);
                if (id >= 0) {
                    mRooms.put(id, new Room(getString(map, "name")));
                }
            }
        }
    }

    private void loadSpeakersFromFirebase(Object object) {
        if (!(object instanceof List)) {
            return;
        }
        List values = (List) object;
        for (Object value : values) {
            if (value instanceof Map) {
                Map map = (Map) value;
                int id = getId(map);
                if (id >= 0) {
                    mSpeakers.put(id, new Speaker(
                            getString(map, "name"),
                            getString(map, "bio"),
                            getString(map, "company"),
                            getString(map, "surname"),
                            getString(map, "thumbnailUrl")
                    ));
                }
            }
        }
    }

    private void loadSessionsFromFirebase(Object object) {
        if (!(object instanceof List)) {
            return;
        }
        List values = (List) object;
        for (Object value : values) {
            if (value instanceof Map) {
                Map map = (Map) value;
                int id = getId(map);
                if (id >= 0) {
                    mSessions.put(id, new Session(
                            getString(map, "title"),
                            getString(map, "description"),
                            getString(map, "language"),
                            getIntArray(map, "speakers"),
                            getString(map, "subtype")
                    ));
                }
            }
        }
    }

    private void loadScheduleFromFirebase(Object object) {
        if (!(object instanceof List)) {
            return;
        }
        List values = (List) object;
        for (Object value : values) {
            if (value instanceof Map) {
                Map map = (Map) value;
                int roomId = getInt(map, "roomId", -1);
                int sessionId = getInt(map, "sessionId", -1);
                long startDate = getTimestamp(getString(map, "startDate"));
                long endDate = getTimestamp(getString(map, "endDate"));
                if (startDate > 0 && endDate > 0
                        && sessionId >= 0 && roomId >= 0) {
                    ScheduleSlot schedule = new ScheduleSlot(
                            roomId, sessionId, startDate, endDate);
                    mScheduleSlots.add(schedule);
                }
            }
        }
    }

    private int getId(Map map) {
        return getInt(map, "id", -1);
    }

    private long getTimestamp(String dateIso) {
        if (dateIso == null) {
            return 0;
        }
        if (dateIso.endsWith("Z")) {
            dateIso = dateIso.substring(0, dateIso.length() - 1) + "+0000";
        }
        try {
            return ISO_8601_DATEFORMAT.parse(dateIso).getTime();
        } catch (ParseException e) {
            return 0;
        }
    }
}