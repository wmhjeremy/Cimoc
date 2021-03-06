package com.hiroshi.cimoc;

import android.app.Application;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.hiroshi.cimoc.component.AppGetter;
import com.hiroshi.cimoc.core.Storage;
import com.hiroshi.cimoc.fresco.ControllerBuilderProvider;
import com.hiroshi.cimoc.helper.DBOpenHelper;
import com.hiroshi.cimoc.helper.UpdateHelper;
import com.hiroshi.cimoc.manager.PreferenceManager;
import com.hiroshi.cimoc.manager.SourceManager;
import com.hiroshi.cimoc.model.DaoMaster;
import com.hiroshi.cimoc.model.DaoSession;
import com.hiroshi.cimoc.saf.DocumentFile;
import com.hiroshi.cimoc.ui.adapter.GridAdapter;

import org.greenrobot.greendao.identityscope.IdentityScopeType;

import okhttp3.OkHttpClient;

/**
 * Created by Hiroshi on 2016/7/5.
 */
public class App extends Application implements AppGetter {

    public static int mWidthPixels;
    public static int mHeightPixels;
    public static int mCoverWidthPixels;
    public static int mCoverHeightPixels;
    public static int mLargePixels;

    private static OkHttpClient mHttpClient;

    private DocumentFile mDocumentFile;
    private PreferenceManager mPreferenceManager;
    private ControllerBuilderProvider mBuilderProvider;
    private RecyclerView.RecycledViewPool mRecycledPool;
    private DBOpenHelper mOpenHelper;
    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        mOpenHelper = new DBOpenHelper(this, "cimoc.db");
        UpdateHelper.update(getPreferenceManager(), getDaoSession());
        Fresco.initialize(this);
        initPixels();
    }

    @Override
    public App getAppInstance() {
        return this;
    }

    private void initPixels() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        mWidthPixels = metrics.widthPixels;
        mHeightPixels = metrics.heightPixels;
        mCoverWidthPixels = mWidthPixels / 3;
        mCoverHeightPixels = mHeightPixels * mCoverWidthPixels / mWidthPixels;
        mLargePixels = 3 * metrics.widthPixels * metrics.heightPixels;
    }

    public void initRootDocumentFile() {
        String uri = mPreferenceManager.getString(PreferenceManager.PREF_OTHER_STORAGE);
        mDocumentFile = Storage.initRoot(this, uri);
    }

    public DocumentFile getDocumentFile() {
        if (mDocumentFile == null) {
            initRootDocumentFile();
        }
        return mDocumentFile;
    }

    public DaoSession getDaoSession() {
        if (mDaoSession == null) {
            mDaoSession = new DaoMaster(mOpenHelper.getWritableDatabase()).newSession(IdentityScopeType.None);
        }
        return mDaoSession;
    }

    public PreferenceManager getPreferenceManager() {
        if (mPreferenceManager == null) {
            mPreferenceManager = new PreferenceManager(getApplicationContext());
        }
        return mPreferenceManager;
    }

    public RecyclerView.RecycledViewPool getGridRecycledPool() {
        if (mRecycledPool == null) {
            mRecycledPool = new RecyclerView.RecycledViewPool();
            mRecycledPool.setMaxRecycledViews(GridAdapter.TYPE_GRID, 20);
        }
        return mRecycledPool;
    }

    public ControllerBuilderProvider getBuilderProvider() {
        if (mBuilderProvider == null) {
            mBuilderProvider = new ControllerBuilderProvider(getApplicationContext(),
                    SourceManager.getInstance(this).new HeaderGetter(), true);
        }
        return mBuilderProvider;
    }

    public static OkHttpClient getHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new OkHttpClient();
        }
        return mHttpClient;
    }

}
