package archtectsproductions.floaty_boxxy_release;

import android.content.Context;
import android.content.SharedPreferences;

public class Saver {

    public static final String PREFS = "prefs";
    private static Saver instance = null;
    private SharedPreferences prefs;

    private Saver(Context context)
    {
        this.prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static Saver getInstance(Context context)
    {
        if(instance == null)
            instance = new Saver(context);
        return instance;
    }

    public void saveString(String key, String value)
    {
        this.prefs.edit().putString(key, value).commit();
    }

    public String getString(String key)
    {
        return this.prefs.getString(key,null);
    }

    // Removes all entries from the shared preferences file.
    public boolean clearColor()
    {
        return this.prefs.edit().clear().commit();
    }
}
