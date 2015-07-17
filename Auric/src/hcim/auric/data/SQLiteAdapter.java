package hcim.auric.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteAdapter extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "Auric_Database";
	
	private static SQLiteAdapter INSTANCE;
	
	public static SQLiteAdapter getInstance(Context context){
		if(INSTANCE == null)
			INSTANCE = new SQLiteAdapter(context);
		return INSTANCE;
	}

	private SQLiteAdapter(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(IntrusionTable.CREATE);
		db.execSQL(SessionTable.CREATE);
		db.execSQL(IntruderTable.CREATE);
		db.execSQL(EventLogTable.CREATE);
		db.execSQL(PictureTable.CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + IntrusionTable.TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + EventLogTable.TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + IntruderTable.TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + SessionTable.TABLE);
		db.execSQL("DROP TABLE IF EXISTS " + PictureTable.TABLE);
		
		this.onCreate(db);
	}

}
