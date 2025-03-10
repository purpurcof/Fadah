package info.preva1l.fadah.hooks.impl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import info.preva1l.fadah.Fadah;
import info.preva1l.fadah.config.Config;
import info.preva1l.fadah.data.DatabaseManager;
import info.preva1l.hooker.annotation.*;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Hook(id = "influxdb")
@Reloadable(async = true)
@Require(type = "config", value = "influxdb")
public class InfluxDBHook {
    private InfluxDBClient client;
    private WriteApiBlocking writeApi;

    @OnStart
    public boolean onStart() {
        Config.Hooks.InfluxDB conf = Config.i().getHooks().getInfluxdb();
        try {
            String url = conf.getUri();
            String token = conf.getToken();
            String org = conf.getOrg();
            String bucket = conf.getBucket();
            this.client = InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
            this.writeApi = client.getWriteApiBlocking();
            return true;
        } catch (Exception e) {
            Fadah.getConsole().log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    public void log(String message) {
        CompletableFuture.runAsync(() -> {
            Point point = Point.measurement("Transaction-Logs")
                    .time(Instant.now(), WritePrecision.MS)
                    .addField("message", message);
            writeApi.writePoint(point);
        }, DatabaseManager.getInstance().getThreadPool());
    }

    @OnStop
    public void onStop() {
        if (client == null) return;
        client.close();
    }
}
