package com.moods.bikersrides.database.dao;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.moods.bikersrides.database.vao.Via;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table VIA.
*/
public class ViaDao extends AbstractDao<Via, Long> {

    public static final String TABLENAME = "VIA";

    /**
     * Properties of entity Via.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Via = new Property(1, String.class, "via", false, "VIA");
        public final static Property RideId = new Property(2, Long.class, "rideId", false, "RIDE_ID");
    };

    private Query<Via> ride_ViasQuery;

    public ViaDao(DaoConfig config) {
        super(config);
    }
    
    public ViaDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'VIA' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'VIA' TEXT," + // 1: via
                "'RIDE_ID' INTEGER);"); // 2: rideId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'VIA'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Via entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String via = entity.getVia();
        if (via != null) {
            stmt.bindString(2, via);
        }
 
        Long rideId = entity.getRideId();
        if (rideId != null) {
            stmt.bindLong(3, rideId);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Via readEntity(Cursor cursor, int offset) {
        Via entity = new Via( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // via
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2) // rideId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Via entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setVia(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setRideId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Via entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Via entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "vias" to-many relationship of Ride. */
    public List<Via> _queryRide_Vias(Long rideId) {
        synchronized (this) {
            if (ride_ViasQuery == null) {
                QueryBuilder<Via> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.RideId.eq(null));
                ride_ViasQuery = queryBuilder.build();
            }
        }
        Query<Via> query = ride_ViasQuery.forCurrentThread();
        query.setParameter(0, rideId);
        return query.list();
    }

}
