package dgounaris.dev.sch.DBHelper;

import android.provider.BaseColumns;

/**
 * Created by DimitrisLPC on 13/5/2017.
 */

final public class MyDBContract {

    //make it private so that nobody can initialize this
    private MyDBContract() {
    }

    public static class People implements BaseColumns {
        public static final String TABLE_NAME = "People";
        public static final String COLUMN_NAME_ID = "Id";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_SURNAME = "Surname";
        public static final String COLUMN_NAME_IMAGE = "Image";
        public static final String COLUMN_NAME_POINTS = "Points";
    }

    public static class Credentials implements BaseColumns {
        public static final String TABLE_NAME = "Credentials";
        public static final String COLUMN_NAME_USERNAME = "Username";
        public static final String COLUMN_NAME_PASSWORD = "Password";
        public static final String COLUMN_NAME_PERSON_ID = "Person_Id";
    }

    public static class Trophies implements BaseColumns {
        public static final String TABLE_NAME = "Trophies";
        public static final String COLUMN_NAME_ID = "Id";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_DESCRIPTION = "Description";
        public static final String COLUMN_NAME_IMAGE = "Image";
    }

    public static class People_Trophies implements BaseColumns {
        public static final String TABLE_NAME = "People_Trophies";
        public static final String COLUMN_NAME_PERSON_ID = "Person_Id";
        public static final String COLUMN_NAME_TROPHY_ID = "Trophy_id";
    }

    public static class Services implements BaseColumns {
        public static final String TABLE_NAME = "Services";
        public static final String COLUMN_NAME_ID = "Id";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_EMPTY_SLOTS = "EmptySlots";
        public static final String COLUMN_NAME_POINTS = "Points";
    }

    public static class Bins implements BaseColumns {
        public static final String TABLE_NAME = "Bins";
        public static final String COLUMN_NAME_ID = "Id";
        public static final String COLUMN_NAME_LATITUDE = "Lat";
        public static final String COLUMN_NAME_LONGITUDE = "Long";
        public static final String COLUMN_NAME_SPACE = "Space";
    }

}
