package com.kaixindev.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;

class CallLogHelper {
    public static final int ALL_CALLS = -1;

    public static class Call {
        public static final String INCOMING = "Incoming";
        public static final String OUTGOING = "Outgoing";
        public static final String MISSED = "Missed";

        public String type = null;
        public long duration = 0;
        public String number = null;
        public String standardnumber = null;
        public String name = null;
        public String location = null;
        public long date = 0;
        public long contactid = -1;

        @Override
        public String toString() {
            final JSONObject obj = new JSONObject();
            try {
                final Date oDate = new Date(date);
                final SimpleDateFormat sdf = new SimpleDateFormat("M.dd");
                final String strDate = sdf.format(oDate);
                obj.put("type", type)
                        .put("duration", duration)
                        .put("number", number)
                        .put("name", (name == null || name.length() == 0) ? number : name)
                        .put("location", location)
                        .put("date", strDate)
                        .put("contactid", contactid);
            }
            catch (final Exception e) {
                Log.w(e.getMessage(),Log.TAG);
            }
            return obj.toString();
        }
    }

    public static void askForPermission(final Context context) {
        final ContentResolver cr = context.getContentResolver();
        cr.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
    }

    public static void listColumns(final Cursor cursor) {
        for (final String name : cursor.getColumnNames()) {
            Log.i("CallLog:" + name,Log.TAG);
        }
    }

    private static String getString(final Cursor cursor, final String field, final String defValue)
    {
        if (cursor.getColumnIndex(field) >= 0) {
            return cursor.getString(cursor.getColumnIndex(field));
        }
        else {
            return defValue;
        }
    }

    private static int getInt(final Cursor cursor, final String field, final int defValue)
    {
        if (cursor.getColumnIndex(field) >= 0) {
            return cursor.getInt(cursor.getColumnIndex(field));
        }
        else {
            return defValue;
        }
    }

    private static long getLong(final Cursor cursor, final String field, final long defValue)
    {
        if (cursor.getColumnIndex(field) >= 0) {
            return cursor.getLong(cursor.getColumnIndex(field));
        }
        else {
            return defValue;
        }
    }

    public static Call[] getLogs(final Context context, final int rows) {
        final ContentResolver cr = context.getContentResolver();
        final String order = CallLog.Calls.DATE + " DESC";
        final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, order);
        if (cursor != null) {
            final int count = cursor.getCount();
            if (count == 0) {
                return null;
            }

            final ArrayList<Call> calls = new ArrayList<Call>();
            cursor.moveToFirst();
            do {
                final long contactid = getLong(cursor, "contactsid", 0);
                final String number = getString(cursor, CallLog.Calls.NUMBER, "");
                final String standardnumber = getString(cursor, "standardnumber", number);
                boolean bSkip = false;
                if (!cursor.isNull(6)) {
                    for (final Call call : calls) {
                        if ((call.contactid > 0 && call.contactid == contactid)
                            || (call.contactid == contactid && call.standardnumber.equalsIgnoreCase(standardnumber)))
                        {
                            bSkip = true;
                            break;
                        }
                    }
                }
                if (bSkip) {
                    continue;
                }
                final Call call = new Call();
                switch (getInt(cursor, CallLog.Calls.TYPE, CallLog.Calls.INCOMING_TYPE)) {
                case CallLog.Calls.INCOMING_TYPE:
                    call.type = Call.INCOMING;
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    call.type = Call.OUTGOING;
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    call.type = Call.MISSED;
                    break;
                }

                call.duration = getLong(cursor, CallLog.Calls.DURATION, 0);
                call.number = number;
                call.standardnumber = standardnumber;
                call.name = getString(cursor, CallLog.Calls.CACHED_NAME, "");
                call.date = getLong(cursor, CallLog.Calls.DATE, 0);
                call.location = getString(cursor, "callerlocation", "");
                call.contactid = contactid;

                calls.add(call);
            } while (cursor.moveToNext() && calls.size() < rows);

            if (calls.size() > 0) {
                final Call[] _calls = new Call[calls.size()];
                int nCount = 0;
                for (final Call call : calls) {
                    _calls[nCount++] = call;
                }
                return _calls;
            }
        }
        return null;
    }

    public static int getContactIdFromPhoneNumber(final Context context, final String number)
    {
        final Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        final String[] projection = { PhoneLookup._ID };
        final Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            return c.getInt(0);
        }
        else {
            return -1;
        }
    }

    public static void openContactInfo(final Context context, final long contactid) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        final Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactid));
        intent.setData(uri);
        context.startActivity(intent);
    }

    public static void makePhoneCall(final Activity context, final String number) {
        Log.i("makePhoneCall:" + number,Log.TAG);
        if (number != null && number.length() > 0) {
            final Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            context.startActivity(intent);
        }
        else {
            Log.w("Phone number is empty.",Log.TAG);
        }
    }
}
