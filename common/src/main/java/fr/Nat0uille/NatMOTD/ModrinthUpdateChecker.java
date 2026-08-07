package fr.Nat0uille.NatMOTD;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public class ModrinthUpdateChecker {

    public static class LatestVersion {
        public final String versionNumber;
        public final String downloadUrl;
        public final String fileName;

        public LatestVersion(String versionNumber, String downloadUrl, String fileName) {
            this.versionNumber = versionNumber;
            this.downloadUrl = downloadUrl;
            this.fileName = fileName;
        }
    }

    private static HttpClient client() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public static LatestVersion fetchLatest(String projectSlug, String loader) throws IOException, InterruptedException {
        String url = "https://api.modrinth.com/v2/project/" + projectSlug + "/version?loaders=[%22" + loader + "%22]";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Nat0uille/NAT-MOTD-updater")
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = client().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) {
            throw new IOException("Modrinth API returned HTTP " + response.statusCode());
        }

        JsonArray versions = JsonParser.parseString(response.body()).getAsJsonArray();
        if (versions.isEmpty()) {
            return null;
        }

        JsonObject latest = versions.get(0).getAsJsonObject();
        String versionNumber = latest.get("version_number").getAsString();
        JsonArray files = latest.getAsJsonArray("files");

        JsonObject chosenFile = null;
        for (JsonElement fileElement : files) {
            JsonObject file = fileElement.getAsJsonObject();
            if (file.has("primary") && file.get("primary").getAsBoolean()) {
                chosenFile = file;
                break;
            }
        }
        if (chosenFile == null && !files.isEmpty()) {
            chosenFile = files.get(0).getAsJsonObject();
        }
        if (chosenFile == null) {
            return null;
        }

        return new LatestVersion(versionNumber, chosenFile.get("url").getAsString(), chosenFile.get("filename").getAsString());
    }

    public static void downloadTo(String downloadUrl, Path target) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(downloadUrl))
                .header("User-Agent", "Nat0uille/NAT-MOTD-updater")
                .GET()
                .build();

        Files.createDirectories(target.getParent());
        client().send(request, HttpResponse.BodyHandlers.ofFile(target));
    }
}
