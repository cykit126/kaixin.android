package com.kaixindev.net;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kaixindev.core.FileLog;
import com.kaixindev.core.StringUtil;
import com.kaixindev.io.Copier;
import com.kaixindev.io.InputBuffer;
import com.kaixindev.io.OutputBuffer;
import com.kaixindev.io.StreamInputBuffer;

public class HTTPDownloadBuilder {
    private int mTimeoutMillis = 10 * 1000;

    private static class Size {
        public long value;
    }

    public HTTPDownloadBuilder setTimeout(final int millis) {
        if (millis > 0) {
            mTimeoutMillis = millis;
        }
        return this;
    }

    public Copier build(final String uri, final OutputBuffer dest, final File logFile) {
        if (StringUtil.isEmpty(uri)
            || dest == null
            || logFile == null)
        {
            return null;
        }
        final HttpURLConnection conn = openConnection(uri);
        if (conn == null) {
            return null;
        }
        conn.setConnectTimeout(mTimeoutMillis);
        conn.setReadTimeout(mTimeoutMillis);

        final FileLog log = FileLog.newInstance(logFile);
        if (log.isOK()) {
            addRequestProperties(conn, log, dest.getSize());
        }
        else {
            if (!log.init()) {
                return null;
            }
        }

        try {
            conn.connect();
            //Log.d("ContentLength:" + conn.getContentLength());
            final Copier copier = new Copier();
            final Size startSize = new Size();
            //final Size totalSize = new Size();
            copier.addOnStartListener(new Copier.OnStartListener() {
                @Override
                public boolean onStart(final InputBuffer input) {
					final long[] sizes = parseSize(conn);
					final long current = sizes[0];
					startSize.value = current;
					final long total = sizes[1];
					if (total >= 0) {
						// totalSize.value = total;
						if (!dest.seek(current)) {
							return false;
						}
						if (!log.setCurrentSize(current)) {
							return false;
						}
						if (!log.setTotalSize(total)) {
							return false;
						}
						return log.setLastModified(conn.getLastModified());
					} else {
						return true;
					}
                }
            });
            copier.addOnProgressListener(new Copier.OnProgressListener() {
                @Override
                public boolean onProgress(final InputBuffer input, final OutputBuffer output, final long current)
                {
                	//long current_total = current + startSize.value;
                	//Log.d("start:" +startSize.value + ", current:" + current + ", current_total:" + current_total + ", total:" + totalSize.value);
                    return log.setCurrentSize(current + startSize.value);
                }
            });
            final StreamInputBuffer input = new StreamInputBuffer(conn.getInputStream());
            input.setSize(conn.getContentLength());
            input.setPath(uri);
            copier.setFrom(input);
            copier.setTo(dest);
            return copier;
        }
        catch (final IOException e) {
            return null;
        }
    }

    /**
     * Open connection.
     * @param timeout if timeout <=0, timeout is set to {@link #TIMEOUT_SHORT}.
     * @return
     */
    private HttpURLConnection openConnection(final String uri) {
        try {
            final URL oUrl = new URL(uri);
            final HttpURLConnection conn = (HttpURLConnection)oUrl.openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setUseCaches(false);
            return conn;
        }
        catch (final Exception e) {
            return null;
        }
    }

    private void addRequestProperties(final HttpURLConnection conn, final FileLog log, final long fileSize)
    {
        final long total = log.getTotalSize();
        final long current = log.getCurrentSize();
        final String lastModified = log.getLastModifiedString();
        if (total == fileSize) {// File log may still be up to date.
            if (current == total) { // Downloaded, check if modified.
                if (!StringUtil.isEmpty(lastModified)) {
                    conn.addRequestProperty("If-Modified-Since", lastModified);
                }
            }
            else if (current < total) { // Not finished.
                final StringBuilder range = new StringBuilder();
                range.append("bytes=")
                        .append(current)
                        .append("-")
                        .append(total);
                conn.addRequestProperty("Range", range.toString());
                if (!StringUtil.isEmpty(lastModified)) {
                    conn.addRequestProperty("If-Range", lastModified);
                }
            }
            else {
                // File log is out-dated. Don't need to do anything.
            }
        }
    }

    private static long[] parseSize(final URLConnection conn) {
        final String range = conn.getHeaderField("Content-Range");
        if (range != null) {
            final Pattern pattern = Pattern.compile("bytes\\s+(\\d+)\\-(\\d+)\\/(\\d+)");
            final Matcher matcher = pattern.matcher(range);
            long start = 0;
            long total = 0;
            if (matcher != null && matcher.matches()) {
                final String strStart = matcher.group(1);
                if (!StringUtil.isEmpty(strStart)) {
                    start = Long.parseLong(strStart);
                }
                final String strTotal = matcher.group(3);
                if (!StringUtil.isEmpty(strTotal)) {
                    total = Long.parseLong(strTotal);
                }
            }
            return new long[] { start, total };
        }
        else {
            return new long[] { 0L, conn.getContentLength() };
        }
    }
}
