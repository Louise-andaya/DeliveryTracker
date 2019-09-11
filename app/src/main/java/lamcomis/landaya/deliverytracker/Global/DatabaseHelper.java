package lamcomis.landaya.deliverytracker.Global;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Delivery";
    public static final String TABLE_NAME = "delivery";
    public static final String COL_1 = "Si_number";
    public static final String COL_2 = "Driver";
    public static final String COL_3 = "Customer_name";
    public static final String COL_4 = "Contact_person";
    public static final String COL_5 = "Customer_code";
    public static final String COL_6 = "Dtr_number";
    public static final String COL_7 = "Remarks";
    public static final String COL_8 = "First_man";
    public static final String COL_9 = "Second_man";
    public static final String COL_10 = "DTR_date";
    public static final String COL_11 = "Status";
    public static final String COL_12 = "Receipt";
    public static final String COL_13 = "Signature";
    public static final String COL_14 = "Date_added";
    public static final String COL_15 = "Latitude";
    public static final String COL_16 = "Longitude";
    public static final String COL_17 = "Address";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" ('"+COL_1+"' INTEGER PRIMARY KEY ON CONFLICT REPLACE, "+
                "'"+COL_2+"' TEXT, '"+COL_3+"' TEXT, '"+COL_4+"' TEXT, '"+COL_5+"' TEXT, '"+COL_6+"' TEXT, '"+COL_7+"' TEXT, " +
                "'"+COL_8+"' TEXT, '"+COL_9+"' TEXT,'"+COL_10+"' DATE, '"+COL_11+"'TEXT, '"+COL_12+"'TEXT, '"+COL_13+"'TEXT, " +
                "'"+COL_14+"'TEXT, '"+COL_15+"'TEXT, '"+COL_16+"'TEXT, '"+COL_17+"'TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String si_number, String driver, String customer_name, String contact_person, String customer_code,
                              String dtr_number, String remarks, String first_man, String second_man, String dtr_date, String status,
                              String receipt, String signature, String date_added, String latitude, String longitude, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1,si_number);
        contentValues.put(COL_2,driver);
        contentValues.put(COL_3,customer_name);
        contentValues.put(COL_4,contact_person);
        contentValues.put(COL_5,customer_code);
        contentValues.put(COL_6,dtr_number);
        contentValues.put(COL_7,remarks);
        contentValues.put(COL_8,first_man);
        contentValues.put(COL_9,second_man);
        contentValues.put(COL_10,dtr_date);
        contentValues.put(COL_11,status);
        contentValues.put(COL_12,receipt);
        contentValues.put(COL_13,signature);
        contentValues.put(COL_14,date_added);
        contentValues.put(COL_15,latitude);
        contentValues.put(COL_16,longitude);
        contentValues.put(COL_17,address);
        long result = db.insert(TABLE_NAME,null ,contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }
    public Cursor getCount() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor data = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        return data;
    }
    public Cursor getDelivery(String trans_date) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" WHERE " +COL_10+" = ?",new String[]{trans_date});
        return res;
    }
    public Cursor getDetails(String customer_name, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" WHERE " +COL_3+" = ?"+" AND "+COL_11+" = ?",new String[]{customer_name, status});
        return res;
    }

    public Integer deleteData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, null, null);
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT DISTINCT Customer_name, Contact_person, Dtr_number, First_man, Second_man, Status FROM "+TABLE_NAME ,null);
        return res;
    }
    public Cursor getSaveDel(String customer, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" WHERE " +COL_3+" = ?"+" AND "+ COL_11+" = ?",new String[]{customer, status});
        return res;
    }
    public boolean updateData(String status, String receipt, String signature, String date_added, String latitude,
                              String longitude, String address, String si_number)
    {
        ContentValues contentValues= new ContentValues();
        contentValues.put(COL_11,status);
        contentValues.put(COL_12,receipt);
        contentValues.put(COL_13,signature);
        contentValues.put(COL_14,date_added);
        contentValues.put(COL_15,latitude);
        contentValues.put(COL_16,longitude);
        contentValues.put(COL_17,address);

        SQLiteDatabase db = this.getWritableDatabase();
        return db.update(TABLE_NAME, contentValues, COL_3 + "=?", new String[]{si_number}) > 0;
    }
    public boolean deletePending(String customer_name, String status)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL_3+" = ?"+" AND "+ COL_11+" = ?",new String[]{customer_name, status}) > 0;
    }
}
